package com.cjy.shop.service;

import com.cjy.shop.domain.Goods;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 初始商品数据。
 *
 * 与前端改造前 goods.js 里的 DEFAULT_GOODS 完全一致，
 * 这样从 localStorage 版本切到接口版本后，页面看到的内容没有变化。
 */
final class DefaultGoods {

    private DefaultGoods() {
    }

    static List<Goods> build() {
        List<Goods> list = new ArrayList<>();
        list.add(goods("羊排1000g", "120", "肉禽蛋", "羊肉 羊排 肉", "lamb", 60));
        list.add(goods("猪瘦肉", "80", "肉禽蛋", "猪肉 瘦肉 肉", "pork", 80));
        list.add(goods("龙虾", "160", "海鲜水产", "虾 海鲜 水产", "lobster", 30));
        list.add(goods("红富士苹果", "29.9", "新鲜水果", "苹果 水果", "apple", 200));
        list.add(goods("西红柿", "6.9", "蔬菜", "番茄 蔬菜", "tomato", 150));
        list.add(goods("纯牛奶", "45", "乳品烘焙", "牛奶 奶 乳品", "milk", 90));
        list.add(goods("全麦面包", "8.8", "乳品烘焙", "面包 烘焙 早餐", "bread", 70));
        list.add(goods("大黄鱼", "69.9", "海鲜水产", "鱼 黄花鱼 海鲜", "yellowFish", 40));
        list.add(goods("土豆", "3.9", "蔬菜", "马铃薯 蔬菜", "potato", 300));
        list.add(goods("香蕉", "15.9", "新鲜水果", "水果", "banana", 120));
        list.add(goods("土鸡蛋", "19.9", "肉禽蛋", "鸡蛋 蛋", "egg", 100));
        list.add(goods("三黄鸡", "39.9", "肉禽蛋", "鸡肉 鸡 肉", "chicken", 45));
        return list;
    }

    private static Goods goods(String name, String price, String category,
                               String alias, String picKey, int stock) {
        Goods g = new Goods();
        g.setName(name);
        g.setPrice(new BigDecimal(price));
        g.setCategory(category);
        g.setAlias(alias);
        g.setPicKey(picKey);
        g.setStock(stock);
        g.setOnSale(true);
        return g;
    }
}
