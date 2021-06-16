package com.orvillex.bortus.manager.httpclient.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 查询单据回应
 */
@Data
@AllArgsConstructor
public class QueryTicketPagesResVo implements Serializable {
    private PostBackParameterVo postBackParameter;

    private Long pageSize;

    private List<Item> result;

    @Data
    public static class Item {
        /**
         * 收银员唯一标识
         */
        private Long cashierUid;

        /**
         * 会员唯一标识
         */
        private Long customerUid;

        /**
         * 单据序列号
         */
        private String sn;

        /**
         * 单据产生的时间，格式为yyyy-MM-dd hh:mm:ss
         */
        private String datetime;

        /**
         * 单据实收总额
         */
        private BigDecimal totalAmount;

        /**
         * 单据总利润
         */
        private BigDecimal totalProfit;

        /**
         * 单据折扣
         */
        private BigDecimal discount;

        /**
         * 第三方平台支付单号
         */
        private String externalOrderNo;

        /**
         * 备注
         */
        private String remark;

        /**
         * 抹零数额，比如3.1元收别人3元，0.1就是被抹零数额
         */
        private BigDecimal rounding;

        /**
         * 单据类型：SELL销售单据, SELL_RETURN退货单据。不区分大小写
         */
        private String ticketType;

        /**
         * 值为1时表示单据已作废
         */
        private Integer invalid;

        /**
         * 多种支付方式
         */
        private List<PayMent> payments;

        /**
         * 单据条目实体
         */
        private List<SubItem> items;
    }

    @Data
    public static class PayMent {
        /**
         * 支付方式代码
         */
        private String code;

        /**
         * 支付金额
         */
        private BigDecimal amount;
    }

    @Data
    public static class SubItem {
        /**
         * 商品名称
         */
        private String name;
        
        /**
         * 商品进货价
         */
        private BigDecimal buyPrice;

        /**
         * 商品销售价
         */
        private BigDecimal sellPrice;

        /**
         * 商品会员价
         */
        private BigDecimal customerPrice;

        /**
         * 销售的商品数量
         */
        private BigDecimal quantity;

        /**
         * 所打的折扣
         */
        private BigDecimal discount;

        /**
         * 会员折扣
         */
        private BigDecimal customerDiscount;

        /**
         * 总价
         */
        private BigDecimal totalAmount;

        /**
         * 总利润
         */
        private BigDecimal totalProfit;

        /**
         * 数据为1时表求享受了会员折扣
         */
        private Long isCustomerDiscount;

        /**
         * 商品唯一标识
         */
        private Long productUid;

        /**
         * 条目属性
         */
        private List<ItemAttributes> ticketitemattributes;
    }

    @Data
    public static class ItemAttributes {
        private String attributeName;
        private String attributeValue;
    }
}
