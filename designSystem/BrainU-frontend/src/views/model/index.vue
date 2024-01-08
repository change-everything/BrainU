<template>
    <div style="height: 100%;">
        <el-card>
            <el-form :inline="true" class="demo-form-inline" :model="queryParams">
                <el-form-item label="模型名">
                    <el-input v-model="queryParams.modelName" placeholder="模型名" clearable></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="query">查询</el-button>
                </el-form-item>
            </el-form>
        </el-card>

        <el-card style="height: auto;">
            <div>
                <el-button type="primary" plain size="small" @click="handleSave">添加</el-button>
            </div>
            <br>
            <el-table :data="tableData" border style="width: 100%">
                <div slot="empty" style="text-align: left;">
                    <el-empty description="暂无数据" />
                </div>
                <el-table-column type="index" width="50">
                </el-table-column>
                <el-table-column prop="modelName" label="模型名" align="center">
                </el-table-column>
                <el-table-column prop="modelPath" label="模型路径" align="center">
                </el-table-column>
                <el-table-column prop="modelDescribe" label="模型描述" align="center">
                </el-table-column>
                <el-table-column label="操作" width="150" align="center">
                    <template slot-scope="scope">
                        <el-button @click="handlePreview(scope.row)" type="text" size="small">查看</el-button>
                        <el-button @click="handleUpdate(scope.row)" type="text" size="small">修改</el-button>
                        <el-button @click="handleDelete(scope.row)" type="text" size="small">删除</el-button>
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

        <el-dialog :title="title" :visible.sync="dialogFormVisible">
            <el-form :model="detailData">
                <el-form-item label="模型名" label-width="120px">
                    <el-input v-model="detailData.modelName" autocomplete="off" :disabled="isPreview"></el-input>
                </el-form-item>
                <el-form-item label="模型路径" label-width="120px">
                    <el-input v-model="detailData.modelPath" autocomplete="off" :disabled="isPreview"></el-input>
                </el-form-item>
                <el-form-item label="模型描述" label-width="120px">
                    <el-input type="textarea" v-model="detailData.modelDescribe" autocomplete="off"
                        :disabled="isPreview"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer" v-if="!isPreview">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="doSaveOrUpdate">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>
<script>
import { listModelPage, saveModel, deleteModel } from '@/api/modelInfo';
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
            isPreview: true,
            title: ''
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
            listModelPage(pageNum, pagesize, param).then(res => {
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
        handleSave() {
            this.title = '新增模型';
            this.detailData = {}
            this.dialogFormVisible = true;
            this.isPreview = false;
        },
        handleUpdate(row) {
            this.detailData = JSON.parse(JSON.stringify(row));
            this.dialogTitle = '修改模型'
            this.isPreview = false;
            this.dialogFormVisible = true;
        },
        handleDelete(row) {
            this.$confirm('此操作将永久删除该患者信息, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                deleteModel(row.id).then(res => {
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

        doSaveOrUpdate() {
            console.log(this.detailData);
            saveModel(this.detailData).then(res => {
                if (res.data.code === 0) {
                    this.$message({
                        showClose: true,
                        message: '操作成功',
                        type: 'success'
                    });
                    this.dialogFormVisible = false
                    this.getList();
                }
            })
        }
    }
};
</script>
<style lang='' scoped>
</style>