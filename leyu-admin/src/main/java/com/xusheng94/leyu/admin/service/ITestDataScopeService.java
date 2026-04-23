package com.xusheng94.leyu.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xusheng94.leyu.admin.entity.TestDataScope;
import com.xusheng94.leyu.admin.model.query.TestDataScopeQuery;
import com.xusheng94.leyu.admin.model.vo.TestDataScopeVo;

/**
 * <p>
 * 测试--数据权限表 服务接口
 * </p>
 *
 * @author xusheng
 * @since 2026-04-17 18:20:39
 */
public interface ITestDataScopeService extends IService<TestDataScope> {

    /**
     * 分页查询
     * 
     * @param query 查询参数
     * @return 分页结果 VO
     */
    Page<TestDataScopeVo> page(TestDataScopeQuery query);

    /**
     * 根据 ID 获取单条记录
     */
    TestDataScopeVo getVoById(Long id);

}