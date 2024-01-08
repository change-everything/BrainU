# -*- coding:utf-8 -*-
# @description: todo
# @author: peiYP


import socket
from data_loader.data_loader_self import DataLoaderSelf

from src.utils import *

from test.saveoss import *

from torch.utils.data import DataLoader
from torch.autograd import Variable
from PIL import Image
from unet.multi_unet2d import Multi_Unet
from unet.unet2d import UNet2D

import sys
import torch
import torch.nn as nn
import torch.optim as optim
import torch.nn.functional as F


# ********** Hyper Parameter **********


saved_model_path = 'D:/GraduationDesign/AI/model/mmunet/model/best_epoch.pth'
batch_size = 32

# multi-GPU
cuda_available = torch.cuda.is_available()
device_ids = [0]  # multi-GPU
torch.cuda.set_device(device_ids[0])

def load_model(modelPath, modelName):
    if (modelName == "U-Net模型"):
        net = UNet2D(4, 5, 64)
    else:
        net = Multi_Unet(1, 5, 32)
    if cuda_available:
        net = net.cuda()
        net = nn.DataParallel(net, device_ids=device_ids)

    state_dict = torch.load(modelPath)
    net.load_state_dict(state_dict)
    return net

def to_var(tensor):
    return Variable(tensor.cuda() if cuda_available else tensor)



def evaluation(net, test_dataset, criterion, save_dir=None, model_dir=None):
    """
    :param net:
    :param test_dataset:  data loader batch size = 1
    :param criterion:
    :param temporal:
    :return:
    """
    test_loss = []
    iou_5_class_all = []
    dice_whole_tumor = []

    with torch.no_grad():
        net.eval()
        for step, (images_vol, label_vol, subject) in enumerate(test_dataset):
            # images_vol     5D tensor     (bz, 155, 4, 240, 240)
            # label_vol      4D tensor     (bz, 155, 240, 240)
            subj_target = label_vol.long().squeeze()  # 3D tensor  155 * 240 * 240
            subj_predict = torch.zeros(label_vol.squeeze().shape)  # 3D tensor  155 * 240 * 240
            for t in range(155):  #
                image = to_var(images_vol[:, t, ...])   # 4D  bz(1) * 4 * 240 * 240
                label = to_var(label_vol[:, t, ...])    # 4D tensor   bz(1)  * 240 * 240
                predicts = net(image)  # 4D tensor   bz(1) * 5 * 240 * 240
                loss_valid = criterion(predicts, label.long())
                test_loss.append(float(loss_valid))
                # softmax and reverse
                predicts = one_hot_reverse(predicts)  # 3D long T     bz(1)* 240 * 240 (0-4)
                subj_predict[t, ...] = predicts.squeeze().long().data
            # calculate IoU
            subj_5class_iou = cal_subject_iou_5class(subj_target, subj_predict)  # list length 4
            subj_whole_tumor_dice = cal_subject_dice_whole_tumor(subj_target, subj_predict)  # label(1+2+3+4)
            iou_5_class_all.append(subj_5class_iou)
            dice_whole_tumor.append(subj_whole_tumor_dice)
            # save
            if save_dir is not None:
                # hl, name = subject[0].split('/')[-2:]
                img_save_dir = save_dir + '/result' + '.mha'
                save_array_as_mha(subj_predict, img_save_dir)
                print("保存成功了")
        torch.save(net.state_dict(),model_dir)

            # print('subject ...' + subject[0])
            # print(subj_5class_iou)
            # print(subj_whole_tumor_dice)

        print('Dice for whole tumor is ')
        average_iou_whole_tumor = sum(dice_whole_tumor) / (len(dice_whole_tumor) * 1.0)
        print(average_iou_whole_tumor)

        for i in range(5):
            iou_i = []
            for iou5 in iou_5_class_all:
                iou_i.append(iou5[i])
            average_iou_label_i = sum(iou_i) / (len(iou_i) * 1.0)
            print('Iou for label ' + str(i) + '   is    ' + str(average_iou_label_i))

    return average_iou_whole_tumor, test_loss



End = 'end send' # 这里是为了判断对应的客户端请求命令的结束标志，目的是为了接受超过1024个字符的(client)客户端请求。
def recv_end(the_socket):
    total_data = []
    while True:
        data = the_socket.recv(8192).decode('utf-8')
        if End in data:
            total_data.append(data[:data.find(End)])
            break
        total_data.append(data)
        if len(total_data) > 1:
            # check if end_of_data was split
            last_pair = total_data[-2] + total_data[-1]
            if End in last_pair:
                total_data[-2] = last_pair[:last_pair.find(End)]
                total_data.pop()
                break
    return ''.join(total_data)

def segment(modelPath, filePath, modelName):
    net = load_model(modelPath, modelName)

    print('test data ....')
    test_data = DataLoaderSelf(data_dir=filePath)  # 30 subject, 4650 images
    test_dataset = DataLoader(dataset=test_data, batch_size=1, shuffle=False)

    weight = torch.from_numpy(test_data.weight).float()  # weight for all class
    weight = to_var(weight)
    criterion = nn.CrossEntropyLoss(weight=weight)

    evaluation(net, test_dataset, criterion, save_dir=filePath, model_dir=modelPath)

    img_path = SaveOssUtil.saveImg(filePath)

    return img_path



HOST = ''  # Symbolic name meaning all available interfaces
PORT = 50007  # Arbitrary non-privileged port
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.bind((HOST, PORT))
s.listen(1)
while 1:
    conn, addr = s.accept()
    print('Connected by', addr)
    data = recv_end(conn)
    method = data[0]
    subdata = data[1:]
    if (method == "1"):  # 当客户端传递过来的第一个字符为1时，params表示传递过来的参数
        params = subdata.split("|")
        # 这里写要调用的函数
        print(params[0], params[1], params[2])
        img_path = segment(params[0], params[1], params[2])
        # 将结果返回给客户端
        conn.send(img_path.encode('utf-8'))

    # elif method=='2': #当客户端传递过来的第一个字符为2时
    #   #需要执行的命令2

    # update plot
    conn.close()