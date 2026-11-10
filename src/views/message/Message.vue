<template>
  <div class="message-page">
    <div class="msg-header">
      <h2>我的消息</h2>
    </div>

    <div class="msg-list">
      <div 
        class="msg-item"
        v-for="msg in messageList" 
        :key="msg.id"
        @click="goToOrder(msg)" 
      >
        <div class="msg-left">
          <div class="msg-title">订单通知</div>
          <div class="msg-content">{{ msg.content }}</div>
          <div class="msg-time">{{ msg.time }}</div>
        </div>
        <div class="msg-badge" v-if="!msg.read">未读</div>
      </div>

      <div class="empty" v-if="messageList.length === 0">暂无消息</div>
    </div>

    <div style="height:50px"></div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useStore } from 'vuex'
import { useRouter } from 'vue-router'

const store = useStore()
const router = useRouter()

const messageList = computed(() => store.getters.messageList)

const goToOrder = (msg) => {
  store.dispatch('readMessage', msg.id)
  router.push('/order-list')
}
</script>

<style scoped>
.message-page {
  background: #f5f5f5;
  min-height: 100vh;
}

/* 标题居中， */
.msg-header {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 15px;
  background: #fff;
  border-bottom: 1px solid #eee;
}
.msg-header h2 {
  margin: 0;
  font-size: 18px;
}

.msg-list {
  padding: 10px;
}
.msg-item {
  background: #fff;
  padding: 15px;
  border-radius: 10px;
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
}
.msg-title {
  font-weight: bold;
  font-size: 15px;
  margin-bottom: 4px;
}
.msg-content {
  font-size: 14px;
  color: #333;
  margin-bottom: 4px;
}
.msg-time {
  font-size: 12px;
  color: #999;
}
.msg-badge {
  background: #ff3333;
  color: #fff;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 10px;
}

.empty {
  text-align: center;
  color: #999;
  padding: 50px 0;
}
</style>