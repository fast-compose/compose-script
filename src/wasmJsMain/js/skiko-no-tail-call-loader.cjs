// Webpack loader for skiko.mjs: turns every Skia export wrapper `(...a) => (X = loadedWasm._["X"])(...a)` into one
// that makes the same call inside `try`. A call inside `try` is never a tail call, which avoids WebKit bug 329421:
// Safari 27.2 passes NaN to a Wasm float parameter when JS calls Wasm as a tail call, so Compose draws nothing.
// Arguments, the returned value and thrown errors stay the same. https://bugs.webkit.org/show_bug.cgi?id=329421

const WRAPPER = /\(\.\.\.a\) => \((\w+) = loadedWasm\._\["\1"\]\)\(\.\.\.a\)/g;

module.exports = function (source) {
    let patched = 0;
    const result = source.replace(WRAPPER, (_, name) => {
        patched++;
        return `(...a) => { try { return (${name} = loadedWasm._["${name}"])(...a) } catch (e) { throw e } }`;
    });
    if (patched === 0) throw new Error("skiko.mjs wrapper format changed: WebKit 329421 workaround not applied");
    return result;
};
