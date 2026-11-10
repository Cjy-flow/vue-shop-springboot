import { createStore } from 'vuex'

export default createStore({
  state: {
    cartList: [],
    orderList: [],
    // 🔥 新增：消息系统
    messages: [],       // 消息列表
    unreadCount: 0      // 未读消息数量（控制角标）
  },
  mutations: {
    // 初始化加载当前用户数据（登录时调用）
    INIT_USER_DATA(state, username) {
      // 购物车
      const cart = localStorage.getItem(`cart_${username}`)
      state.cartList = cart ? JSON.parse(cart) : []
      // 订单
      const order = localStorage.getItem(`order_${username}`)
      state.orderList = order ? JSON.parse(order) : []
      // 🔥 新增：消息初始化
      const msg = localStorage.getItem(`messages_${username}`)
      state.messages = msg ? JSON.parse(msg) : []
      // 计算未读数量
      state.unreadCount = state.messages.filter(m => !m.read).length
    },

    // 用本地数据直接覆盖购物车（购物车页 onMounted 恢复时调用）
    SET_CART(state, list) {
      state.cartList = Array.isArray(list) ? list : []
    },

    // 🔥 新增：创建订单消息
    ADD_ORDER_MESSAGE(state, order) {
      const user = localStorage.getItem('loginUser')
      if (!user) return

      const msg = {
        id: Date.now(),
        orderId: order.id,
        content: `订单 ${order.id} 已提交成功，点击查看详情`,
        time: new Date().toLocaleString(),
        read: false
      }

      state.messages.unshift(msg)
      state.unreadCount++

      // 保存到用户本地
      localStorage.setItem(`messages_${user}`, JSON.stringify(state.messages))
    },

    // 🔥 新增：标记消息为已读
    READ_MESSAGE(state, msgId) {
      const user = localStorage.getItem('loginUser')
      if (!user) return

      const msg = state.messages.find(m => m.id === msgId)
      if (msg && !msg.read) {
        msg.read = true
        state.unreadCount--
        localStorage.setItem(`messages_${user}`, JSON.stringify(state.messages))
      }
    },

    // ==================== 原有逻辑不动 ====================
    ADD_TO_CART(state, item) {
      const exist = state.cartList.find(i => i.id === item.id)
      if (exist) {
        exist.count++
      } else {
        state.cartList.push({ ...item, count: 1 })
      }
      const user = localStorage.getItem('loginUser')
      if (user) localStorage.setItem(`cart_${user}`, JSON.stringify(state.cartList))
    },

    DEL_CART(state, id) {
      state.cartList = state.cartList.filter(i => i.id !== id)
      const user = localStorage.getItem('loginUser')
      if (user) localStorage.setItem(`cart_${user}`, JSON.stringify(state.cartList))
    },

    CHANGE_COUNT(state, { id, count }) {
      const item = state.cartList.find(i => i.id === id)
      if (item) {
        item.count = count
      }
      const user = localStorage.getItem('loginUser')
      if (user) localStorage.setItem(`cart_${user}`, JSON.stringify(state.cartList))
    },

    CREATE_ORDER(state, selectedIds) {
      const goods = state.cartList.filter(item => selectedIds.includes(item.id))
      if (goods.length === 0) return

      const totalPrice = goods.reduce((sum, item) => sum + item.price * item.count, 0)

      const order = {
        id: Date.now(),
        goods,
        totalPrice,
        time: new Date().toLocaleString(),
        status: '已支付'
      }

      state.orderList.unshift(order)
      
      const user = localStorage.getItem('loginUser')
      if (user) {
        localStorage.setItem(`order_${user}`, JSON.stringify(state.orderList))
      }

      state.cartList = state.cartList.filter(item => !selectedIds.includes(item.id))
      if (user) {
        localStorage.setItem(`cart_${user}`, JSON.stringify(state.cartList))
      }
    }
  },
  actions: {
    // 🔥 新增：发送订单消息
    sendOrderMessage({ commit }, order) {
      commit('ADD_ORDER_MESSAGE', order)
    },
    // 🔥 新增：标记已读
    readMessage({ commit }, msgId) {
      commit('READ_MESSAGE', msgId)
    },

    // ==================== 原有逻辑不动 ====================
    addToCart({ commit }, item) {
      commit('ADD_TO_CART', item)
    },
    delCart({ commit }, id) {
      commit('DEL_CART', id)
    },
    changeCount({ commit }, payload) {
      commit('CHANGE_COUNT', payload)
    },
    createOrder({ commit }, selectedIds) {
      commit('CREATE_ORDER', selectedIds)
    }
  },
  getters: {
    totalPrice(state) {
      return state.cartList.reduce((sum, item) => sum + item.price * item.count, 0)
    },
    totalCount(state) {
      return state.cartList.reduce((sum, item) => sum + item.count, 0)
    },
    // 🔥 新增：获取未读消息数量
    unreadCount(state) {
      return state.unreadCount
    },
    // 🔥 新增：获取所有消息
    messageList(state) {
      return state.messages
    }
  }
})