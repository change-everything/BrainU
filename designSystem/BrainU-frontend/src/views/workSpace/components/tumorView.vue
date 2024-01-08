<template>
    <div>
        <el-dialog :visible.sync="dialogVisible" width="70%" :before-close="handleClose" :close-on-click-modal="false"
            :close-on-press-escape="false" top="0vh" :fullscreen="false" style="position: absolute; z-index: 0;"
            @close="dialogClose()" @open="open()" destroy-on-close>
            <div style="margin-left: 20vh; width: 100vh; height: 100vh;">
                <div class="out1" id="seDiv">
                    <span class="postitonText" style="margin-left: 20vh;">A</span>
                    <span class="postitonText" style="margin-top: 20vh;">R</span>
                    <span class="postitonText" style="margin-left: 39vh; margin-top: 20vh;">L</span>
                    <span class="postitonText" style="margin-left: 20vh; margin-top: 38vh;">P</span>
                    <div style="position: absolute; z-index: 999; width: 40vh; height: 40vh;"
                        @mousewheel="handleMouseWheel1">
                        <div class="coordinateX" id="seX" style="top: 117px"></div>
                        <div class="coordinateY" id="seY" style="left: 96px"></div>
                    </div>
                    <div ref="scrollDiv1" class="div1" v-for="(item, key) in semgentUrls" :key="'se-' + key">
                        <el-image class="image1" :src="item" v-show="seIndex == key">
                        </el-image>
                    </div>
                    <el-link v-if="isSeRender" id="selink" :underline="false" icon="el-icon-view"
                        style="margin-left: 41.2vh;" @click="previewPic('se')"></el-link>
                    <el-link v-if="!isSeRender" id="selink" :underline="false" icon="el-icon-magic-stick"
                        style="margin-left: 41.2vh;" @click="previewPic('se')"></el-link>
                    <el-link id="selink" :underline="false" icon="el-icon-camera-solid" style="margin-left: 41.2vh;"
                        @click="downloadSnap('se')"></el-link>
                    <el-slider id="seSlider" class="sliderMain" :max="semgentLen" :show-tooltip="false" v-model="seIndex"
                        vertical height="35vh" style="margin-left: 39vh;" @input="sliderchange1">
                    </el-slider>
                    <span class="text">{{ seIndex + 1 }} of {{ semgentUrls.length }}</span>
                </div>
                <div class="out2">
                    <span class="postitonText" style="margin-left: 20vh;">S</span>
                    <span class="postitonText" style="margin-top: 20vh;">A</span>
                    <span class="postitonText" style="margin-left: 39vh; margin-top: 20vh;">I</span>
                    <span class="postitonText" style="margin-left: 20vh; margin-top: 38vh;">P</span>
                    <div style="position: absolute; z-index: 999; width: 40vh; height: 40vh;"
                        @mousewheel="handleMouseWheel3">
                        <div class="coordinateX" id="sideX" style="top: 60px"></div>
                        <div class="coordinateY" id="sideY" style="left: 117px"></div>
                    </div>
                    <div class="div2" v-for="(item, key) in sideUrls" :key="'side' + key">
                        <el-image class="image2" :src="item" v-show="sideIndex == key">
                        </el-image>
                    </div>
                    <el-link v-if="isSideRender" id="selink" :underline="false" icon="el-icon-view"
                        style="margin-left: 41.2vh;" @click="previewPic('seSide')"></el-link>
                    <el-link v-if="!isSideRender" id="selink" :underline="false" icon="el-icon-magic-stick"
                        style="margin-left: 41.2vh;" @click="previewPic('seSide')"></el-link>
                    <el-link id="sidelink" :underline="false" icon="el-icon-camera-solid" style="margin-left: 41.2vh;"
                        @click="downloadSnap('seSide')"></el-link>
                    <el-slider class="sliderMain" :max="sideLen" :show-tooltip="false" v-model="sideIndex" vertical
                        height="35vh" style="margin-left: 39vh;" @input="sliderchange3">
                    </el-slider>
                    <span class="text">{{ sideIndex + 1 }} of {{ sideUrls.length }}</span>
                </div>
                <div class="out3">
                    <span class="postitonText" style="margin-left: 20vh;">S</span>
                    <span class="postitonText" style="margin-top: 20vh;">R</span>
                    <span class="postitonText" style="margin-left: 39vh; margin-top: 20vh;">L</span>
                    <span class="postitonText" style="margin-left: 20vh; margin-top: 38vh;">I</span>
                    <div style="position: absolute; z-index: 999; width: 40vh; height: 40vh;"
                        @mousewheel="handleMouseWheel2">
                        <div class="coordinateX" id="fontX" style="top: 150px"></div>
                        <div class="coordinateY" id="fontY" style="left: 96px"></div>
                    </div>
                    <div class="div3" v-for="(item, key) in fontUrls" :key="'font-' + key">
                        <el-image class="image3" :src="item" v-show="fontIndex == key">
                        </el-image>
                    </div>
                    <el-link v-if="isFontRender" id="selink" :underline="false" icon="el-icon-view"
                        style="margin-left: 41.2vh;" @click="previewPic('seFont')"></el-link>
                    <el-link v-if="!isFontRender" id="selink" :underline="false" icon="el-icon-magic-stick"
                        style="margin-left: 41.2vh;" @click="previewPic('seFont')"></el-link>
                    <el-link id="fontlink" :underline="false" icon="el-icon-camera-solid" style="margin-left: 41.2vh;"
                        @click="downloadSnap('seFont')"></el-link>
                    <el-slider class="sliderMain" :max="fontLen" :show-tooltip="false" v-model="fontIndex" vertical
                        height="35vh" style="margin-left: 39vh;" @input="sliderchange2">
                    </el-slider>
                    <span class="text">{{ fontIndex + 1 }} of {{ fontUrls.length }}</span>
                </div>
                <div class="out4">
                    <div class="div4" ref="threeTarget">
                    </div>
                </div>
            </div>
        </el-dialog>
    </div>
