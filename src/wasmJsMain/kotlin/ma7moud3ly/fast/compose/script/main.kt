package ma7moud3ly.fast.compose.script

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.w3c.dom.HTMLElement

/** Shows every `@Preview` function of its code, with labels and frames. */
private const val PREVIEW_TAG = "compose-preview"

/** Shows its code as the body of a composable, sized by that composable. */
private const val CONTENT_TAG = "compose-content"

/**
 * Starts every `<compose-preview>` and `<compose-content>` tag on the page once it scrolls into view. `loading.js` has
 * already put each tag in place of its `<script type="text/compose-preview">` or `<script type="text/compose-content">`,
 * with the script inside it. Scripts keep their code as written, so `<` and `>` need no escaping.
 */
@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onDomReady {
        val tags = document.querySelectorAll("$PREVIEW_TAG, $CONTENT_TAG")
        for (i in 0 until tags.length) {
            val tag = tags.item(i) as? HTMLElement ?: continue
            val script = tag.querySelector("script") ?: continue
            val isPreview = tag.localName == PREVIEW_TAG
            val source = script.textContent.orEmpty().trimIndent()
            val entry = script.getAttribute("data-composable")?.trim()?.takeIf { it.isNotEmpty() }
            whenVisible(tag) {
                tag.removeAttribute("data-loading")
                // Clears the tag, script included, before drawing.
                ComposeViewport(tag) {
                    if (isPreview) PreviewTag(source) else ContentTag(tag, source, entry)
                }
            }
        }
    }
}
