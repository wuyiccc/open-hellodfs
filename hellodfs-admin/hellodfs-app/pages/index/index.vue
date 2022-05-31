<template>
	<view>

    <!--导航栏-->
    <nav-bar>
      <text slot="left" class="font-md ml-3">首页</text>
      <template slot="right">
        <view style="width: 60rpx; height: 60rpx" class="flex align-center justify-center bg-light rounded-circle mr-3">
          <text class="iconfont icon-zengjia"></text>
        </view>
        <view style="width: 60rpx; height: 60rpx" class="flex align-center justify-center bg-light rounded-circle mr-3">
          <text class="iconfont icon-gengduo"></text>
        </view>
      </template>
    </nav-bar>

    <view class="px-3 py-2">
      <view class="position-relative">
        <view style="height: 70rpx; width: 70rpx; position: absolute; top: 0; left: 0;" class="flex align-center justify-center">
          <text class="iconfont icon-sousuo text-light-muted"></text>
        </view>
        <input style="height: 70rpx; padding-left: 70rpx;" type="text" class="bg-light font-md rounded-circle" placeholder="搜索hellodfs文件"/>
      </view>
    </view>


	</view>
</template>

<script>

	import NavBar from "../../components/common/nav-bar";
  export default {
    components: {NavBar},
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