</template>
<script>
import { getPicUrl, downloadPic, changePicUrl } from "@/api/segment";
import { ThreeEngine } from './js/TEngine'
import { allLights } from './js/TLights'
import { allBaseObject, setXPosition, setYPosition, setZPosition } from './js/TBaseObject'
export default {
    name: 'TumorView',
    components: {

    },
    mixins: [],
    props: {

    },
    data() {
        return {
            dialogVisible: false,
            timer: null,
            newPic: 0,
            seIndex: 77,
            fontIndex: 120,
            sideIndex: 120,
            semgentUrls: [],
            fontUrls: [],
            sideUrls: [],
            semgentLen: 0,
            fontLen: 0,
            sideLen: 0,
            rootPath: '',
            fullscreenLoading: false,
            isSeRender: true,
            isSideRender: true,
            isFontRender: true,
            ThreeEngine: null
        }
    },
    computed: {

    },
    watch: {
        seIndex(value, oldValue) {
            // 第一个参数为新值，第二个参数为旧值，不能调换顺序
            if (this.seIndex != 77) {
                setYPosition(oldValue - value)
            }
        },
        fontIndex(value, oldValue) {
            // 第一个参数为新值，第二个参数为旧值，不能调换顺序
            if (this.fontIndex != 120) {
                setXPosition(oldValue - value)
            }
        },
        sideIndex(value, oldValue) {
            // 第一个参数为新值，第二个参数为旧值，不能调换顺序
            if (this.sideIndex != 120) {
                setZPosition(oldValue - value)
            }
        }
    },
    mounted() {


    },
    methods: {
        openFullScreen() {
            let loading = this.$loading({
                lock: true,
                text: '数据加载中，请稍后...',
                spinner: 'el-icon-loading',
                background: 'rgba(255, 255, 255, 255)'
            });
            setTimeout(() => {
                this.dialogVisible = true;
            }, 1000);
            setTimeout(() => {
                console.log("调用")
                loading.close();
                this.$message({
                    showClose: true,
                    message: '加载完成',
                    type: 'success'
                });
            }, 10000);

        },
        previewPic(key) {
            let tmpPath = ""
            let loading = this.$loading({
                lock: true,
                text: '数据加载中，请稍后...',
                spinner: 'el-icon-loading',
                background: 'rgba(255, 255, 255, 255)'
            });
            if (key === 'se') {
                if (this.isSeRender) {
                    tmpPath = this.rootPath + "/original"
                    this.isSeRender = false
                } else {
                    tmpPath = this.rootPath
                    this.isSeRender = true
                }
                changePicUrl(tmpPath, key).then(res => {
                    this.semgentUrls = res.data.data;
                })
            } else if (key === 'seSide') {
                if (this.isSideRender) {
                    tmpPath = this.rootPath + "/original"
                    this.isSideRender = false
                } else {
                    tmpPath = this.rootPath
                    this.isSideRender = true
                }
                changePicUrl(tmpPath, key).then(res => {
                    this.sideUrls = res.data.data;
                })
            } else if (key === 'seFont') {
                if (this.isFontRender) {
                    tmpPath = this.rootPath + "/original"
                    this.isFontRender = false
                } else {
                    tmpPath = this.rootPath
                    this.isFontRender = true
                }
                changePicUrl(tmpPath, key).then(res => {
                    this.fontUrls = res.data.data;
                })
            }
            setTimeout(() => {
                console.log("调用")
                loading.close();
                this.$message({
                    showClose: true,
                    message: '加载完成',
                    type: 'success'
                });
            }, 3000);
        },
        downloadSnap(path) {
            let index = '';
            let tmpPath = '';
            if (path === 'se') {
                if (!this.isSeRender) {
                    tmpPath = this.rootPath + "/original"
                } else {
                    tmpPath = this.rootPath
                }
                index = this.seIndex;
            } else if (path === 'seSide') {
                if (!this.isSideRender) {
                    tmpPath = this.rootPath + "/original"
                } else {
                    tmpPath = this.rootPath
                }
                index = this.sideIndex;
            } else if (path === 'seFont') {
                if (!this.isFontRender) {
                    tmpPath = this.rootPath + "/original"
                } else {
                    tmpPath = this.rootPath
                }
                index = this.fontIndex;
            }
            let fileName = "image_" + index + ".png";
            let bucketFileName = tmpPath + "/" + path + "/" + fileName;
            let originalFileName = new Date().getTime() + ".png";
            downloadPic(bucketFileName, originalFileName).then(res => {
                this.resolveBlob(res, originalFileName)
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

        dialogOpen(val) {
            console.log("打开")
            this.rootPath = val.imgPath;
            this.getFile(val.imgPath);
        },
        open() {
            this.$nextTick(() => {
                console.log(this.$refs.threeTarget)
                this.ThreeEngine = new ThreeEngine(this.$refs.threeTarget)
                this.ThreeEngine.addObject(...allLights)  // 添加光线
                this.ThreeEngine.addObject(...allBaseObject)  // 添加基础模型
            })
        },
        dialogClose() {
            console.log("关闭")
            this.semgentUrls = []
            this.fontUrls = []
            this.sideUrls = []
        },
        handleClick(tab, event) {
            console.log(tab, event);
        },
        handleClose(done) {
            this.$confirm('是否关闭页面？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消'
            }).then(() => {
                done();
            }).catch(() => { });
        },
        getFile(rootPath) {
            getPicUrl(rootPath).then(res => {
                this.semgentUrls = res.data.data.seList;
                this.fontUrls = res.data.data.seFontList;
                this.sideUrls = res.data.data.seSideList;
                this.semgentLen = res.data.data.seList.length - 1;
                this.fontLen = res.data.data.seFontList.length - 1;
                this.sideLen = res.data.data.seSideList.length - 1;
            })
            this.openFullScreen();
        },
        handleMouseWheel1(e) {
            e.preventDefault()
            clearTimeout(this.timer)
            this.timer = setTimeout(() => {
                if (!window.scrollY) {
                    // 禁止页面滚动
                    if (e.wheelDelta) {
                        // 判断浏览器IE，谷歌滑轮事件
                        var fontX = document.getElementById('fontX');
                        var sideX = document.getElementById('sideX');
                        if (e.wheelDelta > 0) {
                            if (this.seIndex > 0) {
                                this.seIndex--;
                                fontX.setAttribute("style", "bottom: " + this.seIndex + "px !important");
                                sideX.setAttribute("style", "bottom: " + this.seIndex + "px !important");
                            }
                        }
                        if (e.wheelDelta < 0) {
                            // 当滑轮向下滚动时
                            if (this.seIndex < this.semgentUrls.length - 1) {
                                this.seIndex++;
                                fontX.setAttribute("style", "bottom: " + this.seIndex + "px !important");
                                sideX.setAttribute("style", "bottom: " + this.seIndex + "px !important");
                            }
                        }
                    }
                }
            }, 1)
        },
        handleMouseWheel2(e) {
            e.preventDefault()
            clearTimeout(this.timer)
            this.timer = setTimeout(() => {
                if (!window.scrollY) {
                    // 禁止页面滚动
                    if (e.wheelDelta) {
                        // 判断浏览器IE，谷歌滑轮事件
                        var seX = document.getElementById('seX');
                        var sideY = document.getElementById('sideY');
                        if (e.wheelDelta > 0) {
                            if (this.fontIndex > 0) {
                                this.fontIndex--;
                                seX.setAttribute("style", "top: " + this.fontIndex + "px !important");
                                sideY.setAttribute("style", "left: " + this.fontIndex + "px !important");
                            }
                        }
                        if (e.wheelDelta < 0) {
                            // 当滑轮向下滚动时
                            if (this.fontIndex < this.fontUrls.length - 1) {
                                this.fontIndex++;
                                seX.setAttribute("style", "top: " + this.fontIndex + "px !important");
                                sideY.setAttribute("style", "left: " + this.fontIndex + "px !important");
                            }
                        }
                    }
                }
            }, 1)
        },
        handleMouseWheel3(e) {
            e.preventDefault()
            clearTimeout(this.timer)
            var seY = document.getElementById('seY');
            var fontY = document.getElementById('fontY');
            this.timer = setTimeout(() => {
                if (!window.scrollY) {
                    // 禁止页面滚动
                    if (e.wheelDelta) {
                        // 判断浏览器IE，谷歌滑轮事件
                        if (e.wheelDelta > 0) {
                            if (this.sideIndex > 0) {
                                this.sideIndex--;
                                seY.setAttribute("style", "left: " + this.sideIndex + "px !important");
                                fontY.setAttribute("style", "left: " + this.sideIndex + "px !important");
                            }
                        }
                        if (e.wheelDelta < 0) {
                            // 当滑轮向下滚动时
                            if (this.sideIndex < this.sideUrls.length - 1) {
                                this.sideIndex++;
                                seY.setAttribute("style", "left: " + this.sideIndex + "px !important");
                                fontY.setAttribute("style", "left: " + this.sideIndex + "px !important");
                            }
                        }
                    }
                }
            }, 1)
        },
        sliderchange1() {
            var fontX = document.getElementById('fontX');
            var sideX = document.getElementById('sideX');
            fontX.setAttribute("style", "bottom: " + (this.seIndex + 75) + "px !important");
            sideX.setAttribute("style", "bottom: " + (this.seIndex + 75) + "px !important");
        },
        sliderchange2() {
            var seX = document.getElementById('seX');
            var sideY = document.getElementById('sideY');
            seX.setAttribute("style", "top: " + (this.fontIndex + 20) + "px !important");
            sideY.setAttribute("style", "left: " + (this.fontIndex + 20) + "px !important");
        },
        sliderchange3() {
            var seY = document.getElementById('seY');
            var fontY = document.getElementById('fontY');
            seY.setAttribute("style", "left: " + (this.sideIndex + 20) + "px !important");
            fontY.setAttribute("style", "left: " + (this.sideIndex + 20) + "px !important");
        }
    }
};
</script>
<style>
.out1 {
    width: 45vh;
    height: 45vh;
    /* border: 2px solid red; */
    position: absolute;
    z-index: 100;
}

.out2 {
    width: 45vh;
    height: 45vh;
    margin-left: 50vh;
    /* border: 2px solid blue; */
    position: absolute;
    z-index: 100;
}

.out3 {
    width: 45vh;
    height: 45vh;
    margin-top: 50vh;
    margin-left: 50vh;
    /* border: 2px solid gold; */
    position: absolute;
    z-index: 100;
}

.out4 {
    width: 45vh;
    height: 45vh;
    margin-top: 50vh;
    /* margin-left: 50vh; */
    /* border: 2px solid gold; */
    position: absolute;
    z-index: 100;
}

.div1 {
    width: 40vh;
    height: 40vh;
    position: absolute;
    z-index: 100;
    /* border: 2px solid red; */
}

.div2 {
    width: 40vh;
    height: 40vh;
    position: absolute;
    z-index: 100;
    /* border: 2px solid blue; */
}

.div3 {
    width: 40vh;
    height: 40vh;
    position: absolute;
    z-index: 100;
    /* border: 2px solid gold; */
}

.div4 {
    width: 40vh;
    height: 40vh;
    position: absolute;
    z-index: 100;
    /* border: 2px solid gold; */
    background-color: black;
}

.image1 {
    width: 40vh;
    height: 40vh;
}

.image2 {
    width: 40vh;
    height: 40vh;
}

.image3 {
    width: 40vh;
    height: 40vh;
}

.text {
    position: absolute;
    width: 65px;
    top: 41vh;
    left: 35vh;
    font-size: smaller;
}

.el-slider.is-vertical .el-slider__runway {
    width: 15px !important;
}

.el-slider.is-vertical .el-slider__bar {
    width: 15px !important;
}

.postitonText {
    color: gold;
    position: absolute;
    z-index: 9999;
    font-family: 'Franklin Gothic Medium', 'Arial Narrow', Arial, sans-serif;
    font-size: medium;
}

.coordinateX {
    width: 40vh;
    height: 0px;
    position: absolute;
    z-index: 9999;
    /* margin-top: 20vh; */
    border: 2px dashed rgb(68, 63, 221);
}

.coordinateY {
    width: 0px;
    height: 40vh;
    position: absolute;
    z-index: 9999;
    /* margin-left: 20vh; */
    border: 2px dashed rgb(68, 63, 221);
}

.el-loading-mask {
    position: absolute !important;
    z-index: 10000 !important;
    background-color: rgba(255, 255, 255, .9);
    margin: 0;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    transition: opacity .3s
}
</style>

<style lang="scss">
.sliderMain {
    .el-slider {
        width: 190px;
        height: 6px;
        position: relative;
        top: -14px;
        left: 2px;
    }

    .el-slider__runway {
        background: #dedede;
        border-radius: 0;
    }


    .el-slider__bar {
        background: #dedede;
        border-radius: 0;
    }


    .el-slider__button {
        width: 15px;
        height: 20px;
        background: #ffffff;
        border: none;
        border-radius: 0;
        box-shadow: 0 2px 4px rgba(0, 0, 0, .12), 0 0 6px rgba(0, 0, 0, .04);
        margin-left: 10px;
    }

    .el-slider__button-wrapper::after {
        width: 0%;
    }

}
</style>