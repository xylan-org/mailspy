const path = require("path");
module.exports = {
    babel: {
        plugins: ["babel-plugin-transform-typescript-metadata"]
    },
    webpack: {
        configure: (webpackConfig, { env, paths }) => {
            paths.appBuild = webpackConfig.output.path = path.resolve("build", "webpack");
            return webpackConfig;
        }
    }
};
