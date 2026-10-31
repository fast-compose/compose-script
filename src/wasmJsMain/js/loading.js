// Runs as soon as compose-script.js loads, while its .wasm files are still downloading. It adds the default styles and
// puts a <compose-content> or <compose-preview> tag with a loading animation in place of every Compose script, with the
// script moved inside it. The Kotlin code then starts each tag and removes its loading state.

const PREVIEW_TAG = "compose-preview";
const CONTENT_TAG = "compose-content";

// `:where` keeps the specificity at zero, so any page style overrides these. Compose sizes its canvas from the tag, so
// a preview tag needs a height before it starts. A content tag sits in the text flow like an image, at 1 × 1 px from its
// start until its composable is measured.
const STYLE = `
:where(${PREVIEW_TAG}) { display: block; height: 400px; }
:where(${CONTENT_TAG}) { display: inline-block; width: 1px; height: 1px; }
:where(${CONTENT_TAG}[data-loading]) { width: 160px; height: 48px; }
:where(${PREVIEW_TAG}[data-loading], ${CONTENT_TAG}[data-loading]) {
    border-radius: 8px;
    background: linear-gradient(90deg, rgba(128, 128, 128, 0.1) 25%, rgba(128, 128, 128, 0.25) 50%,
        rgba(128, 128, 128, 0.1) 75%) 0 0 / 200% 100%;
    animation: compose-script-loading 1.2s ease-in-out infinite;
}
@keyframes compose-script-loading {
    from { background-position: 100% 0; }
    to { background-position: -100% 0; }
}
@media (prefers-reduced-motion: reduce) {
    :where(${PREVIEW_TAG}[data-loading], ${CONTENT_TAG}[data-loading]) { animation: none; }
}
`;

function addStyle() {
    const style = document.createElement("style");
    style.textContent = STYLE;
    document.head.appendChild(style);
}

// The tag takes the script's `id`, `class` and `style`. A script in <head>, as in a page without <body>, gets its tag at
// the start of <body>, since <head> is never shown.
function placeTags() {
    const scripts = document.querySelectorAll(`script[type='text/${PREVIEW_TAG}'], script[type='text/${CONTENT_TAG}']`);
    const bodyStart = document.body.firstChild;
    for (const script of scripts) {
        const tag = document.createElement(script.getAttribute("type").slice("text/".length));
        for (const attribute of ["id", "class", "style"]) {
            const value = script.getAttribute(attribute);
            if (value !== null) tag.setAttribute(attribute, value);
        }
        script.removeAttribute("id");
        tag.setAttribute("data-loading", "");
        if (script.closest("head")) document.body.insertBefore(tag, bodyStart);
        else script.replaceWith(tag);
        tag.appendChild(script);
    }
}

addStyle();
if (document.readyState === "loading") document.addEventListener("DOMContentLoaded", placeTags);
else placeTags();
