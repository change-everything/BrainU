<template>
    <div>
        <div>
            <h1>关于BrainU 1.0的操作文档</h1>
            <h4>上传要分割的脑部图像</h4>

            在BrainU网站首页，点击或将多模态MRI脑肿瘤影像的文件夹拖入到文件上传框内


            支持的图像格式：mha。

            注意：上传的图像大小不能超过50MB。

            <h4>等待分割结果</h4>
            <p>上传成功后，系统会自动开始对图像进行分割处理。</p>
            <p>您需要等待一段时间，直到系统处理完成并返回结果。</p>

            <h4>查看分割结果</h4>
            <p>分割处理完成后，系统会返回分割结果。</p>
            <p>您可以在BrainU网站的结果页面查看分割结果。</p>
            <p>分割结果将包括原始图像和分割后的肿瘤图像。</p>
            <p>您还可以查看肿瘤的位置和大小。</p>

            <h4>下载分割后的图像</h4>
            <p>如果您对分割结果满意，可以在结果页面下载分割后的肿瘤模态图。</p>
            <p>您可以选择下载原始图像、分割后的肿瘤图像或两者都下载。</p>
            <p>下载的图像将保存为.mha格式。</p>

            <h4>注意事项</h4>
            <ul>
                <li>BrainU系统仅用于脑肿瘤的分割处理，不能用于诊断和治疗。</li>
                <li>上传的图像必须是脑部CT或MRI扫描图像，不能是其他类型的图像。</li>
                <li>上传的图像大小不能超过50MB。</li>
                <li>BrainU系统可能需要一定时间来完成图像分割处理，具体时间取决于图像大小和复杂程度。</li>
                <li>BrainU系统可能无法对所有图像进行有效的分割处理，如果您有任何问题，请<el-link type="primary">联系我们</el-link>。</li>
            </ul>
        </div>

    </div>
</template>
<script>
export default {
    name: 'GetStart',
    components: {

    },
    mixins: [],
    props: {

    },
    data() {
        return {
        }
    },
    computed: {

    },
    watch: {

    },
    mounted() {

    },
    methods: {

    }
};
</script>
<style></style>