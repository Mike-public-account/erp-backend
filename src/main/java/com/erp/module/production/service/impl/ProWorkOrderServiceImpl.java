package com.erp.module.production.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.constant.GlobalConstant;
import com.erp.common.exception.BusinessException;
import com.erp.common.utils.OrderNoUtil;
import com.erp.module.base.entity.BasMaterial;
import com.erp.module.base.entity.BasWarehouse;
import com.erp.module.base.mapper.BasMaterialMapper;
import com.erp.module.base.mapper.BasWarehouseMapper;
import com.erp.module.inventory.dto.StockOccupyDTO;
import com.erp.module.inventory.entity.InvPreoccupy;
import com.erp.module.inventory.mapper.InvPreoccupyMapper;
import com.erp.module.inventory.service.StockService;
import com.erp.module.production.constant.ProductionOrderStatusEnum;
import com.erp.module.production.dto.WorkOrderFinishDTO;
import com.erp.module.production.dto.WorkOrderPageDTO;
import com.erp.module.production.dto.WorkOrderPickDTO;
import com.erp.module.production.dto.WorkOrderSaveDTO;
import com.erp.module.production.entity.ProWorkOrder;
import com.erp.module.production.entity.ProWorkOrderMaterial;
import com.erp.module.production.mapper.ProWorkOrderMapper;
import com.erp.module.production.mapper.ProWorkOrderMaterialMapper;
import com.erp.module.production.service.ProBomService;
import com.erp.module.production.service.ProWorkOrderService;
import com.erp.module.production.vo.BomVO;
import com.erp.module.production.vo.WorkOrderExportVO;
import com.erp.module.production.vo.WorkOrderMaterialVO;
import com.erp.module.production.vo.WorkOrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// 增加 implements ProWorkOrderService
public class ProWorkOrderServiceImpl extends ServiceImpl<ProWorkOrderMapper, ProWorkOrder> implements ProWorkOrderService {
    // 区分两类Mapper，彻底解决变量重名冲突
    private final ProWorkOrderMapper workOrderMapper;
    private final ProWorkOrderMaterialMapper orderMaterialMapper;
    private final ProBomService bomService;
    private final BasMaterialMapper basMaterialMapper;
    private final BasWarehouseMapper warehouseMapper;
    private final StockService stockService;
    private final InvPreoccupyMapper preoccupyMapper;

    @Override
    public Page<WorkOrderVO> workOrderPage(WorkOrderPageDTO dto) {
        Page<ProWorkOrder> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        LambdaQueryWrapper<ProWorkOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProWorkOrder::getIsDeleted, GlobalConstant.NOT_DELETE);
        if (StringUtils.hasText(dto.getWorkOrderNo())) {
            wrapper.like(ProWorkOrder::getWorkOrderNo, dto.getWorkOrderNo());
        }

        if (dto.getProductId() != null) {
            wrapper.eq(ProWorkOrder::getProductId, dto.getProductId());
        }
        if (dto.getOrderStatus() != null) {
            wrapper.eq(ProWorkOrder::getOrderStatus, dto.getOrderStatus());
        }
        wrapper.orderByDesc(ProWorkOrder::getCreateTime);
        Page<ProWorkOrder> entityPage = workOrderMapper.selectPage(page, wrapper);
        List<WorkOrderVO> voList = entityPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        Page<WorkOrderVO> result = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createWorkOrder(WorkOrderSaveDTO dto) {
        // 校验成品、仓库
        BasMaterial product = basMaterialMapper.selectById(dto.getProductId());
        if (product == null || product.getIsDeleted().equals(GlobalConstant.DELETED)) {
            throw new BusinessException("成品物料不存在");
        }
        BasWarehouse warehouse = warehouseMapper.selectById(dto.getWarehouseId());
        if (warehouse == null || warehouse.getIsDeleted().equals(GlobalConstant.DELETED)) {
            throw new BusinessException("仓库不存在");
        }
        // 查询BOM配方（参数修正为productId）
        List<BomVO> bomList = bomService.getBomByProductId(dto.getProductId());
        if (bomList.isEmpty()) {
            throw new BusinessException("该成品未配置BOM配方，无法创建工单");
        }
        // 生成工单号
        String orderNo = OrderNoUtil.generate("WO");
        ProWorkOrder order = BeanUtil.copyProperties(dto, ProWorkOrder.class);
        order.setWorkOrderNo(orderNo);
        order.setProductId(dto.getProductId());
        order.setOrderStatus(ProductionOrderStatusEnum.PENDING_PRODUCE.getCode());
        save(order);
        Long orderId = order.getId();
        BigDecimal totalCost = BigDecimal.ZERO;
        // 循环生成原料明细、预锁定库存
        for (BomVO bom : bomList) {
            // 应领数量 = 计划产量 * 单件用料 * (1+损耗率)
            BigDecimal baseQty = dto.getPlanQty().multiply(bom.getPerUnitQty());
            BigDecimal lossScale = BigDecimal.ONE.add(bom.getLossRate().divide(new BigDecimal(100), 4, RoundingMode.HALF_UP));
            BigDecimal requireQty = baseQty.multiply(lossScale).setScale(4, RoundingMode.HALF_UP);
            BasMaterial rawMaterial = basMaterialMapper.selectById(bom.getRawMaterialId());
            BigDecimal unitCost = rawMaterial.getAvgCost();
            BigDecimal itemTotal = requireQty.multiply(unitCost);

            // 插入工单原料明细
            ProWorkOrderMaterial item = new ProWorkOrderMaterial();
            item.setWorkOrderId(orderId);
            item.setMaterialId(bom.getRawMaterialId());
            item.setPlanQty(requireQty);
            item.setActualQty(BigDecimal.ZERO);
            item.setUnitCost(unitCost);
            item.setTotalCost(itemTotal);
            item.setStatus(0);
            orderMaterialMapper.insert(item);

            // 预锁定原料库存：构造DTO调用标准preOccupy方法
            StockOccupyDTO occupyDTO = new StockOccupyDTO();
            occupyDTO.setMaterialId(bom.getRawMaterialId());
            occupyDTO.setWarehouseId(dto.getWarehouseId());
            occupyDTO.setQty(requireQty);
            occupyDTO.setRefId(orderId);
            occupyDTO.setRefType("PRO_WORK_ORDER"); // 单据类型标记生产工单
            stockService.preOccupy(occupyDTO);

            totalCost = totalCost.add(itemTotal);
        }
        // 更新工单总原料成本
        order.setTotalMaterialCost(totalCost);
        updateById(order);
        return orderId;
    }

