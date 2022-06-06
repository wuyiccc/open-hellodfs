<template>
  <view style="height: 100vh;" class="flex flex-column">
    <view class="flex border-bottom border-light-secondary" style="height: 100rpx;">
      <view class="flex-1 flex flex-column align-center justify-center " v-for="(item,index) in tabBars" :key="index"
            :class="index === tabIndex ? 'text-main' : ''" @click="changeTab(index)">
        <text class="font-md">{{ item.name }}</text>
        <text style="height: 8rpx;width: 30rpx;" class="rounded"
              :class="tabIndex === index ? 'bg-main' : 'bg-white'"></text>
      </view>
    </view>


    <swiper :duration="250" class="flex-1 flex">
      <swiper-item class="flex-1 flex" v-for="(item,index) in tabBars" :key="index">
        <scroll-view scroll-y="true" class="flex-1">
          <view style="height: 60rpx;" class="bg-light flex align-center font-sm px-2 text-muted">
            文件下载至：storage/xxxx/xxxx
          </view>
          <view class="p-2 border-bottom border-light-secondary font text-muted">
            下载中(1)
          </view>

          <!--文件下载列表-->
          <f-list v-for="(item,index) in list" :key="index" :item="item" :index="index"></f-list>
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
      list: [{
        type: "dir",
        name: "学习笔记1",
        create_time: "2022-06-01 08:00",
        checked: false
      },
        {
          type: "dir",
          name: "学习笔记2",
          create_time: "2022-06-01 08:00",
          checked: false
        },
        {
          type: "dir",
          name: "学习笔记3",
          create_time: "2022-06-01 08:00",
          checked: false
        },
        {
          type: "dir",
          name: "学习笔记4",
          create_time: "2022-06-01 08:00",
          checked: false
        },
        {
          type: "dir",
          name: "学习笔记5",
          create_time: "2022-06-01 08:00",
          checked: false
        },
        {
          type: "dir",
          name: "学习笔记6",
          create_time: "2022-06-01 08:00",
          checked: false
        },
        {
          type: "image",
          name: "程潇1.jpg",
          data: "/static/chengxiao1.jpg",
          create_time: "2022-06-01 08:00",
          checked: false
        },
        {
          type: "image",
          name: "程潇2.jpg",
          data: "/static/chengxiao2.jpg",
          create_time: "2022-06-01 08:00",
          checked: false
        },
        {
          type: "video",
          name: "学习视频.mp4",
          data: "https://wuyiccc.oss-cn-hangzhou.aliyuncs.com/VID_20220529_233701.mp4",
          create_time: "2022-06-01 08:00",
          checked: false,
        },
        {
          type: "video",
          name: "学习视频2.mp4",
          data: "https://wuyiccc.oss-cn-hangzhou.aliyuncs.com/VID_20220529_233701.mp4",
          create_time: "2022-06-01 08:00",
          checked: false,
        },
        {
          type: "text",
          name: "临时笔记.txt",
          create_time: "2022-06-01 08:00",
          checked: false
        }, {
          type: "none",
          name: "软件压缩.rar",
          create_time: "2022-06-01 08:00",
          checked: false
        }],
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