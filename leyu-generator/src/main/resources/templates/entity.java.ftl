package ${package.Entity};

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import ${parentPackage}.common.BaseEntity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
<#assign hasOffsetDateTime = false>
<#assign hasJsonField = false>
<#assign hasBigDecimalField = false>
<#list table.fields as field>
    <#if !ignoreFields?seq_contains(field.name)>
        <#if field.propertyType == "OffsetDateTime">
            <#assign hasOffsetDateTime = true>
        </#if>
        <#if field.propertyType == "BigDecimal">
            <#assign hasBigDecimalField = true>
        </#if>
        <#if field.propertyType == "Map<String,Object>" || field.propertyType == "List<Map<String,Object>>">
            <#assign hasJsonField = true>
        </#if>
    </#if>
</#list>
<#if hasOffsetDateTime>
import java.time.OffsetDateTime;
</#if>
<#if hasJsonField>
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

import java.util.List;
import java.util.Map;
</#if>
<#if hasBigDecimalField>
import java.math.BigDecimal;
</#if>

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
@TableName(<#if hasJsonField>value = "${table.name}", autoResultMap = true<#else>"${table.name}"</#if>)
@Schema(description = "${table.comment}")
public class ${entity} extends BaseEntity {

<#list table.fields as field>
<#if !ignoreFields?seq_contains(field.name)>
    <#-- 主键 -->
    <#if field.keyFlag>
    @TableId(type = IdType.<#if autoIdTables?seq_contains(table.name)>AUTO<#else>ASSIGN_ID</#if>)
    </#if>
    <#-- 自动忽略敏感字段 -->
    <#if jsonIgnoreFields?seq_contains(field.name)>
    @JsonIgnore
    </#if>
    <#-- 密码类字段：只写 -->
    <#if field.name == "password">
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    </#if>
    <#-- 只读字段：只读 -->
    <#if readOnlyFields?seq_contains(field.name) && !(table.name == "sys__user" && field.name == "dept_id")>
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    </#if>
    <#-- 自动填充字段 -->
    <#if fieldFillInsert?seq_contains(field.name) && !(table.name == "sys__user" && field.name == "dept_id")>
    @TableField(fill = FieldFill.INSERT)
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
    <#if field.propertyType == "Map<String,Object>" || field.propertyType == "List<Map<String,Object>>">
    <#if field.fill??>
    @TableField(fill = FieldFill.${field.fill}, typeHandler = JacksonTypeHandler.class)
    <#elseif field.name != field.columnName>
    @TableField(value = "${field.columnName}", typeHandler = JacksonTypeHandler.class)
    <#else>
    @TableField(typeHandler = JacksonTypeHandler.class)
    </#if>
    <#elseif field.fill??>
    @TableField(fill = FieldFill.${field.fill})
    <#elseif field.name != field.columnName>
    @TableField("${field.columnName}")
    </#if>
    private ${field.propertyType} ${field.propertyName};

</#if>
</#list>
}