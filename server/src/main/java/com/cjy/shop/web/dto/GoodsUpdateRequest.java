package com.cjy.shop.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 修改商品请求体。
 *
 * 所有字段都可为空：为 null 表示「这一项不改」。
 * 这样前端「点击上下架」只需提交 { "onSale": false }，
 * 不用先把整条商品查出来再整体提交。
 */
public record GoodsUpdateRequest(

        @Size(max = 20, message = "商品名称最多 20 个字")
        String name,

        @DecimalMin(value = "0", message = "价格不能为负数")
        BigDecimal price,

        String category,

        @Size(max = 30, message = "搜索别名最多 30 个字")
        String alias,

        @Min(value = 0, message = "库存不能为负数")
        Integer stock,

        String picKey,

        Boolean onSale
) {
}
