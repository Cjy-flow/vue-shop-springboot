import { createRouter, createWebHistory } from 'vue-router'

// 底部导航分包页面
import Home from '../views/home/Home.vue'
import Category from '../views/category/Category.vue'
import Cart from '../views/cart/Cart.vue'
import Message from '../views/message/Message.vue'
import Mine from '../views/mine/Mine.vue'

// 公共通用分包页面（位于 components 文件夹）
import Detail from '../components/Detail.vue'
import OrderList from '../components/OrderList.vue'
import OrderDetail from '../components/OrderDetail.vue'
import Collect from '../components/Collect.vue'
import AddressManage from '../components/AddressManage.vue'
import AccountSetting from '../components/AccountSetting.vue'

/**
 * 这里只放前台商城的路由。
 * 后台管理系统已拆成独立应用，入口是 admin/index.html（地址 /admin/），
 * 不再出现在前台的代码和路由表里。
 */
const routes = [
  { path: '/', redirect: '/home' },
  { path: '/home', component: Home },
  { path: '/category', component: Category },
  { path: '/detail/:id', component: Detail },
  { path: '/cart', component: Cart },
  { path: '/message', component: Message },
  { path: '/mine', component: Mine },
  { path: '/order-list', component: OrderList },
  { path: '/order-detail/:id', component: OrderDetail },
  { path: '/collect', component: Collect },
  { path: '/address-manage', component: AddressManage },
  { path: '/account-setting', component: AccountSetting },
  { path: '/:pathMatch(.*)*', redirect: '/home' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
