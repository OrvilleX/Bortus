package com.orvillex.bortus.manager.modules.tools.domain;

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
    private Long id;

    @NotBlank
    private String host;

    @NotBlank
    private String port;

    @NotBlank
    private String user;

    @NotBlank
    private String pass;

    @NotBlank
    private String fromUser;
}
