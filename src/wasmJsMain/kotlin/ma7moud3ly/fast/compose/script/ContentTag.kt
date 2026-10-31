package ma7moud3ly.fast.compose.script

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import kotlinx.browser.window
import ma7moud3ly.fast.compose.ui.FastComposeContent
import ma7moud3ly.fast.compose.ui.rememberComposeCode
import org.w3c.dom.HTMLElement
import kotlin.math.roundToInt

/** Name of the composable that holds the code of a `<compose-content>` tag. */
private const val CONTENT_ENTRY = "ComposeScriptContent"

/**
 * The code of a `<compose-content>` tag, without previews, labels or frames. Without [entry], [source] is the body of a
 * composable; with it, [source] is a file and [entry] names its top-level `@Composable` function to show.
 * A `width` or `height` in the tag's `style` attribute is kept, and the content is measured within it. The composable
 * sets the rest of the size: up to the width of the tag's parent and the height of the window.
 */
@Composable
internal fun ContentTag(tag: HTMLElement, source: String, entry: String?) {
    // The body starts on the first line, so error line numbers match the tag's code.
    val code = rememberComposeCode(
        remember(source, entry) { if (entry != null) source else "@Composable fun $CONTENT_ENTRY() { $source\n}" }
    )
    val density = LocalDensity.current.density
    // Read before the composable's size is written into the same style.
    val fixed = remember(tag) { FixedSize(tag.style.width.isNotEmpty(), tag.style.height.isNotEmpty()) }
    var available by remember { mutableStateOf(availableSize(tag, fixed)) }
    LaunchedEffect(tag) {
        window.addEventListener("resize", { available = availableSize(tag, fixed) })
    }
    Column(
        Modifier
            // Before measureWithin, so it covers the whole canvas rather than the content's size.
            .transparentBackground()
            .measureWithin((available.first * density).roundToInt(), (available.second * density).roundToInt())
            .onSizeChanged { size ->
                if (fixed.width && fixed.height) return@onSizeChanged
                if (!fixed.width) tag.style.width = "${size.width / density}px"
                if (!fixed.height) tag.style.height = "${size.height / density}px"
                notifyResize()
            }
    ) {
        FastComposeContent(code, entry = entry ?: CONTENT_ENTRY)
        ErrorsPane(code)
    }
}

/** Whether the tag's `style` attribute sets its width and height. */
private data class FixedSize(val width: Boolean, val height: Boolean)

/**
 * The size the content may take, in CSS pixels: the tag's own width and height where [fixed], otherwise the width inside
 * the tag's parent and the height of the window.
 */
private fun availableSize(tag: HTMLElement, fixed: FixedSize): Pair<Int, Int> =
    (if (fixed.width) tag.clientWidth else parentContentWidth(tag)) to
        (if (fixed.height) tag.clientHeight else window.innerHeight)

/**
 * Measures the content within [maxWidth] and [maxHeight] pixels instead of the viewport's own size, so its size does
 * not depend on the tag it resizes.
 */
private fun Modifier.measureWithin(maxWidth: Int, maxHeight: Int): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(Constraints(maxWidth = maxWidth, maxHeight = maxHeight))
    layout(constraints.maxWidth, constraints.maxHeight) { placeable.place(0, 0) }
}
