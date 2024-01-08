import request from '@/util/request'


export function login(data) {
    return request({
        url: '/auth/login',
        method: 'POST',
        data: data
    })
}