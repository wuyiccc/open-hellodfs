<template>

  <view>

    <!--fixed-top是让状态栏固定在顶部, 不会随着屏幕滑动而变化-->
    <view class="fixed-top bg-white">
      <!--应用状态栏-->
      <uni-status-bar></uni-status-bar>

      <view style="height: 44px;" class="flex border-bottom align-center">
        <view class="flex-1 flex">
          <slot name="left"></slot>
        </view>

        <view class="flex-1 flex justify-center">
          <slot></slot>
        </view>

        <view class="flex-1 flex justify-end">
          <slot name="right"></slot>
        </view>

      </view>

    </view>

    <!--该view用于隔离状态栏与下面中间的内容区域-->
    <view>
      <uni-status-bar></uni-status-bar>
      <view style="height: 44px;"></view>
    </view>
  </view>


</template>

<script>
import UniStatusBar from "../uni-ui/uni-status-bar/uni-status-bar";
export default {
  name: "nav-bar",
  components: {UniStatusBar}
}
</script>

<style scoped>

</style>