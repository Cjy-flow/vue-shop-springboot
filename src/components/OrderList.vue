<template>
  <div class="order-list-page">
    <div class="header">
      <button @click="$router.back()">← 返回</button>
      <h2>我的订单</h2>
    </div>

    <div class="tabs">
      <span
        v-for="tab in tabs"
        :key="tab"
        :class="{ active: currentTab === tab }"
        @click="currentTab = tab"
      >
        {{ tab }}
      </span>
    </div>

    <div class="order-list">
      <div class="empty-tip" v-if="filteredOrders.length === 0">暂无订单</div>

      <div class="order-card" v-for="order in filteredOrders" :key="order.id">
        <div class="order-top">
          <span>订单号：{{ order.id }}</span>
          <span class="status">{{ order.status }}</span>
        </div>

        <div class="goods-list">
          <div
            class="goods-item"
            v-for="g in order.goods"
            :key="g.id"
            @click="goToGoods(g.id)"
          >
            <img :src="g.pic" alt="" class="goods-img">
            <div class="goods-info">
              <p class="name">{{ g.name }}</p>
              <p class="price">¥{{ g.price }} × {{ g.count }}</p>
            </div>
          </div>
        </div>

        <div class="order-bottom">
          <span>合计：¥{{ order.totalPrice }}</span>
          <span>{{ order.time }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useStore } from 'vuex'
import { useRouter, useRoute } from 'vue-router'

const store = useStore()
const router = useRouter()
const route = useRoute()

// 已按你要求修改标签文字
const tabs = ['全部', '待支付', '待发货', '待收货', '已完成', '售后/退款']
const currentTab = ref('全部')

const allOrders = computed(() => store.state.orderList)
const filteredOrders = computed(() => {
  if (currentTab.value === '全部') return allOrders.value

  // 🔥 核心改动：已支付 + 待发货 都归到待发货里
  if (currentTab.value === '待发货') {
    return allOrders.value.filter(o => 
      o.status === '待发货' || o.status === '已支付'
    )
  }

  return allOrders.value.filter(o => o.status === currentTab.value)
})

// 接收个人中心跳转，自动选中对应标签
onMounted(() => {
  const type = route.query.type
  if (!type) return

  const map = {
    pay: '待支付',
    send: '待发货',
    receive: '待收货',
    done: '已完成',
    refund: '售后/退款'
  }

  if (map[type]) {
    currentTab.value = map[type]
  }
})

const goToGoods = (id) => {
  router.push(`/detail/${id}`)
}
</script>

<style scoped>
.order-list-page {
  padding: 15px;
  background: #f5f5f5;
  min-height: 100vh;
}
.header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}
.header button {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
}
.tabs {
  display: flex;
  justify-content: space-around;
  background: #fff;
  padding: 10px 0;
  margin-bottom: 10px;
  border-radius: 8px;
}
.tabs span {
  font-size: 14px;
  color: #666;
  cursor: pointer;
}
.tabs span.active {
  color: #ff3333;
  font-weight: bold;
}
.order-card {
  background: #fff;
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 10px;
}
.order-top {
  display: flex;
  justify-content: space-between;
  font-size: 14px;
  margin-bottom: 8px;
}
.status {
  color: #ff3333;
  font-weight: bold;
}
.goods-item {
  display: flex;
  align-items: center;
  padding: 6px 0;
  cursor: pointer;
}
.goods-img {
  width: 46px;
  height: 46px;
  border-radius: 6px;
  object-fit: cover;
  margin-right: 8px;
}
.goods-info .name {
  margin: 0;
  font-size: 14px;
}
.goods-info .price {
  margin: 2px 0 0;
  font-size: 13px;
  color: #ff3333;
}
.order-bottom {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #999;
  margin-top: 8px;
}
.empty-tip {
  text-align: center;
  color: #999;
  padding: 30px 0;
}
</style>