<template>
  <view>
    <view class="p-3 flex align-center">
      <image src="/static/face.jpg" style="width: 120rpx;height: 120rpx;"
             class="rounded-circle flex-shrink mr-3"></image>
      <view class="flex-1 flex flex-column text-muted font">
        <view class="flex align-end">
          <text class="font-lg text-dark mr-2">wuyiccc</text>男 湖南
        </view>
        <text class="text-ellipsis">个人开发者</text>
      </view>
    </view>
    <!--分割线-->
    <view class="bg-light" style="height: 20rpx;"></view>
    <view class="p-3">
      <!--容量进度条-->
      <progress class="mb-3" percent="80" active stroke-width="3"/>
      <view class="flex align-center justify-between font">
        <text class="text-light-muted">总：50GB</text>
        <text class="text-warning">已用：40GB</text>
      </view>
    </view>

    <!--分隔栏-->
    <view class="bg-light" style="height: 20rpx;"></view>

    <uni-list-item title="其他设置"></uni-list-item>
  </view>

</template>

<script>

import UniListItem from "../../components/uni-ui/uni-list-item/uni-list-item";
export default {
  components: {UniListItem},
  data() {
    return {}
  },
  methods: {}
}
</script>

<style scoped>

</style>