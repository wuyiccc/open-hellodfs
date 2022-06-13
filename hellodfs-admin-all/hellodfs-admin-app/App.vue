<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style>
	/*每个页面公共css */
  /* 引入官方样式库 */
  @import url("/common/uni.css");
  /* 引入公共样式库 */
  @import url("/common/common.css");
  /* 引入free全局样式库 */
  @import url("/common/free.css");
  /* 引入图标库 */
  @import url("/common/icon.css");
</style>
