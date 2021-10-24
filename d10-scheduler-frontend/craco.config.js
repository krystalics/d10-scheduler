const CracoLessPlugin = require('craco-less');

//https://ant.design/docs/react/customize-theme-cn 定制主题的文档页面

module.exports = {
    plugins: [
        {
            plugin: CracoLessPlugin,
            options: {
                lessLoaderOptions: {
                    lessOptions: {
                        modifyVars: { '@primary-color': '#1890ff' },
                        javascriptEnabled: true,
                    },
                },
            },
        },
    ],
};