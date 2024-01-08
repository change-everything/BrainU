<template>
    <div style="height: 100%;">
        <el-card>
            <el-form :inline="true" class="demo-form-inline" :model="queryParams">
                <el-form-item label="医生姓名">
                    <el-input v-model="queryParams.doctorName" placeholder="医生姓名" clearable></el-input>
                </el-form-item>
                <el-form-item label="医生工号">
                    <el-input v-model="queryParams.doctorId" placeholder="医生姓名" clearable></el-input>
                </el-form-item>
                <el-form-item label="医生办公室">
                    <el-input v-model="queryParams.doctorOffice" placeholder="医生办公室" clearable></el-input>
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
                <el-table-column prop="doctorName" label="医生姓名" align="center">
                </el-table-column>
                <el-table-column prop="doctorId" label="医生工号" align="center">
                </el-table-column>
                <el-table-column prop="doctorPhone" label="医生电话" align="center">
                </el-table-column>
                <el-table-column prop="doctorEmail" label="医生邮箱" align="center">
                </el-table-column>
                <el-table-column prop="doctorOffice" label="医生办公室" align="center">
                </el-table-column>
                <el-table-column prop="status" label="医生状态" align="center">
                    <template slot-scope="scope">
                        <el-tag v-if="scope.row.status == 0" type="danger">休</el-tag>
                        <el-tag v-if="scope.row.status == 1" type="success">班</el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="150" align="center">
                    <template slot-scope="scope">
                        <el-button @click="handlePreview(scope.row)" type="text" size="small">查看</el-button>
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
                <el-form-item label="医生姓名" label-width="120px">
                    <el-input v-model="detailData.doctorName" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="医生工号" label-width="120px">
                    <el-input v-model="detailData.doctorId" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="医生电话" label-width="120px">
                    <el-input type="textarea" v-model="detailData.doctorPhone" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="医生邮箱" label-width="120px">
                    <el-input type="textarea" v-model="detailData.doctorEmail" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="医生办公室" label-width="120px">
                    <el-input type="textarea" v-model="detailData.doctorOffice" autocomplete="off"></el-input>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>
<script>
import { listDoctorPage } from '@/api/doctor';
export default {
    name: '',
    components: {

    },
    mixins: [],
    props: {

    },
    data() {
        return {
            dialogFormVisible: false,
            detailData: {},
            tableData: [],
            queryParams: {
                modelName: ''
            },
            pageNum: 1,
            pageSize: 5,
            total: 0,
        }
    },
    computed: {

    },
    watch: {

    },
    mounted() {
        this.getList();
    },
    methods: {
        getList() {
            let param = this.queryParams;
            let pagesize = this.pageSize;
            let pageNum = this.pageNum;
            listDoctorPage(pageNum, pagesize, param).then(res => {
                this.tableData = res.data.data.records;
                this.total = res.data.data.total;
            })
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
    }
};
</script>
<style lang='' scoped>
</style>