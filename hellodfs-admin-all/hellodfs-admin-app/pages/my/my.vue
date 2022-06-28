<template>
  <view>
    <view class="p-3 flex align-center">
      <image src="/static/face.jpg" style="width: 120rpx;height: 120rpx;"
             class="rounded-circle flex-shrink mr-3"></image>
      <view class="flex-1 flex flex-column text-muted font">
        <view class="flex align-end">
          <text class="font-lg text-dark mr-2">{{ user.nickname || user.username }}</text>
          {{user.sex}}
        </view>
        <text class="text-ellipsis">{{user.desc || '暂无描述'}}</text>
      </view>
    </view>
    <!--分割线-->
    <view class="bg-light" style="height: 20rpx;"></view>
    <view class="p-3">
      <!--容量进度条-->
      <progress class="mb-3" percent="80" active stroke-width="3"/>
      <view class="flex align-center justify-between font">
        <text class="text-light-muted">总：{{user.total_size | bytesToSize}}</text>
        <text class="text-warning">已用：{{user.used_size | bytesToSize}}</text>
      </view>
    </view>

    <!--分隔栏-->
    <view class="bg-light" style="height: 20rpx;"></view>

    <uni-list-item title="其他设置"></uni-list-item>
  </view>

</template>

<script>

import UniListItem from "../../components/uni-ui/uni-list-item/uni-list-item";
import {mapState} from 'vuex'

export default {
  components: {UniListItem},
  data() {
    return {}
  },
  computed: {
    ...mapState({
      user: state => state.user
    }),
    progress() {
      if (this.user.total_size === 0) {
        return 0
      }
      return (this.user.used_size / this.user.total_size) * 100
    }
  },
  onShow() {
    this.getSize()
  },
  methods: {
    // TODO(wuyiccc): 获取容量信息
    getSize() {
      this.$H.get('/getsize', {token: true}).then(res => {
        this.$store.dispatch('updateSize', res)
      })
    }
  },
  filters: {
    bytesToSize(bytes) {
      if (bytes === 0) return '0 KB';
      var k = 1000, // or 1024
          sizes = ['KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'],
          i = Math.floor(Math.log(bytes) / Math.log(k));

      return (bytes / Math.pow(k, i)).toPrecision(3) + ' ' + sizes[i];
    }
  }
}
</script>

<style scoped>

</style>