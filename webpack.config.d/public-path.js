// A release built with COMPOSE_SCRIPT_VERSION loads its .wasm files from that exact version when the page loads it from
// a CDN without a version, e.g. https://cdn.jsdelivr.net/npm/@fast-compose/compose-script/compose-script.js. The .wasm
// file names change with every release, so a cached script must not ask the CDN's newest version for them.
;(function (config) {
    const version = process.env.COMPOSE_SCRIPT_VERSION;
    if (!version) return;
    const path = require("path");
    const fs = require("fs");
    const file = path.resolve(__dirname, "compose-script-public-path.js");
    fs.writeFileSync(file, `
const script = document.currentScript;
const src = script ? script.src : "";
const pinned = src.replace(/\\/@fast-compose\\/compose-script(@[^/]*)?\\//, "/@fast-compose/compose-script@${version}/");
if (pinned !== src) __webpack_public_path__ = pinned.slice(0, pinned.lastIndexOf("/") + 1);
`);
    config.entry.main.unshift(file);
})(config);
