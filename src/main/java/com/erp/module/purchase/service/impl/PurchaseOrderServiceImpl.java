package com.erp.module.purchase.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.OrderConstant;
import com.erp.common.constant.OrderStatusEnum;
import com.erp.common.exception.BusinessException;
import com.erp.common.utils.ExcelUtil;
import com.erp.common.utils.HaversineUtil;
import com.erp.common.utils.OrderNoUtil;
import com.erp.common.utils.SecurityUtil;
import com.erp.module.base.entity.BasMaterial;
import com.erp.module.base.entity.BasSupplier;
import com.erp.module.base.entity.BasWarehouse;
import com.erp.module.base.mapper.BasWarehouseMapper;
import com.erp.module.base.service.BasMaterialService;
import com.erp.module.base.service.BasSupplierService;
import com.erp.module.base.service.BasWarehouseService;
import com.erp.module.inventory.dto.StockOccupyDTO;
import com.erp.module.inventory.service.StockService;
import com.erp.module.purchase.dto.PurOrderImportDTO;
import com.erp.module.purchase.dto.PurchaseOrderDTO;
import com.erp.module.purchase.dto.PurchasePageDTO;
import com.erp.module.purchase.entity.PurOrder;
import com.erp.module.purchase.entity.PurOrderItem;
import com.erp.module.purchase.mapper.PurOrderMapper;
import com.erp.module.purchase.mapper.PurOrderItemMapper;
import com.erp.module.purchase.service.PurchaseOrderService;
import com.erp.module.purchase.vo.PurchaseOrderVO;
import com.erp.module.purchase.vo.PurOrderItemVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.erp.module.system.util.LoginUserUtil;

@Service
public class PurchaseOrderServiceImpl extends ServiceImpl<PurOrderMapper, PurOrder> implements PurchaseOrderService {

    @Resource
    private PurOrderItemMapper orderItemMapper;
    @Resource
    private BasSupplierService supplierService;
    @Resource
    private BasWarehouseService warehouseService;
    @Resource
    private BasMaterialService materialService;
    @Resource
    private StockService stockService;
    @Resource
    private BasWarehouseMapper basWarehouseMapper;

    // 审批金额阈值，超过需财务审批
    @Value("${erp.purchase.approve-amount:10000}")
    private BigDecimal approveAmountThreshold;

