<template>
  <div class="clinical-layout">
    <aside class="clinical-sidebar">
      <div class="brand">
        <div class="brand-mark"><span></span><span></span></div>
        <div>
          <strong>BrainU</strong>
          <small>医学影像辅助诊断平台</small>
        </div>
      </div>

      <div class="nav-caption">临床工作台</div>
      <el-menu router :default-active="$route.path" class="clinical-menu" background-color="transparent"
        text-color="#9fb3c5" active-text-color="#ffffff">
        <el-menu-item index="/getStart"><i class="el-icon-data-analysis"></i><span>临床概览</span></el-menu-item>
        <el-menu-item index="/segment"><i class="el-icon-upload2"></i><span>导入影像</span></el-menu-item>
        <el-submenu index="workspace">
          <template slot="title"><i class="el-icon-first-aid-kit"></i><span>病例工作区</span></template>
          <el-menu-item index="/newPatients">待诊断病例</el-menu-item>
          <el-menu-item index="/workSpace">已诊断病例</el-menu-item>
        </el-submenu>
        <div class="nav-caption nav-caption-secondary">系统管理</div>
        <el-menu-item index="/modelManage"><i class="el-icon-cpu"></i><span>分割模型</span></el-menu-item>
        <el-menu-item index="/doctorManage"><i class="el-icon-user"></i><span>医生团队</span></el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <div class="system-state"><span></span>系统服务正常</div>
        <small>BrainU Clinical v1.0</small>
      </div>
    </aside>

    <section class="clinical-content">
      <header class="clinical-header">
        <div>
          <div class="header-eyebrow">BRAIN TUMOR SEGMENTATION</div>
          <h1>{{ currentTitle }}</h1>
        </div>
        <div class="header-actions">
          <div class="privacy-state"><i class="el-icon-lock"></i> 数据安全连接</div>
          <el-badge :value="number" :hidden="number === 0" class="notice-badge">
            <button class="icon-button" type="button" title="通知" @click="notifyDrawer = true">
              <i class="el-icon-bell"></i>
            </button>
          </el-badge>
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="doctor-profile">
              <el-avatar size="small" icon="el-icon-user-solid"></el-avatar>
              <div><strong>临床医生</strong><small>影像诊断中心</small></div>
              <i class="el-icon-arrow-down"></i>
            </div>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="notifiy">查看通知</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </div>
      </header>

      <main class="clinical-main"><router-view /></main>
    </section>

    <el-drawer title="临床通知" :visible.sync="notifyDrawer" direction="rtl" size="420px">
      <el-table :data="notifies" @row-click="rowClick" class="notice-table">
        <el-table-column type="index" width="54"></el-table-column>
        <el-table-column property="context" label="内容"></el-table-column>
        <el-table-column property="createTime" label="时间" width="160"></el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>

<script>
import { removeToken } from '@/util/auth'
import { getAllNotify } from '@/api/segment'

export default {
  name: 'Index',
  data() {
    return { number: 0, notifies: [], notifyDrawer: false }
  },
  computed: {
    currentTitle() {
      return (this.$route.meta && this.$route.meta.title || '临床工作台').split(' · ')[0]
    }
  },
  mounted() {
    getAllNotify().then(res => {
      this.notifies = res.data.data || []
      this.number = this.notifies.length
    }).catch(() => {})
  },
  methods: {
    rowClick() {
      this.$router.push('/newPatients').catch(() => {})
      this.notifyDrawer = false
    },
    handleCommand(command) {
      if (command === 'notifiy') {
        this.notifyDrawer = true
        return
      }
      if (command !== 'logout') return
      this.$confirm('确定退出当前登录吗？', '退出登录', {
        confirmButtonText: '退出', cancelButtonText: '取消', type: 'warning'
      }).then(() => {
        removeToken()
        this.$router.push('/login')
      }).catch(() => {})
    }
  }
}
</script>

