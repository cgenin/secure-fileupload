const CompressionPlugin = require('compression-webpack-plugin');

module.exports = {
    devServer: {
        proxy: {
            '^/upload': {
                target: 'http://localhost:8000',
            },
        }
    },
    outputDir:'../src/main/resources/webroot/wc',
    configureWebpack: {
        plugins: [
            new CompressionPlugin()
        ]
    }
}