    @Override
    public Page<PurchaseOrderVO> pageList(PurchasePageDTO dto) {
        LambdaQueryWrapper<PurOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurOrder::getIsDeleted, 0);
        if (dto.getSupplierId() != null) {
            wrapper.eq(PurOrder::getSupplierId, dto.getSupplierId());
        }
        if (dto.getOrderStatus() != null) {
            wrapper.eq(PurOrder::getOrderStatus, dto.getOrderStatus());
        }
        if (StringUtils.hasText(dto.getOrderNo())) {
            wrapper.like(PurOrder::getOrderNo, dto.getOrderNo());
        }
        if (dto.getStartTime() != null) {
            wrapper.ge(PurOrder::getCreateTime, dto.getStartTime());
        }
        if (dto.getEndTime() != null) {
            wrapper.le(PurOrder::getCreateTime, dto.getEndTime());
        }
        wrapper.orderByDesc(PurOrder::getCreateTime);
        Page<PurOrder> entityPage = page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);

        // 实体转VO分页
        Page<PurchaseOrderVO> voPage = new Page<>();
        voPage.setTotal(entityPage.getTotal());
        voPage.setCurrent(entityPage.getCurrent());
        voPage.setSize(entityPage.getSize());
        List<PurchaseOrderVO> voList = entityPage.getRecords().stream().map(order -> {
            PurchaseOrderVO vo = new PurchaseOrderVO();
            BeanUtils.copyProperties(order, vo);
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public PurchaseOrderVO getDetail(Long id) {
        PurOrder order = getById(id);
        if (order == null) {
            throw new BusinessException("采购单不存在");
        }

        List<PurOrderItem> itemList = orderItemMapper.selectList(
                new LambdaQueryWrapper<PurOrderItem>().eq(PurOrderItem::getOrderId, id)
        );

        PurchaseOrderVO vo = new PurchaseOrderVO();
        BeanUtils.copyProperties(order, vo);
        List<PurOrderItemVO> itemVOList = itemList.stream().map(item -> {
            PurOrderItemVO itemVO = new PurOrderItemVO();
            BeanUtils.copyProperties(item, itemVO);
            BasMaterial material = materialService.getById(item.getMaterialId());
            if (material != null) {
                itemVO.setMaterialName(material.getMaterialName());
                itemVO.setMaterialCode(material.getMaterialCode());
                itemVO.setUnit(material.getUnit());
            }
            return itemVO;
        }).collect(Collectors.toList());
        vo.setItemList(itemVOList);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createOrder(PurchaseOrderDTO dto) {
        Long purchaserId = SecurityUtil.getUserId();
        // 校验供应商、仓库
        BasSupplier supplier = supplierService.getById(dto.getSupplierId());
        if (supplier == null) {
            throw new BusinessException("供应商不存在");
        }
        BasWarehouse warehouse = warehouseService.getById(dto.getWarehouseId());
        if (warehouse == null) {
            throw new BusinessException("仓库不存在");
        }

        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (PurchaseOrderDTO.Item item : dto.getItemList()) {
            BasMaterial material = materialService.getById(item.getMaterialId());
            if (material == null) {
                throw new BusinessException("物料ID:" + item.getMaterialId() + " 不存在");
            }
            BigDecimal amount = item.getPlanQty().multiply(item.getUnitPrice());
            totalAmount = totalAmount.add(amount);
        }

        // 计算到货时间、超时时间
        double distance = HaversineUtil.calcDistanceKm(
                supplier.getLatitude().doubleValue(), supplier.getLongitude().doubleValue(),
                warehouse.getLatitude().doubleValue(), warehouse.getLongitude().doubleValue()
        );
        // 陆运平均速度60km/h + 装卸2小时
        double hours = distance / 60.0 + 2.0;
        LocalDateTime estimatedArrival = LocalDateTime.now().plusMinutes((long) (hours * 60));
        long estimatedMinutes = ChronoUnit.MINUTES.between(LocalDateTime.now(), estimatedArrival);
        LocalDateTime timeoutTime = estimatedArrival.plusMinutes(estimatedMinutes);

        // 保存采购主单
        PurOrder order = new PurOrder();
        BeanUtils.copyProperties(dto, order);
        order.setOrderNo(OrderNoUtil.generatePurchaseNo());
        order.setTotalAmount(totalAmount);
        order.setOrderStatus(OrderStatusEnum.DRAFT.getCode());
        order.setPurchaserId(purchaserId);
        order.setEstimatedArrivalTime(estimatedArrival);
        order.setTimeoutTime(timeoutTime);
        save(order);

        // 保存明细
        List<PurOrderItem> itemList = new ArrayList<>();
        for (PurchaseOrderDTO.Item itemDTO : dto.getItemList()) {
            PurOrderItem item = new PurOrderItem();
            BeanUtils.copyProperties(itemDTO, item);
            item.setOrderId(order.getId());
            item.setAmount(itemDTO.getPlanQty().multiply(itemDTO.getUnitPrice()));
            item.setArrivedQty(BigDecimal.ZERO);
            itemList.add(item);
        }
        itemList.forEach(orderItemMapper::insert);

        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitAudit(Long id) {
        // 原有逻辑完全不动，只改方法名、删掉purchaserId相关
        PurOrder order = getById(id);
        if (order == null) {
            throw new BusinessException("采购单不存在");
        }
        if (!OrderStatusEnum.DRAFT.getCode().equals(order.getOrderStatus())) {
            throw new BusinessException("仅草稿状态可提交审批");
        }

        // 金额低于阈值自动审批通过
        if (order.getTotalAmount().compareTo(approveAmountThreshold) <= 0) {
            order.setOrderStatus(OrderStatusEnum.AUDIT_PASS.getCode());
            order.setApproveTime(LocalDateTime.now());

            List<PurOrderItem> itemList = orderItemMapper.selectList(
                    new LambdaQueryWrapper<PurOrderItem>().eq(PurOrderItem::getOrderId, order.getId())
            );
            for (PurOrderItem item : itemList) {
                StockOccupyDTO occupyDTO = new StockOccupyDTO();
                occupyDTO.setMaterialId(item.getMaterialId());
                occupyDTO.setWarehouseId(order.getWarehouseId());
                BigDecimal remainQty = item.getPlanQty().subtract(item.getArrivedQty());
                occupyDTO.setQty(remainQty);
                occupyDTO.setRefType("PUR_ORDER");
                occupyDTO.setRefId(order.getId());
                stockService.preOccupy(occupyDTO);
            }
        } else {
            order.setOrderStatus(OrderStatusEnum.AUDIT_PENDING.getCode());
        }
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditOrder(Long id, Integer pass, String remark) {
        PurOrder order = getById(id);
        if (order == null) {
            throw new BusinessException("采购单不存在");
        }
        if (!OrderStatusEnum.AUDIT_PENDING.getCode().equals(order.getOrderStatus())) {
            throw new BusinessException("仅待审批状态可审批");
        }

        order.setApproveTime(LocalDateTime.now());
        order.setApproveRemark(remark);
        // approverId 从当前登录用户上下文获取，不要放方法入参
        // order.setApproverId(SecurityUtil.getUserId());

        if (pass == 1) {
            order.setOrderStatus(OrderStatusEnum.AUDIT_PASS.getCode());
            List<PurOrderItem> itemList = orderItemMapper.selectList(
                    new LambdaQueryWrapper<PurOrderItem>().eq(PurOrderItem::getOrderId, order.getId())
            );
            for (PurOrderItem item : itemList) {
                StockOccupyDTO occupyDTO = new StockOccupyDTO();
                occupyDTO.setMaterialId(item.getMaterialId());
                occupyDTO.setWarehouseId(order.getWarehouseId());
                BigDecimal remainQty = item.getPlanQty().subtract(item.getArrivedQty());
                occupyDTO.setQty(remainQty);
                occupyDTO.setRefType("PUR_ORDER");
                occupyDTO.setRefId(order.getId());
                stockService.preOccupy(occupyDTO);
            }
        } else {
            order.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        }
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(Long id) {
        PurOrder order = getById(id);
        if (order == null) {
            throw new BusinessException("采购单不存在");
        }
        if (OrderStatusEnum.ALL_COMPLETE.getCode().equals(order.getOrderStatus())) {
            throw new BusinessException("已全部入库的采购单不可取消");
        }

        // 释放库存预占
        stockService.releasePreoccupyByRef(id, "PUR_ORDER");

        order.setOrderStatus(OrderStatusEnum.CANCEL.getCode());
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiptStock(Long id) {
        Long operatorId = SecurityUtil.getUserId(); // 示例，从登录上下文拿操作人
        PurOrder order = getById(id);
        if (order == null) {
            throw new BusinessException("采购单不存在");
        }
        if (!OrderStatusEnum.AUDIT_PASS.getCode().equals(order.getOrderStatus())
                && !OrderStatusEnum.PART_COMPLETE.getCode().equals(order.getOrderStatus())) {
            throw new BusinessException("仅审批通过/部分入库状态可入库");
        }

        List<PurOrderItem> itemList = orderItemMapper.selectList(
                new LambdaQueryWrapper<PurOrderItem>().eq(PurOrderItem::getOrderId, id)
        );

        boolean allFull = true;
        for (PurOrderItem item : itemList) {
            BigDecimal remainQty = item.getPlanQty().subtract(item.getArrivedQty());
            if (remainQty.compareTo(BigDecimal.ZERO) <= 0) {
                continue;
            }

            stockService.purchaseIn(
                    item.getMaterialId(),
                    order.getWarehouseId(),
                    remainQty,
                    item.getUnitPrice(),
                    "PUR_ORDER",
                    order.getId(),
                    item.getId(),
                    operatorId
            );

            item.setArrivedQty(item.getPlanQty());
            orderItemMapper.updateById(item);

            if (item.getArrivedQty().compareTo(item.getPlanQty()) < 0) {
                allFull = false;
            }
        }

        order.setOrderStatus(allFull ? OrderStatusEnum.ALL_COMPLETE.getCode() : OrderStatusEnum.PART_COMPLETE.getCode());
        order.setActualArrivalTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importExcel(MultipartFile file) throws Exception {
        List<PurOrder> list = ExcelUtil.importExcel(file, PurOrder.class);
        saveBatch(list);
    }

    @Override
    public void exportExcel(HttpServletResponse response) throws IOException {
        List<PurOrder> list = list(new LambdaQueryWrapper<PurOrder>().eq(PurOrder::getIsDeleted, 0));
        ExcelUtil.export(list, PurOrder.class, "采购单列表.xlsx", response);
    }
    @Override
    public Page<PurchaseOrderVO> getTimeoutOrder(PurchasePageDTO dto) {
        LambdaQueryWrapper<PurOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PurOrder::getIsDeleted, 0);
        wrapper.lt(PurOrder::getTimeoutTime, LocalDateTime.now());
        if (dto.getSupplierId() != null) {
            wrapper.eq(PurOrder::getSupplierId, dto.getSupplierId());
        }
        wrapper.orderByDesc(PurOrder::getCreateTime);
        Page<PurOrder> entityPage = page(new Page<>(dto.getPageNum(), dto.getPageSize()), wrapper);

        Page<PurchaseOrderVO> voPage = new Page<>();
        voPage.setTotal(entityPage.getTotal());
        voPage.setCurrent(entityPage.getCurrent());
        voPage.setSize(entityPage.getSize());
        List<PurchaseOrderVO> voList = entityPage.getRecords().stream().map(order -> {
            PurchaseOrderVO vo = new PurchaseOrderVO();
            BeanUtils.copyProperties(order, vo);
            return vo;
        }).collect(Collectors.toList());
        voPage.setRecords(voList);
        return voPage;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<PurOrder> importOrder(List<PurOrderImportDTO> importList, Long loginUserId) {
        List<PurOrder> orderList = importList.stream().map(dto -> {
            PurOrder order = new PurOrder();
            BeanUtils.copyProperties(dto, order);

            // 替换数字1为订单草稿状态常量
            order.setOrderStatus(OrderConstant.ORDER_STATUS_DRAFT);
            order.setPurchaserId(loginUserId);

            // 1. 获取当前单据选择的仓库ID
            Long warehouseId = dto.getWarehouseId();
            // 2. 从数据库查询该仓库完整信息（含经纬度）
            BasWarehouse warehouse = basWarehouseMapper.selectById(warehouseId);
            if (warehouse == null) {
                throw new BusinessException("仓库ID【" + warehouseId + "】不存在");
            }

            // 3. 供应商经纬度来自导入DTO
            Double supplierLat = dto.getSupplierLat();
            Double supplierLng = dto.getSupplierLng();
            if (supplierLat == null || supplierLng == null) {
                throw new BusinessException("导入行供应商经纬度不能为空");
            }

            // 4. Haversine计算距离：供应商坐标 + 当前单据对应仓库坐标
            double distanceKm = HaversineUtil.calcDistanceKm(
                    supplierLat,
                    supplierLng,
                    warehouse.getLatitude().doubleValue(),
                    warehouse.getLongitude().doubleValue()
            );

            // 5. 换算运输时长，替换硬编码60为车速常量
            double transportHour = distanceKm / OrderConstant.TRANSPORT_SPEED_KM_PER_HOUR;
            long transportMinute = Math.round(transportHour * 60);

            // 6. 计算预计到货时间、超时时间
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime arriveTime = now.plusMinutes(transportMinute);
            order.setEstimatedArrivalTime(arriveTime);
            // 超时延迟分钟使用常量
            order.setTimeoutTime(arriveTime.plusMinutes(OrderConstant.ORDER_TIMEOUT_MIN));

            return order;
        }).collect(Collectors.toList());

        saveBatch(orderList);
        return orderList;
    }
}