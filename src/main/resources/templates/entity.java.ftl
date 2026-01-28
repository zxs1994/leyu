package ${package.Entity};

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import ${basePackage}.common.BaseEntity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * <p>
 * ${table.comment} 实体
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("${table.name}")
@Schema(description = "${table.comment}")
public class ${entity} extends BaseEntity {

<#list table.fields as field>
<#if !ignoreFields?seq_contains(field.name)>
    <#-- 主键 -->
    <#if field.keyFlag>
    @TableId(type = IdType.<#if autoIdTables?seq_contains(table.name)>AUTO<#else>ASSIGN_ID</#if>)
    </#if>
    <#-- 自动忽略敏感字段 -->
    <#if field.name == "tenant_id">
    @JsonIgnore
    </#if>
    <#-- 密码类字段：只写 -->
    <#if field.name == "password">
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    </#if>
    <#-- source字段：只读 -->
    <#if readOnlyFields?seq_contains(field.name)>
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    </#if>
    <#-- 逻辑删除 -->
    <#if field.logicDeleteField>
    @TableLogic
    @JsonIgnore
    </#if>
    <#if field.keyFlag || field.name?ends_with("_id")>
    @JsonSerialize(using = ToStringSerializer.class)
    @Schema(description = "${field.comment}", example = "8088")
    <#else>
    @Schema(description = "${field.comment}")
    </#if>
    <#-- 自动填充 -->
    <#if field.fill??>
    @TableField(fill = FieldFill.${field.fill})
    <#elseif field.name != field.columnName>
    @TableField("${field.columnName}")
    </#if>
    private ${field.propertyType} ${field.propertyName};

</#if>
</#list>
}