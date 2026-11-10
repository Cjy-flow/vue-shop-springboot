/**
 * 统一 HTTP 请求封装（基于原生 fetch，不额外引 axios）。
 *
 * 职责：
 *   1. 拼 baseURL —— 开发环境走 Vite 代理，所以默认就是 /api
 *   2. 自动带上后台登录令牌 Authorization: Bearer xxx
 *   3. 把后端的错误 JSON 转成带中文 message 的 ApiError，页面直接展示
 *   4. 遇到 401 自动清理本地登录态并跳回登录页
 */

const BASE_URL = (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/$/, '')

export class ApiError extends Error {
  constructor(message, status = 0, payload = null) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.payload = payload
  }
}

const getToken = () => localStorage.getItem('adminToken') || ''

const buildQuery = (params) => {
  if (!params) return ''
  const pairs = Object.entries(params).filter(
    ([, v]) => v !== undefined && v !== null && v !== ''
  )
  if (pairs.length === 0) return ''
  return '?' + pairs.map(([k, v]) => `${encodeURIComponent(k)}=${encodeURIComponent(v)}`).join('&')
}

/** 令牌失效：清掉本地登录态，把用户送回后台登录页 */
const handleUnauthorized = () => {
  localStorage.removeItem('adminToken')
  localStorage.removeItem('adminUser')
  // 只有当前确实在后台页面时才跳转，前台浏览商品不会被打断
  if (window.location.pathname.startsWith('/admin')) {
    window.location.assign('/admin/#/login')
  }
}

export async function request(path, { method = 'GET', body, params } = {}) {
  const url = BASE_URL + path + buildQuery(params)

  const headers = {}
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  const token = getToken()
  if (token) headers.Authorization = `Bearer ${token}`

  let response
  try {
    response = await fetch(url, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body)
    })
  } catch (e) {
    // 后端没启动、端口不对、断网都会走到这里
    throw new ApiError('无法连接后端服务，请确认 Spring Boot 已启动（http://localhost:8090）', 0)
  }

  if (response.status === 204) return null

  const text = await response.text()
  let data = null
  if (text) {
    try {
      data = JSON.parse(text)
    } catch (e) {
      data = text
    }
  }

  if (!response.ok) {
    if (response.status === 401) handleUnauthorized()
    const message = (data && data.message) || `请求失败（HTTP ${response.status}）`
    throw new ApiError(message, response.status, data)
  }

  return data
}

export const get = (path, params) => request(path, { params })
export const post = (path, body) => request(path, { method: 'POST', body })
export const put = (path, body) => request(path, { method: 'PUT', body })
export const del = (path) => request(path, { method: 'DELETE' })
