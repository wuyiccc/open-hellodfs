<template>
  <view>

    <!--导航栏-->
    <nav-bar>
      <template v-if="checkCount === 0">
        <text slot="left" class="font-md ml-3">首页</text>
        <template slot="right">
          <view style="width: 60rpx; height: 60rpx"
                class="flex align-center justify-center bg-light rounded-circle mr-3">
            <text class="iconfont icon-zengjia"></text>
          </view>
          <view style="width: 60rpx; height: 60rpx"
                class="flex align-center justify-center bg-light rounded-circle mr-3">
            <text class="iconfont icon-gengduo"></text>
          </view>
        </template>
      </template>

      <template v-else>
        <view slot="left" class="font-md ml-3 text-primary" @click="handleCheckAll(false)">取消</view>
        <text class="font-md font-weight-bold">已选中{{ checkCount }}个</text>
        <view slot="right" class="font-md mr-3 text-primary" @click="handleCheckAll(true)">全选</view>
      </template>


    </nav-bar>

    <!--搜索栏-->
    <view class="px-3 py-2">
      <view class="position-relative">
        <view style="height: 70rpx; width: 70rpx; position: absolute; top: 0; left: 0;"
              class="flex align-center justify-center">
          <text class="iconfont icon-sousuo text-light-muted"></text>
        </view>
        <input style="height: 70rpx; padding-left: 70rpx;" type="text" class="bg-light font-md rounded-circle"
               placeholder="搜索hellodfs文件"/>
      </view>
    </view>

    <!--文件列表-->
    <f-list v-for="(item,index) in list" :key="index"
            :item="item" :index="index" @select="select"></f-list>

    <!--底部操作栏-->
    <view v-if="checkCount > 0">
      <!--占位-->
      <view style="height: 115rpx;"></view>
      <view style="height: 115rpx;" class="flex align-stretch bg-primary text-white fixed-bottom">
        <view class="flex-1 flex flex-column align-center justify-center" style="line-height: 1.5;"
              v-for="(item, index) in actions" :key="index"
              hover-class="bg-hover-primary" @click="handleBottomEvent(item)">
          <text class="iconfont" :class="item.icon"></text>
          {{ item.name }}
        </view>

      </view>
    </view>

    <f-dialog ref="dialog">
      是否删除选用的文件?
    </f-dialog>

  </view>
</template>

<script>

import NavBar from "../../components/common/nav-bar";
import FList from "../../components/common/f-list";
import FDialog from "../../components/common/f-dialog";

export default {
  components: {FDialog, FList, NavBar},
  data() {
    return {
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
          create_time: "2022-06-01 08:00",
          checked: false
        },
        {
          type: "image",
          name: "程潇2.jpg",
          create_time: "2022-06-01 08:00",
          checked: false
        },
        {
          type: "video",
          name: "学习视频.mp4",
          create_time: "2022-06-01 08:00",
          checked: false,
        },
        {
          type: "video",
          name: "学习视频2.mp4",
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
        }]
    }
  },
  onLoad() {

  },
  computed: {
    checkList() {
      return this.list.filter(item => item.checked);
    },
    checkCount() {
      return this.checkList.length;
    },
    actions() {
      if (this.checkCount > 1) {
        return [{
          icon: "icon-xiazai",
          name: "下载"
        }, {
          icon: "icon-shanchu",
          name: "删除"
        }]
      }

      return [{
        icon: "icon-xiazai",
        name: "下载"
      }, {
        icon: "icon-fenxiang-1",
        name: "分享"
      }, {
        icon: "icon-shanchu",
        name: "删除"
      }, {
        icon: "icon-chongmingming",
        name: "重命名"
      }]
    }
  },
  methods: {
    select(e) {
      this.list[e.index].checked = e.value;
    },
    handleCheckAll(checked) {
      this.list.forEach(item => {
        item.checked = checked;
      })
    },
    handleBottomEvent(item) {
      switch (item.name) {
        case "删除":
          this.$refs.dialog.open((close) => {
            close();
          })
          break;
        default:
          break
      }
    }
  }
}
</script>

<style>
</style>
