/**
 * @author wuyiccc
 * @date 2022/6/7 22:22
 */
import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex)

export default new Vuex.Store({
    state:{
        user:null,
        token:null
    },
    actions:{
        login({ state },user){
            state.user = user
            state.token = user.token

            uni.setStorageSync('user',JSON.stringify(user))
            uni.setStorageSync('token',user.token)
        },
        initUser({ state }){
            let user = uni.getStorageSync('user')
            if(user){
                state.user = JSON.parse(user)
                state.token = state.user.token
            }
        },
        updateSize({ state },e){
            state.user.total_size = e.total_size
            state.user.used_size = e.used_size
        }
    }
})