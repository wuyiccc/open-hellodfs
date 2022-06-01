<template>
  <uni-popup ref="dialog">
    <view style="width: 600rpx;" class="bg-white rounded">
      <!--标题-->
      <view class="flex align-center justify-center font-weight-bold border-bottom border-light-secondary" style="height: 100rpx;">{{title}}</view>

      <!--内容-->
      <view class="flex align-center justify-center p-3">
        <slot></slot>
      </view>
      <!--取消与确定-->
      <view class="flex border-top border-light-secondary" style="height: 100rpx;">
        <view class="flex-1 text-muted flex align-center justify-center"
              @tap="cancel">{{cancelText}}</view>
        <view class="flex-1 text-main flex align-center justify-center"
              @tap="confirm">{{confirmText}}</view>
      </view>

    </view>
  </uni-popup>
</template>

<script>
import UniPopup from "../uni-ui/uni-popup/uni-popup";
export default {
  name: "f-dialog",
  components: {UniPopup},
  props: {
    title: {
      type: String,
      default: "提示"
    },
    cancelText: {
      type: String,
      default: "取消"
    },
    confirmText: {
      type: String,
      default: "确定"
    }
  },
  data() {
    return {
      callback: false
    }
  },
  methods: {
    // 又外部组件调用该方法
    open(callback = false) {
      // 赋值回调函数(留给确定的时候调用)
      this.callback = callback;
      // 打开弹窗
      this.$refs.dialog.open();
    },
    // 取消按钮调用
    cancel() {
      // 向父组件发送cancel信息
      this.$emit('cancel');
      // 关闭弹窗
      this.$refs.dialog.close();
    },
    // 确认按钮调用
    confirm() {
      // 确认时调用callback父组件传递过来的函数, 由父组件选择是否关闭弹窗
      if (typeof this.callback === 'function') {
        this.callback(()=> {
          this.cancel();
        })
      } else {
        // 向父组件发送confirm消息
        this.$emit('confirm');
        // 调用取消事件
        this.cancel();
      }
    }

  }
}
</script>

<style>

</style>