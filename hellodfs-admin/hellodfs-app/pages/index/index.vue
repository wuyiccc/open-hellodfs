<template>
	<view>
    <view>
      <!--应用状态栏-->
      <uni-status-bar></uni-status-bar>

      <view style="height: 44px;" class="flex border-bottom align-center">

        <view class="flex-1 flex">
          <text class="font-md ml-3">首页</text>
        </view>

        <view class="flex-1 flex justify-center"></view>

        <view class="flex-1 flex justify-end">
          <view style="width: 60rpx; height: 60rpx" class="flex align-center justify-center bg-light rounded-circle mr-3">
            <text class="iconfont icon-zengjia"></text>
          </view>
          <view style="width: 60rpx; height: 60rpx" class="flex align-center justify-center bg-light rounded-circle mr-3">
            <text class="iconfont icon-gengduo"></text>
          </view>
        </view>

      </view>

    </view>
	</view>
</template>

<script>
import UniStatusBar from "../../components/uni-ui/uni-status-bar/uni-status-bar";

	export default {
    components: {UniStatusBar},
    data() {
			return {
			}
		},
		onLoad() {

		},
		methods: {

		}
	}
</script>

<style>
</style>
