package com.xusheng94.leyu.admin.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xusheng94.leyu.admin.entity.DataScopeTest;
import com.xusheng94.leyu.admin.mapper.DataScopeTestMapper;
import com.xusheng94.leyu.admin.service.IDataScopeTestService;
import com.xusheng94.leyu.admin.model.query.DataScopeTestQuery;
import com.xusheng94.leyu.admin.model.vo.DataScopeTestVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;

/**
 * <p>
 * 数据权限测试表 服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-04-17 18:20:39
 */
@Service
public class DataScopeTestServiceImpl extends ServiceImpl<DataScopeTestMapper, DataScopeTest> implements IDataScopeTestService {

    /**
     * 分页查询（返回 VO）
     */
    @Override
    public Page<DataScopeTestVo> page(DataScopeTestQuery query) {
        Page<DataScopeTest> entityPage = new Page<>(query.getPage(), query.getSize());
        QueryWrapper<DataScopeTest> qw = new QueryWrapper<>();

        if (query.getName() != null
            && StringUtils.hasText(query.getName())
        ) {
            qw.like("name", query.getName());

        }
        if (query.getCreatedAtStart() != null
            && query.getCreatedAtEnd() != null) {
            qw.between(
                "created_at",
                query.getCreatedAtStart(),
                query.getCreatedAtEnd()
            );
        }

        entityPage = super.page(entityPage, qw);
        return entityPageToVoPage(entityPage);
    }

    /**
     * 根据 ID 获取 VO
     */
    @Override
    public DataScopeTestVo getVoById(Long id) {
        DataScopeTest entity = this.getById(id);
        if (entity == null) return null;
        return convertToVo(entity);
    }

    // ===================== 私有 VO 转换方法 =====================

    /**
     * 单个实体转 VO
     */
    private DataScopeTestVo convertToVo(DataScopeTest entity) {
        DataScopeTestVo vo = new DataScopeTestVo();
        BeanUtils.copyProperties(entity, vo);
        // TODO 组装额外数据
        return vo;
    }

    /**
     * 实体分页转 VO 分页
     */
    private Page<DataScopeTestVo> entityPageToVoPage(Page<DataScopeTest> entityPage) {
        Page<DataScopeTestVo> voPage = new Page<>(entityPage.getCurrent(), entityPage.getSize(), entityPage.getTotal());
        voPage.setRecords(entityPage.getRecords().stream()
                .map(this::convertToVo)
                .toList());
        return voPage;
    }
}