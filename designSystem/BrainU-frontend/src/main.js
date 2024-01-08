import Vue from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'

import ElementUI from 'element-ui';                      // 引入element-ui
import 'element-ui/lib/theme-chalk/index.css';
// fade/zoom 等
import 'element-ui/lib/theme-chalk/base.css';
import './assets/css/global.css';

//图片放大组件
import VueDirectiveImagePreviewer from 'vue-directive-image-previewer'
import 'vue-directive-image-previewer/dist/assets/style.css'
Vue.use(VueDirectiveImagePreviewer)

axios.defaults.baseURL = 'http://localhost:8989';

Vue.config.productionTip = false
//全局挂载
Vue.prototype.$axios = axios;

Vue.use(ElementUI);

new Vue({
  render: h => h(App),
  router,
  axios
}).$mount('#app')
