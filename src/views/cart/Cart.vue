<template>
  <!-- 顶部标题条：一级页面，固定显示「购物车」，无返回键 -->
  <div class="page-header">
    <span class="header-title">购物车</span>
    <span class="header-count" v-if="totalCount > 0">({{ totalCount }})</span>
  </div>

  <div class="cart-page">
    <!-- 购物车为空时的占位，避免整页空白 -->
    <div class="empty-cart" v-if="cartList.length === 0">
      <div class="empty-icon">🛒</div>
      <p class="empty-text">购物车还是空的</p>
      <button class="empty-btn" @click="$router.push('/home')">去逛逛</button>
    </div>

    <div class="cart-list" v-else>
      <div class="cart-item" v-for="(item, index) in cartList" :key="item.id">
        <input type="checkbox" class="checkbox" v-model="selectedIds" :value="item.id" />
        <img :src="item.pic" alt="" class="goods-img" />
        <div class="goods-info">
          <p class="name">{{ item.name }}</p>
          <p class="price">¥{{ item.price }}</p>
        </div>
        <div class="num-box">
          <button @click="cutNum(item.id)">-</button>
          <span>{{ item.count }}</span>
          <button @click="addNum(item.id)">+</button>
        </div>
        <button class="delete-btn" @click="delItem(item.id)">-</button>
      </div>
    </div>

    <div class="total-bar">
      <div class="select-all">
        <input type="checkbox" class="checkbox" v-model="isAllSelected" @click="toggleSelectAll" />
        <span>全选</span>
      </div>

      <div class="total-info">
        <span>合计：</span>
        <span class="total-price">¥{{ totalPrice }}</span>
      </div>

      <button class="checkout-btn" @click="checkout">结算</button>
    </div>

    <div class="address-modal" v-if="showAddressModal">
      <div class="modal-mask" @click="showAddressModal = false"></div>
      <div class="modal-content">
        <h3>选择收货地址</h3>
        <div class="address-list">
          <div 
            class="address-item" 
            v-for="(addr, idx) in addressList" 
            :key="idx"
            :class="{ active: selectedAddrIndex === idx }"
            @click="selectedAddrIndex = idx"
          >
            <p>{{ addr.name }} &nbsp;&nbsp; {{ addr.phone }}</p>
            <p>{{ addr.address }}</p>
          </div>
        </div>
        <button class="confirm-btn" @click="confirmOrder">确认下单</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import { useStore } from 'vuex'

const store = useStore()
const cartList = computed(() => store.state.cartList)
const selectedIds = ref([])

const isAllSelected = computed(() => {
  if (cartList.value.length === 0) return false
  return selectedIds.value.length === cartList.value.length
})

const toggleSelectAll = () => {
  isAllSelected.value ? selectedIds.value = [] : selectedIds.value = cartList.value.map(i => i.id)
}

watch(cartList, () => {
  const ids = cartList.value.map(i => i.id)
  selectedIds.value = selectedIds.value.filter(id => ids.includes(id))
}, { deep: true })

// 标题条右侧的件数：所有商品的数量之和
const totalCount = computed(() => cartList.value.reduce((t, i) => t + i.count, 0))

const totalPrice = computed(() => {
  return cartList.value
    .filter(i => selectedIds.value.includes(i.id))
    .reduce((t, i) => t + i.price * i.count, 0)
    .toFixed(2)
})

const addNum = (id) => {
  const item = cartList.value.find(i => i.id === id)
  store.dispatch('changeCount', { id, count: item.count + 1 })
}
const cutNum = (id) => {
  const item = cartList.value.find(i => i.id === id)
  if (item.count <= 1) return
  store.dispatch('changeCount', { id, count: item.count - 1 })
}
const delItem = (id) => store.dispatch('delCart', id)

const showAddressModal = ref(false)
const addressList = ref([])
const selectedAddrIndex = ref(-1)

// 按当前登录账号加载地址（已隔离）
const loadAddresses = () => {
  const currentUser = localStorage.getItem('loginUser') || 'guest'
  const STORAGE_KEY = `user_addresses_${currentUser}`
  const data = localStorage.getItem(STORAGE_KEY)
  if (data) addressList.value = JSON.parse(data)
}