    @Override
    public WorkOrderVO getOrderDetail(Long id) {
        ProWorkOrder order = getById(id);
        if (order == null || order.getIsDeleted().equals(GlobalConstant.DELETED)) {
            throw new BusinessException("工单不存在");
        }
        return convertToVO(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void pickMaterial(WorkOrderPickDTO dto) {
        ProWorkOrder order = getById(dto.getWorkOrderId());
        if (!order.getOrderStatus().equals(ProductionOrderStatusEnum.PENDING_PRODUCE.getCode())) {
            throw new BusinessException("仅待生产工单可领料");
        }

        // 修复：替换不存在的 selectByOrderId，使用MP条件查询
        LambdaQueryWrapper<ProWorkOrderMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProWorkOrderMaterial::getWorkOrderId, dto.getWorkOrderId());
        List<ProWorkOrderMaterial> materialList = orderMaterialMapper.selectList(wrapper);

        // 循环更新实际领料
        for (WorkOrderPickDTO.PickItem pickItem : dto.getItemList()) {
            ProWorkOrderMaterial item = materialList.stream()
                    .filter(m -> m.getMaterialId().equals(pickItem.getRawMaterialId()))
                    .findFirst().orElseThrow(() -> new BusinessException("工单无此原料BOM"));

            if (pickItem.getPickQty().compareTo(item.getPlanQty()) > 0) {
                throw new BusinessException("领料数量不可超过应领数量");
            }
            item.setActualQty(pickItem.getPickQty());
            item.setStatus(1);
            // 替换为Mapper更新明细记录
            orderMaterialMapper.updateById(item);
        }

        // 工单状态改为生产中
        order.setOrderStatus(ProductionOrderStatusEnum.PRODUCING.getCode());
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishWorkOrder(WorkOrderFinishDTO dto) {
        ProWorkOrder order = getById(dto.getWorkOrderId());
        if (!order.getOrderStatus().equals(ProductionOrderStatusEnum.PRODUCING.getCode())) {
            throw new BusinessException("仅生产中工单可完工");
        }
        // 修复除零异常，增加判断
        if (dto.getActualQty().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("完工入库数量必须大于0");
        }

        // 修复1：替换不存在的 selectByOrderId
        LambdaQueryWrapper<ProWorkOrderMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProWorkOrderMaterial::getWorkOrderId, dto.getWorkOrderId());

        // 加权计算单件原料成本
        BigDecimal unitCost = order.getTotalMaterialCost()
                .divide(dto.getActualQty(), 6, RoundingMode.HALF_UP);

        // 修复3：方法名purchaseIn，补齐全部参数 refType、refId
        stockService.purchaseIn(
                order.getProductId(),
                order.getWarehouseId(),
                dto.getActualQty(),
                unitCost,
                "PRO_WORK_ORDER",
                order.getId(),
                null,
                order.getCreatorId()
        );

        // 更新工单
        order.setActualQty(dto.getActualQty());
        order.setOrderStatus(ProductionOrderStatusEnum.ALL_FINISH.getCode());
        order.setActualEndTime(LocalDateTime.now());
        updateById(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelWorkOrder(Long orderId) {
        ProWorkOrder order = getById(orderId);
        if (!order.getOrderStatus().equals(ProductionOrderStatusEnum.PENDING_PRODUCE.getCode())) {
            throw new BusinessException("仅待生产工单可取消");
        }
        // 批量释放工单预占库存（二选一，推荐方案A）
        // 方案A：StockService 新增 default void releasePreoccupyByRef(Long refId)
        // stockService.releasePreoccupyByRef(orderId);

        // 方案B：不改动库存接口，本地查询释放
    /*
    List<InvPreoccupy> preoccupyList = invPreoccupyMapper.selectByRefId(orderId);
    for (InvPreoccupy preoccupy : preoccupyList) {
        stockService.releaseOccupy(preoccupy);
    }
    */

        order.setOrderStatus(ProductionOrderStatusEnum.CANCEL.getCode());
        updateById(order);
    }

    /** 实体转VO，全部字段与数据库实体对齐 */
    private WorkOrderVO convertToVO(ProWorkOrder order) {
        WorkOrderVO vo = BeanUtil.copyProperties(order, WorkOrderVO.class);
        // 成品物料信息
        BasMaterial product = basMaterialMapper.selectById(order.getProductId());
        if (product != null) {
            vo.setProductMaterialName(product.getMaterialName());
            vo.setProductMaterialCode(product.getMaterialCode());
        }
        // 仓库名称
        BasWarehouse warehouse = warehouseMapper.selectById(order.getWarehouseId());
        if (warehouse != null) vo.setWarehouseName(warehouse.getWarehouseName());
        // 状态文本匹配枚举
        for (ProductionOrderStatusEnum status : ProductionOrderStatusEnum.values()) {
            if (status.getCode().equals(order.getOrderStatus())) {
                vo.setStatusText(status.getText());
                break;
            }
        }

        // 修复：替换不存在 selectByOrderId，使用MP条件查询
        LambdaQueryWrapper<ProWorkOrderMaterial> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProWorkOrderMaterial::getWorkOrderId, order.getId());
        List<ProWorkOrderMaterial> rawList = orderMaterialMapper.selectList(wrapper);

        List<WorkOrderMaterialVO> materialVOList = rawList.stream().map(raw -> {
            WorkOrderMaterialVO mVo = BeanUtil.copyProperties(raw, WorkOrderMaterialVO.class);
            // 修复：实体字段 materialId，不是 rawMaterialId
            BasMaterial rawMat = basMaterialMapper.selectById(raw.getMaterialId());
            if (rawMat != null) {
                mVo.setRawMaterialName(rawMat.getMaterialName());
                mVo.setRawMaterialCode(rawMat.getMaterialCode());
            }
            // 修复：实体字段 status，无 pickStatus
            mVo.setPickStatusText(raw.getStatus() == 0 ? "未领料" : "已领料");
            return mVo;
        }).collect(Collectors.toList());
        vo.setMaterialList(materialVOList);
        return vo;
    }
    @Override
    public List<WorkOrderExportVO> exportWorkOrder(WorkOrderPageDTO dto) {
        LambdaQueryWrapper<ProWorkOrder> wrapper = new LambdaQueryWrapper<>();

        // 工单编号模糊
        if (StringUtils.hasText(dto.getWorkOrderNo())) {
            wrapper.like(ProWorkOrder::getWorkOrderNo, dto.getWorkOrderNo());
        }
        // 产品ID
        if (dto.getProductId() != null) {
            wrapper.eq(ProWorkOrder::getProductId, dto.getProductId());
        }
        // 工单状态
        if (dto.getOrderStatus() != null) {
            wrapper.eq(ProWorkOrder::getOrderStatus, dto.getOrderStatus());
        }
        // 创建时间区间
        if (dto.getStartTime() != null && dto.getEndTime() != null) {
            wrapper.between(ProWorkOrder::getCreateTime, dto.getStartTime(), dto.getEndTime());
        } else if (dto.getStartTime() != null) {
            wrapper.ge(ProWorkOrder::getCreateTime, dto.getStartTime());
        } else if (dto.getEndTime() != null) {
            wrapper.le(ProWorkOrder::getCreateTime, dto.getEndTime());
        }

        wrapper.orderByDesc(ProWorkOrder::getCreateTime);
        List<ProWorkOrder> list = baseMapper.selectList(wrapper);
        return BeanUtil.copyToList(list, WorkOrderExportVO.class);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchCancelWorkOrder(List<Long> orderIdList) {
        // 1. 批量更新工单状态为4-已取消
        LambdaUpdateWrapper<ProWorkOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(ProWorkOrder::getId, orderIdList)
                .set(ProWorkOrder::getOrderStatus, 4);
        baseMapper.update(null, updateWrapper);

        // 2. 批量查询工单对应的预出库记录，批量释放库存
        List<InvPreoccupy> preList = preoccupyMapper.selectByRefIds("PROD_ORDER", orderIdList);
        for (InvPreoccupy pre : preList) {
            stockService.releaseOccupy(pre);
        }

        // 3. 批量同步物料Redis缓存
        List<Long> materialIds = preList.stream()
                .map(InvPreoccupy::getMaterialId)
                .distinct()
                .toList();
        for (Long mid : materialIds) {
            stockService.syncMaterialStockCache(basMaterialMapper.selectById(mid));
        }
    }
}