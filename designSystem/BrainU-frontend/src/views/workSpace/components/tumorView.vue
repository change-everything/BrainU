<template>
  <el-dialog :visible.sync="dialogVisible" custom-class="medical-viewer-dialog" width="96%" top="2vh"
    :close-on-click-modal="false" append-to-body @opened="initThreeScene" @closed="releaseViewer">
    <template slot="title">
      <div class="viewer-titlebar">
        <div class="viewer-brand"><span class="viewer-logo">B</span><div><strong>BrainU 影像工作站</strong><small>多平面重建与肿瘤分割预览</small></div></div>
        <div class="patient-summary">
          <span><small>患者</small>{{ patient.patientName || '未命名' }}</span>
          <span><small>病例号</small>{{ patient.id || '--' }}</span>
          <span><small>影像序列</small>MRI · MPR</span>
        </div>
      </div>
    </template>

    <div class="viewer-toolbar">
      <div class="tool-group"><button title="窗宽窗位"><i class="el-icon-s-operation"></i><span>窗宽窗位</span></button><button title="测量"><i class="el-icon-data-line"></i><span>测量</span></button><button title="复位"><i class="el-icon-refresh-left"></i><span>复位</span></button></div>
      <div class="viewer-legend"><span class="legend-segment"></span>分割结果 <span class="legend-crosshair"></span>定位线</div>
    </div>

    <div v-loading="studyLoading" element-loading-text="正在读取影像序列…" element-loading-background="rgba(5, 14, 20, .88)" class="viewer-grid">
      <slice-viewport title="轴位" subtitle="AXIAL" :urls="semgentUrls" v-model="seIndex" :segmented="isSeRender"
        :crosshair-x="slicePercent(sideIndex, sideUrls)" :crosshair-y="slicePercent(fontIndex, fontUrls)"
        :orientation="{ top:'A', right:'L', bottom:'P', left:'R' }" @toggle="toggleSeries('se')" @download="downloadCurrent('se')" />
      <slice-viewport title="矢状位" subtitle="SAGITTAL" :urls="sideUrls" v-model="sideIndex" :segmented="isSideRender"
        :crosshair-x="slicePercent(fontIndex, fontUrls)" :crosshair-y="100 - slicePercent(seIndex, semgentUrls)"
        :orientation="{ top:'S', right:'I', bottom:'I', left:'A' }" @toggle="toggleSeries('seSide')" @download="downloadCurrent('seSide')" />
      <slice-viewport title="冠状位" subtitle="CORONAL" :urls="fontUrls" v-model="fontIndex" :segmented="isFontRender"
        :crosshair-x="slicePercent(sideIndex, sideUrls)" :crosshair-y="100 - slicePercent(seIndex, semgentUrls)"
        :orientation="{ top:'S', right:'L', bottom:'I', left:'R' }" @toggle="toggleSeries('seFont')" @download="downloadCurrent('seFont')" />

      <section class="volume-viewport">
        <header><div><span></span><strong>三维定位</strong><small>3D LOCALIZER</small></div><em>拖拽旋转 · 滚轮缩放</em></header>
        <div ref="threeTarget" class="three-stage"></div>
        <footer><span><i></i>X 轴</span><span><i></i>Y 轴</span><span><i></i>Z 轴</span></footer>
      </section>
    </div>

    <div class="viewer-statusbar"><span><i class="status-online"></i>影像数据已加密加载</span><span>滚轮浏览切片 · 相邻 2 帧智能预取</span><span>{{ totalSlices }} 张切片</span></div>
  </el-dialog>
</template>

<script>
import { getPicUrl, downloadPic, changePicUrl } from '@/api/segment'
import SliceViewport from './sliceViewport'
import { ThreeEngine } from './js/TEngine'
import { allLights } from './js/TLights'
import { allBaseObject, resetPositions, setXPosition, setYPosition, setZPosition } from './js/TBaseObject'

