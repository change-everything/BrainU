import Index from '../views/index'
import GetStart from '../views/segment/getStart'
import UploadAndSegment from '../views/segment/uploadAndSegment'
import WorkSpace from '../views/workSpace/workSpace'
import NewPatients from '../views/workSpace/newPatients'
import ModelManage from '../views/model/index'
import DoctorManage from '../views/doctor/index'
import Login from '../views/login'

import Vue from 'vue'
import VueRouter from 'vue-router'

import { getToken } from '@/util/auth'

//2. 调用Vue.use()函数，把VueRouter安装为Vue的插件
Vue.use(VueRouter)


const router = new VueRouter({
    mode: 'history', // 去掉url中的#
    scrollBehavior: () => ({ y: 0 }),
    routes: [
        {
            path: '/login',
            component: Login,
            name: '登录',
            meta: { title: '登录' },
            requiresAuth: false
        },
        {
            path: '/',
            component: Index,
            name: '首页',
            meta: { title: '首页' },
            children: [
                {
                    path: '/getStart',
                    component: GetStart,
                    name: '快速开始',
                    meta: { title: '快速开始' },
                    requiresAuth: true
                },
                {
                    path: '/segment',
                    component: UploadAndSegment,
                    name: '数据添加',
                    meta: { title: '数据添加' },
                    requiresAuth: true
                },
                {
                    path: '/workSpace',
                    component: WorkSpace,
                    name: '已诊断患者',
                    meta: { title: '工作空间' },
                    requiresAuth: true
                },
                {
                    path: '/newPatients',
                    component: NewPatients,
                    name: '未诊断患者',
                    meta: { title: '工作空间' },
                    requiresAuth: true
                },
                {
                    path: '/modelManage',
                    component: ModelManage,
                    name: '模型信息管理',
                    meta: { title: '模型信息管理' },
                    requiresAuth: true
                },
                {
                    path: '/doctorManage',
                    component: DoctorManage,
                    name: '医生信息管理',
                    meta: { title: '医生信息管理' },
                    requiresAuth: true
                }
            ]
        }
    ]
})


// 路由拦截，判断是否需要登录
router.beforeEach((to, from, next) => {
    if (to.meta.title) {
        document.title = to.meta.title;
    }
    if (to.path == "/") {
        next({
            path: '/getStart',
            // query: { redirect: to.fullPath }
        });
    }
    if (to.path === '/login') {
        next();
    } else {
        let token = getToken();
        if (token === null || token === '' || token === undefined) {
            next('/login');
        } else {
            next();
        }
    }

});


export default router;

