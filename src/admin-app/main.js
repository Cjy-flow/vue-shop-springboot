/**
 * 后台管理系统入口（独立应用，与前台商城互不影响）
 *
 * 访问地址：http://localhost:5180/admin/
 * 前台地址：http://localhost:5180/
 *
 * 两个应用共用同一套后端接口和同一个数据层（src/data/goods.js），
 * 但各自打包、各自路由，前台的包体里不含后台的任何代码。
 */
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import { initGoods } from '@/data/goods'
import './admin.css'

const app = createApp(App)
app.use(router)

// 进来先把商品数据拉一遍，表格不会先空一下
initGoods()
  .catch((err) => {
    console.error('[后台] 商品数据加载失败：', err.message)
  })
  .finally(() => {
    app.mount('#app')
  })
