<#-- templates/controller.java.ftl -->
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
<#-- 短的表名 -->
<#assign commentParts = entityComment?split("--")>
<#assign entityShortComment = commentParts[commentParts?size - 1]>
package ${package.Controller};

import ${basePackage}.common.BizException;
import ${package.Entity}.${entity};
import ${package.Service}.I${entity}Service;
import ${package.Query}.${entity}Query;
import ${package.Dto}.${entity}Dto;
import ${package.Vo}.${entity}Vo;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * <p>
 * ${table.comment} Controller 控制器
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */

@RequiredArgsConstructor
@RestController
@RequestMapping("/${table.name?replace('__', '/')?replace('_', '-')}")
@Tag(name = "${entityComment}", description = "${entityComment}控制器")
public class ${entity}Controller {

    private final I${entity}Service ${entityLower}Service;

    @GetMapping("/page")
    @Operation(summary = "${entityShortComment}列表(分页)")
    public Page<${entity}Vo> page(${entity}Query query) {
        return ${entityLower}Service.page(query);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取${entityShortComment}")
    public ${entity}Vo item(@PathVariable Long id) {
        ${entity}Vo vo = ${entityLower}Service.getVoById(id);
        if (vo == null) {
            throw new BizException(404, "${entityShortComment}未找到");
        }
        return vo;
    }

    @PostMapping
    @Operation(summary = "新增${entityShortComment}")
    public Long add(@RequestBody ${entity}Dto dto) {
        boolean success = ${entityLower}Service.save(dto);
        if (!success) {
            throw new BizException(400, "新增${entityShortComment}失败");
        }
        return dto.getId();
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新${entityShortComment}")
    public void update(@PathVariable Long id, @RequestBody ${entity}Dto dto) {
        dto.setId(id);
        boolean success = ${entityLower}Service.updateById(dto);
        if (!success) {
            throw new BizException(400, "更新${entityShortComment}失败");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除${entityShortComment}")
    public void delete(@PathVariable Long id) {
        boolean success = ${entityLower}Service.removeById(id);
        if (!success) {
            throw new BizException(400, "删除${entityShortComment}失败");
        }
    }

    @GetMapping
    @Operation(summary = "${entityShortComment}列表")
    public List<${entity}> list() {
        return ${entityLower}Service.list();
    }

}