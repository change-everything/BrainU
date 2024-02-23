<template>
  <div class="app-container">
    <el-container class="operation-wrapper">
      <el-aside width="200px">
        <div style="position:fixed; width: 200px; height: 100%;">
          <span style="font-size:xx-large;"><img class="logoImg" src="../assets/images/logo.png" alt=""></span>
          <span style="line-height: 160px;"></span>
          <br>
          <span style="font-size:small;">Version 1.0.0</span>
          <br>
          <span style="font-size:small;">2023年3月15日</span>
          <br>
          <p style="line-height: 160px;"></p>
          <span style="font-size:small;">Copyright(C) 2024</span>
          <br>
          <span style="font-size:small;">裴永鹏毕业设计作品</span>
          <br>
          <br><br><br><br><br><br><br><br><br><br><br>
          <span style="font-size:xx-small;">
            天津市津南区<br>
            海河教育园区<br>
            天津中德应用技术大学<br>
            软件与通信学院<br>
            2019级软件工程5班<br>
            <a href="https://beian.miit.gov.cn/#/Integrated/index">津ICP备2024012731号</a>
          </span>
        </div>
      </el-aside>
      <el-container>
        <el-header style="--el-header-padding: 0 0px">
          <el-menu router :default-active="this.$route.path" mode="horizontal" style="text-align: center;"
            active-text-color="#409EFF">
            <el-menu-item style="margin-left: 100px;" index="/getStart">快速开始</el-menu-item>
            <el-menu-item style="margin-left: 100px;" index="/segment">数据添加</el-menu-item>
            <el-submenu style="margin-left: 100px;" index="3">
              <template slot="title">工作空间</template>
              <el-menu-item index="/newPatients">未诊断患者</el-menu-item>
              <el-menu-item index="/workSpace">已诊断患者</el-menu-item>
            </el-submenu>
            <el-menu-item style="margin-left: 100px;" index="/modelManage">模型信息管理</el-menu-item>
            <el-menu-item style="margin-left: 100px;" index="/doctorManage">医生信息预览</el-menu-item>
            <el-dropdown trigger="click" @command="handleCommand">
              <el-badge style="top: 12px;position: fixed;" :value="number" class="number"
                :class="number > 0 ? 'flash' : ''" :hidden="number == 0">
                <el-avatar src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
              </el-badge>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item class="clearfix" command="notifiy">
                  通知
                  <el-badge class="mark" :value="number" />
                </el-dropdown-item>
                <el-dropdown-item class="clearfix" command="logout">
                  退出登录
                  <el-badge class="mark" />
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </el-menu>
        </el-header>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
    <el-drawer title="通知" :visible.sync="notifyDrawer" direction="rtl" size="40%">
      <el-table :data="notifies" @row-click="rowClick">
        <el-table-column type="index"></el-table-column>
        <el-table-column property="context" label="内容" width="300"></el-table-column>
        <el-table-column property="createTime" label="时间" width="200"></el-table-column>
      </el-table>
    </el-drawer>
  </div>
</template>
<script>
import { removeToken } from '@/util/auth'
import { getAllNotify } from "@/api/segment"
export default {
  name: "Index",
  components: {
  },
  data() {
    return {
      number: 0,
      userData: {},
      notifies: [],
      notifyDrawer: false
    };
  },
  mounted() {
    getAllNotify().then(res => {
      this.notifies = res.data.data;
      this.number = res.data.data.length;
    })

  },
  methods: {
    rowClick() {
      this.$router.push({ name: '未诊断患者', path: '/newPatients' }).catch(() => { });
      this.notifyDrawer = false;
    },

    handleCommand(command) {
      if (command === 'logout') {
        this.$confirm('确定退出登录吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          removeToken();
          this.$message({
            showClose: true,
            message: '登出成功',
            type: 'success'
          });
          this.$router.push({ name: '登录', path: '/login' }).catch(() => { });
        }).catch(() => {
          this.$message({
            type: 'info',
            message: '已登出系统'
          });
        });
      } else if (command == 'notifiy') {
        this.notifyDrawer = true;
      }
    }
  }
};
</script>
<style>
.item {
  margin-top: 20px;
  margin-right: 35px;
}

.el-menu {
  width: 100%;
  display: inline-block;
  align-items: center;
  /* 垂直居中 */
  justify-content: center;
  /* 水平居中 */
}

.el-header,
.el-footer {
  /* background-color: #B3C0D1; */
  color: #333;
  text-align: center;
  line-height: 60px;
  /* position: fixed; */
  width: 100%;
  padding: 0 0px;
}

.el-main {
  color: #333;
  box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
  min-height: 100%;
  overflow-y: scroll;
}

html,
body,
.el-container {
  /*设置内部填充为0，几个布局元素之间没有间距*/
  padding: 0px;
  /*外部间距也是如此设置*/
  margin: 0px;
  /*统一设置高度为100%*/
  height: 100%;
}

.el-container:nth-child(5) .el-aside,
.el-container:nth-child(6) .el-aside {
  line-height: 260px;
}

.el-container:nth-child(7) .el-aside {
  line-height: 320px;
}

.el-header,
.el-footer {
  color: #333;
  text-align: center;
  line-height: 60px;
}

.el-aside {
  color: #333;
  text-align: center;
  height: 100%;
}


.logoImg {
  margin-top: 50px;
  width: 150px;
  height: 90px;
}
</style>

<style lang="scss">
.operation-wrapper {
  width: 100% !important;

  .el-header {
    height: 61px;
  }

  .el-aside {
    width: 200px;
    height: calc(100vh - 61px); //61px为顶部header盒子高度
    overflow-y: auto;
  }

  .el-main {
    padding: 0px 16px !important;
    height: calc(100vh - 61px); //61px为顶部header盒子高度
    overflow-y: auto;
  }
}
</style>


<style scoped>
/*闪烁动画*/
@keyframes twinkle {
  from {
    opacity: 1.0;
  }

  50% {
    opacity: 0.4;
  }

  to {
    opacity: 1.0;
  }
}

.flash /deep/ .el-badge__content {
  animation: twinkle 2s;
  animation-iteration-count: infinite;
}
</style>