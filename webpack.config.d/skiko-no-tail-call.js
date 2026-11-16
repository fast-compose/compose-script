// Runs skiko.mjs through src/wasmJsMain/js/skiko-no-tail-call-loader.cjs, so Compose draws on Safari 27.2.
;(function (config) {
    config.module.rules.push({
        test: /[\\/]skiko\.mjs$/,
        loader: require("path").resolve(__dirname, "../../../../src/wasmJsMain/js/skiko-no-tail-call-loader.cjs"),
    });
})(config);
