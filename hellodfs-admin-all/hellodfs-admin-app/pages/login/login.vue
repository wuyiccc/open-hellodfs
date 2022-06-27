<template>
  <view>
    <view style="height: 44px;"></view>
    <view class="flex align-center justify-center font-lg text-muted" style="margin-top: 100rpx;margin-bottom: 80rpx;">欢迎使用hellodfs</view>

    <view class="px-4">
      <input type="text" v-model="form.username" class="uni-input bg-light rounded mb-4" placeholder="请输入用户名"/>
      <input type="text" v-model="form.password" class="uni-input bg-light rounded mb-4" placeholder="请输入密码"/>
      <input v-if="type === 'reg'" type="text" v-model="form.repassword" class="uni-input bg-light rounded mb-4" placeholder="请输入确认密码"/>

      <view class="bg-main text-white flex align-center justify-center font-md py-2 rounded-circle" hover-class="bg-main-hover" @click="submit">{{ type === 'login' ? '登 录' : '注 册' }}</view>
    </view>

    <view class="flex align-center justify-center pt-5">
      <view class="text-muted mx-2 font-sm" @click="changeType">
        {{ type === 'login' ? '注册账号' : '去登录' }}
      </view>
    </view>

  </view>
</template>

<script>
export default {
  name: "login",
  data() {
    return {
      type: "login",
      form: {
        username: "",
        password: "",
        repassword: ""
      }
    }
  },
  methods: {
    changeType() {
      this.type = this.type === 'login' ? 'reg' : 'login'
    },
    submit(){
      // TODO(wuyiccc): 请求登录注册接口
      let msg = this.type === 'login' ? '登录' : '注册'
      this.$H.post('/' + this.type,this.form).then(res=>{
        uni.showToast({
          title: msg + '成功',
          icon: 'none'
        });
        if(this.type === 'login'){
          this.$store.dispatch('login',res).then(result=>{
            uni.switchTab({
              url:"../index/index"
            })
          })
        } else {
          this.form = {
            username:"",
            password:"",
            repassword:""
          }
          this.changeType()
        }

      })
    }

  }
}
</script>

<style scoped>

</style>