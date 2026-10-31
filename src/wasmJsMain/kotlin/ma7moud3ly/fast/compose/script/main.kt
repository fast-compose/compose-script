package ma7moud3ly.fast.compose.script

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.w3c.dom.Element
import org.w3c.dom.HTMLElement

/** Shows every `@Preview` function of its code, with labels and frames. */
private const val PREVIEW_TAG = "compose-preview"

/** Shows its code as the body of a composable, sized by that composable. */
private const val CONTENT_TAG = "compose-content"

/**
 * Default look of the tags. `:where` keeps their specificity at zero, so any page style overrides them.
 * Compose sizes its canvas from the tag, so a preview tag needs a height before it starts; a content tag gets its size
 * from its composable once measured.
 */
private const val DEFAULT_STYLE = """
:where($PREVIEW_TAG) { display: block; height: 400px; }
:where($CONTENT_TAG) { display: block; height: 1px; }
"""

/**
 * Replaces every `<script type="text/compose-preview">` and `<script type="text/compose-content">` on the page with a
 * `<compose-preview>` or `<compose-content>` tag that shows its code in a Compose viewport once it scrolls into view.
 * Scripts keep their code as written, so `<` and `>` need no escaping.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    addStyle(DEFAULT_STYLE)
    onDomReady {
        val scripts = document.querySelectorAll("script[type='text/$PREVIEW_TAG'], script[type='text/$CONTENT_TAG']")
        for (i in 0 until scripts.length) {
            val script = scripts.item(i) as? Element ?: continue
            val isPreview = script.getAttribute("type") == "text/$PREVIEW_TAG"
            val source = script.textContent.orEmpty().trimIndent()
            val entry = script.getAttribute("data-composable")?.trim()?.takeIf { it.isNotEmpty() }
            val tag = replaceWithTag(script, if (isPreview) PREVIEW_TAG else CONTENT_TAG)
            whenVisible(tag) {
                ComposeViewport(tag) {
                    if (isPreview) PreviewTag(source) else ContentTag(tag, source, entry)
                }
            }
        }
    }
}

/** Puts a [name] element in place of [script], with the script's `id`, `class` and `style`. */
private fun replaceWithTag(script: Element, name: String): HTMLElement {
    val tag = document.createElement(name) as HTMLElement
    for (attribute in listOf("id", "class", "style")) {
        script.getAttribute(attribute)?.let { tag.setAttribute(attribute, it) }
    }
    script.replaceWith(tag)
    return tag
}

private fun addStyle(css: String) {
    val style = document.createElement("style")
    style.textContent = css
    document.head?.appendChild(style)
}
