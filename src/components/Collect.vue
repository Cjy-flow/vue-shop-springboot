<template>
  <div class="collect-page">
    <!-- 顶部导航栏 -->
    <div class="collect-header">
      <button class="back-btn" @click="$router.back()">← 返回</button>
      <h2>我的收藏</h2>
      <div class="placeholder"></div>
    </div>

    <!-- 收藏商品列表 -->
    <div class="collect-list">
      <div 
        class="collect-item" 
        v-for="item in collectList" 
        :key="item.id"
        @click="$router.push(`/detail/${item.id}`)"
      >
        <img :src="item.pic" alt="" class="collect-img">
        <div class="collect-info">
          <p class="name">{{ item.name }}</p>
          <p class="price">¥{{ item.price }}</p>
        </div>
        <div class="collect-actions">
          <button class="add-cart-btn" @click.stop="addToCart(item)">加入购物车</button>
          <button class="del-btn" @click.stop="removeCollect(item.id)">取消收藏</button>
        </div>
      </div>
      <p class="empty-tip" v-if="collectList.length === 0">暂无收藏商品</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const collectList = ref([])

// 加载当前用户收藏
const loadCollect = () => {
  const user = localStorage.getItem('loginUser')
  if (!user) return
  const d = localStorage.getItem(`collect_${user}`)
  collectList.value = d ? JSON.parse(d) : []
}

// 加入购物车
const addToCart = (item) => {
  store.dispatch('addToCart', item)
  alert('已加入购物车！')
}

// 取消收藏
const removeCollect = (id) => {
  const user = localStorage.getItem('loginUser')
  collectList.value = collectList.value.filter(i => i.id !== id)
  localStorage.setItem(`collect_${user}`, JSON.stringify(collectList.value))
  alert('已取消收藏')
}

onMounted(() => {
  loadCollect()
})
</script>

<style scoped>
.collect-page {
  background: #f5f5f5;
  min-height: 100vh;
  padding-bottom: 60px;
}

.collect-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 15px;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.back-btn {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
}
.collect-header h2 {
  margin: 0;
  font-size: 18px;
}
.placeholder {
  width: 40px;
}

.collect-list {
  padding: 10px;
}
.collect-item {
  display: flex;
  align-items: center;
  background: #fff;
  padding: 10px;
  margin-bottom: 10px;
  border-radius: 8px;
}
.collect-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 5px;
}
.collect-info {
  flex: 1;
  margin-left: 10px;
}
.collect-info .name {
  margin: 0;
  font-size: 14px;
}
.collect-info .price {
  margin: 5px 0 0;
  color: #ff3333;
  font-weight: bold;
}
.collect-actions {
  display: flex;
  gap: 5px;
}
.add-cart-btn {
  background: #ff9900;
  color: #fff;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}
.del-btn {
  background: #ff3333;
  color: #fff;
  border: none;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 12px;
  cursor: pointer;
}
.empty-tip {
  text-align: center;
  color: #999;
  font-size: 14px;
  padding: 50px 0;
}
</style>