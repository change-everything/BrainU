import request from '@/util/request'


export function listModelPage(pageNum, pageSize, params) {
    return request({
        url: '/model/modelInfo/list/' + pageNum + '/' + pageSize,
        method: 'GET',
        data: params
    })
}

export function listModel() {
    return request({
        url: '/model/modelInfo/list',
        method: 'GET'
    })
}

export function saveModel(data) {
    return request({
        url: '/model/modelInfo',
        method: 'POST',
        data: data
    })
}

export function deleteModel(id) {
    return request({
        url: '/model/modelInfo/' + id,
        method: 'DELETE'
    })
}