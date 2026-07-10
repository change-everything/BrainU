import Vue from 'vue'
import VueRouter from 'vue-router'
import { getToken } from '@/util/auth'

Vue.use(VueRouter)

// Keep the login and application shell small. Feature pages (especially the
// Three.js medical viewer) are downloaded only when the user opens them.
const Index = () => import(/* webpackChunkName: "layout" */ '../views/index')
const Login = () => import(/* webpackChunkName: "auth" */ '../views/login')
const GetStart = () => import(/* webpackChunkName: "guide" */ '../views/segment/getStart')
const UploadAndSegment = () => import(/* webpackChunkName: "segment" */ '../views/segment/uploadAndSegment')
const WorkSpace = () => import(/* webpackChunkName: "workspace" */ '../views/workSpace/workSpace')
const NewPatients = () => import(/* webpackChunkName: "workspace" */ '../views/workSpace/newPatients')
const ModelManage = () => import(/* webpackChunkName: "management" */ '../views/model/index')
const DoctorManage = () => import(/* webpackChunkName: "management" */ '../views/doctor/index')

const router = new VueRouter({
    mode: 'history',
    scrollBehavior: () => ({ y: 0 }),
    routes: [
        {
            path: '/login',
            component: Login,
            name: '登录',
            meta: { title: '登录 · BrainU' }
        },
        {
            path: '/',
            component: Index,
            name: '首页',
            redirect: '/getStart',
            children: [
                { path: '/getStart', component: GetStart, name: '快速开始', meta: { title: '临床概览 · BrainU', requiresAuth: true } },
                { path: '/segment', component: UploadAndSegment, name: '数据添加', meta: { title: '导入影像 · BrainU', requiresAuth: true } },
                { path: '/workSpace', component: WorkSpace, name: '已诊断患者', meta: { title: '已诊断病例 · BrainU', requiresAuth: true } },
                { path: '/newPatients', component: NewPatients, name: '未诊断患者', meta: { title: '待诊断病例 · BrainU', requiresAuth: true } },
                { path: '/modelManage', component: ModelManage, name: '模型信息管理', meta: { title: '模型管理 · BrainU', requiresAuth: true } },
                { path: '/doctorManage', component: DoctorManage, name: '医生信息管理', meta: { title: '医生团队 · BrainU', requiresAuth: true } }
            ]
        }
    ]
})

router.beforeEach((to, from, next) => {
    document.title = to.meta.title || 'BrainU 医学影像平台'
    if (!to.meta.requiresAuth || getToken()) {
        next()
        return
    }
    next({ path: '/login', query: { redirect: to.fullPath } })
})

export default router
