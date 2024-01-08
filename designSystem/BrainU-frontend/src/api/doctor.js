import request from '@/util/request'


export function listDoctorPage(pageNum, pagesize, param) {
    return request({
        url: '/user/doctor-info/list/' + pageNum + '/' + pagesize,
        method: 'POST',
        data: param
    })
}

export function getOneByDoctorId(id) {
    return request({
        url: '/user/doctor-info/info/' + id,
        method: 'GET'
    })
}

export function listDoctor() {
    return request({
        url: '/user/doctor-info/list',
        method: 'POST'
    })
}
