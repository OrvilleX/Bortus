package com.orvillex.bortus.manager.httpclient.vo;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 查询所有门店信息回应
 */
@Data
@AllArgsConstructor
public class QueryAllUserResVo implements Serializable {
    private PostBackParameterVo postBackParameter;

    /**
     * 记录数
     */
    private Long pageSize;

    private List<Item> result;

    /**
     * 数据项
     */
    @Data
    public static class Item {
        /**
         * 标识
         */
        private Long id;

        /**
         * 门店账户
         */
        private String account;

        /**
         * 邮箱
         */
        private String email;

        /**
         * 地址
         */
        private String address;

        /**
         * 联系电话
         */
        private String tel;

        /**
         * 公司名称
         */
        private String company;

        /**
         * 行业
         */
        private String industry;

        /**
         * 用于接口
         */
        private String appId;

        /**
         * 门店编号
         */
        private String number;
    }
}
