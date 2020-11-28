package com.orvillex.bortus.modules.tools.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 邮件配置类
 * @author y-z-f
 * @version 0.1
 */
@Entity
@Data
@Table(name = "tool_email_config")
public class EmailConfig implements Serializable {

    @Id
    @Column(name = "config_id")
    @ApiModelProperty(value = "ID", hidden = true)
    private Long id;

    @NotBlank
    @ApiModelProperty(value = "邮件服务器SMTP地址")
    private String host;

    @NotBlank
    @ApiModelProperty(value = "邮件服务器 SMTP 端口")
    private String port;

    @NotBlank
    @ApiModelProperty(value = "发件者用户名")
    private String user;

    @NotBlank
    @ApiModelProperty(value = "密码")
    private String pass;

    @NotBlank
    @ApiModelProperty(value = "收件人")
    private String fromUser;
}
