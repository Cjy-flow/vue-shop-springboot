package com.cjy.shop.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 商品仓储层。继承 JpaRepository 即拥有 save / findAll / deleteById 等常用方法，
 * 无需手写实现。
 */
public interface GoodsRepository extends JpaRepository<Goods, Long> {

    /** 按分类查商品（分类统计用） */
    List<Goods> findByCategoryOrderByIdAsc(String category);

    /** 名称精确匹配，用于新增时做重名校验 */
    boolean existsByName(String name);
}
