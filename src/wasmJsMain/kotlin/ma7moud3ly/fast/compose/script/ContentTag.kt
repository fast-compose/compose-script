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
 * The composable sets the size of [tag]: up to the width of its parent and the height of the window.
 */
@Composable
internal fun ContentTag(tag: HTMLElement, source: String, entry: String?) {
    // The body starts on the first line, so error line numbers match the tag's code.
    val code = rememberComposeCode(
        remember(source, entry) { if (entry != null) source else "@Composable fun $CONTENT_ENTRY() { $source\n}" }
    )
    val density = LocalDensity.current.density
    var available by remember { mutableStateOf(availableSize(tag)) }
    LaunchedEffect(tag) {
        window.addEventListener("resize", { available = availableSize(tag) })
    }
    Column(
        Modifier
            .measureWithin((available.first * density).roundToInt(), (available.second * density).roundToInt())
            .onSizeChanged { size ->
                tag.style.width = "${size.width / density}px"
                tag.style.height = "${size.height / density}px"
                notifyResize()
            }
    ) {
        FastComposeContent(code, entry = entry ?: CONTENT_ENTRY)
        ErrorsPane(code)
    }
}

/** Width inside the parent of [tag] and height of the window, in CSS pixels. */
private fun availableSize(tag: HTMLElement): Pair<Int, Int> = parentContentWidth(tag) to window.innerHeight

/**
 * Measures the content within [maxWidth] and [maxHeight] pixels instead of the viewport's own size, so its size does
 * not depend on the tag it resizes.
 */
private fun Modifier.measureWithin(maxWidth: Int, maxHeight: Int): Modifier = layout { measurable, constraints ->
    val placeable = measurable.measure(Constraints(maxWidth = maxWidth, maxHeight = maxHeight))
    layout(constraints.maxWidth, constraints.maxHeight) { placeable.place(0, 0) }
}
