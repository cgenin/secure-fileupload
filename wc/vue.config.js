const CompressionPlugin = require('compression-webpack-plugin');

module.exports = {
    outputDir:'../src/main/resources/webroot/wc',
    configureWebpack: {
        plugins: [
            new CompressionPlugin()
        ]
    }
}