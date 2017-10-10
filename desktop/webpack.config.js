const CopyWebpackPlugin = require('copy-webpack-plugin');
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const path = require('path');

module.exports = {
  context: path.join(__dirname, 'src'),
  entry: './js/index.js',
  output: {
    path: path.join(__dirname, 'dist', 'js'),
    filename: "bundle.js"
  },
  module: {
    loaders: [
      {
        test: /\.elm$/,
        loader: 'elm-webpack-loader'
      },
      {
        test: /\.scss$/,
        loader: ExtractTextPlugin.extract({
          fallback: "style-loader",
          use: ["css-loader", "sass-loader" ],
          publicPath: "dist"
        })
      },
      {
        test: /\.woff[0-9]?$/,
        loader: "url-loader?limit=10000&minetype=application/font-woff"
      },
      {
        test: /\.(ttf|eot|svg)(\?v=[0-9]\.[0-9]\.[0-9])?$/,
        loader: "file-loader"
      }
    ]
  },
  plugins: [
    new CopyWebpackPlugin([
      {
        from: path.join('html'),
        to: '..',
      },
      {
        from: 'js/electron.js',
        to: 'electron.js'
      }
    ]),
    new ExtractTextPlugin("../css/app.css")
  ]
};