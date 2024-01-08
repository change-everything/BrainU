<template>
    <div style="text-align: center; height: 62vh;">
        <div class="component-upload-image" style="margin-top: 30vh;">
            <el-upload drag action="#" :auto-upload="false" :multiple="true" :before-upload="beforeUpload"
                :on-success="handleUploadSuccess" :on-change="handleChange" :class="{ disabled: uploadDisabled }"
                ref="upload" :file-list="files">
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            </el-upload>
            <!-- 上传提示 -->
            <div class="el-upload__tip" slot="tip" v-if="showTip" style="margin-bottom: 10px;">
                <b style="color: #f56c6c">请上传需要分析的样本</b><br>
                <b style="color: #f56c6c">上传成功后自动跳转到未诊断患者页面，可以对患者脑肿瘤进行分割</b>
            </div>
        </div>
        <el-button type="primary" @click="submitUpload">提交</el-button>

        <el-dialog title="患者信息" :visible.sync="dialogFormVisible">
            <el-form :model="patientData">
                <el-form-item label="患者姓名" label-width="120px">
                    <el-input v-model="patientData.patientName" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="患者年龄" label-width="120px">
                    <el-input v-model="patientData.patientAge" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="患者性别" label-width="120px" style="text-align: left;">
                    <el-select v-model="patientData.patientGender" placeholder="请选择患者性别">
                        <el-option label="男" value="1"></el-option>
                        <el-option label="女" value="0"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="患者电话" label-width="120px">
                    <el-input v-model="patientData.patientPhone" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="患者身份证号" label-width="120px">
                    <el-input v-model="patientData.patientIdcard" autocomplete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="savePatient">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>
<script>

import { uploadFile } from '@/api/segment';
export default {
    name: 'UploadAndSegment',
    components: {

    },
    mixins: [],
    props: {
        // 值
        value: [String, Object, Array],
        // 数量限制
        limit: {
            type: Number,
            default: 5,
        },
        // 大小限制(MB)
        fileSize: {
            type: Number,
            default: 50,
        },
        // 文件类型, 例如['png', 'jpg', 'jpeg']
        fileType: {
            type: Array,
            default: () => ['mha'],
        },
        // 是否显示提示
        isShowTip: {
            type: Boolean,
            default: true
        }
    },
    data() {
        return {
            dialogFormVisible: false,
            responsePath: "",
            resultPath: "",
            currentDate: new Date(),
            centerDialogVisible: false,
            number: 0,
            files: [],
            dialogImageUrl: "",
            disabled: false,
            uploadDisabled: false,
            imgUrl: "",
            dialogVisible: false,
            hideUpload: false,
            uploadUrl: "localhost:8989/segment/upload",
            patientData: {}
        };
    },
    watch: {
        value: {
            handler(val) {
                if (val) {
                    // 首先将值转为数组
                    const list = Array.isArray(val) ? val : this.value.split(',');
                    // 然后将数组转为对象数组
                    this.fileList = list.map(item => {
                        if (typeof item === "string") {
                            if (item.indexOf(this.baseUrl) === -1) {
                                item = { name: this.baseUrl + item, url: this.baseUrl + item };
                            } else {
                                item = { name: item, url: item };
                            }
                        }
                        return item;
                    });
                } else {
                    this.fileList = [];
                    return [];
                }
            },
            deep: true,
            immediate: true
        }
    },
    mounted() {
        //以下代码，有时候可能写法不同，可在控制台打印一层一层的找input，再给加webkitdirectory 属性
        this.$refs.upload.$children[0].$refs.input.webkitdirectory = true;
    },
    computed: {
        // 是否显示提示
        showTip() {
            return this.isShowTip && (this.fileType || this.fileSize);
        },
    },
    methods: {
        // 点击文件夹路径上传按钮
        handleAddFolder() {
            this.$nextTick(() => {
                this.$refs.uploadFile.$children[0].$refs.input.webkitdirectory = true
            })
        },
        // 文件夹路径上传之前钩子函数
        beforeUpload(file) {
            // file.path为文件夹的路径
            // this.form.Folder = file.path
            console.log(file);

        },

        submitUpload() {
            let loading = this.$loading({
                lock: true,
                text: '正在上传，请稍后...',
                spinner: 'el-icon-loading',
                // background: 'rgba(0, 0, 0, 0.8)'
            });
            console.log(this.patientData)
            let data = this.patientData;
            //-- 创建新的数据对象 -->
            let formData = new FormData();
            //-- 将上传的文件放到数据对象中 -->
            this.files.forEach((file) => {
                formData.append("files", file.raw)
            })
            formData.append("patientInfo", JSON.stringify(data))
            // let files = { "files": formData }
            console.log(formData)
            uploadFile(formData).then(res => {
                console.log(res);
                if (res.data.code === 0) {
                    loading.close();
                    this.$message({
                        showClose: true,
                        message: '文件绑定成功',
                        type: 'success'
                    });
                    this.$router.push({ name: '未诊断患者', path: '/newPatients' })
                }
            })
        },
        handleChange(file, fileList) {
            console.log("change========>", file, fileList)
            this.files = fileList;
            this.dialogFormVisible = true;
        },
        // 上传前loading加载
        handleBeforeUpload(file) {
            console.log(file)
        },
        // 文件个数超出
        handleExceed() {
        },
        // 上传成功回调
        handleUploadSuccess(res, file) {
            console.log("成功=========>", res, file)

        },
        // 上传失败
        handleUploadError() {
        },
        // 上传结束处理
        uploadedSuccessfully() {
            console.log("成功啦")
        },
        // 对象转成指定字符串分隔
        listToString(list, separator) {
            let strs = "";
            separator = separator || ",";
            for (let i in list) {
                if (list[i].url) {
                    strs += list[i].url.replace(this.baseUrl, "") + separator;
                }
            }
            return strs != '' ? strs.substr(0, strs.length - 1) : '';
        },
        handleClick(tab, event) {
            console.log(tab, event);
        },
        savePatient() {
            console.log(this.patientData)
            this.$message({
                showClose: true,
                message: '患者信息保存成功',
                type: 'success'
            });
            this.dialogFormVisible = false;
        }
    }
};
</script>
<style>
.disabled .el-upload--picture-card {
    display: none !important;
}

.time {
    font-size: 13px;
    color: #999;
}

.bottom {
    margin-top: 13px;
    line-height: 12px;
}

.button {
    padding: 0;
    float: right;
}

.image {
    width: 100%;
    display: block;
}

.clearfix:before,
.clearfix:after {
    display: table;
    content: "";
}

.clearfix:after {
    clear: both
}
</style>