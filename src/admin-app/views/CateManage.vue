<template>
  <AdminLayout title="分类管理">
    <p class="tip">
      分类由商品数据自动聚合，共 <b>{{ cateStats.length }}</b> 个分类、<b>{{ allGoods.length }}</b> 件商品。
      要调整商品所属分类，请到「商品管理」中编辑该商品。
    </p>

    <!-- 分类卡片 -->
    <section class="cate-grid">
      <div
        class="cate-card"
        v-for="c in cateStats"
        :key="c.name"
        :class="{ expanded: expanded === c.name }"
        @click="toggle(c.name)"
      >
        <header class="cate-head">
          <span class="cate-name">{{ c.name }}</span>
          <span class="cate-count">{{ c.count }} 件</span>
        </header>

        <div class="bar">
          <div class="bar-inner" :style="{ width: c.percent + '%' }"></div>
        </div>
        <div class="bar-label">占全站 {{ c.percent }}%</div>

        <ul class="cate-meta">
          <li><span>在售</span><b>{{ c.onSale }}</b></li>
          <li><span>库存合计</span><b>{{ c.stock }}</b></li>
          <li><span>平均价格</span><b>¥{{ c.avgPrice }}</b></li>
        </ul>

        <div class="expand-hint">{{ expanded === c.name ? '收起商品 ▲' : '查看商品 ▼' }}</div>

        <!-- 展开的商品明细 -->
        <div class="cate-goods" v-if="expanded === c.name" @click.stop>
          <div class="mini-goods" v-for="g in c.goods" :key="g.id">
            <img :src="g.pic" :alt="g.name" />
            <div class="mini-info">
              <div class="mini-name">
                {{ g.name }}
                <span v-if="!g.onSale" class="off-tag">已下架</span>
              </div>
              <div class="mini-sub">¥{{ g.price }} ｜ 库存 {{ g.stock }}</div>
            </div>
          </div>
          <div class="mini-empty" v-if="c.goods.length === 0">该分类下暂无商品</div>
        </div>
      </div>
    </section>

    <!-- 明细表 -->
    <section class="panel">
      <div class="panel-head">
        <h3>分类明细汇总</h3>
      </div>
      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th>分类</th>
              <th>商品数</th>
              <th>在售</th>
              <th>已下架</th>
              <th>库存合计</th>
              <th>最低价</th>
              <th>最高价</th>
              <th>平均价</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="c in cateStats" :key="c.name">
              <td class="cate-cell">{{ c.name }}</td>
              <td>{{ c.count }}</td>
              <td class="on">{{ c.onSale }}</td>
              <td class="muted">{{ c.count - c.onSale }}</td>
              <td>{{ c.stock }}</td>
              <td>{{ c.minPrice }}</td>
              <td>{{ c.maxPrice }}</td>
              <td class="price">¥{{ c.avgPrice }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </AdminLayout>
</template>

<script setup>
import { ref, computed } from 'vue'
import AdminLayout from './AdminLayout.vue'
import { allGoods, CATEGORY_NAMES } from '@/data/goods'

const expanded = ref('')
const toggle = (name) => {
  expanded.value = expanded.value === name ? '' : name
}

const cateStats = computed(() => {
  const total = allGoods.value.length || 1
  return CATEGORY_NAMES.map(name => {
    const goods = allGoods.value.filter(g => g.category === name)
    const prices = goods.map(g => Number(g.price) || 0)
    const stock = goods.reduce((sum, g) => sum + g.stock, 0)
    const avg = prices.length
      ? (prices.reduce((a, b) => a + b, 0) / prices.length).toFixed(1)
      : '0.0'
    const min = prices.length ? Math.min(...prices).toFixed(1) : '—'
    const max = prices.length ? Math.max(...prices).toFixed(1) : '—'
    return {
      name,
      goods,
      count: goods.length,
      onSale: goods.filter(g => g.onSale).length,
      stock,
      avgPrice: avg,
      minPrice: min === '—' ? '—' : '¥' + min,
      maxPrice: max === '—' ? '—' : '¥' + max,
      percent: Math.round((goods.length / total) * 100)
    }
  })
})
</script>

<style scoped>
.tip {
  margin: 0 0 16px;
  padding: 12px 16px;
  border-radius: 8px;
  background: #fffbe8;
  border: 1px solid #ffe9a8;
  font-size: 12px;
  color: #7a6400;
  line-height: 1.7;
}
.tip b {
  color: #d48806;
}

/* ===== 分类卡片 ===== */
.cate-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(232px, 1fr));
  gap: 14px;
  margin-bottom: 18px;
  align-items: start;
}
.cate-card {
  padding: 16px;
  background: #fff;
  border: 1px solid transparent;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: border-color 0.15s, box-shadow 0.15s;
}
.cate-card:hover {
  box-shadow: 0 4px 14px rgba(0, 0, 0, 0.08);
}
.cate-card.expanded {
  border-color: #ffd233;
}
.cate-head {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
}
.cate-name {
  font-size: 14px;
  font-weight: bold;
}
.cate-count {
  font-size: 12px;
  color: #999;
}
.bar {
  height: 6px;
  margin: 10px 0 5px;
  border-radius: 3px;
  background: #f0f2f5;
  overflow: hidden;
}
.bar-inner {
  height: 100%;
  border-radius: 3px;
  background: linear-gradient(90deg, #ffd233, #ffb300);
  transition: width 0.3s;
}
.bar-label {
  font-size: 11px;
  color: #aaa;
}
.cate-meta {
  display: flex;
  gap: 14px;
  margin: 12px 0 8px;
  padding: 0;
  list-style: none;
}
.cate-meta li {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.cate-meta span {
  font-size: 11px;
  color: #999;
}
.cate-meta b {
  font-size: 13px;
}
.expand-hint {
  padding-top: 8px;
  border-top: 1px dashed #f0f2f5;
  font-size: 11px;
  color: #d48806;
  text-align: center;
}

/* 展开的明细 */
.cate-goods {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #f4f6f8;
}
.mini-goods {
  display: flex;
  align-items: center;
  gap: 9px;
  padding: 6px 0;
}
.mini-goods img {
  width: 34px;
  height: 34px;
  flex-shrink: 0;
  border-radius: 6px;
  object-fit: cover;
  background: #f5f5f5;
}
.mini-name {
  font-size: 12px;
}
.off-tag {
  margin-left: 5px;
  padding: 1px 6px;
  border-radius: 8px;
  background: #f4f5f7;
  color: #999;
  font-size: 10px;
}
.mini-sub {
  margin-top: 2px;
  font-size: 11px;
  color: #999;
}
.mini-empty {
  padding: 10px 0;
  font-size: 12px;
  color: #bbb;
  text-align: center;
}

/* ===== 明细表 ===== */
.panel {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  overflow: hidden;
}
.panel-head {
  padding: 14px 18px;
  border-bottom: 1px solid #f0f2f5;
}
.panel-head h3 {
  margin: 0;
  font-size: 14px;
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
  font-size: 12px;
  font-weight: normal;
  color: #888;
  background: #fafbfc;
  white-space: nowrap;
}
.table td {
  padding: 11px 14px;
  border-top: 1px solid #f4f6f8;
  white-space: nowrap;
}
.table tbody tr:hover {
  background: #fbfcfd;
}
.cate-cell {
  font-weight: bold;
}
.muted {
  color: #999;
}
.on {
  color: #1a7f37;
  font-weight: bold;
}
.price {
  color: #ee0a24;
  font-weight: bold;
}
</style>
