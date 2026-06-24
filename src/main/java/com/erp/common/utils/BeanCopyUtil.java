package com.erp.common.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import java.util.ArrayList;
import java.util.List;

public class BeanCopyUtil {

    /**
     * 源对象 → 新建目标对象（兼容hutool 5.8.18，无newInstance方法）
     */
    public static <S, T> T copy(S source, Class<T> targetCls) {
        if (source == null) {
            return null;
        }
        try {
            // 原生反射创建实例，兼容低版本hutool
            T target = targetCls.getDeclaredConstructor().newInstance();
            BeanUtil.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("目标类必须提供公共无参构造函数", e);
        }
    }

    /**
     * 源对象 → 已有目标对象
     */
    public static <S, T> void copy(S source, T target) {
        if (source == null || target == null) {
            return;
        }
        BeanUtil.copyProperties(source, target);
    }

    /**
     * List批量转换
     */
    public static <S, T> List<T> copyList(List<S> sourceList, Class<T> targetCls) {
        List<T> targetList = new ArrayList<>();
        if (CollUtil.isEmpty(sourceList)) {
            return targetList;
        }
        for (S source : sourceList) {
            T target = copy(source, targetCls);
            targetList.add(target);
        }
        return targetList;
    }
}