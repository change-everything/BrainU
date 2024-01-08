<template>
    <div style="height: 100%;">
        <el-card>
            <el-form :inline="true" class="demo-form-inline" :model="queryParams">
                <el-form-item label="患者姓名">
                    <el-input v-model="queryParams.patientName" placeholder="患者姓名" clearable></el-input>
                </el-form-item>
                <el-form-item label="处理医生">
                    <el-select v-model="queryParams.handleBy" placeholder="请选择" clearable
                        @visible-change="listDoctor($event)">
                        <el-option v-for="item in doctorList" :label="item.doctorName" :value="item.doctorId"
                            :key="item.doctorId"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="query">查询</el-button>
                </el-form-item>
            </el-form>
        </el-card>
        <el-card style="height: auto;">
            <el-table :data="tableData" border style="width: 100%">
                <div slot="empty" style="text-align: left;">
                    <el-empty description="暂无数据" />
                </div>
                <el-table-column type="index" width="50">
                </el-table-column>
                <el-table-column prop="patientName" label="患者姓名" align="center">
                </el-table-column>
                <el-table-column prop="url" label="肿瘤切面图" align="center">
                    <template slot-scope="scope">
                        <el-collapse>
                            <el-collapse-item title="点击查看" style="border:none;">
                                <el-image style="width: 100px; height: 100px" :src="scope.row.url">
                                </el-image>
                            </el-collapse-item>
                        </el-collapse>
                    </template>
                </el-table-column>
                <el-table-column prop="patientAge" label="患者年龄" align="center">
                </el-table-column>
                <el-table-column prop="patientGender" label="患者性别" align="center">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.patientGender == 0" type="danger">女</el-tag>
                        <el-tag v-if="scope.row.patientGender == 1">男</el-tag>
                    </template>
                </el-table-column>
                <el-table-column prop="patientPhone" label="患者电话" align="center">
                </el-table-column>
                <el-table-column prop="patientIdcard" label="患者身份证号" width="180" align="center">
                </el-table-column>
                <el-table-column prop="handleByName" label="处理医生" align="center">
                </el-table-column>
                <el-table-column v-if="false" prop="imgPath"></el-table-column>
                <el-table-column label="操作" width="150" align="center">
                    <template slot-scope="scope">
                        <el-button type="text" size="small" @click="headlePreTumor(scope.row)">预览肿瘤</el-button>
                        &nbsp;&nbsp;&nbsp;
                        <el-dropdown @command="handleCommand">
                            <span class="el-dropdown-link">
                                更多<i class="el-icon-arrow-down el-icon--right"></i>
                            </span>
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item :command="beforeHandleCommand(scope.row, 'preview')">查看</el-dropdown-item>
                                <el-dropdown-item
                                    :command="beforeHandleCommand(scope.row, 'download')">下载肿瘤文件</el-dropdown-item>
                                <el-dropdown-item :command="beforeHandleCommand(scope.row, 'update')">修改</el-dropdown-item>
                                <el-dropdown-item :command="beforeHandleCommand(scope.row, 'delete')">删除</el-dropdown-item>
                            </el-dropdown-menu>
                        </el-dropdown>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <div>
            <el-pagination background @size-change="handleSizeChange" @current-change="handleCurrentChange"
                :current-page=pageNum :page-sizes="[5, 10, 15, 20]" :page-size=pageSize
                layout="total,->, sizes, prev, pager, next, jumper" :total=total>
            </el-pagination>
        </div>

        <el-dialog :title="dialogTitle" :visible.sync="dialogFormVisible">

            <el-form :model="detailData">
                <el-form-item label="患者姓名" label-width="120px">
                    <el-input v-model="detailData.patientName" autocomplete="off" :disabled="!isUpdate"></el-input>
                </el-form-item>
                <el-form-item label="患者年龄" label-width="120px">
                    <el-input v-model="detailData.patientAge" autocomplete="off" :disabled="!isUpdate"></el-input>
                </el-form-item>
                <el-form-item label="患者性别" label-width="120px">
                    <el-select v-model="detailData.patientGender" placeholder="请选择" :disabled="!isUpdate">
                        <el-option label="女" :value="0"></el-option>
                        <el-option label="男" :value="1"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="患者电话" label-width="120px">
                    <el-input v-model="detailData.patientPhone" autocomplete="off" :disabled="!isUpdate"></el-input>
                </el-form-item>
                <el-form-item label="患者身份证号" label-width="120px">
                    <el-input v-model="detailData.patientIdcard" autocomplete="off" :disabled="!isUpdate"></el-input>
                </el-form-item>
                <!-- <el-form-item label="处理医生" label-width="120px">
                    <el-input v-model="detailData.handleBy" autocomplete="off" :disabled="true"></el-input>
                </el-form-item> -->
            </el-form>
            <el-tag style="margin-left: 600px;" type="warning">处理医生：{{ detailData.handleByName }}</el-tag>
            <span slot="footer" class="dialog-footer" v-if="isUpdate">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="doUpdate">确 定</el-button>
            </span>
        </el-dialog>
        <TumorView ref="tumorDialog"></TumorView>
    </div>
