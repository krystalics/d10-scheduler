const path = require('path')
const webpack = require('webpack')
const HtmlWebpackPlugin = require('html-webpack-plugin')
const MiniCssExtractPlugin = require("mini-css-extract-plugin")
const apiMocker = require('webpack-api-mocker')

const prod = process.env.NODE_ENV === 'production'
const useMock = process.env.USE_MOCK === 'true'
const prodOutputPath = path.resolve('../web/src/main/resources/static/dist')
const prodPublicPath = '/static/dist'


console.log(prod)
module.exports = () => {

    const styleLoader = (loaders=[]) => [
        prod ? MiniCssExtractPlugin.loader : 'style-loader',
        'css-loader',
        ...loaders
    ]

    return {
        mode: prod ? 'production' : 'development',
        devtool: prod ? 'source-map' : 'cheap-module-eval-source-map',
        entry: './src/index.js',
        output: {
            path: prod ? prodOutputPath : path.resolve('./app'),
            filename: prod ? 'js/[name].[contenthash:8].js' : 'js/[name].js',
            publicPath:  prod ? prodPublicPath : '',
        },
        module: {
            rules: [{
                test: /\.(jsx?|tsx?)$/,
                exclude: /node_modules/,
                use: 'babel-loader'
            }, {
                test: /\.css$/,
                use: styleLoader()
            }, {
                test: /\.less$/,
                use: styleLoader(["less-loader"])
            }, {
                test: /\.scss$/,
                use: [
                    'style-loader',
                    'css-loader',
                    'sass-loader'
                ]
            } , {
                test: /\.(jpe?g|png|gif|bmp|svg)$/,
                use: {
                    loader: 'url-loader',
                    options: {
                        limit: 8 * 1024,
                        name: prod ? 'img/[name].[contenthash:8].[ext]' : '[name].[ext]',
                    },
                }
            }, {
                test: /\.(svg|eot|woff|ttf)$/,
                use: {
                    loader: 'file-loader',
                    options: {
                        name: prod ? 'font/[name].[contenthash:8].[ext]' : '[name].[ext]',
                    }
                }
            }]
        },
        resolve: {
            modules: ['node_modules', 'src'],
            alias:{
                '@': path.resolve('src')
            },
            extensions: ['.ts', '.tsx', '.js', '.jsx']
        },
        devServer: {
            contentBase: './app',
            disableHostCheck: true,
            // host: '0.0.0.0',
            // useLocalIp: true,
            open: 'Google Chrome',
            hot: true,
            publicPath: '/',
            historyApiFallback: true,
            before(app) {
                if(useMock){
                    apiMocker(app, path.resolve(__dirname, './mock/index.js'), {})
                }
            },
            proxy: {
                '/': {
                    target: "http://127.0.0.1:11800",
                    ws: false,
                    changeOrigin: true,
                }
            }
        },
        plugins: [
            new HtmlWebpackPlugin({
                template: './src/index.html.ejs'
            }),
            new webpack.EnvironmentPlugin({
                ...process.env
            }),
            prod && new MiniCssExtractPlugin({
                filename: 'css/[name].[contenthash:8].css'
            })
        ].filter(Boolean)
    }
}
