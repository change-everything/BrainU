import { BoxGeometry, Color, Mesh, MeshStandardMaterial } from "three"

export const allBaseObject = []  // 返回所有基础模型

// 创建地面
export const box = new Mesh(
    new BoxGeometry(2, 155, 2),  // 设置立方体的大小
    new MeshStandardMaterial({   // 设置材质
        color: '#ff3366',  // 设置材质的颜色
        metalness: 0.5,   // 金属度 （1 最像金属，0 最不想金属）
        roughness: 0   // 粗糙度（0 最光滑，1 最粗糙）
    })

)
export const box1 = new Mesh(
    new BoxGeometry(240, 2, 2),  // 设置立方体的大小
    new MeshStandardMaterial({   // 设置材质
        color: '#ff3366',  // 设置材质的颜色
        metalness: 0.5,   // 金属度 （1 最像金属，0 最不想金属）
        roughness: 0   // 粗糙度（0 最光滑，1 最粗糙）
    })

)
export const box2 = new Mesh(
    new BoxGeometry(2, 2, 240),  // 设置立方体的大小
    new MeshStandardMaterial({   // 设置材质
        color: '#ff3366',  // 设置材质的颜色
        metalness: 0.5,   // 金属度 （1 最像金属，0 最不想金属）
        roughness: 0   // 粗糙度（0 最光滑，1 最粗糙）
    })

)
box.name = 'box'  // 设置模型 name
box1.name = 'box1'  // 设置模型 name
box2.name = 'box2'  // 设置模型 name
// box.position.x = 25

// 给模型添加鼠标移入事件
box.addEventListener("mouseenter", () => {
    box.material.color = new Color("#ff3366")  // 修改材质颜色为红色
})
// 给模型添加鼠标移除事件
box.addEventListener("mouseleave", () => {
    box.material.color = new Color("#ff3366") // 恢复模型的材质
})


allBaseObject.push(box, box1, box2)  // 添加到模型数组


export function setXPosition(num) {
    box1.position.x += num
}
export function setYPosition(num) {
    box.position.y += num
}
export function setZPosition(num) {
    box2.position.z += num
}