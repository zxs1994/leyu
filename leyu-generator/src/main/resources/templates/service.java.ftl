package ${package.Service};

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${package.Entity}.${entity};
import ${package.Query}.${entity}Query;
import ${package.Vo}.${entity}Vo;

/**
 * <p>
 * ${table.comment!"${entity}"} 服务接口
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
public interface I${entity}Service extends IService<${entity}> {

    /**
     * 分页查询
     * @param query 查询参数
     * @return 分页结果 VO
     */
    Page<${entity}Vo> page(${entity}Query query);

    /**
     * 根据 ID 获取单条记录
     */
    ${entity}Vo getVoById(Long id);

}