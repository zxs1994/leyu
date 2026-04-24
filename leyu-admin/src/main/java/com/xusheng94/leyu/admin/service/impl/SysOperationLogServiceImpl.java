package com.xusheng94.leyu.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xusheng94.leyu.admin.entity.SysOperationLog;
import com.xusheng94.leyu.admin.entity.SysUser;
import com.xusheng94.leyu.admin.mapper.SysOperationLogMapper;
import com.xusheng94.leyu.admin.mapper.SysUserMapper;
import com.xusheng94.leyu.admin.service.ISysOperationLogService;
import com.xusheng94.leyu.admin.model.query.SysOperationLogQuery;
import com.xusheng94.leyu.admin.model.vo.SysOperationLogVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统--操作日志表 服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-04-23 15:14:17
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog>
        implements ISysOperationLogService {

    private final SysUserMapper sysUserMapper;

    @Override
    @Async
    public void saveAsync(SysOperationLog operationLog) {
        try {
            this.save(operationLog);
        } catch (Exception ex) {
            log.warn("Persist operation log async failed, method={}, path={}",
                    operationLog.getMethod(), operationLog.getPath(), ex);
        }
    }

    /**
     * 分页查询（返回 VO）
     */
    @Override
    public Page<SysOperationLogVo> page(SysOperationLogQuery query) {
        Page<SysOperationLog> entityPage = new Page<>(query.getPage(), query.getSize());
        QueryWrapper<SysOperationLog> qw = new QueryWrapper<>();

        if (query.getStatus() != null) {
            qw.eq("status", query.getStatus());

        }
        if (query.getCreatedAtStart() != null
                && query.getCreatedAtEnd() != null) {
            qw.between(
                    "created_at",
                    query.getCreatedAtStart(),
                    query.getCreatedAtEnd());
        }

        if (query.getUserId() != null && query.getUserId() != 0L) {
            qw.eq("user_id", query.getUserId());
        }

        qw.orderByDesc("created_at").orderByAsc("id");

        entityPage = super.page(entityPage, qw);
        Map<Long, String> usernameMap = buildUsernameMap(entityPage.getRecords());
        return entityPageToVoPage(entityPage, usernameMap);
    }

    /**
     * 根据 ID 获取 VO
     */
    @Override
    public SysOperationLogVo getVoById(Long id) {
        SysOperationLog entity = this.getById(id);
        if (entity == null)
            return null;
        Map<Long, String> usernameMap = buildUsernameMap(List.of(entity));
        return convertToVo(entity, usernameMap);
    }

    // ===================== 私有 VO 转换方法 =====================

    /**
     * 单个实体转 VO
     */
    private SysOperationLogVo convertToVo(SysOperationLog entity, Map<Long, String> usernameMap) {
        SysOperationLogVo vo = new SysOperationLogVo();
        BeanUtils.copyProperties(entity, vo);
        Long userId = entity.getUserId();
        vo.setUsername(userId == null ? null : usernameMap.get(userId));
        return vo;
    }

    private Map<Long, String> buildUsernameMap(List<SysOperationLog> logs) {
        Set<Long> userIds = logs.stream()
                .map(SysOperationLog::getUserId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return new HashMap<>();
        }

        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id", "name", "email").in("id", userIds);
        return sysUserMapper.selectList(queryWrapper).stream()
                .collect(Collectors.toMap(
                        SysUser::getId,
                        user -> StringUtils.hasText(user.getName()) ? user.getName() : user.getEmail(),
                        (left, right) -> left));
    }

    /**
     * 实体分页转 VO 分页
     */
    private Page<SysOperationLogVo> entityPageToVoPage(Page<SysOperationLog> entityPage,
            Map<Long, String> usernameMap) {
        Page<SysOperationLogVo> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(),
                entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream()
                .map(entity -> convertToVo(entity, usernameMap))
                .toList());
        return voPage;
    }
}