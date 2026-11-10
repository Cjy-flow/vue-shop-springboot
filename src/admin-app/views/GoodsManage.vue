<template>
  <AdminLayout title="商品管理">
    <!-- 工具条 -->
    <section class="toolbar">
      <div class="toolbar-left">
        <div class="search-wrap">
          <svg class="search-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor"
               stroke-width="2" stroke-linecap="round">
            <circle cx="11" cy="11" r="7"></circle>
            <line x1="16.5" y1="16.5" x2="21" y2="21"></line>
          </svg>
          <input v-model="keyword" class="search-input" type="text" placeholder="搜索商品名称 / 分类 / 别名" />
          <button v-if="keyword" class="clear-btn" type="button" @click="keyword = ''">×</button>
        </div>

        <select v-model="filterCat" class="select">
          <option value="">全部分类</option>
          <option v-for="c in CATEGORY_NAMES" :key="c" :value="c">{{ c }}</option>
        </select>
      </div>

      <div class="toolbar-right">
        <button class="btn btn-ghost" type="button" @click="handleReset">恢复默认商品</button>
        <button class="btn btn-primary" type="button" @click="openAdd">+ 新增商品</button>
      </div>
    </section>

    <!-- 列表 -->
    <section class="panel">
      <div class="panel-head">
        <h3>商品列表 <span class="count">共 {{ filtered.length }} 件</span></h3>
      </div>

      <div class="table-wrap">
        <table class="table">
          <thead>
            <tr>
              <th style="width: 56px;">ID</th>
              <th>商品信息</th>
              <th style="width: 110px;">分类</th>
              <th style="width: 90px;">价格</th>
              <th style="width: 80px;">库存</th>
              <th style="width: 84px;">状态</th>
              <th style="width: 130px;">操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="g in filtered" :key="g.id">
              <td class="muted">{{ g.id }}</td>
              <td>
                <div class="goods-cell">
                  <img :src="g.pic" :alt="g.name" />
                  <div>
                    <div class="goods-name">{{ g.name }}</div>
                    <div class="goods-alias">{{ g.alias || '—' }}</div>
                  </div>
                </div>
              </td>
              <td class="muted">{{ g.category }}</td>
              <td class="price">¥{{ g.price }}</td>
              <td :class="{ low: g.stock < 50 }">{{ g.stock }}</td>
              <td>
                <button
                  class="tag"
                  :class="g.onSale ? 'tag-on' : 'tag-off'"
                  type="button"
                  :title="g.onSale ? '点击下架' : '点击上架'"
                  @click="toggleSale(g)"
                >
                  {{ g.onSale ? '在售' : '已下架' }}
                </button>
              </td>
              <td>
                <div class="op-cell">
                  <button class="op-btn" type="button" @click="openEdit(g)">编辑</button>
                  <button class="op-btn danger" type="button" @click="askDelete(g)">删除</button>
                </div>
              </td>
            </tr>
            <tr v-if="filtered.length === 0">
              <td colspan="7" class="empty">
                没有匹配的商品，换个关键词或
                <button class="empty-link" type="button" @click="openAdd">新增一件</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <!-- 新增 / 编辑弹窗 -->
    <div class="modal-mask" v-if="showForm" @click.self="closeForm">
      <div class="modal">
        <div class="modal-head">
          <h3>{{ editingId ? '编辑商品' : '新增商品' }}</h3>
          <button class="modal-close" type="button" @click="closeForm">×</button>
        </div>

        <div class="modal-body">
          <div class="form-row">
            <label>商品名称 <i>*</i></label>
            <input v-model.trim="form.name" type="text" placeholder="如：红富士苹果" maxlength="20" />
          </div>

          <div class="form-row two-col">
            <div>
              <label>分类 <i>*</i></label>
              <select v-model="form.category">
                <option v-for="c in CATEGORY_NAMES" :key="c" :value="c">{{ c }}</option>
              </select>
            </div>
            <div>
              <label>价格（元） <i>*</i></label>
              <input v-model="form.price" type="number" min="0" step="0.1" placeholder="0.00" />
            </div>
          </div>

          <div class="form-row two-col">
            <div>
              <label>库存</label>
              <input v-model="form.stock" type="number" min="0" step="1" placeholder="0" />
            </div>
            <div>
              <label>搜索别名</label>
              <input v-model.trim="form.alias" type="text" placeholder="如：苹果 水果" maxlength="30" />
            </div>
          </div>

          <div class="form-row">
            <label>商品图片</label>
            <div class="pic-grid">
              <button
                v-for="p in PIC_OPTIONS"
                :key="p.key"
                class="pic-item"
                :class="{ active: form.picKey === p.key }"
                type="button"
                @click="form.picKey = p.key"
              >
                <img :src="p.url" :alt="p.label" />
                <span>{{ p.label }}</span>
              </button>
            </div>
          </div>

          <div class="form-row">
            <label class="switch-row">
              <input v-model="form.onSale" type="checkbox" />
              <span>上架销售（取消勾选则前台不展示）</span>
            </label>
          </div>

          <p class="form-error" v-if="formError">{{ formError }}</p>
        </div>

        <div class="modal-foot">
          <button class="btn btn-ghost" type="button" @click="closeForm">取消</button>
          <button class="btn btn-primary" type="button" :disabled="submitting" @click="submitForm">
            {{ submitting ? '保存中…' : '保存' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 删除确认 -->
    <div class="modal-mask" v-if="deleteTarget" @click.self="cancelDelete">
      <div class="modal modal-sm">
        <div class="modal-head">
          <h3>确认删除</h3>
          <button class="modal-close" type="button" @click="cancelDelete">×</button>
        </div>
        <div class="modal-body">
          <p class="confirm-text">
            确定删除商品「<b>{{ deleteTarget.name }}</b>」吗？此操作会同时把它从首页和分类页移除。
          </p>
        </div>
        <div class="modal-foot">
          <button class="btn btn-ghost" type="button" @click="cancelDelete">取消</button>
          <button class="btn btn-danger" type="button" @click="confirmDelete">确认删除</button>
        </div>
      </div>
    </div>

    <!-- 轻提示 -->
    <transition name="toast">
      <div class="toast" v-if="toast">{{ toast }}</div>
    </transition>
  </AdminLayout>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import AdminLayout from './AdminLayout.vue'
import {
  allGoods, CATEGORY_NAMES, PIC_OPTIONS,
  addGoods, updateGoods, removeGoods, resetGoods, refreshGoods
} from '@/data/goods'

// ===== 筛选 =====
const keyword = ref('')
const filterCat = ref('')

const filtered = computed(() => {
  const kw = keyword.value.trim().toLowerCase()
  return allGoods.value.filter(g => {
    const hitKw = !kw || `${g.name} ${g.category} ${g.alias}`.toLowerCase().includes(kw)
    const hitCat = !filterCat.value || g.category === filterCat.value
    return hitKw && hitCat
  })
})

// ===== 轻提示 =====
const toast = ref('')
let toastTimer = null
const showToast = (msg) => {
  toast.value = msg
  clearTimeout(toastTimer)
  toastTimer = setTimeout(() => { toast.value = '' }, 2000)
}

// 进入页面时跟后端对齐一次，避免别处改过数据后这里还是旧缓存
onMounted(() => {
  refreshGoods().catch(e => showToast(e.message))
})

// ===== 新增 / 编辑 =====
const showForm = ref(false)
const editingId = ref(null)
const formError = ref('')

const emptyForm = () => ({
  name: '',
  price: '',
  category: CATEGORY_NAMES[0],
  alias: '',
  stock: '',
  picKey: 'apple',
  onSale: true
})

const form = reactive(emptyForm())

const openAdd = () => {
  Object.assign(form, emptyForm())
  editingId.value = null
  formError.value = ''
  showForm.value = true
}

const openEdit = (g) => {
  Object.assign(form, {
    name: g.name,
    price: g.price,
    category: g.category,
    alias: g.alias,
    stock: g.stock,
    picKey: g.picKey,
    onSale: g.onSale
  })
  editingId.value = g.id
  formError.value = ''
  showForm.value = true
}

const closeForm = () => {
  showForm.value = false
  editingId.value = null
  formError.value = ''
}

const submitting = ref(false)

const submitForm = async () => {
  formError.value = ''

  if (!form.name) {
    formError.value = '请填写商品名称'
    return
  }
  const price = Number(form.price)
  if (form.price === '' || isNaN(price) || price < 0) {
    formError.value = '请填写有效的价格（不能为负数）'
    return
  }
  const stock = Number(form.stock)
  if (form.stock !== '' && (isNaN(stock) || stock < 0)) {
    formError.value = '库存必须是不小于 0 的数字'
    return
  }

  const payload = {
    name: form.name,
    price,
    category: form.category,
    alias: form.alias,
    stock: form.stock === '' ? 0 : stock,
    picKey: form.picKey,
    onSale: form.onSale
  }

  submitting.value = true
  try {
    // 提交给后端，成功后本地缓存同步更新，前台首页立刻能看到
    if (editingId.value) {
      await updateGoods(editingId.value, payload)
      showToast('已保存修改')
    } else {
      await addGoods(payload)
      showToast('商品已新增，前台首页可见')
    }
    closeForm()
  } catch (e) {
    // 后端校验失败（重名、分类非法等）会带中文提示，直接显示在表单里
    formError.value = e.message || '保存失败，请稍后重试'
  } finally {
    submitting.value = false
  }
}

// ===== 上下架 =====
const toggleSale = async (g) => {
  try {
    await updateGoods(g.id, { onSale: !g.onSale })
    showToast(g.onSale ? `「${g.name}」已下架` : `「${g.name}」已上架`)
  } catch (e) {
    showToast(e.message)
  }
}

// ===== 删除 =====
const deleteTarget = ref(null)
const askDelete = (g) => { deleteTarget.value = g }
const cancelDelete = () => { deleteTarget.value = null }
const confirmDelete = async () => {
  const target = deleteTarget.value
  try {
    await removeGoods(target.id)
    deleteTarget.value = null
    showToast(`已删除「${target.name}」`)
  } catch (e) {
    deleteTarget.value = null
    showToast(e.message)
  }
}

// ===== 恢复默认 =====
const handleReset = async () => {
  if (!window.confirm('将把商品恢复为初始的 12 件，你新增和修改的内容会丢失，确定继续？')) return
  try {
    await resetGoods()
    keyword.value = ''
    filterCat.value = ''
    showToast('已恢复默认商品')
  } catch (e) {
    showToast(e.message)
  }
}
</script>

<style scoped>
/* ===== 工具条 ===== */
.toolbar {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 16px;
}
.toolbar-left,
.toolbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
}
.search-wrap {
  position: relative;
  display: flex;
  align-items: center;
}
.search-icon {
  position: absolute;
  left: 11px;
  width: 15px;
  height: 15px;
  color: #aaa;
  pointer-events: none;
}
.search-input {
  width: 260px;
  height: 36px;
  padding: 0 30px 0 33px;
  border: 1px solid #e2e5ea;
  border-radius: 8px;
  background: #fff;
  font-size: 13px;
  outline: none;
  box-sizing: border-box;
}
.search-input:focus {
  border-color: #ffc107;
}
.clear-btn {
  position: absolute;
  right: 9px;
  width: 18px;
  height: 18px;
  border: none;
  border-radius: 50%;
  background: #ddd;
  color: #fff;
  font-size: 13px;
  line-height: 16px;
  cursor: pointer;
}
.select {
  height: 36px;
  padding: 0 10px;
  border: 1px solid #e2e5ea;
  border-radius: 8px;
  background: #fff;
  font-size: 13px;
  color: #333;
  outline: none;
  cursor: pointer;
}
.btn {
  height: 36px;
  padding: 0 16px;
  border-radius: 8px;
  border: 1px solid transparent;
  font-size: 13px;
  cursor: pointer;
  transition: background 0.15s, border-color 0.15s;
}
.btn-primary {
  background: #ffc107;
  color: #3d3200;
  font-weight: bold;
}
.btn-primary:hover {
  background: #ffb300;
}
.btn-ghost {
  background: #fff;
  border-color: #e2e5ea;
  color: #555;
}
.btn-ghost:hover {
  border-color: #c8ccd4;
}
.btn-danger {
  background: #ff4d4f;
  color: #fff;
  font-weight: bold;
}
.btn-danger:hover {
  background: #e63c3e;
}

/* ===== 列表 ===== */
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
.count {
  margin-left: 6px;
  font-size: 12px;
  font-weight: normal;
  color: #999;
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
  padding: 12px 14px;
  border-top: 1px solid #f4f6f8;
  vertical-align: middle;
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
  gap: 10px;
}
.goods-cell img {
  width: 42px;
  height: 42px;
  flex-shrink: 0;
  border-radius: 6px;
  object-fit: cover;
  background: #f5f5f5;
}
.goods-name {
  font-size: 13px;
}
.goods-alias {
  margin-top: 3px;
  font-size: 11px;
  color: #aaa;
}
.tag {
  padding: 3px 10px;
  border: none;
  border-radius: 10px;
  font-size: 11px;
  cursor: pointer;
}
.tag-on {
  background: #e8f8ee;
  color: #1a7f37;
}
.tag-off {
  background: #f4f5f7;
  color: #888;
}
.op-cell {
  display: flex;
  gap: 6px;
}
.op-btn {
  padding: 4px 10px;
  border: 1px solid #e2e5ea;
  border-radius: 6px;
  background: #fff;
  color: #555;
  font-size: 12px;
  cursor: pointer;
}
.op-btn:hover {
  border-color: #ffc107;
  color: #d48806;
}
.op-btn.danger:hover {
  border-color: #ff4d4f;
  color: #ff4d4f;
}
.empty {
  padding: 40px 0 !important;
  text-align: center;
  color: #999;
}
.empty-link {
  border: none;
  background: none;
  color: #d48806;
  font-size: 13px;
  cursor: pointer;
  text-decoration: underline;
}

/* ===== 弹窗 ===== */
.modal-mask {
  position: fixed;
  inset: 0;
  z-index: 200;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: rgba(0, 0, 0, 0.45);
}
.modal {
  width: 100%;
  max-width: 560px;
  max-height: 88vh;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 50px rgba(0, 0, 0, 0.25);
}
.modal-sm {
  max-width: 400px;
}
.modal-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f2f5;
}
.modal-head h3 {
  margin: 0;
  font-size: 15px;
}
.modal-close {
  width: 26px;
  height: 26px;
  border: none;
  border-radius: 50%;
  background: #f4f5f7;
  color: #888;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
}
.modal-body {
  padding: 18px 20px;
  overflow-y: auto;
}
.modal-foot {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 14px 20px;
  border-top: 1px solid #f0f2f5;
  background: #fafbfc;
}

