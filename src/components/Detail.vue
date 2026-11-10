<template>
  <div class="detail-page">
    <!-- 顶部标题条：显示来源分类标签名，无来源时显示商品名 -->
    <div class="page-header">
      <button class="back-btn" type="button" aria-label="返回" @click="goBack">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"
             stroke-linecap="round" stroke-linejoin="round">
          <polyline points="15 18 9 12 15 6"></polyline>
        </svg>
      </button>
      <span class="header-title">{{ headerTitle }}</span>
    </div>

    <img v-if="currentGoods" :src="currentGoods.pic" alt="" class="goods-img">
    <div class="goods-info" v-if="currentGoods">
      <h3>{{ currentGoods.name }}</h3>
      <p class="price">¥{{ currentGoods.price }}</p>
      <p class="desc">运费：0元 | 库存：{{ currentGoods.stock }} 件</p>
    </div>

    <div class="action-bar" v-if="currentGoods">
      <button class="btn-collect" @click="toggleCollect">
        {{ isCollected ? '★ 已收藏' : '☆ 收藏' }}
      </button>
      <div class="btn-group">
        <button class="btn-cart" @click="addCart">加入购物车</button>
        <button class="btn-buy" @click="buyNow">立即购买</button>
      </div>
    </div>

    <!-- 商品不存在（后台已删除该商品） -->
    <div class="empty-detail" v-if="!currentGoods">
      <div class="empty-icon">📦</div>
      <p class="empty-title">商品不存在或已下架</p>
      <p class="empty-sub">它可能已在后台被删除，去首页看看别的吧</p>
      <button class="empty-btn" type="button" @click="router.push('/home')">返回首页</button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStore } from 'vuex'

import { allGoods } from '@/data/goods'

const route = useRoute()
const router = useRouter()
const store = useStore()

const currentGoods = computed(() => allGoods.value.find(item => item.id === Number(route.params.id)))

// 顶部标题：优先显示来源分类标签名（如「新鲜水果」），其次是商品名
const headerTitle = computed(() => route.query.cat || currentGoods.value?.name || '商品详情')

// 有上一页就返回上一页，直接打开本页时回首页
const goBack = () => {
  if (window.history.state && window.history.state.back) {
    router.back()
  } else {
    router.push('/home')
  }
}

const addCart = () => {
  store.dispatch('addToCart', currentGoods.value)
  alert('已成功加入购物车！')
}

const buyNow = () => {
  store.dispatch('addToCart', currentGoods.value)
  router.push('/cart')
}

const isCollected = ref(false)

onMounted(() => {
  const user = localStorage.getItem('loginUser')
  if (!user) return
  const list = JSON.parse(localStorage.getItem(`collect_${user}`) || '[]')
  if (!currentGoods.value) return
  isCollected.value = list.some(i => i.id === currentGoods.value.id)
})

const toggleCollect = () => {
  if (!currentGoods.value) return
  const user = localStorage.getItem('loginUser')
  if (!user) return alert('请先登录')

  let list = JSON.parse(localStorage.getItem(`collect_${user}`) || '[]')
  const exists = list.some(i => i.id === currentGoods.value.id)

  if (exists) {
    list = list.filter(i => i.id !== currentGoods.value.id)
    isCollected.value = false
    alert('已取消收藏')
  } else {
    list.push(currentGoods.value)
    isCollected.value = true
    alert('收藏成功')
  }

  localStorage.setItem(`collect_${user}`, JSON.stringify(list))
  window.collectList = list
}
</script>

<style scoped>
.detail-page {
  padding-bottom: 110px;
}

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
.back-btn {
  position: absolute;
  left: 8px;
  top: 0;
  width: 40px;
  height: 46px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: none;
  background: transparent;
  color: #333;
  cursor: pointer;
}
.back-btn svg {
  width: 20px;
  height: 20px;
}
.header-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.goods-img {
  width: 100%;
  max-width: 500px;
  height: 300px;
  object-fit: cover;
  display: block;
  margin: 0 auto;
}

.goods-info {
  padding: 15px;
  max-width: 500px;
  margin: 0 auto;
}
.goods-info h3 {
  font-size: 18px;
  margin-bottom: 10px;
}
.price {
  font-size: 22px;
  color: #ee0a24;
  font-weight: bold;
  margin-bottom: 10px;
}
.desc {
  color: #666;
  font-size: 14px;
}

.action-bar {
  position: fixed;
  bottom: 50px;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 500px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  padding: 0 10px;
  box-sizing: border-box;
}

.btn-collect {
  background: transparent;
  border: none;
  color: #666;
  font-size: 14px;
  cursor: pointer;
}

.btn-group {
  display: flex;
}

.btn-cart {
  background: #ffd233;
  border: none;
  padding: 0 20px;
  height: 50px;
  font-size: 16px;
  cursor: pointer;
}

.btn-buy {
  background: #ee0a24;
  color: #fff;
  border: none;
  padding: 0 20px;
  height: 50px;
  font-size: 16px;
  cursor: pointer;
  margin-left: 4px;
}

/* 商品不存在时的空态 */
.empty-detail {
  padding: 70px 24px;
  text-align: center;
}
.empty-icon {
  font-size: 46px;
  opacity: 0.5;
}
.empty-title {
  margin: 16px 0 6px;
  font-size: 15px;
  font-weight: bold;
  color: #333;
}
.empty-sub {
  margin: 0 0 20px;
  font-size: 12px;
  color: #999;
  line-height: 1.7;
}
.empty-btn {
  padding: 9px 24px;
  border: 1px solid #ffd233;
  border-radius: 20px;
  background: #fffbe8;
  color: #8a6d00;
  font-size: 13px;
  cursor: pointer;
}
</style>