export default {
  name: 'TumorView',
  components: { SliceViewport },
  data() {
    return {
      dialogVisible: false,
      studyLoading: false,
      patient: {},
      rootPath: '',
      seIndex: 0,
      fontIndex: 0,
      sideIndex: 0,
      semgentUrls: [],
      fontUrls: [],
      sideUrls: [],
      isSeRender: true,
      isSideRender: true,
      isFontRender: true,
      threeEngine: null,
      requestVersion: 0,
      syncingIndices: false
    }
  },
  computed: {
    totalSlices() { return this.semgentUrls.length + this.fontUrls.length + this.sideUrls.length }
  },
  watch: {
    seIndex(value, oldValue) { if (this.dialogVisible && !this.syncingIndices && Number.isFinite(oldValue)) setYPosition(oldValue - value) },
    fontIndex(value, oldValue) { if (this.dialogVisible && !this.syncingIndices && Number.isFinite(oldValue)) setXPosition(oldValue - value) },
    sideIndex(value, oldValue) { if (this.dialogVisible && !this.syncingIndices && Number.isFinite(oldValue)) setZPosition(oldValue - value) }
  },
  beforeDestroy() { this.releaseViewer() },
  methods: {
    async dialogOpen(patient) {
      this.patient = patient || {}
      this.rootPath = patient.imgPath
      this.dialogVisible = true
      this.studyLoading = true
      this.syncingIndices = true
      this.resetStudy()
      const version = ++this.requestVersion
      try {
        const res = await getPicUrl(this.rootPath)
        if (version !== this.requestVersion) return
        const data = res.data.data || {}
        this.semgentUrls = data.seList || []
        this.fontUrls = data.seFontList || []
        this.sideUrls = data.seSideList || []
        this.seIndex = this.middleIndex(this.semgentUrls)
        this.fontIndex = this.middleIndex(this.fontUrls)
        this.sideIndex = this.middleIndex(this.sideUrls)
      } catch (error) {
        this.$message.error('影像序列读取失败，请稍后重试')
      } finally {
        if (version === this.requestVersion) {
          this.studyLoading = false
          this.$nextTick(() => { this.syncingIndices = false })
        }
      }
    },
    middleIndex(list) { return list.length ? Math.floor((list.length - 1) / 2) : 0 },
    slicePercent(index, list) { return list.length > 1 ? Math.round(index / (list.length - 1) * 100) : 50 },
    resetStudy() {
      this.semgentUrls = []
      this.fontUrls = []
      this.sideUrls = []
      this.seIndex = this.fontIndex = this.sideIndex = 0
      this.isSeRender = this.isSideRender = this.isFontRender = true
      resetPositions()
    },
    initThreeScene() {
      this.disposeThreeScene()
      this.$nextTick(() => {
        if (!this.$refs.threeTarget) return
        this.threeEngine = new ThreeEngine(this.$refs.threeTarget)
        this.threeEngine.addObject(...allLights)
        this.threeEngine.addObject(...allBaseObject)
      })
    },
    async toggleSeries(key) {
      const config = {
        se: { flag: 'isSeRender', list: 'semgentUrls', index: 'seIndex' },
        seSide: { flag: 'isSideRender', list: 'sideUrls', index: 'sideIndex' },
        seFont: { flag: 'isFontRender', list: 'fontUrls', index: 'fontIndex' }
      }[key]
      if (!config) return
      const nextSegmented = !this[config.flag]
      const path = nextSegmented ? this.rootPath : this.rootPath + '/original'
      this.studyLoading = true
      try {
        const res = await changePicUrl(path, key)
        this[config.list] = res.data.data || []
        this[config.index] = Math.min(this[config.index], Math.max(this[config.list].length - 1, 0))
        this[config.flag] = nextSegmented
      } catch (error) {
        this.$message.error('切片模式切换失败')
      } finally {
        this.studyLoading = false
      }
    },
    downloadCurrent(key) {
      const config = {
        se: { rendered: this.isSeRender, index: this.seIndex },
        seSide: { rendered: this.isSideRender, index: this.sideIndex },
        seFont: { rendered: this.isFontRender, index: this.fontIndex }
      }[key]
      if (!config) return
      const path = config.rendered ? this.rootPath : this.rootPath + '/original'
      const fileName = 'image_' + config.index + '.png'
      downloadPic(path + '/' + key + '/' + fileName, fileName).then(res => this.resolveBlob(res, fileName))
    },
    resolveBlob(res, fileName) {
      const url = URL.createObjectURL(new Blob([res.data]))
      const link = document.createElement('a')
      link.href = url
      link.download = fileName
      link.click()
      URL.revokeObjectURL(url)
    },
    disposeThreeScene() {
      if (this.threeEngine) this.threeEngine.dispose()
      this.threeEngine = null
    },
    releaseViewer() {
      this.requestVersion++
      this.disposeThreeScene()
      this.resetStudy()
      this.studyLoading = false
      this.syncingIndices = false
    }
  }
}
</script>

