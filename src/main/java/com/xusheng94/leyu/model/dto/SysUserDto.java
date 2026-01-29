package com.xusheng94.leyu.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.xusheng94.leyu.config.LongListToStringSerializer;
import com.xusheng94.leyu.entity.SysUser;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class SysUserDto extends SysUser {

    @Schema(description = "角色ids", example = "[8808]")
    @JsonSerialize(using = LongListToStringSerializer.class)
    List<Long> roleIds;
}
