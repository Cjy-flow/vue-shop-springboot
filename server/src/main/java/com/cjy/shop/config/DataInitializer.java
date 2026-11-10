package com.cjy.shop.config;

import com.cjy.shop.domain.GoodsRepository;
import com.cjy.shop.service.GoodsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 首次启动时灌入初始商品。
 * 库里已有数据就跳过，不会覆盖你自己在后台改过的内容。
 */
@Configuration
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    ApplicationRunner seedGoods(GoodsService goodsService, GoodsRepository repository) {
        return args -> {
            long before = repository.count();
            goodsService.seedIfEmpty();
            long after = repository.count();
            if (before == 0 && after > 0) {
                log.info("数据库为空，已初始化 {} 件默认商品", after);
            } else {
                log.info("数据库已有 {} 件商品，跳过初始化", after);
            }
        };
    }
}
