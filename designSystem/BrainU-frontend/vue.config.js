module.exports = {
    devServer: {
        host: '0.0.0.0',  // 项目运行的IP
        port: 80,  // 项目运行的端口
        https: false,
        // 开发环境跨域使用dev server解决
        proxy: {
            '/api': {    // 匹配到/api的接口请求，我们下面将所有api请求全部加上api标识
                target: 'http://localhost:8000/',  // 要转发到的我们的api接口地址
                changeOrigin: true,
                pathRewrite: {
                    '^/api': ''
                }
            }
        },
    }
}