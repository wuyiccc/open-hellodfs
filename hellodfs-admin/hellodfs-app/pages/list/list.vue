<template>
  <view style="height: 100vh;" class="flex flex-column">
    <view class="flex border-bottom border-light-secondary" style="height: 100rpx;">
      <view class="flex-1 flex flex-column align-center justify-center " v-for="(item,index) in tabBars" :key="index" :class="index === tabIndex ? 'text-main' : ''" @click="changeTab(index)">
        <text class="font-md">{{item.name}}</text>
        <text style="height: 8rpx;width: 30rpx;" class="rounded" :class="tabIndex === index ? 'bg-main' : 'bg-white'"></text>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      tabIndex:0,
      tabBars:[{
        name:"下载列表"
      },{
        name:"上传列表"
      }]
    }
  },
  methods: {
    changeTab(index){
      this.tabIndex = index
    }
  }
}
</script>

<style>

</style>