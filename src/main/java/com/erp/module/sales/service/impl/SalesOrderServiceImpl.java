package com.erp.module.sales.service.impl;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.OrderConstant;
import com.erp.common.exception.BusinessException;
import com.erp.common.utils.BeanCopyUtil;
import com.erp.common.utils.OrderNoUtil;
import com.erp.common.utils.HaversineUtil;
import com.erp.common.utils.SecurityUtil;
import com.erp.module.base.entity.BasCustomer;
import com.erp.module.base.entity.BasMaterial;
import com.erp.module.base.entity.BasWarehouse;
import com.erp.module.base.mapper.BasCustomerMapper;
import com.erp.module.base.mapper.BasMaterialMapper;
import com.erp.module.base.mapper.BasWarehouseMapper;
import com.erp.module.inventory.dto.StockOccupyDTO;
import com.erp.module.inventory.service.StockService;
import com.erp.module.sales.constant.SalesOrderStatusEnum;
import com.erp.module.sales.dto.*;
import com.erp.module.sales.entity.SalOrder;
import com.erp.module.sales.entity.SalOrderItem;
import com.erp.module.sales.entity.SalLogistics;
import com.erp.module.sales.mapper.SalOrderItemMapper;
import com.erp.module.sales.mapper.SalOrderMapper;
import com.erp.module.sales.mapper.SalLogisticsMapper;
import com.erp.module.sales.service.LogisticsService;
import com.erp.module.sales.service.SalesOrderService;
import com.erp.module.sales.vo.SalOrderItemVO;
import com.erp.module.sales.vo.SalOrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalesOrderServiceImpl extends ServiceImpl<SalOrderMapper, SalOrder> implements SalesOrderService {
    private final SalOrderItemMapper itemMapper;
    private final SalLogisticsMapper logisticsMapper;
    private final BasCustomerMapper customerMapper;
    private final BasMaterialMapper materialMapper;
    private final BasWarehouseMapper warehouseMapper;
    private final StockService stockService;
    private final LogisticsService logisticsService;

    @Override
    public Page<SalOrderVO> pageList(SalesOrderPageDTO dto) {
        Page<SalOrder> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SalOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalOrder::getIsDeleted, 0);
        if(StringUtils.hasText(dto.getOrderNo())) wrapper.like(SalOrder::getOrderNo, dto.getOrderNo());
        if(dto.getCustomerId() != null) wrapper.eq(SalOrder::getCustomerId, dto.getCustomerId());
        if(dto.getOrderStatus() != null) wrapper.eq(SalOrder::getOrderStatus, dto.getOrderStatus());
        if(dto.getStartTime() != null) wrapper.ge(SalOrder::getCreateTime, dto.getStartTime());
        if(dto.getEndTime() != null) wrapper.le(SalOrder::getCreateTime, dto.getEndTime());
        wrapper.orderByDesc(SalOrder::getCreateTime);
        Page<SalOrder> entityPage = baseMapper.selectPage(page, wrapper);
        List<SalOrderVO> voList = entityPage.getRecords().stream().map(this::convertVO).collect(Collectors.toList());
        Page<SalOrderVO> res = new Page<>();
        res.setTotal(entityPage.getTotal());
        res.setRecords(voList);
        return res;
    }

    @Override
    public SalOrderVO getDetail(Long id) {
        SalOrder order = getById(id);
        return convertVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(SalesOrderSaveDTO dto, Long loginUserId) {
        BasCustomer customer = customerMapper.selectById(dto.getCustomerId());
        BasWarehouse warehouse = warehouseMapper.selectById(dto.getWarehouseId());
        // 替换原有SecurityUtil.getUserId()，使用上层Controller传入的登录人ID
        Long salesmanId = loginUserId;
        // 下面原有逻辑完全不动
        SalOrder order = BeanCopyUtil.copy(dto, SalOrder.class);
        order.setOrderNo(OrderNoUtil.generate("SO"));
        order.setOrderStatus(SalesOrderStatusEnum.DRAFT.getCode());
        order.setPaymentStatus(1);
        order.setSalesmanId(salesmanId);
        // 总金额
        BigDecimal total = BigDecimal.ZERO;
        for(SalesOrderSaveDTO.Item i : dto.getItemList()) {
            BasMaterial mat = materialMapper.selectById(i.getMaterialId());
            BigDecimal sub = i.getPlanQty().multiply(i.getUnitPrice());
            total = total.add(sub);
        }
        order.setTotalAmount(total);
        order.setActualAmount(total.subtract(dto.getDiscountAmount() == null ? BigDecimal.ZERO : dto.getDiscountAmount()));
        save(order);
        Long orderId = order.getId();
        // 明细 + 预出库
        List<SalOrderItem> itemList = dto.getItemList().stream().map(itemDto->{
            SalOrderItem item = BeanCopyUtil.copy(itemDto, SalOrderItem.class);
            item.setOrderId(orderId);
            BasMaterial mat = materialMapper.selectById(itemDto.getMaterialId());
            item.setUnitCost(mat.getAvgCost());
            item.setAmount(itemDto.getPlanQty().multiply(itemDto.getUnitPrice()));
            item.setShippedQty(BigDecimal.ZERO);
            return item;
        }).toList();
        for (SalOrderItem item : itemList) {
            itemMapper.insert(item);
        }
        // 预出库库存
        for(SalOrderItem item : itemList) {
            StockOccupyDTO occ = new StockOccupyDTO();
            occ.setMaterialId(item.getMaterialId());
            occ.setWarehouseId(dto.getWarehouseId());
            occ.setQty(item.getPlanQty());
            occ.setRefType("SALE_ORDER");
            occ.setRefId(orderId);
            stockService.preOccupy(occ);
        }
        // 状态改为待出库
        order.setOrderStatus(SalesOrderStatusEnum.PENDING_SHIP.getCode());
        updateById(order);
        return orderId;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id) {
        SalOrder order = getById(id);
        if(!SalesOrderStatusEnum.PENDING_SHIP.getCode().equals(order.getOrderStatus())) {
            throw new RuntimeException("仅待出库可取消");
        }
        // 释放预出库
        stockService.releasePreoccupyByRef(id, "SALE_ORDER");
        order.setOrderStatus(SalesOrderStatusEnum.CANCEL.getCode());
        updateById(order);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipGoods(Long orderId, Long warehouseId) {
        // 简易发货入口，根据订单ID查询基础信息，构建空ShipDTO，适用于仅传仓库ID快速发货场景
        SalesShipDTO shipDTO = new SalesShipDTO();
        shipDTO.setOrderId(orderId);
        // 查询当前订单明细填充到ShipDTO（需实现明细查询）
        List<SalOrderItem> itemList = itemMapper.selectList(
                new LambdaQueryWrapper<SalOrderItem>().eq(SalOrderItem::getOrderId, orderId)
        );
        List<SalesShipDTO.ShipItem> shipItems = itemList.stream().map(item -> {
            SalesShipDTO.ShipItem si = new SalesShipDTO.ShipItem();
            si.setItemId(item.getId());
            // 默认全部剩余未发数量一次性出库
            si.setShipQty(item.getPlanQty().subtract(item.getShippedQty()));
            return si;
        }).collect(Collectors.toList());
        shipDTO.setItemList(shipItems);
        // 物流信息可前端后续补充，这里给默认值
        shipDTO.setLogisticsCompany("自提");
        shipDTO.setTrackingNo("");
        shipDTO.setFreightAmount(BigDecimal.ZERO);
        // 调用原有完整DTO发货逻辑复用代码
        shipGoods(shipDTO);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void shipGoods(SalesShipDTO dto) {
        SalOrder order = getById(dto.getOrderId());
        if(!SalesOrderStatusEnum.PENDING_SHIP.getCode().equals(order.getOrderStatus())
                && !SalesOrderStatusEnum.PART_SHIP.getCode().equals(order.getOrderStatus())) {
            throw new RuntimeException("仅待出库/部分出库可发货");
        }
        BasCustomer customer = customerMapper.selectById(order.getCustomerId());
        BasWarehouse warehouse = warehouseMapper.selectById(order.getWarehouseId());
        // 计算运距、预估送达时间 Haversine
        double km = HaversineUtil.calcDistanceKm(
                customer.getLongitude().doubleValue(),
                customer.getLatitude().doubleValue(),
                warehouse.getLongitude().doubleValue(),
                warehouse.getLatitude().doubleValue()
        );
        BigDecimal freightEst = new BigDecimal(km).multiply(new BigDecimal("1.2"));
        LocalDateTime arriveEst = LocalDateTime.now().plusMinutes((long)(km/60*60 + 120));
        // 物流记录
        SalLogistics log = new SalLogistics();
        log.setOrderId(order.getId());
        log.setLogisticsCompany(dto.getLogisticsCompany());
        log.setTrackingNo(dto.getTrackingNo());
        log.setFreightAmount(dto.getFreightAmount());
        log.setEstimatedFreight(freightEst);
        log.setDistanceKm(new BigDecimal(km));
        log.setShipTime(LocalDateTime.now());
        log.setExpectedArriveTime(arriveEst);
        log.setFreightStatus(1);
        log.setOperatorId(SecurityUtil.getUserId());
        logisticsMapper.insert(log);
        // 出库扣库存
        boolean allFinish = true;
        for(SalesShipDTO.ShipItem shipItem : dto.getItemList()) {
            SalOrderItem item = itemMapper.selectById(shipItem.getItemId());
            BigDecimal out = shipItem.getShipQty();
            stockService.stockOut(item.getMaterialId(), order.getWarehouseId(), out,
                    "SALE_ORDER", order.getId(), SecurityUtil.getUserId());
            item.setShippedQty(item.getShippedQty().add(out));
            itemMapper.updateById(item);
            if(item.getShippedQty().compareTo(item.getPlanQty()) < 0) allFinish = false;
        }
        // 更新单据状态
        if(allFinish) order.setOrderStatus(SalesOrderStatusEnum.ALL_SHIP.getCode());
        else order.setOrderStatus(SalesOrderStatusEnum.PART_SHIP.getCode());
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receivePayment(SalesPaymentDTO dto) {
        SalOrder order = getById(dto.getOrderId());
        order.setActualAmount(order.getActualAmount().add(dto.getPayAmount()));
        // 1待收款 2部分 3全部
        if(order.getActualAmount().compareTo(order.getTotalAmount()) >= 0) {
            order.setPaymentStatus(3);
        } else {
            order.setPaymentStatus(2);
        }
        updateById(order);
    }

    @Override
    public List<SalOrderVO> exportExcel(SalesOrderPageDTO dto, HttpServletResponse response) throws IOException {
        // 1、按查询条件分页查询全部数据（不分页/超大分页导出）
        dto.setPageNum((long)(1));
        dto.setPageSize(Long.MAX_VALUE);
        Page<SalOrderVO> voPage = pageList(dto);
        List<SalOrderVO> dataList = voPage.getRecords();
        // 内部只查询返回数据，导出Excel逻辑交给Controller统一调用ExcelUtil
        return dataList;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importExcel(MultipartFile file) throws Exception {
        // Excel导入
    }

    // 实体转VO
    private SalOrderVO convertVO(SalOrder order) {
        SalOrderVO vo = BeanCopyUtil.copy(order, SalOrderVO.class);
        BasCustomer cus = customerMapper.selectById(order.getCustomerId());
        vo.setCustomerName(cus.getCustomerName());
        BasWarehouse wh = warehouseMapper.selectById(order.getWarehouseId());
        vo.setWarehouseName(wh.getWarehouseName());
        // 状态文本
        for(SalesOrderStatusEnum s : SalesOrderStatusEnum.values()) {
            if(s.getCode().equals(order.getOrderStatus())) vo.setStatusText(s.getDesc());
        }
        // 明细
        List<SalOrderItem> items = itemMapper.selectList(new LambdaQueryWrapper<SalOrderItem>().eq(SalOrderItem::getOrderId, order.getId()));
        List<SalOrderItemVO> itemVos = items.stream().map(i->{
            SalOrderItemVO iv = BeanCopyUtil.copy(i, SalOrderItemVO.class);
            BasMaterial mat = materialMapper.selectById(i.getMaterialId());
            iv.setMaterialCode(mat.getMaterialCode());
            iv.setMaterialName(mat.getMaterialName());
            iv.setUnit(mat.getUnit());
            return iv;
        }).collect(Collectors.toList());
        vo.setItemList(itemVos);
        return vo;
    }
    @Override
    public Page<SalOrderVO> listCreditWarning(SalesOrderPageDTO dto) {
        Page<SalOrder> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<SalOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SalOrder::getIsDeleted, 0);
        // 预警条件：已出库、未结清货款、当前日期超过客户账期
        wrapper.in(SalOrder::getOrderStatus,
                SalesOrderStatusEnum.PART_SHIP.getCode(),
                SalesOrderStatusEnum.ALL_SHIP.getCode()
        );
        wrapper.ne(SalOrder::getPaymentStatus, 3); // 未全额收款
        wrapper.orderByDesc(SalOrder::getCreateTime);
        Page<SalOrder> entityPage = baseMapper.selectPage(page, wrapper);
        List<SalOrderVO> voList = entityPage.getRecords().stream().map(this::convertVO).collect(Collectors.toList());
        Page<SalOrderVO> res = new Page<>();
        res.setTotal(entityPage.getTotal());
        res.setRecords(voList);
        return res;
    }
    @Override
    public BigDecimal calcSingleOrderGross(Long orderId) {
        SalOrder order = getById(orderId);
        List<SalOrderItem> itemList = itemMapper.selectList(
                new LambdaQueryWrapper<SalOrderItem>().eq(SalOrderItem::getOrderId, orderId)
        );
        // 总销售收入
        BigDecimal totalSale = itemList.stream()
                .map(SalOrderItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 总成本（出库加权成本）
        BigDecimal totalCost = itemList.stream()
                .map(item -> item.getUnitCost().multiply(item.getPlanQty()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 毛利 = 销售总额 - 总成本
        return totalSale.subtract(totalCost);
    }
}