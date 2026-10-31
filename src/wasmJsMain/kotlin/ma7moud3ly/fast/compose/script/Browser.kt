package ma7moud3ly.fast.compose.script

import org.w3c.dom.Element

/** Runs [action] once the page is parsed, or right away if it already is. */
internal fun onDomReady(action: () -> Unit): Unit = js(
    """{
    if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', () => action());
    else action();
}"""
)

/** Sends a window `resize` event, so Compose viewports read the size of their tags again. */
internal fun notifyResize(): Unit = js("window.dispatchEvent(new Event('resize'))")

/** Width inside the padding of the parent of [element] in CSS pixels, or the window width without a parent. */
internal fun parentContentWidth(element: Element): Int = js(
    """{
    const parent = element.parentElement;
    if (!parent) return window.innerWidth;
    const style = getComputedStyle(parent);
    return Math.floor(parent.clientWidth - parseFloat(style.paddingLeft) - parseFloat(style.paddingRight));
}"""
)

/** Runs [action] once, when [element] comes within 200 px of the visible part of the page. */
internal fun whenVisible(element: Element, action: () -> Unit): Unit = js(
    """{
    const observer = new IntersectionObserver((entries) => {
        if (entries.some((entry) => entry.isIntersecting)) {
            observer.disconnect();
            action();
        }
    }, { rootMargin: '200px' });
    observer.observe(element);
}"""
)
