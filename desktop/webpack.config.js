const CopyWebpackPlugin = require("copy-webpack-plugin");
const path = require("path");

module.exports = {
    context: path.resolve(__dirname, "src"),
    entry: "./js/index.jsx",
    output: {
        path: path.resolve(__dirname, "dist", "js"),
        filename: "bundle.js"
    },
    module: {
        loaders: [
            {
                test : /\.jsx?/,
                loader : 'babel-loader'
            }
        ]
    },
    plugins: [
        new CopyWebpackPlugin([
            {
                from: "html",
                to: ".."
            },
            {
                from: "js/electron.js",
                to: "electron.js"
            }
        ])
    ]
};
