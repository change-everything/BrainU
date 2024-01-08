# -*- coding:utf-8 -*-
# @description: todo
# @author: peiYP

import os
import torch.nn as nn
import torch
from torch.utils.data import Dataset
import numpy as np
import scipy.misc

from src.utils import *


# 定义标签
modals = ['flair', 't1', 't1c', 't2']


class DataLoaderSelf(Dataset):
    def __init__(self, data_dir):

        print('\n' + '~' * 50)
        print('******** Loading data from disk ********')
        self.data = []
        # 生成一个五阶零方阵
        self.freq = np.zeros(5)
        # 生成一个4*240*240零矩阵
        self.zero_vol = np.zeros((4, 240, 240))
        volume, label = DataLoaderSelf.get_subject(data_dir)   # 4 * 155 * 240 * 240,  155 * 240 * 240
        # 图像归一化 移除相同部分 突出个体
        volume = norm_vol(volume)

        self.freq += self.get_freq(label)

        volume = np.transpose(volume, (1, 0, 2, 3))
        self.data.append([volume, label, data_dir])

        self.freq = self.freq / np.sum(self.freq)
        self.weight = np.median(self.freq) / self.freq
        print('********  Finish loading data  ********')
        print('********  Weight for all classes  ********')
        print(self.weight)

        print('********  Total number of subject is ' + str(len(self.data)) + ' **********')

        print('~' * 50)

    def __len__(self):
        return len(self.data)

    def __getitem__(self, index):
        # ********** get file dir **********
        [image, label, name] = self.data[index]  # 获取单个数据、标签和文件名
        # ********** change data type from numpy to torch.Tensor **********
        image = torch.from_numpy(image).float()  # Float Tensor 4, 240, 240
        label = torch.from_numpy(label).float()    # Float Tensor 240, 240
        return image, label, name

    @staticmethod
    def get_subject(subject):
        """
        :param subject: absolute dir
        :return:
        volume  4D numpy    4 * 155 * 240 * 240
        label   4D numpy    155 * 240 * 240
        """
        # **************** get file ****************
        # 获取文件夹下所有文件
        files = os.listdir(subject)  # [XXX.Flair, XXX.T1, XXX.T1c, XXX.T2, XXX.OT]
        multi_mode_dir = []
        # 标签路径
        label_dir = ""
        for f in files:
            if f == '.DS_Store':
                continue
            if 'Flair' in f or 'T1' in f or 'T2' in f:    # 如果是模态文件就保存在多模态数组中
                multi_mode_dir.append(f)
            elif 'OT.' in f:        # 如果是标签，就保存在标签路径
                label_dir = f

        # ********** load 4 mode images **********
        multi_mode_imgs = []  # list size :4      item size: 155 * 240 * 240
        for mod_dir in multi_mode_dir:
            path = os.path.join(subject, mod_dir)  # absolute directory
            # 加载mha文件
            # img = load_mha_as_array(path + '/' + mod_dir + '.mha')
            img = load_mha_as_array(path)
            # 加入到list
            # 合并为单模态
            multi_mode_imgs.append(img)

        # ********** get label **********
        # 读OT文件（掩模图）
        # label_dir = os.path.join(subject, label_dir) + '/' + label_dir + '.mha'
        label_dir = os.path.join(subject, label_dir)
        # 加载文件
        label = load_mha_as_array(label_dir)  #

        # 转化为数组
        volume = np.asarray(multi_mode_imgs)
        # 返回单通道图像和标签文件
        return volume, label

    def get_freq(self, label):
        """
        :param label: numpy 155 * 240 * 240     val: 0,1,2,3,4
        :return:
        """
        class_count = np.zeros((5))
        for i in range(5):
            a = (label == i) + 0
            class_count[i] = np.sum(a)
        return class_count
