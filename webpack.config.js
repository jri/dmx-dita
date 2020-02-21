const PLUGIN_URI = 'systems.dmx.dita'
const { CleanWebpackPlugin } = require('clean-webpack-plugin')

module.exports = {
  entry: './src/main/js/plugin.js',
  output: {
    path: __dirname + '/src/main/resources/web',
    filename: '[chunkhash].plugin.js',
    chunkFilename: '[chunkhash].[name].js',
    publicPath: '/' + PLUGIN_URI + '/',
    library: '_' + PLUGIN_URI.replace(/[.-]/g, '_'),
    libraryTarget: 'jsonp'
  },
  plugins: [
    new CleanWebpackPlugin(),
  ],
  stats: {
    entrypoints: false
  }
}
