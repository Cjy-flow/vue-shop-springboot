package com.cjy.shop.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体，对应数据库表 goods。
 *
 * 与前端 GoodsManage 表单字段一一对应：
 *   name / price / category / alias / stock / picKey / onSale
 */
@Entity
@Table(name = "goods")
public class Goods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 商品名称 */
    @Column(nullable = false, length = 50)
    private String name;

    /** 价格：用 BigDecimal 而不是 double，避免 0.1 + 0.2 这类精度问题 */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /** 所属分类，如「新鲜水果」。分类清单由前端 CATEGORY_NAMES 维护 */
    @Column(nullable = false, length = 20)
    private String category;

    /**
     * 搜索别名，如「苹果 水果」。
     * 列名用 alias_text 是为了避开部分数据库把 ALIAS 当保留字的坑，
     * Java 字段名保持 alias，所以前端拿到的 JSON 字段仍然是 alias。
     */
    @Column(name = "alias_text", length = 100)
    private String alias;

    /** 图片键，如 apple。前端用它映射 assets 里的真实图片，避免构建 hash 失效 */
    @Column(name = "pic_key", length = 30)
    private String picKey;

    /** 库存 */
    @Column(nullable = false)
    private Integer stock;

    /** 是否上架：下架后前台首页/分类页不再展示 */
    @Column(name = "on_sale", nullable = false)
    private Boolean onSale;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPicKey() {
        return picKey;
    }

    public void setPicKey(String picKey) {
        this.picKey = picKey;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getOnSale() {
        return onSale;
    }

    public void setOnSale(Boolean onSale) {
        this.onSale = onSale;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