/* ===== 表单 ===== */
.form-row {
  margin-bottom: 14px;
}
.form-row.two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px;
}
.form-row label {
  display: block;
  margin-bottom: 6px;
  font-size: 12px;
  color: #666;
}
.form-row label i {
  color: #ff4d4f;
  font-style: normal;
}
.form-row input[type="text"],
.form-row input[type="number"],
.form-row select {
  width: 100%;
  height: 36px;
  padding: 0 10px;
  border: 1px solid #e2e5ea;
  border-radius: 8px;
  font-size: 13px;
  outline: none;
  box-sizing: border-box;
}
.form-row input:focus,
.form-row select:focus {
  border-color: #ffc107;
}
.switch-row {
  display: flex !important;
  align-items: center;
  gap: 8px;
  font-size: 13px !important;
  color: #333 !important;
  cursor: pointer;
}
.switch-row input {
  width: 16px;
  height: 16px;
  accent-color: #ffc107;
}
.pic-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 8px;
}
.pic-item {
  padding: 4px;
  border: 2px solid transparent;
  border-radius: 8px;
  background: #f7f8fa;
  cursor: pointer;
  transition: border-color 0.15s;
}
.pic-item img {
  width: 100%;
  height: 44px;
  border-radius: 5px;
  object-fit: cover;
  display: block;
}
.pic-item span {
  display: block;
  margin-top: 3px;
  font-size: 10px;
  color: #888;
}
.pic-item.active {
  border-color: #ffc107;
  background: #fffbe8;
}
.form-error {
  margin: 4px 0 0;
  padding: 8px 10px;
  border-radius: 6px;
  background: #fff1f0;
  color: #d4380d;
  font-size: 12px;
}
.confirm-text {
  margin: 0;
  font-size: 13px;
  line-height: 1.8;
  color: #555;
}
.confirm-text b {
  color: #333;
}

/* ===== Toast ===== */
.toast {
  position: fixed;
  top: 24px;
  left: 50%;
  transform: translateX(-50%);
  z-index: 300;
  padding: 10px 20px;
  border-radius: 8px;
  background: rgba(32, 38, 50, 0.92);
  color: #fff;
  font-size: 13px;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}
.toast-enter-active,
.toast-leave-active {
  transition: opacity 0.2s, transform 0.2s;
}
.toast-enter-from,
.toast-leave-to {
  opacity: 0;
  transform: translateX(-50%) translateY(-8px);
}

@media (max-width: 820px) {
  .search-input {
    width: 160px;
  }
  .pic-grid {
    grid-template-columns: repeat(4, 1fr);
  }
  .form-row.two-col {
    grid-template-columns: 1fr;
  }
}
</style>
