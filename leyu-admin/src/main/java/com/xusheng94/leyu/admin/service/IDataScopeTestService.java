package com.xusheng94.leyu.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xusheng94.leyu.admin.entity.DataScopeTest;
import com.xusheng94.leyu.admin.model.query.DataScopeTestQuery;
import com.xusheng94.leyu.admin.model.vo.DataScopeTestVo;

/**
 * <p>
 * 数据权限测试表 服务接口
 * </p>
 *
 * @author xusheng
 * @since 2026-04-17 18:20:39
 */
public interface IDataScopeTestService extends IService<DataScopeTest> {

    /**
     * 分页查询
     * @param query 查询参数
     * @return 分页结果 VO
     */
    Page<DataScopeTestVo> page(DataScopeTestQuery query);

    /**
     * 根据 ID 获取单条记录
     */
    DataScopeTestVo getVoById(Long id);

}