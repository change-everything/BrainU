<template>
  <section class="slice-viewport" @wheel.prevent="handleWheel">
    <header class="viewport-header">
      <div><span class="series-dot"></span><strong>{{ title }}</strong><small>{{ subtitle }}</small></div>
      <div class="viewport-actions">
        <button type="button" :title="segmented ? '查看原始影像' : '查看分割结果'" @click.stop="$emit('toggle')">
          <i :class="segmented ? 'el-icon-view' : 'el-icon-magic-stick'"></i>
        </button>
        <button type="button" title="下载当前切片" @click.stop="$emit('download')"><i class="el-icon-download"></i></button>
      </div>
    </header>

    <div class="image-stage">
      <div v-if="!currentUrl" class="empty-state"><i class="el-icon-picture-outline"></i><span>暂无切片</span></div>
      <img v-else :key="currentUrl" :src="currentUrl" :alt="title" @load="onImageLoaded" @error="onImageError">
      <div v-if="loading && currentUrl" class="image-loading"><i class="el-icon-loading"></i></div>
      <span class="orientation orientation-top">{{ orientation.top }}</span>
      <span class="orientation orientation-right">{{ orientation.right }}</span>
      <span class="orientation orientation-bottom">{{ orientation.bottom }}</span>
      <span class="orientation orientation-left">{{ orientation.left }}</span>
      <span class="crosshair crosshair-x" :style="{ top: crosshairY + '%' }"></span>
      <span class="crosshair crosshair-y" :style="{ left: crosshairX + '%' }"></span>
    </div>

    <footer class="viewport-footer">
      <el-slider :value="safeIndex" :min="0" :max="maxIndex" :show-tooltip="false" @input="updateIndex" />
      <span>{{ displayIndex }} / {{ urls.length }}</span>
    </footer>
  </section>
</template>

<script>
export default {
  name: 'SliceViewport',
  props: {
    title: { type: String, required: true },
    subtitle: { type: String, default: '' },
    urls: { type: Array, default: () => [] },
    value: { type: Number, default: 0 },
    segmented: { type: Boolean, default: true },
    crosshairX: { type: Number, default: 50 },
    crosshairY: { type: Number, default: 50 },
    orientation: { type: Object, default: () => ({ top: 'A', right: 'L', bottom: 'P', left: 'R' }) }
  },
  data() { return { loading: false, preloadCache: new Map() } },
  computed: {
    maxIndex() { return Math.max(this.urls.length - 1, 0) },
    safeIndex() { return Math.min(Math.max(this.value, 0), this.maxIndex) },
    displayIndex() { return this.urls.length ? this.safeIndex + 1 : 0 },
    currentUrl() { return this.urls[this.safeIndex] || '' }
  },
  watch: {
    currentUrl: {
      immediate: true,
      handler(url) {
        this.loading = Boolean(url)
        this.preloadNeighbours()
      }
    },
    urls() {
      this.preloadCache.clear()
      this.preloadNeighbours()
    }
  },
  beforeDestroy() { this.preloadCache.clear() },
  methods: {
    updateIndex(index) { this.$emit('input', index) },
    handleWheel(event) {
      if (!this.urls.length) return
      this.updateIndex(Math.min(this.maxIndex, Math.max(0, this.safeIndex + (event.deltaY > 0 ? 1 : -1))))
    },
    onImageLoaded() { this.loading = false },
    onImageError() { this.loading = false },
    preloadNeighbours() {
      if (typeof Image === 'undefined') return
      ;[-2, -1, 1, 2].forEach(offset => {
        const url = this.urls[this.safeIndex + offset]
        if (!url || this.preloadCache.has(url)) return
        const image = new Image()
        image.decoding = 'async'
        image.src = url
        this.preloadCache.set(url, image)
      })
      // Keep the cache deliberately small so long studies do not accumulate
      // decoded images in memory while the user scrolls through a series.
      if (this.preloadCache.size > 12) {
        const keep = new Set(this.urls.slice(Math.max(0, this.safeIndex - 3), this.safeIndex + 4))
        Array.from(this.preloadCache.keys()).forEach(url => { if (!keep.has(url)) this.preloadCache.delete(url) })
      }
    }
  }
}
</script>

<style scoped lang="scss">
.slice-viewport { min-width:0; display:flex; flex-direction:column; overflow:hidden; background:#071018; border:1px solid #203441; border-radius:8px; box-shadow:0 8px 22px rgba(0,0,0,.18); }
.viewport-header { height:42px; display:flex; align-items:center; justify-content:space-between; padding:0 12px; color:#d8e5eb; background:#101f29; border-bottom:1px solid #203441; }
.viewport-header > div:first-child { display:flex; align-items:center; gap:8px; }.viewport-header strong { font-size:12px; }.viewport-header small { color:#698391; font-size:10px; }.series-dot { width:6px; height:6px; border-radius:50%; background:#33c6bc; box-shadow:0 0 0 3px rgba(51,198,188,.12); }
.viewport-actions { display:flex; gap:4px; }.viewport-actions button { width:28px; height:28px; border:0; border-radius:5px; color:#8fa7b3; background:transparent; cursor:pointer; }.viewport-actions button:hover { color:#45d2c8; background:#1a303c; }
.image-stage { position:relative; flex:1; min-height:0; display:flex; align-items:center; justify-content:center; overflow:hidden; background:#03070a; }.image-stage img { width:100%; height:100%; object-fit:contain; user-select:none; -webkit-user-drag:none; }.empty-state { display:flex; flex-direction:column; align-items:center; gap:8px; color:#40535d; font-size:11px; }.empty-state i { font-size:25px; }
.image-loading { position:absolute; inset:0; display:grid; place-items:center; color:#45d2c8; background:rgba(3,7,10,.38); }.orientation { position:absolute; z-index:2; color:#e8c75d; font:600 10px/1 monospace; text-shadow:0 1px 3px #000; }.orientation-top { top:9px; left:50%; }.orientation-right { right:9px; top:50%; }.orientation-bottom { bottom:9px; left:50%; }.orientation-left { left:9px; top:50%; }
.crosshair { position:absolute; z-index:1; pointer-events:none; opacity:.55; }.crosshair-x { left:0; right:0; border-top:1px dashed #35a9df; }.crosshair-y { top:0; bottom:0; border-left:1px dashed #35a9df; }
.viewport-footer { height:38px; display:flex; align-items:center; gap:12px; padding:0 12px; color:#78909c; background:#101f29; border-top:1px solid #203441; font:10px/1 monospace; }.viewport-footer .el-slider { flex:1; }
.viewport-footer ::v-deep .el-slider__runway { height:3px; margin:14px 0; background:#263b47; }.viewport-footer ::v-deep .el-slider__bar { height:3px; background:#36bdb6; }.viewport-footer ::v-deep .el-slider__button { width:10px; height:10px; border:2px solid #36bdb6; background:#071018; }
</style>
