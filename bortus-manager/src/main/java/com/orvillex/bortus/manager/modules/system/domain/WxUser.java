package com.orvillex.bortus.manager.modules.system.domain;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

/**
 * 微信用户表
 * @author y-z-f
 * @version 0.1
 */
@Getter
@Setter
@Entity
@Table(name = "sys_wx_user")
public class WxUser {
    @Id
    @Column(name = "wxuser_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 微信OpenId
     */
    private String wxOpenId;

    /**
     * 手机号
     */
    private String phoneNumber;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 头像
     */
    private String headUrl;

    /**
     * 城市
     */
    private String city;

    /**
     * 省份
     */
    private String province;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 会话令牌
     */
    private String sessionKey;

    /**
     * 微信加密数据
     */
    private String encryptedData;
    private String iv;
    
    /**
     * 用户来源, 1代表微信
     */
    private Long source;
}
