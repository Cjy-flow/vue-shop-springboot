<template>
  <div class="home">
    <!-- 后端未启动/断网时提示，避免页面空白让人摸不着头脑 -->
    <div class="api-alert" v-if="goodsError">
      <span>⚠️ {{ goodsError }}</span>
      <button type="button" @click="reload">重试</button>
    </div>
    <!-- 顶部搜索栏 -->
    <div class="search-bar">
      <div class="search-box">
        <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
             stroke-width="2" stroke-linecap="round">
          <circle cx="11" cy="11" r="7"></circle>
          <line x1="16.5" y1="16.5" x2="21" y2="21"></line>
        </svg>
        <input
          ref="searchInput"
          v-model="keyword"
          class="search-input"
          type="text"
          inputmode="search"
          placeholder="搜索商品 / 分类，如 苹果、肉禽蛋"
          @keyup.enter="doSearch"
          @keydown.esc="clearSearch"
        />
        <button v-if="keyword" class="search-clear" type="button" aria-label="清空"
                @click="clearSearch">×</button>
        <button class="search-btn" type="button" @click="doSearch">搜索</button>
      </div>

      <!-- 搜索结果浮层：不卸载下方内容，轮播与事件监听不受影响 -->
      <div v-if="isSearching" class="search-dropdown">
        <div class="result-head">
          <span>“{{ keyword.trim() }}” 的搜索结果</span>
          <span class="result-count">{{ results.length }} 件</span>
        </div>

        <div v-if="results.length" class="result-list">
          <div class="result-item" v-for="item in results" :key="item.id"
               @click="goDetail(item.id, item.category)">
            <img :src="item.pic" :alt="item.name" />
            <div class="result-info">
              <div class="result-name">{{ item.name }}</div>
              <div class="result-cat">{{ item.category }}</div>
            </div>
            <span class="result-price">¥{{ item.price }}</span>
          </div>
        </div>

        <div v-else class="empty">
          <div class="empty-icon">🔍</div>
          <p class="empty-title">没有找到相关商品</p>
          <p class="empty-tip">换个关键词试试，或点下面的热门搜索</p>
          <div class="hot-words">
            <span v-for="w in hotWords" :key="w" class="hot-word"
                  @click="keyword = w">{{ w }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 轮播图 -->
    <div class="swiper-container" ref="swiperRef">
      <div class="swiper-wrapper" ref="wrapperRef">
        <div class="swiper-slide" v-for="(item, index) in banners" :key="index">
          <img :src="item" alt="" />
        </div>
      </div>
      <div class="swiper-pagination">
        <span class="dot" :class="{active: curIndex === i}" v-for="(_, i) in banners.length" :key="i"></span>
      </div>
    </div>

    <!-- 分类跳转按钮（轮播图下面、每日新鲜上面） -->
    <div class="category-btns">
      <div
        class="btn-item"
        v-for="item in btnList"
        :key="item.key"
        @click="goToCategory(item.key)"
      >
        <div class="btn-icon">{{ item.icon }}</div>
        <div class="btn-text">{{ item.name }}</div>
      </div>
    </div>

    <!-- 标题 -->
    <div class="title">每日新鲜</div>

    <!-- 商品列表 -->
    <div class="list">
      <div class="card" v-for="item in goods" :key="item.id" @click="goDetail(item.id, item.category)">
        <img :src="item.pic" class="pic" />
        <p class="name">{{ item.name }}</p>
        <p class="price">¥{{ item.price }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
const router = useRouter()

// 轮播图
import banner1 from '../../assets/images/banner1.jpg'
import banner2 from '../../assets/images/banner2.jpg'
import banner3 from '../../assets/images/banner3.jpg'

import { onSaleGoods, goodsError, refreshGoods } from '@/data/goods'

// 首页「每日新鲜」：展示全部在售商品
// 后台下架某商品后自动从首页消失，新增商品也会自动出现
const goods = computed(() => onSaleGoods.value)

// 连接失败后的重试（后端启动好了点一下即可，无需刷新整个页面）
const reload = async () => {
  try {
    await refreshGoods()
  } catch (e) {
    // 仍然失败时提示条保持原样
  }
}

// ================= 搜索 =================
const keyword = ref('')
const searchInput = ref(null)
const hotWords = ['苹果', '牛奶', '鸡蛋', '肉禽蛋', '海鲜水产']

const isSearching = computed(() => keyword.value.trim().length > 0)
const results = computed(() => {
  const kw = keyword.value.trim().toLowerCase()
  if (!kw) return []
  return onSaleGoods.value.filter(g =>
    `${g.name} ${g.category} ${g.alias} ${g.price}`.toLowerCase().includes(kw)
  )
})

const clearSearch = () => {
  keyword.value = ''
  searchInput.value?.focus()
}
const doSearch = () => {
  if (!keyword.value.trim()) {
    searchInput.value?.focus()
    return
  }
  searchInput.value?.blur()   // 移动端收起键盘，结果仍保留
}
const goDetail = (id, cat) => {
  keyword.value = ''
  router.push({ path: `/detail/${id}`, query: cat ? { cat } : {} })
}

// 分类按钮配置（名称和分类页标签对应）
const btnList = ref([
  { key: 'fruit', name: '新鲜水果', icon: '🍎' },
  { key: 'vegetable', name: '蔬菜', icon: '🥬' },
  { key: 'meat', name: '肉禽蛋', icon: '🍗' },
  { key: 'seafood', name: '海鲜水产', icon: '🦞' },
  { key: 'dairy', name: '乳品烘焙', icon: '🥛' },
])

// 跳转到分类页，并自动选中对应标签
const goToCategory = (key) => {
  router.push({
    path: '/category',
    query: { type: key }
  })
}

// 轮播图逻辑
const banners = [banner1, banner2, banner3]
const swiperRef = ref(null)
const wrapperRef = ref(null)
const curIndex = ref(0)
const startX = ref(0)
const moveX = ref(0)
const isDragging = ref(false)
let timer = null

const goTo = (index) => {
  curIndex.value = index
  const offset = -index * 100
  wrapperRef.value.style.transform = `translateX(${offset}%)`
}

const next = () => {
  const nextIndex = (curIndex.value + 1) % banners.length
  goTo(nextIndex)
}

const startAutoPlay = () => {
  timer = setInterval(() => {
    if (!isDragging.value) {
      next()
    }
  }, 5000)
}

const stopAutoPlay = () => {
  if (timer) clearInterval(timer)
}

const onTouchStart = (e) => {
  stopAutoPlay()
  isDragging.value = true
  startX.value = e.type.includes('mouse') ? e.clientX : e.touches[0].clientX
}

const onTouchMove = (e) => {
  if (!isDragging.value) return
  const x = e.type.includes('mouse') ? e.clientX : e.touches[0].clientX
  moveX.value = x - startX.value
  const offset = -curIndex.value * 100 + (moveX.value / swiperRef.value.offsetWidth) * 100
  wrapperRef.value.style.transform = `translateX(${offset}%)`
}

const onTouchEnd = () => {
  isDragging.value = false
  const threshold = 50
  if (moveX.value > threshold) {
    const prevIndex = (curIndex.value - 1 + banners.length) % banners.length
    goTo(prevIndex)
  } else if (moveX.value < -threshold) {
    next()
  } else {
    goTo(curIndex.value)
  }
  moveX.value = 0
  startAutoPlay()
}

onMounted(() => {
  if (swiperRef.value) {
    startAutoPlay()
    swiperRef.value.addEventListener('mousedown', onTouchStart)
    swiperRef.value.addEventListener('mousemove', onTouchMove)
    swiperRef.value.addEventListener('mouseup', onTouchEnd)
    swiperRef.value.addEventListener('mouseleave', onTouchEnd)
    swiperRef.value.addEventListener('touchstart', onTouchStart)
    swiperRef.value.addEventListener('touchmove', onTouchMove)
    swiperRef.value.addEventListener('touchend', onTouchEnd)
  }
})

onUnmounted(() => {
  stopAutoPlay()
  if (swiperRef.value) {
    swiperRef.value.removeEventListener('mousedown', onTouchStart)
    swiperRef.value.removeEventListener('mousemove', onTouchMove)
    swiperRef.value.removeEventListener('mouseup', onTouchEnd)
    swiperRef.value.removeEventListener('mouseleave', onTouchEnd)
    swiperRef.value.removeEventListener('touchstart', onTouchStart)
    swiperRef.value.removeEventListener('touchmove', onTouchMove)
    swiperRef.value.removeEventListener('touchend', onTouchEnd)
  }
})
</script>

<style scoped>
/* ============ 顶部搜索栏 ============ */
.search-bar {
  position: sticky;
  top: 0;
  z-index: 50;
  background: #fff;
  padding: 10px 12px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}
.search-box {
  display: flex;
  align-items: center;
  height: 38px;
  padding: 0 4px 0 12px;
  background: #f7f7f7;
  border: 1px solid #ffd233;
  border-radius: 19px;
}
.search-icon {
  width: 17px;
  height: 17px;
  flex-shrink: 0;
  color: #999;
}
.search-input {
  flex: 1;
  min-width: 0;
  height: 100%;
  padding: 0 8px;
  border: none;
  outline: none;
  background: transparent;
  font-size: 14px;
  color: #333;
}
.search-input::placeholder {
  color: #aaa;
}
.search-clear {
  flex-shrink: 0;
  width: 20px;
  height: 20px;
  margin-right: 6px;
  border: none;
  border-radius: 50%;
  background: #ddd;
  color: #fff;
  font-size: 15px;
  line-height: 18px;
  cursor: pointer;
}
.search-btn {
  flex-shrink: 0;
  height: 30px;
  padding: 0 15px;
  border: none;
  border-radius: 15px;
  background: #ffd233;
  color: #333;
  font-size: 13px;
  font-weight: bold;
  cursor: pointer;
}

/* 搜索结果浮层 */
.search-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  max-height: 60vh;
  overflow-y: auto;
  padding: 4px 12px 14px;
  background: #fff;
  border-top: 1px solid #f2f2f2;
  box-shadow: 0 10px 20px rgba(0, 0, 0, 0.12);
}
.result-head {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 2px;
  font-size: 13px;
  color: #666;
}
.result-count {
  color: #ee0a24;
  font-weight: bold;
}
.result-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 8px 2px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
}
.result-item:active {
  background: #fafafa;
}
.result-item img {
  width: 48px;
  height: 48px;
  flex-shrink: 0;
  border-radius: 6px;
  object-fit: cover;
}
.result-info {
  flex: 1;
  min-width: 0;
}
.result-name {
  font-size: 14px;
  color: #333;
}
.result-cat {
  margin-top: 3px;
  font-size: 11px;
  color: #999;
}
.result-price {
  flex-shrink: 0;
  color: #ee0a24;
  font-size: 14px;
  font-weight: bold;
}
.empty {
  padding: 24px 0 8px;
  text-align: center;
}
.empty-icon {
  font-size: 30px;
}
.empty-title {
  margin-top: 8px;
  font-size: 14px;
  color: #333;
}
.empty-tip {
  margin-top: 4px;
  font-size: 12px;
  color: #999;
}
.hot-words {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 8px;
  margin-top: 14px;
}
.hot-word {
  padding: 5px 12px;
  border-radius: 14px;
  background: #f5f5f5;
  color: #666;
  font-size: 12px;
  cursor: pointer;
}

