// Bundles src/wasmJsMain/js/loading.js ahead of the Kotlin code, so it runs as soon as compose-script.js loads, while the
// .wasm files are still downloading.
;(function (config) {
    const path = require("path");
    config.entry.main.unshift(path.resolve(__dirname, "../../../../src/wasmJsMain/js/loading.js"));
})(config);
