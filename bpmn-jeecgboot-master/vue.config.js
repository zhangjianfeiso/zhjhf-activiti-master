const path = require('path')

function resolve(dir) {
    return path.join(__dirname, dir)
}

module.exports = {
    publicPath: './',
    outputDir: 'dist',
    assetsDir: 'static',
    lintOnSave: false,
    productionSourceMap: false,
    devServer: {
        port: 8080,
        open: true,
        overlay: {
            warnings: false,
            errors: true,
        },
        proxy: {
            "/": {
                target: "http://localhost:9091", //配置要替换的后台接口地址
                changOrigin: true, //配置允许改变Origin
                ws: true, // proxy websockets
            },
        }
    },
    chainWebpack(config) {
        // set svg-sprite-loader
        config.module
            .rule('svg')
            .exclude.add(resolve('src/components/formGenerator/icons'))
            .end()
        config.module
            .rule('icons')
            .test(/\.svg$/)
            .include.add(resolve('src/components/formGenerator/icons'))
            .end()
            .use('svg-sprite-loader')
            .loader('svg-sprite-loader')
            .options({
                symbolId: 'icon-[name]',
            })
            .end()
    },
}
