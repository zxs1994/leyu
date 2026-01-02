package com.example.template.service.impl;

import com.example.template.entity.User;
import com.example.template.mapper.UserMapper;
import com.example.template.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xusheng
 * @since 2026-01-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
