import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import { initGoods } from '@/data/goods'

// 关键：必须正确引入 Vant 样式
import 'vant/lib/index.css'

// 一次性引入所有需要的组件，避免重复声明
import {
  Tabbar, TabbarItem, Icon,
  List, Cell, CellGroup, Swipe, SwipeItem,
  Search, Button, NavBar, Checkbox, CheckboxGroup, Stepper,
  ActionBar, ActionBarIcon, ActionBarButton
} from 'vant'

const app = createApp(App)
app.use(router)
app.use(store)

// 逐个注册组件，确保每个都只注册一次
app.use(Tabbar)
app.use(TabbarItem)
app.use(Icon) // 必须注册，图标才能显示
app.use(List)
app.use(Cell)
app.use(CellGroup)
app.use(Swipe)
app.use(SwipeItem)
app.use(Search)
app.use(Button)
app.use(NavBar)
app.use(Checkbox)
app.use(CheckboxGroup)
app.use(Stepper)
app.use(ActionBar)
app.use(ActionBarIcon)
app.use(ActionBarButton)

// 启动时若已登录，先把该账号的购物车/订单/消息读回 store，
// 否则刷新页面后 state 被重置为空，购物车会「看起来丢了」。
const currentUser = localStorage.getItem('loginUser')
if (currentUser) store.commit('INIT_USER_DATA', currentUser)

// 先向后端拉一次商品数据再挂载，避免首页先闪一下空列表。
// 后端没启动时不会卡死：catch 住错误照常挂载，页面顶部会提示「无法连接后端」。
initGoods()
  .catch((err) => {
    console.error('[启动] 商品数据加载失败：', err.message)
  })
  .finally(() => {
    app.mount('#app')
  })
