<#-- templates/controller.java.ftl -->
package ${basePackage}.controller;

import ${basePackage}.common.BizException;
import ${basePackage}.entity.${entity};
import ${basePackage}.service.I${entity}Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

<#-- 如果 entityLower 没传，就用 entity 名称首字母小写 -->
<#assign entityLower = entity?uncap_first>
<#-- 先拿到 comment，如果没有就用实体名 -->
<#assign rawComment = table.comment?if_exists?trim>
<#if rawComment?? && rawComment != "">
    <#assign entityComment = rawComment>
<#else>
    <#assign entityComment = entity>
</#if>
<#-- 如果最后一个字是“表”，去掉 -->
<#if entityComment?ends_with("表")>
    <#assign entityComment = entityComment?substring(0, entityComment?length - 1)>
</#if>
<#assign commentParts = entityComment?split("--")>
<#assign entityShortComment = commentParts[commentParts?size - 1]>
/**
 * <p>
 * ${table.comment} Controller 控制器
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */

@RestController
@RequestMapping("/${table.name?replace('__', '/')?replace('_', '-')}")
@Tag(name = "${entityComment}", description = "${entityComment}控制器")
public class ${entity}Controller {

    @Autowired
    private I${entity}Service ${entityLower}Service;

    @GetMapping
    @Operation(summary = "${entityShortComment}列表")
    public List<${entity}> list() {
        return ${entityLower}Service.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取${entityShortComment}")
    public ${entity} get(@PathVariable Long id) {
        ${entity} entityLower = ${entityLower}Service.getById(id);
        if (entityLower == null) {
            throw new BizException(404, "${entityShortComment}未找到");
        }
        return entityLower;
    }

    @PostMapping
    @Operation(summary = "新增${entityShortComment}")
    public ${entity} save(@RequestBody ${entity} ${entityLower}) {
        boolean success = ${entityLower}Service.save(${entityLower});
        if (!success) {
            throw new BizException(400, "新增${entityShortComment}失败");
        }
        return ${entityLower};
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新${entityShortComment}")
    public ${entity} update(@PathVariable Long id, @RequestBody ${entity} ${entityLower}) {
        ${entityLower}.setId(id);
        boolean success = ${entityLower}Service.updateById(${entityLower});
        if (!success) {
            throw new BizException(400, "更新${entityShortComment}失败");
        }
        return ${entityLower};
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除${entityShortComment}")
    public void delete(@PathVariable Long id) {
        boolean success = ${entityLower}Service.removeById(id);
        if (!success) {
            throw new BizException(400, "删除${entityShortComment}失败");
        }
    }

    <#-- 分页接口 -->
    @GetMapping("/page")
    @Operation(summary = "${entityShortComment}列表(分页)")
    public Page<${entity}> page(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size) {
        return ${entityLower}Service.page(new Page<>(page, size));
    }
}