<#-- templates/controller.java.ftl -->
package ${basePackage}.controller;

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
/**
 * <p>
 * ${table.comment} Controller 控制器
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */

@RestController
@RequestMapping("/${entityLower}")
@Tag(name = "${entityComment}", description = "${entityComment} 控制器")
public class ${entity}Controller {

    @Autowired
    private I${entity}Service ${entityLower}Service;

    @GetMapping
    @Operation(summary = "获取所有 ${entityComment} 列表")
    public List<${entity}> list() {
        return ${entityLower}Service.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 获取 ${entityComment}")
    public ${entity} get(@PathVariable Long id) {
        return ${entityLower}Service.getById(id);
    }

    @PostMapping
    @Operation(summary = "新增 ${entityComment}")
    public boolean save(@RequestBody ${entity} ${entityLower}) {
        return ${entityLower}Service.save(${entityLower});
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新 ${entityComment}")
    public boolean update(@PathVariable Long id, @RequestBody ${entity} ${entityLower}) {
        ${entityLower}.setId(id);
        return ${entityLower}Service.updateById(${entityLower});
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除 ${entityComment} 根据 ID")
    public boolean delete(@PathVariable Long id) {
        return ${entityLower}Service.removeById(id);
    }

    <#-- 分页接口 -->
    @GetMapping("/page")
    @Operation(summary = "分页获取 ${entityComment} 列表")
    public Page<${entity}> page(@RequestParam(defaultValue = "1") long page,
                                 @RequestParam(defaultValue = "10") long size) {
        return ${entityLower}Service.page(new Page<>(page, size));
    }
}