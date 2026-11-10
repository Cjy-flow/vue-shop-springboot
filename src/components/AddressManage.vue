<template>
  <div class="address-manage">
    <div class="header">
      <button class="back-btn" @click="$router.back()">← 返回</button>
      <h2>收货地址管理</h2>
    </div>

    <div class="address-list">
      <div class="address-item" v-for="(addr, index) in addressList" :key="index">
        <div class="info">
          <p><strong>{{ addr.name }}</strong> &nbsp; {{ addr.phone }}</p>
          <p>{{ addr.address }}</p>
        </div>
        <div class="actions">
          <button @click="editAddress(index)">编辑</button>
          <button class="delete" @click="deleteAddress(index)">删除</button>
        </div>
      </div>
    </div>

    <div class="form-card" v-if="showForm">
      <input v-model="currentAddress.name" placeholder="收货人姓名" />
      <input v-model="currentAddress.phone" placeholder="联系电话" />
      <input v-model="currentAddress.address" placeholder="详细地址" />
      <button @click="saveAddress">保存地址</button>
    </div>

    <button class="add-btn" @click="addNewAddress">+ 新增地址</button>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const addressList = ref([])
const showForm = ref(false)
const editingIndex = ref(-1)
const currentAddress = ref({ name: '', phone: '', address: '' })

// 🔥 核心：获取当前登录用户
const getCurrentUser = () => localStorage.getItem('loginUser') || 'guest'

// 🔥 动态获取当前账号的存储KEY
const getStorageKey = () => `user_addresses_${getCurrentUser()}`

// 加载地址（每次都重新获取当前用户）
const loadAddresses = () => {
  const data = localStorage.getItem(getStorageKey())
  addressList.value = data ? JSON.parse(data) : []
}

// 保存地址
const saveAddresses = () => {
  localStorage.setItem(getStorageKey(), JSON.stringify(addressList.value))
}

// 监听用户切换 → 自动刷新地址
watch(() => getCurrentUser(), () => {
  loadAddresses()
}, { immediate: true })

onMounted(() => {
  loadAddresses()
})

// 新增地址
const addNewAddress = () => {
  editingIndex.value = -1
  currentAddress.value = { name: '', phone: '', address: '' }
  showForm.value = true
}

// 编辑地址
const editAddress = (index) => {
  editingIndex.value = index
  currentAddress.value = { ...addressList.value[index] }
  showForm.value = true
}

// 保存地址
const saveAddress = () => {
  if (!currentAddress.value.name || !currentAddress.value.phone || !currentAddress.value.address) {
    return alert('请填写完整信息')
  }

  if (editingIndex.value === -1) {
    addressList.value.push({ ...currentAddress.value })
  } else {
    addressList.value[editingIndex.value] = { ...currentAddress.value }
  }

  saveAddresses()
  showForm.value = false
}

// 删除地址
const deleteAddress = (index) => {
  if (confirm('确定删除该地址吗？')) {
    addressList.value.splice(index, 1)
    saveAddresses()
  }
}
</script>

<style scoped>
/* 样式 100% 完全没动！ */
.address-manage {
  background: #f5f5f5;
  min-height: 100vh;
  padding: 20px;
}
.header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}
.back-btn {
  background: none;
  border: none;
  font-size: 16px;
  cursor: pointer;
  margin-right: 10px;
}
.address-list {
  margin-bottom: 20px;
}
.address-item {
  background: #fff;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.info p { margin: 5px 0; }
.actions button {
  margin-left: 8px;
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.delete { background: #ff3333; color: #fff; }

.form-card {
  background: #fff;
  padding: 15px;
  border-radius: 8px;
  margin-bottom: 15px;
}
.form-card input {
  display: block;
  width: 100%;
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #eee;
  border-radius: 4px;
  box-sizing: border-box;
}
.form-card button {
  width: 100%;
  padding: 10px;
  background: #ff3333;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.add-btn {
  width: 100%;
  padding: 12px;
  background: #fff;
  color: #ff3333;
  border: 1px solid #ff3333;
  border-radius: 8px;
  cursor: pointer;
}
</style>