import request from '@/util/request'


export function listPatientIsHeadle(pageNum, pageSize, params) {
    return request({
        url: '/user/patient/list/' + pageNum + '/' + pageSize + "/1",
        method: 'POST',
        data: params
    })
}
export function listPatientNotHeadle(pageNum, pageSize, params) {
    return request({
        url: '/user/patient/list/' + pageNum + '/' + pageSize + "/0",
        method: 'POST',
        data: params
    })
}

export function getPatientInfo(id) {
    return request({
        url: '/user/patient/' + id,
        method: 'GET'
    })
}

export function savePatient(data) {
    return request({
        url: '/user/patient',
        method: 'POST',
        data: data
    })
}

export function deletePatient(id) {
    return request({
        url: '/user/patient/' + id,
        method: 'DELETE'
    })
}






