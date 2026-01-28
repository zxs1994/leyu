package ${package.Service}.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import ${package.Entity}.${entity};
import ${package.Mapper}.${entity}Mapper;
import ${package.Service}.I${entity}Service;
import ${package.Query}.${entity}Query;
import ${package.Vo}.${entity}Vo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;

/**
 * <p>
 * ${table.comment!"${entity}"} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
public class ${entity}ServiceImpl extends ServiceImpl<${entity}Mapper, ${entity}> implements I${entity}Service {

    /**
     * 分页查询（返回 VO）
     */
    @Override
    public Page<${entity}Vo> page(${entity}Query query) {
        Page<${entity}> entityPage = new Page<>(query.getPage(), query.getSize());
        QueryWrapper<${entity}> qw = new QueryWrapper<>();

        <#-- 遍历表字段 -->
        <#list table.fields as field>
            <#-- 只生成在 queryConfig 中配置过的字段 -->
            <#if queryConfig[field.propertyName]?exists>
                <#assign cfg = queryConfig[field.propertyName]>
                <#assign operator = cfg.operator!"">
                <#assign column = cfg.column!field.name>
                <#assign ignoreEmpty = cfg.ignoreEmpty!false>
                <#assign likeMode = cfg.likeMode!"both">

                <#-- between 单独处理 -->
                <#if operator == "between">
        if (query.get${field.propertyName?cap_first}Start() != null
            && query.get${field.propertyName?cap_first}End() != null) {
            qw.between(
                "${column}",
                query.get${field.propertyName?cap_first}Start(),
                query.get${field.propertyName?cap_first}End()
            );
        }
                <#else>
        if (query.get${field.propertyName?cap_first}() != null
            <#if ignoreEmpty>
            && StringUtils.hasText(query.get${field.propertyName?cap_first}())
            </#if>
        ) {
            <#if operator == "eq">
            qw.eq("${column}", query.get${field.propertyName?cap_first}());

            <#elseif operator == "like">
                <#if likeMode == "left">
            qw.likeLeft("${column}", query.get${field.propertyName?cap_first}());
                <#elseif likeMode == "right">
            qw.likeRight("${column}", query.get${field.propertyName?cap_first}());
                <#else>
            qw.like("${column}", query.get${field.propertyName?cap_first}());
                </#if>

            <#elseif operator == "in">
            qw.in("${column}", query.get${field.propertyName?cap_first}());

            <#elseif operator == "gt">
            qw.gt("${column}", query.get${field.propertyName?cap_first}());

            <#elseif operator == "ge">
            qw.ge("${column}", query.get${field.propertyName?cap_first}());

            <#elseif operator == "lt">
            qw.lt("${column}", query.get${field.propertyName?cap_first}());

            <#elseif operator == "le">
            qw.le("${column}", query.get${field.propertyName?cap_first}());

            <#elseif operator == "ne">
            qw.ne("${column}", query.get${field.propertyName?cap_first}());
            </#if>
        }
                </#if>
            </#if>
        </#list>

        entityPage = super.page(entityPage, qw);
        return entityPageToVoPage(entityPage);
    }

    /**
     * 根据 ID 获取 VO
     */
    @Override
    public ${entity}Vo getVoById(Long id) {
        ${entity} entity = this.getById(id);
        if (entity == null) return null;
        return convertToVo(entity);
    }

    // ===================== 私有 VO 转换方法 =====================

    /**
     * 单个实体转 VO
     */
    private ${entity}Vo convertToVo(${entity} entity) {
        ${entity}Vo vo = new ${entity}Vo();
        BeanUtils.copyProperties(entity, vo);
        // TODO 组装额外数据
        return vo;
    }

    /**
     * 实体分页转 VO 分页
     */
    private Page<${entity}Vo> entityPageToVoPage(Page<${entity}> entityPage) {
        Page<${entity}Vo> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream()
                .map(this::convertToVo)
                .toList());
        return voPage;
    }
}