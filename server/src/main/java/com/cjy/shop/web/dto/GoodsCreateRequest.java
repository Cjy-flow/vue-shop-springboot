package com.cjy.shop.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * 新增商品请求体。字段与前端 GoodsManage 的表单一致。
 */
public record GoodsCreateRequest(

        @NotBlank(message = "商品名称不能为空")
        @Size(max = 20, message = "商品名称最多 20 个字")
        String name,

        @NotNull(message = "价格不能为空")
        @DecimalMin(value = "0", message = "价格不能为负数")
        BigDecimal price,

        @NotBlank(message = "请选择商品分类")
        String category,

        @Size(max = 30, message = "搜索别名最多 30 个字")
        String alias,

        @Min(value = 0, message = "库存不能为负数")
        Integer stock,

        String picKey,

        Boolean onSale
) {
}
