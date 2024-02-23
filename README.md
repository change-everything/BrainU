
![logo.png](designSystem%2FBrainU-backend%2Fimg%2Flogo.png)


# brainU
## 项目介绍

基于多模态UNet神经网络+Pytorch+opencv+SpringBoot+Vue2的脑肿瘤自动分割web平台项目。用户可以针对某个病人的脑肿瘤核磁共振扫描的文件上传平台进行肿瘤分割。

## 模型训练
### Multi-modal U-Net 
![mmunet.png](designSystem%2FBrainU-backend%2Fimg%2Fmmunet.png)
#### 复现
##### 模型参数结构表
![paaramtable.png](designSystem%2FBrainU-backend%2Fimg%2Fpaaramtable.png)
![table2.png](designSystem%2FBrainU-backend%2Fimg%2Ftable2.png)
![table3.png](designSystem%2FBrainU-backend%2Fimg%2Ftable3.png)

##### 训练环境
![env.png](designSystem%2FBrainU-backend%2Fimg%2Fenv.png)
![env2.png](designSystem%2FBrainU-backend%2Fimg%2Fenv2.png)

##### 神经网络结构参数
![unetparams.png](designSystem%2FBrainU-backend%2Fimg%2Funetparams.png)
- batch_size:指定每次训练时使用的样本数。较大的 batch_size 能够提高训练速度，
但可能会降低模型的准确性。
- epoch: 指定训练的轮数。每一轮训练都会使用全部的训练数据，这有助于模型逐渐
提高准确性，但如果训练轮数过多，可能会导致过拟合。
- patience: 指定在验证集上连续多少个 epoch 的性能没有提升时，停止训练。这可以
避免过拟合和浪费时间和计算资源。
- learning rate drop: 当模型训练到一定程度时，为了避免过拟合和提高准确性，我
们可以降低学习率。此超参数可以指定学习率下降的速度和幅度。
- early stop: 当模型在验证集上的性能不再提升时，可以提前停止训练以节省时间和
计算资源。此超参数可以指定在多少个 epoch 没有提升时，停止训练。
- initial learning rate: 指定模型最初的学习率。较高的学习率可以提高训练速度，
但可能会导致模型不稳定和性能下降，本次训练使用 Adam 自适应学习率算法，所以算法
将根据损失函数的变化情况自动调整学习率的大小。

##### 实验结果
- Multi-modal U-Net脑部胶质瘤分割示例，从左到右分别为磁共振成像液体衰减反转序
  列、T1 加权成像、T1c造影剂成像、T2 加权成像、模型预测的肿瘤掩模图以及真实的肿瘤掩模图
![result.png](designSystem%2FBrainU-backend%2Fimg%2Fresult.png)

- IoU 测量每个类别的预测标签和
  地面实况标签之间的重叠。发现标签 0 的 IoU 为 99.6%，表明该模型准确地识别了此类。
  标签 1、2 和 3 的 IoU 值也相对较高，分别为 78.4%、83.9%和 75%。这表明该模型能够准
  确识别这些类，尽管还有一些改进的空间。另一方面，发现标签 4 的 IoU 为 87.2%，表明
  该模型能够准确识别这一类别。

![result1.png](designSystem%2FBrainU-backend%2Fimg%2Fresult1.png)



## 架构图
![system.png](designSystem%2FBrainU-backend%2Fimg%2Fsystem.png)

## 流程图
![process.png](designSystem%2FBrainU-backend%2Fimg%2Fprocess.png)

## 模块设计图
![design.png](designSystem%2FBrainU-backend%2Fimg%2Fdesign.png)

## 系统用例图
![use.png](designSystem%2FBrainU-backend%2Fimg%2Fuse.png)

## 时序图
### 肿瘤分割
![timep.png](designSystem%2FBrainU-backend%2Fimg%2Ftimep.png)


## 项目亮点

1. **成功复现UNet和muti-modal UNet网络：**
   - 基于PyTorch框架，成功复现了UNet和muti-modal UNet网络，并利用2015年脑肿瘤分割大赛的数据集进行训练。
   - 对数据集进行了有效的预处理，并划分为训练集和测试集，在实测中达到了95%的准确率。

2. **前后端交互实现在线脑肿瘤分割：**
   - 借助Spring Boot和Vue框架，实现了系统的后端和前端交互。
   - 调用自己开发的脑肿瘤分割接口，实现了在线脑肿瘤分割，为用户提供了便捷的服务。

3. **数据可靠存储和高效管理：**
   - 使用Minio进行数据的可靠存储和高效管理，将用户上传的脑部MRI影像保存到Minio中，保证了数据的安全性和可靠性。

4. **引入评分机制实现模型的可迭代性：**
   - 引入评分机制，用户可以对本次分割的精准度进行评分。
   - 如果分数高于阈值，将本次训练参数保存到模型文件中，实现了模型的自我训练和迭代优化。



## 截图
### 登录
![login.png](designSystem%2FBrainU-backend%2Fimg%2Flogin.png)
### 数据添加
![add.png](designSystem%2FBrainU-backend%2Fimg%2Fadd.png)
![add2.png](designSystem%2FBrainU-backend%2Fimg%2Fadd2.png)
### 工作空间
![p1.png](designSystem%2FBrainU-backend%2Fimg%2Fp1.png)
![p2.png](designSystem%2FBrainU-backend%2Fimg%2Fp2.png)
### 肿瘤交互
![tumor.png](designSystem%2FBrainU-backend%2Fimg%2Ftumor.png)
### 模型管理
![model.png](designSystem%2FBrainU-backend%2Fimg%2Fmodel.png)
### 医生管理
![doctor.png](designSystem%2FBrainU-backend%2Fimg%2Fdoctor.png)

## 技术栈


![Static Badge](https://img.shields.io/badge/Spring%20Boot-green)
![Static Badge](https://img.shields.io/badge/Redis-red)
![Static Badge](https://img.shields.io/badge/Vue2-skyblue)
![Static Badge](https://img.shields.io/badge/ElementUI-skyblue)
![Static Badge](https://img.shields.io/badge/python-skyblue)
![Static Badge](https://img.shields.io/badge/pytorch-skyblue)
![Static Badge](https://img.shields.io/badge/opencv-green)
...


## 系统部署
待完善

## 作者

- [@peiYp](https://github.com/change-everything)


## 反馈

如果你有任何反馈，请联系我：pyptsguas@163.com

