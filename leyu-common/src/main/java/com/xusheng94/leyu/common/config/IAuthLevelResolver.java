package com.xusheng94.leyu.common.config;

import com.xusheng94.leyu.common.enums.AuthLevel;

public interface IAuthLevelResolver {

    AuthLevel resolve(String path);
}
