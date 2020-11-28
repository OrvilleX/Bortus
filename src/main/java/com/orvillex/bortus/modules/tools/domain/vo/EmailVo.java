package com.orvillex.bortus.modules.tools.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 发送邮件参数类
 * @author y-z-f
 * @version 0.1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailVo {

    @NotEmpty
    private List<String> tos;

    @NotBlank
    private String subject;

    @NotBlank
    private String content;
}
