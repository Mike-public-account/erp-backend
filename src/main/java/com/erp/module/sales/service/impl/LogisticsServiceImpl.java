package com.erp.module.sales.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.erp.common.utils.BeanCopyUtil;
import com.erp.module.sales.dto.LogisticsReconcileDTO;
import com.erp.module.sales.entity.SalLogistics;
import com.erp.module.sales.mapper.SalLogisticsMapper;
import com.erp.module.sales.service.LogisticsService;
import com.erp.module.sales.vo.LogisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LogisticsServiceImpl extends ServiceImpl<SalLogisticsMapper, SalLogistics> implements LogisticsService {
    @Override
    public List<LogisticsVO> getByOrderId(Long orderId) {
        List<SalLogistics> list = baseMapper.selectList(new LambdaQueryWrapper<SalLogistics>().eq(SalLogistics::getOrderId, orderId));
        return list.stream().map(l->{
            LogisticsVO vo = BeanCopyUtil.copy(l, LogisticsVO.class);
            if(l.getFreightStatus() == 1) vo.setFreightText("未对账");
            else if(l.getFreightStatus() == 2) vo.setFreightText("已对账");
            else vo.setFreightText("有差异");
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reconcile(LogisticsReconcileDTO dto) {
        SalLogistics log = getById(dto.getLogisticsId());
        log.setFreightStatus(2);
        log.setReconcileRemark(dto.getReconcileRemark());
        updateById(log);
    }
}