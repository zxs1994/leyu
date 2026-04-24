package com.xusheng94.leyu.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xusheng94.leyu.admin.entity.SysOperationLog;
import com.xusheng94.leyu.admin.model.query.SysOperationLogQuery;
import com.xusheng94.leyu.admin.model.vo.SysOperationLogVo;

/**
 * <p>
 * 系统--操作日志表 服务接口
 * </p>
 *
 * @author xusheng
 * @since 2026-04-23 15:14:17
 */
public interface ISysOperationLogService extends IService<SysOperationLog> {

    /**
     * 异步写入操作日志，避免阻塞主请求。
     */
    void saveAsync(SysOperationLog operationLog);

    /**
     * 分页查询
     * @param query 查询参数
     * @return 分页结果 VO
     */
    Page<SysOperationLogVo> page(SysOperationLogQuery query);

    /**
     * 根据 ID 获取单条记录
     */
    SysOperationLogVo getVoById(Long id);

}