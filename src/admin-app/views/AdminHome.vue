<template>
  <AdminLayout title="数据概览">
    <!-- 统计卡片 -->
    <section class="stat-grid">
      <div class="stat-card" v-for="s in statCards" :key="s.label">
        <div class="stat-icon" :style="{ background: s.bg }">{{ s.icon }}</div>
        <div class="stat-body">
          <div class="stat-value">{{ s.value }}</div>
          <div class="stat-label">{{ s.label }}</div>
        </div>
      </div>
    </section>

    <!-- 商品一览 -->
    <section class="panel">
      <div class="panel-head">
        <h3>商品一览</h3>
        <router-link to="/admin/goods" class="panel-link">前往商品管理 →</router-link>
      </div>

      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th style="width: 56px;">ID</th>
              <th>商品</th>
              <th style="width: 110px;">分类</th>
              <th style="width: 90px;">价格</th>
              <th style="width: 80px;">库存</th>
              <th style="width: 90px;">状态</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="g in allGoods" :key="g.id">
              <td class="muted">{{ g.id }}</td>
              <td>
                <div class="goods-cell">
                  <img :src="g.pic" :alt="g.name" />
                  <span>{{ g.name }}</span>
                </div>
              </td>
              <td class="muted">{{ g.category }}</td>
              <td class="price">¥{{ g.price }}</td>
              <td :class="{ low: g.stock < 50 }">{{ g.stock }}</td>
              <td>
                <span class="tag" :class="g.onSale ? 'tag-on' : 'tag-off'">
                  {{ g.onSale ? '在售' : '已下架' }}
                </span>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <!-- 快捷操作 -->
    <section class="quick-grid">
      <router-link to="/admin/goods" class="quick-card">
        <span class="quick-icon">📦</span>
        <div>
          <div class="quick-title">商品管理</div>
          <div class="quick-desc">新增、编辑、删除与上下架</div>
        </div>
      </router-link>
      <router-link to="/admin/category" class="quick-card">
        <span class="quick-icon">🗂️</span>
        <div>
          <div class="quick-title">分类管理</div>
          <div class="quick-desc">查看各分类商品与库存分布</div>
        </div>
      </router-link>
      <router-link to="/home" class="quick-card">
        <span class="quick-icon">🏬</span>
        <div>
          <div class="quick-title">前台商城</div>
          <div class="quick-desc">查看改动后的实际效果</div>
        </div>
      </router-link>
    </section>
  </AdminLayout>
</template>

<script setup>
import { computed, ref, onMounted } from 'vue'
import AdminLayout from './AdminLayout.vue'
import { allGoods, goodsStats } from '@/data/goods'

// 注册用户数（读前台注册时写入的 users）
const userCount = ref(0)

onMounted(() => {
  try {
    const users = JSON.parse(localStorage.getItem('users') || '[]')
    userCount.value = Array.isArray(users) ? users.length : 0
  } catch (e) {
    userCount.value = 0
  }
})

const statCards = computed(() => [
  { icon: '📦', label: '商品总数', value: goodsStats.value.total, bg: '#fff7d6' },
  { icon: '✅', label: '在售商品', value: goodsStats.value.onSale, bg: '#e8f8ee' },
  { icon: '🚫', label: '已下架', value: goodsStats.value.offSale, bg: '#fdeeee' },
  { icon: '⚠️', label: '低库存（<50）', value: goodsStats.value.lowStock, bg: '#fff2e8' },
  { icon: '🗂️', label: '商品分类', value: goodsStats.value.categoryCount, bg: '#eef3ff' },
  { icon: '👤', label: '注册用户', value: userCount.value, bg: '#f3eefc' }
])
</script>

<style scoped>
/* ===== 统计卡片 ===== */
.stat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 14px;
  margin-bottom: 18px;
}
.stat-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
}
.stat-icon {
  width: 42px;
  height: 42px;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 10px;
  font-size: 19px;
}
.stat-value {
  font-size: 22px;
  font-weight: bold;
  line-height: 1.2;
}
.stat-label {
  margin-top: 3px;
  font-size: 12px;
  color: #888;
}

/* ===== 面板 ===== */
.panel {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  margin-bottom: 18px;
  overflow: hidden;
}
.panel-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  border-bottom: 1px solid #f0f2f5;
}
.panel-head h3 {
  margin: 0;
  font-size: 14px;
}
.panel-link {
  font-size: 12px;
  color: #d48806;
  text-decoration: none;
}
.panel-link:hover {
  text-decoration: underline;
}
.table-wrap {
  overflow-x: auto;
}
.table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
}
.table th {
  padding: 10px 14px;
  text-align: left;
  font-weight: normal;
  font-size: 12px;
  color: #888;
  background: #fafbfc;
  white-space: nowrap;
}
.table td {
  padding: 10px 14px;
  border-top: 1px solid #f4f6f8;
  white-space: nowrap;
}
.table tbody tr:hover {
  background: #fbfcfd;
}
.muted {
  color: #999;
}
.price {
  color: #ee0a24;
  font-weight: bold;
}
td.low {
  color: #fa8c16;
  font-weight: bold;
}
.goods-cell {
  display: flex;
  align-items: center;
  gap: 9px;
}
.goods-cell img {
  width: 34px;
  height: 34px;
  border-radius: 6px;
  object-fit: cover;
  background: #f5f5f5;
}
.tag {
  display: inline-block;
  padding: 2px 9px;
  border-radius: 10px;
  font-size: 11px;
}
.tag-on {
  background: #e8f8ee;
  color: #1a7f37;
}
.tag-off {
  background: #f4f5f7;
  color: #888;
}

/* ===== 快捷入口 ===== */
.quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 14px;
}
.quick-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 18px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  text-decoration: none;
  color: #333;
  transition: transform 0.15s, box-shadow 0.15s;
}
.quick-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.08);
}
.quick-icon {
  font-size: 22px;
}
.quick-title {
  font-size: 14px;
  font-weight: bold;
}
.quick-desc {
  margin-top: 3px;
  font-size: 12px;
  color: #999;
}
</style>
