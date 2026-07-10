import Vue from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import {
  Avatar, Badge, Button, Card, Col, Collapse, CollapseItem, Dialog, Drawer,
  Dropdown, DropdownItem, DropdownMenu, Empty, Form, FormItem, Image, Input,
  Link, Menu, MenuItem, Option, Pagination, Popover, Rate, Row, Select, Slider,
  Step, Steps, Submenu, Table, TableColumn, Tag, Upload, Loading, Message, MessageBox
} from 'element-ui'
import './assets/css/global.css'

import VueDirectiveImagePreviewer from 'vue-directive-image-previewer'
import 'vue-directive-image-previewer/dist/assets/style.css'

[
  Avatar, Badge, Button, Card, Col, Collapse, CollapseItem, Dialog, Drawer,
  Dropdown, DropdownItem, DropdownMenu, Empty, Form, FormItem, Image, Input,
  Link, Menu, MenuItem, Option, Pagination, Popover, Rate, Row, Select, Slider,
  Step, Steps, Submenu, Table, TableColumn, Tag, Upload
].forEach(component => Vue.use(component))

Vue.use(Loading.directive)
Vue.use(VueDirectiveImagePreviewer)
Vue.prototype.$loading = Loading.service
Vue.prototype.$message = Message
Vue.prototype.$confirm = MessageBox.confirm

axios.defaults.baseURL = process.env.VUE_APP_API_BASE_URL || 'http://localhost:8989'
Vue.prototype.$axios = axios
Vue.config.productionTip = false

new Vue({ router, render: h => h(App) }).$mount('#app')
