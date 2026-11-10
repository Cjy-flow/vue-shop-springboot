package com.cjy.shop.web;

import com.cjy.shop.domain.Goods;
import com.cjy.shop.service.GoodsService;
import com.cjy.shop.web.dto.GoodsCreateRequest;
import com.cjy.shop.web.dto.GoodsUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品接口。
 *
 * GET 开放（前台商城用），写操作需要登录令牌（见 AuthInterceptor）。
 */
@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    /**
     * 商品列表。
     * 例：/api/goods?keyword=苹果&category=新鲜水果&onSale=true
     * 参数全部可选；前端目前一次拉全量再本地筛选（12 件数据量很小）。
     */
    @GetMapping
    public List<Goods> list(@RequestParam(required = false) String keyword,
                            @RequestParam(required = false) String category,
                            @RequestParam(required = false) Boolean onSale) {
        return goodsService.list(keyword, category, onSale);
    }

    @GetMapping("/{id}")
    public Goods detail(@PathVariable Long id) {
        return goodsService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Goods create(@Valid @RequestBody GoodsCreateRequest request) {
        return goodsService.create(request);
    }

    @PutMapping("/{id}")
    public Goods update(@PathVariable Long id, @Valid @RequestBody GoodsUpdateRequest request) {
        return goodsService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        goodsService.delete(id);
    }

    /** 恢复默认 12 件商品 */
    @PostMapping("/reset")
    public List<Goods> reset() {
        return goodsService.reset();
    }
}
