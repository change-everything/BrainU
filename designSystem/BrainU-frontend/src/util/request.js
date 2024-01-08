import axios from 'axios'
import VueCookie from 'vue-cookies';
import { getToken, removeToken } from '@/util/auth'

axios.defaults.baseURL = '/api'

const service = axios.create({
    baseURL: "/api",
    timeout: 5000,
    withCredentials: true // 允许携带cookie
});


// 在封装axios的文件中添加拦截器
// 添加请求拦截器，在请求头中加token
service.interceptors.request.use(config => {
    // 判断本地的cookie中是否有token
    if (VueCookie.isKey('UserToken')) {
        config.headers.Authorization = "Bearer " + getToken();
    }
    return config
}, error => {
    return Promise.reject(error)
})

service.interceptors.response.use(
    response => {
        if (response.data.code === 40001) {

            removeToken()
            // 返回接口返回的错误信息
            return Promise.reject(response.data);
        }
        return response
    },
    error => {
        if (error.response) {
            switch (error.response.status) {
                // 返回401，清除token信息并跳转到登录页面
                case 40001:
                    removeToken()
                // router.replace({
                //     path: '/login',
                //     //登录成功后跳入浏览的当前页面
                //     query: { redirect: router.currentRoute.fullPath }
                // })
            }
            // 返回接口返回的错误信息
            return Promise.reject(error.response.data);
        }
    }
);

export default service