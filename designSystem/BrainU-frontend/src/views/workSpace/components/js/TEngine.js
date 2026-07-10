import { WebGLRenderer, Scene, PerspectiveCamera, Vector3, Raycaster, Vector2 } from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls'
import { TransformControls } from 'three/examples/jsm/controls/TransformControls'

export class ThreeEngine {
    constructor(dom) {
        this.dom = dom
        this.disposed = false
        this.animationId = null
        this.cacheObject = null
        this.mouse = new Vector2()
        this.raycaster = new Raycaster()

        this.renderer = new WebGLRenderer({ antialias: true, alpha: true, powerPreference: 'high-performance' })
        this.renderer.setPixelRatio(Math.min(window.devicePixelRatio || 1, 2))
        this.renderer.setClearColor(0x000000, 0)
        dom.appendChild(this.renderer.domElement)

        this.scene = new Scene()
        this.camera = new PerspectiveCamera(45, 1, 1, 1000)
        this.camera.position.set(250, 0, 250)
        this.camera.lookAt(new Vector3(0, 0, 0))
        this.camera.up = new Vector3(0, 1, 0)

        this.controls = new OrbitControls(this.camera, this.renderer.domElement)
        this.controls.enableDamping = true
        this.controls.dampingFactor = 0.08

        this.transformControls = new TransformControls(this.camera, this.renderer.domElement)
        this.transformControls.enabled = false
        this.scene.add(this.transformControls)

        this.handlePointerMove = this.onPointerMove.bind(this)
        this.handleClick = this.onClick.bind(this)
        this.handleKeyUp = this.onKeyUp.bind(this)
        this.renderer.domElement.addEventListener('pointermove', this.handlePointerMove)
        this.renderer.domElement.addEventListener('click', this.handleClick)
        document.addEventListener('keyup', this.handleKeyUp)

        this.handleResize = this.resize.bind(this)
        this.resizeObserver = typeof ResizeObserver !== 'undefined' ? new ResizeObserver(this.handleResize) : null
        if (this.resizeObserver) this.resizeObserver.observe(dom)
        else window.addEventListener('resize', this.handleResize)

        this.resize()
        this.animate()
    }

    resize() {
        if (this.disposed || !this.dom) return
        const width = Math.max(this.dom.clientWidth, 1)
        const height = Math.max(this.dom.clientHeight, 1)
        this.camera.aspect = width / height
        this.camera.updateProjectionMatrix()
        this.renderer.setSize(width, height, false)
    }

    getIntersections(event) {
        const rect = this.renderer.domElement.getBoundingClientRect()
        this.mouse.x = ((event.clientX - rect.left) / rect.width) * 2 - 1
        this.mouse.y = -((event.clientY - rect.top) / rect.height) * 2 + 1
        this.raycaster.setFromCamera(this.mouse, this.camera)
        const selectable = this.scene.children.filter(item => item !== this.transformControls)
        return this.raycaster.intersectObjects(selectable, true)
    }

    onPointerMove(event) {
        const hit = this.getIntersections(event)[0]
        const object = hit && hit.object
        if (object === this.cacheObject) return
        if (this.cacheObject) this.cacheObject.dispatchEvent({ type: 'mouseleave' })
        if (object) object.dispatchEvent({ type: 'mouseenter' })
        this.cacheObject = object || null
    }

    onClick(event) {
        const hit = this.getIntersections(event)[0]
        if (!hit) {
            this.transformControls.detach()
            this.transformControls.enabled = false
            return
        }
        this.transformControls.enabled = true
        this.transformControls.attach(hit.object)
    }

    onKeyUp(event) {
        if (!this.transformControls.enabled) return
        const modes = { e: 'scale', r: 'rotate', t: 'translate' }
        if (modes[event.key.toLowerCase()]) this.transformControls.setMode(modes[event.key.toLowerCase()])
    }

    animate() {
        if (this.disposed) return
        this.animationId = requestAnimationFrame(() => this.animate())
        this.controls.update()
        this.renderer.render(this.scene, this.camera)
    }

    addObject(...objects) {
        objects.forEach(object => this.scene.add(object))
    }

    dispose() {
        if (this.disposed) return
        this.disposed = true
        if (this.animationId) cancelAnimationFrame(this.animationId)
        if (this.resizeObserver) this.resizeObserver.disconnect()
        else window.removeEventListener('resize', this.handleResize)
        this.renderer.domElement.removeEventListener('pointermove', this.handlePointerMove)
        this.renderer.domElement.removeEventListener('click', this.handleClick)
        document.removeEventListener('keyup', this.handleKeyUp)
        this.transformControls.detach()
        this.transformControls.dispose()
        this.controls.dispose()
        this.renderer.dispose()
        this.renderer.forceContextLoss()
        if (this.renderer.domElement.parentNode) this.renderer.domElement.parentNode.removeChild(this.renderer.domElement)
        this.scene.clear()
        this.dom = null
    }
}
