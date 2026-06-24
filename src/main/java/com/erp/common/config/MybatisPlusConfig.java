package com.erp.common.config;

import com.erp.common.utils.DataScopeContext;
import com.erp.common.annotation.DataScope;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DataPermissionInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.erp.module.**.mapper")
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 数据权限插件
        DataPermissionInterceptor dataPermissionInterceptor = new DataPermissionInterceptor();
        dataPermissionInterceptor.setDataPermissionHandler((sqlSegment, mappedStatementId) -> {
            // 无注解 / 超级管理员直接返回原条件，不拼接过滤
            if (DataScopeContext.isAdmin()) {
                return sqlSegment;
            }
            try {
                String mapperClass = mappedStatementId.substring(0, mappedStatementId.lastIndexOf("."));
                String methodName = mappedStatementId.substring(mappedStatementId.lastIndexOf(".") + 1);
                Class<?> mapperCls = Class.forName(mapperClass);
                DataScope anno = null;
                // 匹配Mapper方法上的@DataScope注解
                for (java.lang.reflect.Method m : mapperCls.getDeclaredMethods()) {
                    if (m.getName().equals(methodName) && m.isAnnotationPresent(DataScope.class)) {
                        anno = m.getAnnotation(DataScope.class);
                        break;
                    }
                }
                if (anno == null) {
                    return sqlSegment;
                }
                // 拼接数据权限条件：creator_id = 当前登录用户ID
                String filterSql = String.format(" %s.%s = %d ",
                        anno.alias(), anno.userIdColumn(), DataScopeContext.getUserId());
                if (sqlSegment == null) {
                    return new net.sf.jsqlparser.expression.StringValue(filterSql);
                } else {
                    // 原有where条件 AND 当前用户过滤
                    return net.sf.jsqlparser.parser.CCJSqlParserUtil.parseCondExpression(
                            sqlSegment + " AND " + filterSql
                    );
                }
            } catch (Exception e) {
                return sqlSegment;
            }
        });
        interceptor.addInnerInterceptor(dataPermissionInterceptor);
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}