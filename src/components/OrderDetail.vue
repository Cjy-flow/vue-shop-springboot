<template>
  <div class="order-detail-page">
    <div class="header">
      <button @click="$router.back()">← 返回</button>
      <h2>订单详情</h2>
    </div>

    <!-- 空状态判断 -->
    <div v-if="!order" class="empty">订单不存在或正在加载...</div>

    <div v-else>
      <div class="order-info">
        <p><strong>订单号：</strong>{{ order.id }}</p>
        <p><strong>状态：</strong>{{ order.status }}</p>
        <p><strong>下单时间：</strong>{{ order.time }}</p>
        <p><strong>订单总额：</strong>¥{{ order.totalPrice }}</p>
      </div>

      <div class="goods-list">
        <h3>商品列表</h3>
        <div class="goods-item" v-for="item in order.goods" :key="item.id">
          <img :src="item.pic" alt="" class="goods-img" />
          <div class="goods-info">
            <p class="name">{{ item.name }}</p>
            <p class="price">¥{{ item.price }} × {{ item.count }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

// --- 修改点 1：修正 Store 的导入方式 ---
// 方案 A：如果你使用的是 Vuex (版本 4+)
// 注意：Vite 环境下有时具名导入 { useStore } 会失效，改用默认导入
import Vuex from 'vuex'
// 兼容处理：如果默认导入不存在，尝试从全局或具名导入获取
const useStore = Vuex.useStore || (() => { throw new Error('Vuex useStore not available') })

// 方案 B（推荐）：如果你使用的是 Pinia（Vue 3 标准库），请替换为下面这行：
// import { useStore } from '@/stores/store' // 路径需根据你的项目结构调整

const route = useRoute()
const store = useStore()

// --- 修改点 2：增加数据安全性检查 ---
const order = computed(() => {
  // 确保 state 和 orderList 存在，防止白屏
  const list = store.state?.orderList
  if (!Array.isArray(list)) return null

  // 使用 == 以匹配字符串参数和数字 ID
  return list.find((o) => o.id == route.params.id)
})
</script>

<style scoped>
/* 保持原有样式不变 */
.order-detail-page {
  padding: 15px;
  min-height: 100vh;
  background: #f5f5f5;
}
.header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
}
.header button {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
}
.order-info {
  background: #fff;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
}
.goods-list {
  background: #fff;
  padding: 15px;
  border-radius: 8px;
}
.goods-item {
  display: flex;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #eee;
}
.goods-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 5px;
}
.goods-info {
  margin-left: 10px;
}
.goods-info .name {
  margin: 0;
  font-size: 14px;
}
.goods-info .price {
  margin: 5px 0 0;
  font-size: 14px;
  color: #ff3333;
  font-weight: bold;
}
.empty {
  text-align: center;
  padding: 60px 0;
  color: #999;
}
</style>