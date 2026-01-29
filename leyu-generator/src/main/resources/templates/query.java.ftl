package ${package.Query};

import ${basePackage}.common.BaseQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.time.OffsetDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
public class ${entity}Query extends BaseQuery {

<#-- 循环表字段 -->
<#list table.fields as field>
<#if queryConfig[field.propertyName]?exists>
<#assign cfg = queryConfig[field.propertyName]>

<#-- between：生成 start / end -->
<#if cfg.operator == "between">
    @Schema(description="${field.comment!field.name} - 开始")
    private ${cfg.fieldType!field.propertyType} ${field.propertyName}Start;

    @Schema(description="${field.comment!field.name} - 结束")
    private ${cfg.fieldType!field.propertyType} ${field.propertyName}End;
<#else>
    @Schema(description="${field.comment!field.name}")
    private ${cfg.fieldType!field.propertyType} ${field.propertyName};
</#if>

</#if>
</#list>
}