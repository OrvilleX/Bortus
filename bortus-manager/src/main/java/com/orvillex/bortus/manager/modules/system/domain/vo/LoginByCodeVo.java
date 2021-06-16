package com.orvillex.bortus.manager.modules.system.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信登录凭据
 * @author y-z-f
 * @version 0.1
 */
@Data
@ApiModel(value = "微信登录凭据")
@NoArgsConstructor
@AllArgsConstructor
public class LoginByCodeVo {
    @ApiModelProperty(value = "微信鉴权码")
    private String code;

    @ApiModelProperty(value = "昵称")
    private String nickName;

    @ApiModelProperty(value = "性别")
    private Integer gender;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "区")
    private String country;

    @ApiModelProperty(value = "头像地址")
    private String avatarUrl;

    @ApiModelProperty(value = "加密数据")
    private String encryptedData;

    @ApiModelProperty(value = "偏移量")
    private String iv;
}