<style lang="scss">
.medical-viewer-dialog { height:96vh; margin-bottom:0 !important; display:flex; flex-direction:column; overflow:hidden; background:#07131b; border:1px solid #29404c; border-radius:10px; box-shadow:0 24px 70px rgba(0,0,0,.45); }
.medical-viewer-dialog .el-dialog__header { height:64px; flex:0 0 64px; padding:0 60px 0 18px; display:flex; align-items:center; background:#10232e; border-bottom:1px solid #29404c; box-sizing:border-box; }.medical-viewer-dialog .el-dialog__headerbtn { top:22px; }.medical-viewer-dialog .el-dialog__headerbtn .el-dialog__close { color:#8ca3af; }.medical-viewer-dialog .el-dialog__body { min-height:0; flex:1; display:flex; flex-direction:column; padding:0; color:#c9d8df; }
.viewer-titlebar { width:100%; display:flex; align-items:center; justify-content:space-between; }.viewer-brand { display:flex; align-items:center; gap:10px; }.viewer-logo { width:32px; height:32px; display:grid; place-items:center; border-radius:8px; color:#07131b; background:#43c9c0; font-weight:800; }.viewer-brand strong,.viewer-brand small { display:block; }.viewer-brand strong { color:#eff7f9; font-size:14px; }.viewer-brand small { margin-top:3px; color:#6f8996; font-size:9px; letter-spacing:.5px; }.patient-summary { display:flex; align-items:center; gap:28px; margin-right:24px; }.patient-summary span { color:#c0d0d7; font-size:11px; }.patient-summary small { display:block; margin-bottom:3px; color:#617b88; font-size:8px; }
.viewer-toolbar { height:46px; flex:0 0 46px; display:flex; align-items:center; justify-content:space-between; padding:0 14px; background:#0b1922; border-bottom:1px solid #233743; }.tool-group { display:flex; gap:5px; }.tool-group button { height:30px; display:flex; align-items:center; gap:6px; padding:0 10px; border:1px solid transparent; border-radius:5px; color:#7f99a6; background:transparent; font-size:10px; cursor:pointer; }.tool-group button:hover { color:#d9e7ec; border-color:#2b4654; background:#142630; }.viewer-legend { display:flex; align-items:center; gap:7px; color:#6f8996; font-size:9px; }.legend-segment { width:12px; height:5px; margin-left:13px; background:#db4c72; }.legend-crosshair { width:12px; border-top:1px dashed #35a9df; }
.viewer-grid { min-height:0; flex:1; display:grid; grid-template-columns:1fr 1fr; grid-template-rows:1fr 1fr; gap:6px; padding:6px; background:#040a0e; }.volume-viewport { min-width:0; min-height:0; display:flex; flex-direction:column; overflow:hidden; background:#071018; border:1px solid #203441; border-radius:8px; }.volume-viewport header { height:42px; flex:0 0 42px; display:flex; align-items:center; justify-content:space-between; padding:0 12px; color:#d8e5eb; background:#101f29; border-bottom:1px solid #203441; }.volume-viewport header div { display:flex; align-items:center; gap:8px; }.volume-viewport header span { width:6px; height:6px; border-radius:50%; background:#e6bb4f; }.volume-viewport header strong { font-size:12px; }.volume-viewport header small { color:#698391; font-size:10px; }.volume-viewport header em { color:#526b77; font-size:9px; font-style:normal; }.three-stage { min-height:0; flex:1; overflow:hidden; background:radial-gradient(circle at center, #122530, #03070a 70%); }.three-stage canvas { display:block; }.volume-viewport footer { height:38px; flex:0 0 38px; display:flex; align-items:center; gap:16px; padding:0 12px; color:#607b88; background:#101f29; border-top:1px solid #203441; font-size:9px; }.volume-viewport footer i { display:inline-block; width:8px; margin-right:5px; border-top:2px solid #dd4e70; }.volume-viewport footer span:nth-child(2) i { border-color:#4bc88a; }.volume-viewport footer span:nth-child(3) i { border-color:#4b9fe0; }
.viewer-statusbar { height:28px; flex:0 0 28px; display:flex; align-items:center; justify-content:space-between; padding:0 14px; color:#55707d; background:#0a171f; border-top:1px solid #203441; font-size:9px; }.status-online { display:inline-block; width:6px; height:6px; margin-right:6px; border-radius:50%; background:#38c698; }
@media (max-width: 1000px) { .patient-summary span:nth-child(3) { display:none; }.medical-viewer-dialog { width:98% !important; }.viewer-grid { grid-template-columns:1fr; grid-template-rows:repeat(4, 340px); overflow-y:auto; }.viewer-grid > * { min-height:340px; } }
</style>