</template>
<script>
import { listPatientIsHeadle, savePatient, deletePatient } from "@/api/patient"
import { listDoctor } from "@/api/doctor";
import { downloadTumorFile } from "@/api/segment"
import TumorView from "./components/tumorView"
export default {
    name: '',
    components: {
        TumorView
    },
    mixins: [],
    props: {

    },
    data() {
        return {
            dialogFormVisible: false,
            detailData: {},
            tableData: [],
            doctorList: [],
            queryParams: {
                handleBy: '',
                patientName: ''
            },
            pageNum: 1,
            pageSize: 5,
            total: 0,
            isUpDialog: false,
            gender: [
                {
                    value: '0',
                    label: '女'
                },
                {
                    value: '1',
                    label: '男'
                }
            ],
            isUpdate: false,
            dialogTitle: ''
        }
    },
    computed: {

    },
    watch: {

    },
    mounted() {
        this.getList()
    },
    methods: {
        getList() {
            let param = this.queryParams;
            let pagesize = this.pageSize;
            let pageNum = this.pageNum;
            listPatientIsHeadle(pageNum, pagesize, param).then(res => {
                this.tableData = res.data.data.records;
                this.total = res.data.data.total;
            })
        },
        getDoctorList() {
            listDoctor().then(res => {
                this.doctorList = res.data.data;
            });
        },
        handlePreview(row) {
            console.log(row)
            this.detailData = row;
            this.dialogTitle = '查看信息'
            this.isUpdate = false;
            this.dialogFormVisible = true;
        },
        handleUpdate(row) {
            console.log(row)
            this.detailData = JSON.parse(JSON.stringify(row));
            this.dialogTitle = '修改信息'
            this.isUpdate = true;
            this.dialogFormVisible = true;
        },
        handleDelete(row) {
            this.$confirm('此操作将永久删除该患者信息, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                deletePatient(row.id).then(res => {
                    if (res.data.code === 0) {
                        this.$message({
                            showClose: true,
                            message: '删除成功',
                            type: 'success'
                        });
                        this.getList();
                    }
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },
        query() {
            this.getList();
        },
        handleSizeChange(val) {
            this.pageSize = val;
            this.getList();
        },
        handleCurrentChange(val) {
            this.pageNum = val;
            this.getList();
        },
        listDoctor(callback) {
            if (callback == true) {
                this.getDoctorList();
            }
            console.log(this.doctorList)
        },
        headlePreTumor(val) {
            // this.$refs.tumorDialog.openFullScreen()
            this.$refs.tumorDialog.dialogOpen(val)
        },
        closedDialog() {
            this.isUpDialog = false;
        },
        handleCommand(val) {
            console.log(val)
            if (val.command == 'preview') {
                this.handlePreview(val.row);
            } else if (val.command == 'update') {
                console.log(val.row)
                this.handleUpdate(val.row);
            } else if (val.command == 'delete') {
                console.log(val.row)
                this.handleDelete(val.row);
            } else if (val.command == 'download') {
                console.log(val.row)
                this.handleDownload(val.row);
            }
        },
        beforeHandleCommand(row, command) {
            return {
                row: row,
                command: command
            }
        },

        handleDownload(row) {
            downloadTumorFile(row.id).then(res => {
                this.resolveBlob(res, "brainTumor-" + row.id + ".mha")
            })
        },

        resolveBlob(res, fileName) {
            const aLink = document.createElement('a')
            let blob = new Blob([res.data])
            if (window.navigator.msSaveOrOpenBlob) {
                navigator.msSaveBlob(blob, fileName);
            } else {
                aLink.href = URL.createObjectURL(blob)
                aLink.setAttribute('download', fileName) // 设置下载文件名称
                aLink.click()
                window.URL.revokeObjectURL(aLink.href);
            }
        },
        doUpdate() {
            console.log(this.detailData)
            savePatient(this.detailData).then(res => {
                if (res.data.code === 0) {
                    this.$message({
                        showClose: true,
                        message: '修改成功',
                        type: 'success'
                    });
                }
            })
            this.dialogFormVisible = false;
            this.getList();
        }
    }
};
</script>
<style>
.el-collapse-item {
    border: none;
}

::v-deep .el-collapse-item__header.is-active {
    border: none;
}

::v-deep .el-collapse-item__wrap {
    border: none;
}

.el-dropdown-link {
    cursor: pointer;
    color: #409EFF;
    font-size: smaller;
}

.el-icon-arrow-down {
    font-size: 12px;
}
</style>