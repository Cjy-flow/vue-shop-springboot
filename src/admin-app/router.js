import { createRouter, createWebHashHistory } from 'vue-router'

import AdminLogin from './views/AdminLogin.vue'
import AdminHome from './views/AdminHome.vue'
import GoodsManage from './views/GoodsManage.vue'
import CateManage from './views/CateManage.vue'

/**
 * 后台独立应用的路由。
 *
 * 用 hash 模式（地址里带 #）的原因：
 *   后台是一个独立静态页面 /admin/，刷新 /admin/#/goods 时服务器只会收到 /admin/，
 *   不需要任何 Nginx rewrite 规则就能正常打开，部署到任何静态托管都不会 404。
 */
const routes = [
  { path: '/', redirect: '/home' },
  { path: '/login', component: AdminLogin },
  { path: '/home', component: AdminHome },
  { path: '/goods', component: GoodsManage },
  { path: '/category', component: CateManage },
  { path: '/:pathMatch(.*)*', redirect: '/home' }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

// 登录守卫：没有令牌只能停在登录页；已登录就不必再看登录页
router.beforeEach((to) => {
  const token = localStorage.getItem('adminToken')
  if (to.path !== '/login' && !token) {
    return { path: '/login' }
  }
  if (to.path === '/login' && token) {
    return { path: '/home' }
  }
  return true
})

export default router
