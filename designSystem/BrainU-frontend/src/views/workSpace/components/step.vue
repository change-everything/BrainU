<template>
    <div>
        <el-dialog :visible.sync="dialogVisible" fullscreen v-loading="loading" :element-loading-text="text"
            element-loading-spinner="el-icon-loading" element-loading-background="rgba(0, 0, 0, 0)">
            <el-steps :active="stepActive" align-center finish-status="success">
                <el-step title="读取文件" icon="el-icon-folder-opened" description="读取患者对应的脑肿瘤模型文件"></el-step>
                <el-step title="调用模型" icon="el-icon-cpu" description="调用已训练的模型"></el-step>
                <el-step title="分割图像" icon="el-icon-aim" description="对图像进行分割和肿瘤预测"></el-step>
                <el-step title="渲染图像" icon="el-icon-brush" description="肿瘤图像分割完成，渲染中"></el-step>
                <el-step title="保存图像" icon="el-icon-picture" description="保存图像中"></el-step>
            </el-steps>
        </el-dialog>
    </div>
</template>
<script>
export default {
    name: 'Step',
    components: {

    },
    mixins: [],
    props: {

    },
    data() {
        return {
            dialogVisible: false,
            stepActive: 0,
            text: '正在读取文件，请耐心等待...',
            loading: false,
        }
    },
    computed: {

    },
    watch: {

    },
    mounted() {

    },
    methods: {
        begin() {
            this.dialogVisible = true;
            this.loading = true;
        },

        setStep(active, msg) {
            this.text = msg;
            this.stepActive = active;
        },

        end() {
            this.stepActive = 5;
            this.dialogVisible = false;
            this.loading = false;
        }
    }
};
</script>
<style lang='' scoped>
</style>