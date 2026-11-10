<template>
  <!-- 顶部标题条：一级页面，固定显示「分类」，无返回键 -->
  <div class="page-header">
    <span class="header-title">分类</span>
  </div>

  <div class="category-page">
    <!-- 左侧分类菜单 -->
    <div class="left-menu">
      <div class="menu-item" v-for="(cat, index) in categories" :key="cat.name"
        :class="{ active: activeIndex === index }" @click="activeIndex = index">
        {{ cat.name }}
      </div>
    </div>

    <!-- 右侧商品列表 -->
    <div class="goods-list">
      <div class="goods-item" v-for="goods in categories[activeIndex].goods" :key="goods.id"
        @click="goDetail(goods.id)">
        <img :src="goods.pic" alt="" class="goods-img">
        <p class="goods-name">{{ goods.name }}</p>
        <p class="goods-price">¥{{ goods.price }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

import { categories } from '@/data/goods'

const activeIndex = ref(0)

// 进详情页时把当前分类标签名带过去，供详情页顶部标题条显示
const goDetail = (id) => {
  router.push({
    path: `/detail/${id}`,
    query: { cat: categories.value[activeIndex.value].name }
  })
}

onMounted(() => {
  const type = route.query.type
  if (!type) return

  const map = {
    fruit: 0,
    vegetable: 1,
    meat: 2,
    seafood: 3,
    dairy: 4
  }
  if (map[type] !== undefined) {
    activeIndex.value = map[type]
  }
})
</script>

<style scoped>
/* 顶部标题条 */
.page-header {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 46px;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.header-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.category-page {
  display: flex;
  height: calc(100vh - 106px);
}

.left-menu {
  width: 120px;
  background: #f5f5f5;
}
.menu-item {
  padding: 18px 10px;
  text-align: center;
  font-size: 15px;
  color: #333;
  cursor: pointer;
}
.menu-item.active {
  background: #fff;
  color: #ee0a24;
  font-weight: bold;
}

.goods-list {
  flex: 1;
  padding: 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  align-content: flex-start;
  overflow-y: auto;
}
.goods-item {
  width: 110px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  padding-bottom: 8px;
  cursor: pointer;
}
.goods-img {
  width: 100%;
  height: 110px;
  object-fit: cover;
}
.goods-name {
  margin: 4px 8px;
  font-size: 13px;
}
.goods-price {
  margin: 4px 8px;
  color: #ee0a24;
  font-weight: bold;
}
</style>