<style lang="scss">
.clinical-layout { min-height: 100vh; display: flex; background: #f3f7f9; color: #1a2b3b; }
.clinical-sidebar { position: fixed; inset: 0 auto 0 0; z-index: 20; width: 248px; display: flex; flex-direction: column; background: #0c2438; box-shadow: 12px 0 30px rgba(7, 31, 49, .08); }
.brand { height: 92px; display: flex; align-items: center; gap: 13px; padding: 0 24px; border-bottom: 1px solid rgba(255,255,255,.08); color: white; }
.brand strong { display:block; font: 700 24px/1.1 Inter, sans-serif; letter-spacing: -.5px; }
.brand small { display:block; margin-top: 5px; color:#8fa9bc; font-size: 11px; letter-spacing:.5px; }
.brand-mark { position: relative; width: 36px; height: 36px; border: 1px solid rgba(72, 213, 205, .5); border-radius: 11px; background: rgba(40, 177, 173, .12); }
.brand-mark span { position:absolute; background:#48d5cd; border-radius:2px; }
.brand-mark span:first-child { width:18px; height:5px; left:8px; top:15px; }
.brand-mark span:last-child { width:5px; height:18px; left:15px; top:8px; }
.nav-caption { padding: 25px 25px 9px; color:#66849a; font-size:10px; font-weight:700; letter-spacing:1.6px; }
.nav-caption-secondary { padding-top: 20px; }
.clinical-menu { border:0 !important; }
.clinical-menu .el-menu-item, .clinical-menu .el-submenu__title { height:48px; line-height:48px; margin:3px 12px; padding-left:14px !important; border-radius:8px; }
.clinical-menu .el-menu-item i, .clinical-menu .el-submenu__title i { color:#7895aa; margin-right:12px; }
.clinical-menu .el-menu-item:hover, .clinical-menu .el-submenu__title:hover { background:rgba(255,255,255,.055) !important; }
.clinical-menu .el-menu-item.is-active { background:linear-gradient(90deg, #158f91, #13777e) !important; box-shadow:0 8px 18px rgba(0,0,0,.16); }
.clinical-menu .el-menu-item.is-active i { color:#fff; }
.clinical-menu .el-submenu .el-menu-item { min-width:auto; padding-left:50px !important; background:transparent !important; }
.sidebar-footer { margin-top:auto; padding:20px 24px 24px; border-top:1px solid rgba(255,255,255,.08); color:#6f8da2; }
.system-state { margin-bottom:8px; color:#9fb6c6; font-size:12px; }.system-state span { display:inline-block; width:7px; height:7px; margin-right:8px; border-radius:50%; background:#36c99b; box-shadow:0 0 0 4px rgba(54,201,155,.12); }
.clinical-content { width:calc(100% - 248px); min-width:0; margin-left:248px; }
.clinical-header { position:sticky; top:0; z-index:15; height:91px; display:flex; align-items:center; justify-content:space-between; padding:0 34px; background:rgba(255,255,255,.96); border-bottom:1px solid #e2eaee; backdrop-filter:blur(10px); }
.clinical-header h1 { margin:5px 0 0; font-size:21px; font-weight:650; letter-spacing:-.3px; }.header-eyebrow { color:#7b929f; font-size:9px; font-weight:700; letter-spacing:1.5px; }
.header-actions { display:flex; align-items:center; gap:18px; }.privacy-state { color:#63808d; font-size:12px; }.privacy-state i { color:#15918e; margin-right:6px; }
.icon-button { width:38px; height:38px; border:1px solid #dce7eb; border-radius:10px; background:#fff; color:#506b78; cursor:pointer; }.icon-button:hover { color:#13898a; border-color:#8dc9c7; }
.doctor-profile { display:flex; align-items:center; gap:10px; cursor:pointer; padding-left:3px; }.doctor-profile .el-avatar { background:#dcefee; color:#167f7f; }.doctor-profile strong,.doctor-profile small { display:block; }.doctor-profile strong { font-size:12px; }.doctor-profile small { margin-top:3px; color:#8ba0aa; font-size:10px; }.doctor-profile > i { color:#91a3ac; }
.clinical-main { min-height:calc(100vh - 92px); padding:26px 30px 34px; box-sizing:border-box; }
.notice-table { cursor:pointer; }
@media (max-width: 1050px) { .clinical-sidebar { width:210px; }.clinical-content { width:calc(100% - 210px); margin-left:210px; }.privacy-state { display:none; }.clinical-header { padding:0 22px; }.clinical-main { padding:20px; } }
</style>
