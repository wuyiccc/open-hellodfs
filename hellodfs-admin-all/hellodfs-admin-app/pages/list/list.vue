<template>

  <!--上传/下载列表切换栏-->
  <view style="height: 100vh;" class="flex flex-column">
    <view class="flex border-bottom border-light-secondary" style="height: 100rpx;">
      <view class="flex-1 flex flex-column align-center justify-center " v-for="(item,index) in tabBars" :key="index"
            :class="index === tabIndex ? 'text-main' : ''" @click="changeTab(index)">
        <text class="font-md">{{ item.name }}</text>
        <text style="height: 8rpx;width: 30rpx;" class="rounded"
              :class="tabIndex === index ? 'bg-main' : 'bg-white'"></text>
      </view>
    </view>


    <swiper :duration="250" class="flex-1 flex" :current="tabIndex" @change="changeTab($event.detail.current)">
      <swiper-item class="flex-1 flex" v-for="(item,index) in tabBars" :key="index">
        <scroll-view scroll-y="true" class="flex-1">
          <view style="height: 60rpx;" class="bg-light flex align-center font-sm px-2 text-muted">
            文件下载至：storage/xxxx/xxxx
          </view>

          <view class="p-2 border-bottom border-light-secondary font text-muted">
            下载中({{ downing.length }})
          </view>
          <f-list v-for="(item,index) in downing" :key="'i'+index" :item="item" :index="index">
            <!--暂停图标, 代替默认插槽的选中图标-->
            <view style="height: 70rpx;" class="flex align-center text-main">
              <text class="iconfont icon-zanting"></text>
              <text class="ml-1">{{ item.download }}%</text>
            </view>
            <!--下载进度条, 代替bottom插槽-->
            <progress slot="bottom" :percent="item.download" activeColor="#009CFF" :stroke-width="4"/>
          </f-list>

          <view class="p-2 border-bottom border-light-secondary font text-muted">
            下载完成({{ downed.length }})
          </view>
          <f-list v-for="(item,index) in downed" :key="'d'+index" :item="item" :index="index"
                  :showRight="false"></f-list>

        </scroll-view>
      </swiper-item>
    </swiper>

  </view>
</template>

<script>
import FList from "../../components/common/f-list";

export default {
  components: {FList},
  data() {
    return {
      tabIndex: 0,
      tabBars: [{
        name: "下载列表"
      }, {
        name: "上传列表"
      }],
      list: [
        {
          type: "image",
          name: "程潇1.jpg",
          data: "/static/chengxiao1.jpg",
          create_time: "2022-06-01 08:00",
          download: 100
        },
        {
          type: "image",
          name: "程潇2.jpg",
          data: "/static/chengxiao2.jpg",
          create_time: "2022-06-01 08:00",
          download: 10
        },
        {
          type: "video",
          name: "学习视频.mp4",
          data: "https://wuyiccc.oss-cn-hangzhou.aliyuncs.com/VID_20220529_233701.mp4",
          create_time: "2022-06-01 08:00",
          download: 50
        },
        {
          type: "video",
          name: "学习视频2.mp4",
          data: "https://wuyiccc.oss-cn-hangzhou.aliyuncs.com/VID_20220529_233701.mp4",
          create_time: "2022-06-01 08:00",
          download: 100
        },
        {
          type: "text",
          name: "临时笔记.txt",
          create_time: "2022-06-01 08:00",
          download: 10
        }, {
          type: "none",
          name: "软件压缩.rar",
          create_time: "2022-06-01 08:00",
          download: 0
        }],
    }
  },
  computed: {
    // 下载中
    downing() {
      return this.list.filter(item => {
        return item.download < 100
      })
    },
    // 已经下载完成
    downed() {
      return this.list.filter(item => {
        return item.download === 100
      })
    }
  },
  methods: {
    changeTab(index) {
      this.tabIndex = index
    }
  }
}
</script>

<style>

</style>