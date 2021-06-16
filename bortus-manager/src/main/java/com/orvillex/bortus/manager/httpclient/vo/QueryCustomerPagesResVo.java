package com.orvillex.bortus.manager.httpclient.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 分页查询全部会员回应
 */
@Data
@AllArgsConstructor
public class QueryCustomerPagesResVo implements Serializable {
    private PostBackParameterVo postBackParameter;

    /**
     * 记录数
     */
    private Long pageSize;

    private List<Item> result;

    @Data
    public static class Item {
        /**
         * 标识
         */
        private Long customerUid;

        /**
         * 分类名称
         */
        private String categoryName;

        /**
         * 会员号
         */
        private String number;

        /**
         * 会员姓名
         */
        private String name;

        /**
         * 当前积分
         */
        private BigDecimal point;

        /**
         * 享受的折扣，60%以60表示
         */
        private BigDecimal discount;

        /**
         * 当前通用余额
         */
        private BigDecimal balance;

        /**
         * 联系电话
         */
        private String phone;

        /**
         * 生日
         */
        private String birthday;

        /**
         * QQ号
         */
        private String qq;

        /**
         * 邮箱
         */
        private String email;

        /**
         * 住址
         */
        private String address;

        /**
         * 是否允许赊账，1表示允许
         */
        private Long onAccount;
    }
}