/* ============ 以下为原有样式，未改动 ============ */
.swiper-container {
  width: 100%;
  height: 180px;
  overflow: hidden;
  position: relative;
  margin-bottom: 10px;
  cursor: grab;
}
.swiper-wrapper {
  display: flex;
  width: 100%;
  height: 100%;
  transition: transform 0.3s ease;
}
.swiper-slide {
  flex-shrink: 0;
  width: 100%;
  height: 100%;
}
.swiper-slide img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  pointer-events: none;
}
.swiper-pagination {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 8px;
}
.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255,255,255,0.5);
}
.dot.active {
  background: #fff;
}

.title {
  padding:10px;
  font-weight:bold;
}

.list {
  display:flex;
  flex-wrap:wrap;
  gap:10px;
  padding:10px;
}
.card {
  width:47%;
  background:#fff;
  border-radius:8px;
  overflow:hidden;
  cursor: pointer;
}
.pic {
  width:100%;
  height:160px;
  object-fit:cover;
}
.name {
  padding:8px;
  font-size:14px;
}
.price {
  padding:0 8px 10px;
  color:red;
  font-weight:bold;
}

.category-btns {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  background: #fff;
  padding: 12px 0;
  margin-bottom: 10px;
}
.btn-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 12px;
  color: #333;
  cursor: pointer;
}
.btn-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  margin-bottom: 4px;
}
.btn-text {
  font-size: 12px;
}

/* 后端未连接时的提示条 */
.api-alert {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  margin: 8px 12px;
  padding: 10px 12px;
  border: 1px solid #ffe0b2;
  border-radius: 8px;
  background: #fff8e6;
  color: #a15c00;
  font-size: 12px;
  line-height: 1.5;
}
.api-alert button {
  flex-shrink: 0;
  padding: 4px 12px;
  border: 1px solid #ffc107;
  border-radius: 6px;
  background: #fff;
  color: #a15c00;
  font-size: 12px;
  cursor: pointer;
}
</style>
