import request from '@/util/request'


export function uploadFile(files) {
    return request({
        url: 'tumor/segment/upload',
        method: 'POST',
        data: files,
        headers: {
            "Authorization": "Bearer " + new Date().getTime(),
            "Content-Type": "multipart/form-data"
        },
    })
}

export function getPicUrl(path) {
    return request({
        url: 'tumor/segment/previewPicUrl',
        method: 'GET',
        params: {
            'rootPath': path
        }
    })
}

export function changePicUrl(path, key) {
    return request({
        url: 'tumor/segment/changePicUrl/' + key,
        method: 'GET',
        params: {
            'rootPath': path
        }
    })
}

export function downloadPic(bucketFileName, originalFilename) {
    return request({
        url: 'tumor/segment/downloadFile',
        method: 'GET',
        params: {
            'bucketFileName': bucketFileName,
            'originalFilename': originalFilename
        },
        responseType: 'blob'
    })
}

export function segment(modelId, patientId) {
    return request({
        url: 'tumor/segment/' + modelId + '/' + patientId,
        method: 'GET',
        timeout: 1000000
    })
}

export function downloadTumorFile(patientId) {
    return request({
        url: 'tumor/segment/downloadTumor',
        method: 'GET',
        params: {
            'patientId': patientId,
        },
        responseType: 'blob'
    })
}

export function getAllNotify() {
    return request({
        url: '/tumor/notify-info',
        method: 'GET'
    })
}

export function getPicDiffPre() {
    return request({
        url: '/tumor/segment/getDiff',
        method: 'GET'
    })
}