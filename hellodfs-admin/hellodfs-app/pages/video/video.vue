<template>
  <view style="height: 100vh;">
    <video :src="url" controls autoplay style="width: 750rpx;height: 100vh;" @ended="back"></video>
  </view>
</template>

<script>
export default {
  name: "video",
  data() {
    return {
      url: ""
    }
  },
  onLoad(e) {
    console.log("mmms")
    if (!e.url) {
      uni.showToast({
        title: '非法参数',
        icon: 'none'
      });
      return uni.navigateBack({
        delta: 1
      });
    }
    this.url = e.url
    console.log(e.title)
    if (e.title) {
      uni.setNavigationBarTitle({
        title: e.title
      })
    }
  },
  methods: {
    back() {
      uni.navigateBack({
        delta: 1
      });
    }
  }
}
</script>

<style scoped>

</style>