// 🔥 🔥 🔥 登录切换时自动加载当前账号购物车
const loadUserCart = () => {
  const user = localStorage.getItem('loginUser')
  if (!user) return
  const cartKey = `cart_${user}`
  const cart = JSON.parse(localStorage.getItem(cartKey) || '[]')
  store.commit('SET_CART', cart)
}

onMounted(() => {
  loadAddresses()
  loadUserCart() // 页面加载时加载当前账号购物车
})

const checkout = () => {
  if (!selectedIds.value.length) return alert('请选择商品')
  loadAddresses()
  showAddressModal.value = true
}

const confirmOrder = () => {
  if (selectedAddrIndex.value === -1) {
    return alert('请选择收货地址')
  }
  store.dispatch('createOrder', selectedIds.value)
  store.dispatch('sendOrderMessage', { id: Date.now() })
  alert('结算成功！')
  selectedIds.value = []
  showAddressModal.value = false
  selectedAddrIndex.value = -1
}
</script>

<style scoped>
/* 样式 100% 完全没动！ */
/* 顶部标题条（与分类页保持同一套样式） */
.page-header {
  position: sticky;
  top: 0;
  z-index: 100;
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
.header-count {
  font-size: 14px;
  color: #999;
  margin-left: 2px;
}

.cart-page {
  background: #f5f5f5;
  min-height: calc(100vh - 46px);
  padding-bottom: 110px;
  box-sizing: border-box;
}

/* 空购物车占位 */
.empty-cart {
  padding-top: 90px;
  text-align: center;
}
.empty-icon {
  font-size: 46px;
  line-height: 1;
  opacity: 0.45;
}
.empty-text {
  margin: 14px 0 20px;
  font-size: 14px;
  color: #999;
}
.empty-btn {
  padding: 8px 26px;
  border: none;
  border-radius: 30px;
  background: #ff3333;
  color: #fff;
  font-size: 14px;
  cursor: pointer;
}

.cart-list { padding: 10px; }
.cart-item {
  background: #fff;
  margin-bottom: 10px;
  padding: 12px;
  border-radius: 10px;
  display: flex;
  align-items: center;
}
.checkbox {
  width: 20px;
  height: 20px;
  margin-right: 10px;
  accent-color: #ff3333;
}
.goods-img {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 8px;
}
.goods-info {
  flex: 1;
  margin-left: 10px;
}
.goods-info .name { font-size: 15px; margin: 0; }
.goods-info .price { color: #ff3333; font-weight: bold; margin: 4px 0 0; }
.num-box { display: flex; align-items: center; gap: 6px; margin-right: 10px; }
.num-box button { width: 26px; height: 26px; border: 1px solid #ccc; background: #fff; border-radius: 4px; }
.num-box span { min-width: 20px; text-align: center; }
.delete-btn { width: 32px; height: 32px; background: #ff3333; color: #fff; border: none; border-radius: 50%; cursor: pointer; }

.total-bar {
  position: fixed;
  bottom: 50px;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 750px;
  height: 55px;
  background: #fff;
  border-top: 1px solid #eee;
  display: flex;
  align-items: center;
  padding: 0 20px;
  box-sizing: border-box;
  z-index: 99;
}

.select-all {
  position: absolute;
  left: 25%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
}
.select-all span { margin-left: 4px; font-size: 14px; }

.total-info {
  flex: 1;
  text-align: center;
}
.total-price {
  color: #ff3333;
  font-size: 16px;
  font-weight: bold;
}

.checkout-btn {
  position: absolute;
  right: 25%;
  transform: translateX(50%);
  background: #ff3333;
  color: #fff;
  border: none;
  padding: 8px 22px;
  border-radius: 30px;
  cursor: pointer;
}

.address-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999 !important;
}
.modal-mask {
  position: absolute;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.5);
}
.modal-content {
  position: absolute;
  bottom: 110px !important;
  width: 100%;
  max-width: 750px;
  left: 50%;
  transform: translateX(-50%);
  background: #fff;
  padding: 20px;
  border-radius: 15px;
  box-sizing: border-box;
}
.modal-content h3 {
  margin-top: 0;
  text-align: center;
}
.address-list {
  max-height: 280px;
  overflow-y: auto;
  margin-bottom: 15px;
}
.address-item {
  padding: 12px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 8px;
  cursor: pointer;
}
.address-item.active {
  border-color: #ff3333;
  background: #fff2f2;
}
.confirm-btn {
  width: 100%;
  padding: 12px;
  background: #ff3333;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
}
</style>