/**
 * 全站商品统一数据源（后端接口版）
 *
 * 演进过程：
 *   1. 最初：首页/分类页/详情页各自硬编码一份商品数组，后台改不动前台
 *   2. 第二步：收敛到本文件，数据存 localStorage，实现"改一处、全站生效"
 *   3. 现在：数据存到 Spring Boot + H2 数据库，本文件变成「接口客户端 + 内存缓存」
 *
 * 页面代码几乎没动 —— 因为对外暴露的 allGoods / categories / goodsStats
 * 仍然是 computed，用法与之前完全一致，只是数据来源从浏览器换成了服务器。
 *
 * 图片：数据库只存 picKey（如 'apple'），渲染时映射成真实 URL，
 *      这样即使 Vite 构建后给图片加了 hash，已存的商品记录也不会失效。
 */
import { reactive, computed } from 'vue'
import { get, post, put, del } from '@/api/http'

// ===== 商品图片（整个项目只有这里 import 一次）=====
import lamb from '@/assets/images/lamb.jpg'
import pork from '@/assets/images/pork.jpg'
import lobster from '@/assets/images/lobster.jpg'
import apple from '@/assets/images/apple.jpg'
import tomato from '@/assets/images/tomato.jpg'
import milk from '@/assets/images/milk.jpg'
import bread from '@/assets/images/bread.jpg'
import yellowFish from '@/assets/images/yellowFish.jpg'
import potato from '@/assets/images/potato.jpg'
import banana from '@/assets/images/banana.jpg'
import egg from '@/assets/images/egg.jpg'
import chicken from '@/assets/images/chicken.jpg'

export const PIC_MAP = {
  lamb, pork, lobster, apple, tomato, milk,
  bread, yellowFish, potato, banana, egg, chicken
}

// 后台「新增商品」时可选的图片
export const PIC_OPTIONS = [
  { key: 'apple',      label: '苹果',   url: apple },
  { key: 'banana',     label: '香蕉',   url: banana },
  { key: 'tomato',     label: '番茄',   url: tomato },
  { key: 'potato',     label: '土豆',   url: potato },
  { key: 'lamb',       label: '羊排',   url: lamb },
  { key: 'pork',       label: '猪肉',   url: pork },
  { key: 'chicken',    label: '鸡肉',   url: chicken },
  { key: 'egg',        label: '鸡蛋',   url: egg },
  { key: 'lobster',    label: '龙虾',   url: lobster },
  { key: 'yellowFish', label: '黄鱼',   url: yellowFish },
  { key: 'milk',       label: '牛奶',   url: milk },
  { key: 'bread',      label: '面包',   url: bread }
]

// 商品分类（顺序与后端校验、分类页左侧菜单一致）
export const CATEGORY_NAMES = ['新鲜水果', '蔬菜', '肉禽蛋', '海鲜水产', '乳品烘焙']

// ===== 本地缓存状态 =====
// 商品数量很少（十几件），一次拉全量放内存，页面筛选不用来回请求
const state = reactive({
  list: [],
  loading: false,
  error: '',
  loaded: false
})

// 把接口返回的记录补成页面需要的形状（price 转数字、补图片 URL）
const decorate = (g) => ({
  ...g,
  price: Number(g.price) || 0,
  pic: PIC_MAP[g.picKey] || PIC_MAP.apple,
  alias: g.alias || '',
  stock: Number(g.stock) || 0,
  onSale: g.onSale !== false
})

// ===== 对外只读数据（用法与 localStorage 版本保持一致）=====
export const allGoods = computed(() => state.list.map(decorate))
export const onSaleGoods = computed(() => allGoods.value.filter(g => g.onSale))
export const categories = computed(() => CATEGORY_NAMES.map(name => ({
  name,
  goods: onSaleGoods.value.filter(g => g.category === name)
})))
export const getGoodsById = (id) => allGoods.value.find(g => g.id === Number(id))

/** 加载状态 / 错误信息，页面可用来展示骨架屏或报错提示 */
export const goodsLoading = computed(() => state.loading)
export const goodsError = computed(() => state.error)
export const goodsLoaded = computed(() => state.loaded)

// ===== 与后端同步 =====

/** 首次加载（main.js 启动时调用）；后台页面进入时也可再调一次刷新 */
export async function initGoods() {
  state.loading = true
  state.error = ''
  try {
    const list = await get('/goods')
    state.list = Array.isArray(list) ? list : []
    state.loaded = true
    return state.list
  } catch (e) {
    state.error = e.message
    throw e
  } finally {
    state.loading = false
  }
}

/** 别名，语义更清楚：后台点「刷新」时用 */
export const refreshGoods = initGoods

// ===== 后台增删改（都是异步请求，调用处需要 await）=====

export async function addGoods(data) {
  const created = await post('/goods', data)
  state.list.push(created)
  return decorate(created)
}

export async function updateGoods(id, data) {
  const updated = await put(`/goods/${id}`, data)
  const idx = state.list.findIndex(g => g.id === Number(id))
  if (idx >= 0) {
    state.list[idx] = updated
  } else {
    state.list.push(updated)
  }
  return true
}

export async function removeGoods(id) {
  await del(`/goods/${id}`)
  state.list = state.list.filter(g => g.id !== Number(id))
  return true
}

/** 恢复出厂 12 件商品（后台误删时用） */
export async function resetGoods() {
  const list = await post('/goods/reset')
  state.list = Array.isArray(list) ? list : []
  return state.list
}

/** 商品统计，供后台概览卡片使用（由本地缓存算出，无需再请求接口） */
export const goodsStats = computed(() => {
  const list = allGoods.value
  return {
    total: list.length,
    onSale: list.filter(g => g.onSale).length,
    offSale: list.filter(g => !g.onSale).length,
    lowStock: list.filter(g => g.stock < 50).length,
    categoryCount: new Set(list.map(g => g.category)).size
  }
})
