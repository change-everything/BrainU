# -*- coding:utf-8 -*-
# @description: todo
# @author: peiYP
import uuid
import time
import os
import re

import PIL.ImageShow
import cv2
import numpy
from minio import Minio

from data_loader.data_loader_self import DataLoaderSelf

from src.utils import *

from io import BytesIO


from torch.utils.data import DataLoader
from torch.autograd import Variable
from PIL import Image
import matplotlib.pyplot as plt
from unet.multi_unet2d import Multi_Unet

import sys
import torch
import torch.nn as nn
import torch.optim as optim
import torch.nn.functional as F

class SaveOssUtil:

    @staticmethod
    def saveImg(file_path):
        # 对客户端进行初始化
        client = Minio(
            # endpoint指定的是你Minio的远程IP及端口
            endpoint="43.143.209.51:9000",
            # accesskey指定的是你的Minio服务器访问key
            # 默认值为minioadmin
            access_key="adminMinio",
            # secret_key指定的是你登录时需要用的key，类似密码
            # 默认值也是minioadmin
            secret_key="tjdxdykw.123",
            # secure指定是否以安全模式创建Minio连接
            # 建议为False
            secure=False
        )
        # **************** get file ****************
        pattern = "Flair"  # 匹配模式
        otPattern = "result"
        path = ""
        otPath = ""

        # 获取文件夹中的所有文件名
        file_names = os.listdir(file_path)

        # 遍历文件名，匹配带有"Flair"的文件名
        for file_name in file_names:
            if re.search(pattern, file_name):
                print(file_name)
                path = file_path + file_name
            if re.search(otPattern, file_name):
                print(file_name)
                otPath = file_path + file_name


        img = load_mha_as_array(path)
        otImg = load_mha_as_array(otPath)
        zeros1 = np.zeros((42, 240, 240))
        zeros2 = np.zeros((43, 240, 240))
        img = np.concatenate((zeros1, img), axis=0)
        img = np.concatenate((img, zeros2), axis=0)
        otImg = np.concatenate((zeros1, otImg), axis=0)
        otImg = np.concatenate((otImg, zeros2), axis=0)

        img_no_append_zero = load_mha_as_array(path)
        otImg_no_append_zero = load_mha_as_array(otPath)

        print(img.shape[0])
        print(img.shape[1])
        print(img.shape[2])

        struuid = str(uuid.uuid1()).replace("-", "")
        objName1 = "resultEnv/" + struuid + "/se"
        objRelaName1 = "resultEnv/" + struuid + "/original/se"
        for i in range(img_no_append_zero.shape[0]):
            oneImg_arr = img_no_append_zero[i, :, :]
            otImg_arr = otImg_no_append_zero[i, :, :]
            # print(oneImg_arr.shape)

            # np.savetxt(r"img.txt", oneImg_arr, delimiter=',', fmt='%5s')

            max_pix = np.amax(oneImg_arr)
            label_train = oneImg_arr / max_pix  # 归一化
            image = Image.fromarray(label_train * 255)

            max_pix_ot = np.amax(otImg_arr)
            label_train_ot = otImg_arr / max_pix_ot  # 归一化
            image_ot = Image.fromarray(label_train_ot * 255)
            # # image = cv2.normalize(oneImg_arr, None, 2, 255, cv2.NORM_MINMAX, cv2.CV_8U)
            # # cv2.imwrite("D:/GraduationDesign/AI/dataset/BRATS2015/data/fairSide/" + "image_" + str(i) + ".png", image)

            # image = image.resize((240, 240))
            image = image.convert("L")
            # image = image.transpose(Image.FLIP_TOP_BOTTOM)
            #
            image_ot = image_ot.convert("L")
            # image_ot = image_ot.transpose(Image.FLIP_TOP_BOTTOM)
            # image.save("D:/GraduationDesign/AI/dataset/BRATS2015/data/fairFont/" + "image_" + str(i) + ".png")

            image_stream = BytesIO()
            image.save(image_stream, format="JPEG")
            image_stream.seek(0)

            client.put_object(bucket_name="graduationdesign", object_name=objRelaName1 + f"/image_{i}.png",
                              data=image_stream,
                              length=len(image_stream.getvalue())
                              )

            image = numpy.asarray(image)
            image_ot = numpy.asarray(image_ot)
            _, patient_mask0 = cv2.threshold(image_ot, 127, 255, 0)
            ret, patient_mask1 = cv2.threshold(image_ot, 175, 255, cv2.THRESH_TOZERO_INV)
            ret2, patient_mask2 = cv2.threshold(image_ot, 100, 100, cv2.THRESH_TOZERO_INV)
            # 轮廓线提取
            contours1, _1 = cv2.findContours(patient_mask0, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
            contours2, _2 = cv2.findContours(patient_mask1, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
            contours3, _3 = cv2.findContours(patient_mask2, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)

            image = cv2.cvtColor(image, cv2.COLOR_GRAY2BGR)

            # 在ct图上绘制轮廓线
            overlap_img = cv2.drawContours(image.copy(), contours2, -1, (127, 255, 0), thickness=-1)  # 绿色
            overlap_img1 = cv2.drawContours(overlap_img.copy(), contours1, -1, (240, 230, 140), thickness=-1)  # 黄色
            overlap_img2 = cv2.drawContours(overlap_img1.copy(), contours3, -1, (255, 69, 0), thickness=-1)  # 红色
            final_img = Image.fromarray(overlap_img2)

            # if i == 80:
            #     np.set_printoptions(threshold=np.inf)
            #     # print(overlap_img2)
            #
            #     cv2.namedWindow("Image_Sobel", cv2.WINDOW_NORMAL)
            #     cv2.imshow("Image_Sobel",overlap_img2)
            #     cv2.waitKey()
            #     cv2.destroyAllWindows()
            #     print(final_img)
                # final_img.show()
            # 将图片转换为BytesIO对象
            image_stream = BytesIO()
            final_img.save(image_stream, format="JPEG")
            image_stream.seek(0)

            client.put_object(bucket_name="graduationdesign", object_name=objName1 + f"/image_{i}.png", data=image_stream,
                              length=len(image_stream.getvalue())
                              )


        objName2 = "resultEnv/" + struuid + "/seFont"
        objRelaName2 = "resultEnv/" + struuid + "/original/seFont"
        for i in range(img.shape[1]):
            oneImg_arr = img[:, i, :]
            otImg_arr = otImg[:, i, :]
            # print(oneImg_arr.shape)

            # np.savetxt(r"img.txt", oneImg_arr, delimiter=',', fmt='%5s')

            max_pix = np.amax(oneImg_arr)
            label_train = oneImg_arr / max_pix  # 归一化
            image = Image.fromarray(label_train * 255)

            max_pix_ot = np.amax(otImg_arr)
            label_train_ot = otImg_arr / max_pix_ot  # 归一化
            image_ot = Image.fromarray(label_train_ot * 255)
            # # image = cv2.normalize(oneImg_arr, None, 2, 255, cv2.NORM_MINMAX, cv2.CV_8U)
            # # cv2.imwrite("D:/GraduationDesign/AI/dataset/BRATS2015/data/fairSide/" + "image_" + str(i) + ".png", image)

            # image = image.resize((240, 240))
            image = image.convert("L")
            image = image.transpose(Image.FLIP_TOP_BOTTOM)

            image_ot = image_ot.convert("L")
            image_ot = image_ot.transpose(Image.FLIP_TOP_BOTTOM)
            # image.save("D:/GraduationDesign/AI/dataset/BRATS2015/data/fairFont/" + "image_" + str(i) + ".png")

            image_stream = BytesIO()
            image.save(image_stream, format="JPEG")
            image_stream.seek(0)

            client.put_object(bucket_name="graduationdesign", object_name=objRelaName2 + f"/image_{i}.png",
                              data=image_stream,
                              length=len(image_stream.getvalue())
                              )

            image = numpy.asarray(image)
            image_ot = numpy.asarray(image_ot)
            _, patient_mask0 = cv2.threshold(image_ot, 127, 255, 0)
            ret, patient_mask1 = cv2.threshold(image_ot, 175, 255, cv2.THRESH_TOZERO_INV)
            ret2, patient_mask2 = cv2.threshold(image_ot, 100, 100, cv2.THRESH_TOZERO_INV)
            # 轮廓线提取
            contours1, _1 = cv2.findContours(patient_mask0, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
            contours2, _2 = cv2.findContours(patient_mask1, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
            contours3, _3 = cv2.findContours(patient_mask2, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
            image = cv2.cvtColor(image, cv2.COLOR_GRAY2BGR)
            # 在ct图上绘制轮廓线
            overlap_img = cv2.drawContours(image.copy(), contours2, -1, (127, 255, 0), thickness=-1)  # 绿色
            overlap_img1 = cv2.drawContours(overlap_img.copy(), contours1, -1, (240, 230, 140), thickness=-1)  # 黄色
            overlap_img2 = cv2.drawContours(overlap_img1.copy(), contours3, -1, (255, 69, 0), thickness=-1)  # 红色
            final_img = Image.fromarray(overlap_img2)
            # 将图片转换为BytesIO对象
            image_stream = BytesIO()
            final_img.save(image_stream, format="JPEG")
            image_stream.seek(0)
            client.put_object(bucket_name="graduationdesign", object_name=objName2 + f"/image_{i}.png",
                              data=image_stream,
                              length=len(image_stream.getvalue())
                              )


        objName3 = "resultEnv/" + struuid + "/seSide"
        objRelaName3 = "resultEnv/" + struuid + "/original/seSide"
        for i in range(img.shape[2]):
            oneImg_arr = img[:, :, i]
            otImg_arr = otImg[:, :, i]
            # print(oneImg_arr.shape)

            # np.savetxt(r"img.txt", oneImg_arr, delimiter=',', fmt='%5s')

            max_pix = np.amax(oneImg_arr)
            label_train = oneImg_arr / max_pix  # 归一化
            image = Image.fromarray(label_train * 255)

            max_pix_ot = np.amax(otImg_arr)
            label_train_ot = otImg_arr / max_pix_ot  # 归一化
            image_ot = Image.fromarray(label_train_ot * 255)
            # # image = cv2.normalize(oneImg_arr, None, 2, 255, cv2.NORM_MINMAX, cv2.CV_8U)
            # # cv2.imwrite("D:/GraduationDesign/AI/dataset/BRATS2015/data/fairSide/" + "image_" + str(i) + ".png", image)

            # image = image.resize((240, 240))
            image = image.convert("L")
            image = image.transpose(Image.FLIP_TOP_BOTTOM)

            image_ot = image_ot.convert("L")
            image_ot = image_ot.transpose(Image.FLIP_TOP_BOTTOM)
            # image.save("D:/GraduationDesign/AI/dataset/BRATS2015/data/fairFont/" + "image_" + str(i) + ".png")

            image_stream = BytesIO()
            image.save(image_stream, format="JPEG")
            image_stream.seek(0)

            client.put_object(bucket_name="graduationdesign", object_name=objRelaName3 + f"/image_{i}.png",
                              data=image_stream,
                              length=len(image_stream.getvalue())
                              )

            image = numpy.asarray(image)
            image_ot = numpy.asarray(image_ot)
            _, patient_mask0 = cv2.threshold(image_ot, 127, 255, 0)
            ret, patient_mask1 = cv2.threshold(image_ot, 175, 255, cv2.THRESH_TOZERO_INV)
            ret2, patient_mask2 = cv2.threshold(image_ot, 100, 100, cv2.THRESH_TOZERO_INV)
            # 轮廓线提取
            contours1, _1 = cv2.findContours(patient_mask0, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
            contours2, _2 = cv2.findContours(patient_mask1, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)
            contours3, _3 = cv2.findContours(patient_mask2, cv2.RETR_TREE, cv2.CHAIN_APPROX_NONE)

            image = cv2.cvtColor(image, cv2.COLOR_GRAY2BGR)

            # 在ct图上绘制轮廓线
            overlap_img = cv2.drawContours(image.copy(), contours2, -1, (127, 255, 0), thickness=-1)  # 绿色
            overlap_img1 = cv2.drawContours(overlap_img.copy(), contours1, -1, (240, 230, 140), thickness=-1)  # 黄色
            overlap_img2 = cv2.drawContours(overlap_img1.copy(), contours3, -1, (255, 69, 0), thickness=-1)  # 红色

            final_img = Image.fromarray(overlap_img2)
            # 将图片转换为BytesIO对象
            image_stream = BytesIO()
            final_img.save(image_stream, format="JPEG")
            image_stream.seek(0)
            client.put_object(bucket_name="graduationdesign", object_name=objName3 + f"/image_{i}.png",
                              data=image_stream,
                              length=len(image_stream.getvalue())
                              )
        print("完成")
        return "resultEnv/" + struuid