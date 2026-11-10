package com.cjy.shop.service;

import com.cjy.shop.common.ApiException;
import com.cjy.shop.domain.Goods;
import com.cjy.shop.domain.GoodsRepository;
import com.cjy.shop.web.dto.GoodsCreateRequest;
import com.cjy.shop.web.dto.GoodsUpdateRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Set;

/**
 * 商品业务逻辑。
 *
 * 数据从「浏览器 localStorage」搬到了数据库，前端只需要换个数据源，
 * 页面代码不用动——这正是当初把商品收敛到 src/data/goods.js 的收益。
 */
@Service
public class GoodsService {

    /** 与前端 CATEGORY_NAMES 保持一致，防止脏分类写进库 */
    private static final Set<String> ALLOWED_CATEGORIES =
            Set.of("新鲜水果", "蔬菜", "肉禽蛋", "海鲜水产", "乳品烘焙");

    private final GoodsRepository repository;

    public GoodsService(GoodsRepository repository) {
        this.repository = repository;
    }

    /** 列表查询，支持关键词（名称/分类/别名）、分类、上架状态过滤 */
    @Transactional(readOnly = true)
    public List<Goods> list(String keyword, String category, Boolean onSale) {
        List<Goods> all = repository.findAll(Sort.by(Sort.Direction.ASC, "id"));

        String kw = keyword == null ? "" : keyword.trim().toLowerCase();
        boolean hasKeyword = !kw.isEmpty();
        boolean hasCategory = category != null && !category.isBlank();

        return all.stream()
                .filter(g -> !hasKeyword || matches(g, kw))
                .filter(g -> !hasCategory || category.equals(g.getCategory()))
                .filter(g -> onSale == null || onSale.equals(g.getOnSale()))
                .toList();
    }

    private boolean matches(Goods g, String keyword) {
        return contains(g.getName(), keyword)
                || contains(g.getCategory(), keyword)
                || contains(g.getAlias(), keyword);
    }

    private boolean contains(String source, String keyword) {
        return source != null && source.toLowerCase().contains(keyword);
    }

    @Transactional(readOnly = true)
    public Goods get(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> ApiException.notFound("商品不存在（id=" + id + "）"));
    }

    @Transactional
    public Goods create(GoodsCreateRequest req) {
        String name = req.name().trim();
        if (repository.existsByName(name)) {
            throw ApiException.badRequest("已存在同名商品「" + name + "」");
        }
        validateCategory(req.category());

        Goods goods = new Goods();
        goods.setName(name);
        goods.setPrice(normalizePrice(req.price()));
        goods.setCategory(req.category());
        goods.setAlias(req.alias() == null ? "" : req.alias().trim());
        goods.setPicKey(defaultPicKey(req.picKey()));
        goods.setStock(req.stock() == null ? 0 : req.stock());
        goods.setOnSale(req.onSale() == null || req.onSale());
        return repository.save(goods);
    }

    /** 局部更新：请求里为 null 的字段保持原值 */
    @Transactional
    public Goods update(Long id, GoodsUpdateRequest req) {
        Goods goods = get(id);

        if (req.name() != null && !req.name().isBlank()) {
            goods.setName(req.name().trim());
        }
        if (req.price() != null) {
            goods.setPrice(normalizePrice(req.price()));
        }
        if (req.category() != null && !req.category().isBlank()) {
            validateCategory(req.category());
            goods.setCategory(req.category());
        }
        if (req.alias() != null) {
            goods.setAlias(req.alias().trim());
        }
        if (req.stock() != null) {
            goods.setStock(req.stock());
        }
        if (req.picKey() != null && !req.picKey().isBlank()) {
            goods.setPicKey(req.picKey());
        }
        if (req.onSale() != null) {
            goods.setOnSale(req.onSale());
        }
        return repository.save(goods);
    }

    @Transactional
    public void delete(Long id) {
        Goods goods = get(id);
        repository.delete(goods);
    }

    /** 恢复出厂 12 件商品（后台误删兜底） */
    @Transactional
    public List<Goods> reset() {
        repository.deleteAllInBatch();
        List<Goods> defaults = DefaultGoods.build().stream()
                .map(repository::save)
                .toList();
        return repository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

    @Transactional(readOnly = true)
    public boolean isEmpty() {
        return repository.count() == 0;
    }

    @Transactional
    public void seedIfEmpty() {
        if (!isEmpty()) {
            return;
        }
        repository.saveAll(DefaultGoods.build());
    }

    private void validateCategory(String category) {
        if (!ALLOWED_CATEGORIES.contains(category)) {
            throw ApiException.badRequest("未知分类「" + category + "」，可选：" + ALLOWED_CATEGORIES);
        }
    }

    private BigDecimal normalizePrice(BigDecimal price) {
        return price.setScale(2, RoundingMode.HALF_UP);
    }

    private String defaultPicKey(String picKey) {
        return (picKey == null || picKey.isBlank()) ? "apple" : picKey;
    }
}
