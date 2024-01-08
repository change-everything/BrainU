<template>
    <div style="height: 100%;">
        <el-card>
            <el-form :inline="true" class="demo-form-inline" :model="queryParams">
                <el-form-item label="患者姓名">
                    <el-input v-model="queryParams.patientName" placeholder="患者姓名" clearable></el-input>
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
                <!-- <el-empty :image-size="200"></el-empty> -->
                <el-table-column type="index" width="50">
                </el-table-column>
                <el-table-column prop="patientName" label="患者姓名" align="center">
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
                <!-- <el-table-column prop="handleBy" label="处理医生" align="center">
                </el-table-column> -->
                <el-table-column v-if="false" prop="imgPath"></el-table-column>
                <el-table-column label="操作" width="150" align="center">
                    <template slot-scope="scope">
                        <el-popover placement="top" width="160" :ref="`popover-${scope.$index}`">
                            <p>请选择模型</p>
                            <el-select v-model="modelValue" filterable placeholder="请选择">
                                <el-option v-for="item in modelList" :key="item.id" :label="item.modelName"
                                    :value="item.id">
                                    <span style="float: left">{{ item.modelName }}</span>
                                    <span style="float: right" v-if="item.id == 2"><el-tag size="mini"
                                            type="warning">推荐</el-tag></span>
                                </el-option>
                            </el-select>
                            <div style="text-align: right; margin: 0">
                                <el-button size="mini" type="text" @click="cancel(scope.$index)">取消</el-button>
                                <el-button type="primary" size="mini" @click="headleTumorSegment(scope)">确定</el-button>
                            </div>
                            <el-button slot="reference" type="primary" plain size="small">分割</el-button>
                        </el-popover>
                        <el-button @click="handlePreview(scope.row)" type="success" plain size="small">查看</el-button>
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

        <el-dialog title="查看信息" :visible.sync="dialogFormVisible">
            <el-form :model="detailData">
                <el-form-item label="患者姓名" label-width="120px">
                    <el-input v-model="detailData.patientName" autocomplete="off" disabled></el-input>
                </el-form-item>
                <el-form-item label="患者年龄" label-width="120px">
                    <el-input v-model="detailData.patientAge" autocomplete="off" disabled></el-input>
                </el-form-item>
                <el-form-item label="患者性别" label-width="120px">
                    <el-select v-model="detailData.patientGender" placeholder="请选择" disabled>
                        <el-option label="女" :value="0"></el-option>
                        <el-option label="男" :value="1"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="患者电话" label-width="120px">
                    <el-input v-model="detailData.patientPhone" autocomplete="off" disabled></el-input>
                </el-form-item>
                <el-form-item label="患者身份证号" label-width="120px">
                    <el-input v-model="detailData.patientIdcard" autocomplete="off" disabled></el-input>
                </el-form-item>
            </el-form>
        </el-dialog>

        <el-dialog title="分割结果预览" :visible.sync="rateDialogVisible" width="30%">
            <el-row>
                <el-col :span="12">
                    <span style="text-align: center;">分割前</span>
                </el-col>
                <el-col :span="12">
                    <span style="text-align: center;">分割后</span>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="12">
                    <el-image style="width: 200px; height: 200px" :src="picDiffData.beforeUrl">
                    </el-image>
                </el-col>
                <el-col :span="12">
                    <el-image style="width: 200px; height: 200px" :src="picDiffData.afterUrl">
                    </el-image>
                </el-col>
            </el-row>
            <br>
            <el-row>
                <el-col :span="24">
                    <span class="demonstration">为这次结果评个分吧！</span>
                    <el-rate v-model="rateValue" :colors="['#99A9BF', '#F7BA2A', '#FF9900']">
                    </el-rate>
                </el-col>
            </el-row>
            <span slot="footer" class="dialog-footer">
                <el-button @click="rateDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="rateDialogSubmit">确 定</el-button>
            </span>
        </el-dialog>

        <Step ref="stepRef"></Step>
    </div>
</template>
<script>
import { listPatientNotHeadle } from "@/api/patient"
import { listDoctor } from "@/api/doctor";
import { listModel } from '@/api/modelInfo';
import { segment, getPicDiffPre } from '@/api/segment';
export default {
    name: '',
    components: {
        Step: () => import('./components/step')
    },
    mixins: [],
    props: {

    },
    data() {
        return {
            dialogFormVisible: false,
            popoverVisible: false,
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
            modelList: [],
            modelValue: '',
            stepBegin: false,
            stepActive: 1,
            beforeUrl: '',
            afterUrl: '',
            rateDialogVisible: false,
            picDiffData: {},
            rateValue: null,
            iconClasses: ['icon-rate-face-1', 'icon-rate-face-2', 'icon-rate-face-3']
        }
    },
    computed: {

    },
    watch: {

    },
    mounted() {
        this.getList()
        this.getModelList()
    },
    methods: {
        headleTumorSegment(val) {
            console.log(val)
            this.cancel(val.$index)

            this.$refs.stepRef.begin();

            setTimeout(() => {
                this.$refs.stepRef.setStep(1, "正在调用模型，请耐心等待...");
            }, 5000);
            setTimeout(() => {
                this.$refs.stepRef.setStep(2, "正在分割图像，请耐心等待...");
            }, 10000);
            setTimeout(() => {
                this.$refs.stepRef.setStep(3, "正在渲染图像，请耐心等待...");
            }, 15000);
            setTimeout(() => {
                this.$refs.stepRef.setStep(4, "正在保存图像，请耐心等待...");
            }, 20000);

            segment(this.modelValue, String(val.row.id)).then(res => {
                console.log("segment----------->", res.data)
                this.$message({
                    showClose: true,
                    message: '分割成功',
                    type: 'success'
                });
                this.$refs.stepRef.end();
                getPicDiffPre().then(res => {
                    console.log("getPicDiffPre==========>", res.data)
                    if (res.data.code == 0) {
                        this.picDiffData = res.data.data;
                        this.rateDialogVisible = true;
                    }
                })
            })
        },
        rateDialogSubmit() {
            if (this.rateValue !== 0) {
                this.$message({
                    showClose: true,
                    message: '感谢您的反馈',
                    type: 'success'
                });
                this.$router.push({ name: '已诊断患者', path: '/workSpace' })
            }
        },
        cancel(index) {
            this.$refs[`popover-${index}`].doClose();
        },
        getModelList() {
            listModel().then(res => {
                console.log(res.data)
                this.modelList = res.data.data;
            })
            console.log(this.modelValue)
        },
        getList() {
            let param = this.queryParams;
            let pagesize = this.pageSize;
            let pageNum = this.pageNum;
            listPatientNotHeadle(pageNum, pagesize, param).then(res => {
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
            this.dialogFormVisible = true;
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
        closedDialog() {
            this.isUpDialog = false;
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
</style>