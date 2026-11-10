import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

// Spring Boot 后端地址（server/ 目录，端口 8090）
const BACKEND_URL = 'http://localhost:8090'

// 开发时把 /api 转发给后端，浏览器看到的是同源请求，天然没有跨域问题
const proxy = {
  '/api': {
    target: BACKEND_URL,
    changeOrigin: true
  }
}

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src')
    }
  },
  server: {
    // 5173 常被其他项目占用，这里固定 5180，配合后端 8090
    port: 5180,
    proxy
  },
  preview: {
    port: 4180,
    proxy
  },
  build: {
    rollupOptions: {
      // 两个入口分别打包：
      //   index.html        前台商城  →  访问 /
      //   admin/index.html  后台管理  →  访问 /admin/
      input: {
        main: resolve(__dirname, 'index.html'),
        admin: resolve(__dirname, 'admin/index.html')
      }
    }
  }
})
