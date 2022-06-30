<template>
  <view>
    <view class="bg-main text-white flex align-center justify-center font-md py-2 rounded-circle" hover-class="bg-main-hover" @click="logout">退出登录</view>
  </view>
</template>

<script>
export default {
  data() {
    return {

    }
  },
  methods: {
    logout(){
      // TODO(wuyiccc): 退出登录
      this.$store.dispatch('logout').then(res=>{
        uni.showToast({
          title: '退出成功',
          icon: 'none'
        });
      })
    }
  }
}
</script>

<style>

</style>
