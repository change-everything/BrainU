import { AmbientLight, PointLight } from "three"


/**
 * 光线
 */
export const allLights = []

// 添加环境光
export const ambientLight = new AmbientLight('rgb(255,255,255)', 0.8)

// 点光源
export const pointLight = new PointLight(
    'rgb(255,255,255)',
    0.5,
    600,
    0.2
)
pointLight.position.set(0, 50, 100)  // 设置点光源位置

allLights.push(ambientLight, pointLight)