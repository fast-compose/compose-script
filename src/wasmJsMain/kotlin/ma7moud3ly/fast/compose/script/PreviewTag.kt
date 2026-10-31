package ma7moud3ly.fast.compose.script

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ma7moud3ly.fast.compose.ui.ComposeCode
import ma7moud3ly.fast.compose.ui.FastComposeLogs
import ma7moud3ly.fast.compose.ui.FastComposePreview
import ma7moud3ly.fast.compose.ui.rememberComposeCode

/** Every `@Preview` of [source] in a scrollable list that fills the tag, with [ErrorsPane] below. */
@Composable
internal fun PreviewTag(source: String) {
    val code = rememberComposeCode(source)
    Column(Modifier.fillMaxSize()) {
        FastComposePreview(code, Modifier.weight(1f).fillMaxWidth())
        ErrorsPane(code)
    }
}

/** Parse and runtime errors of [code]; shown only while there are any. */
@Composable
internal fun ErrorsPane(code: ComposeCode) {
    if (code.diagnostics.isNotEmpty() || code.logs.any { it.isError }) {
        FastComposeLogs(code, Modifier.fillMaxWidth().height(120.dp), title = "Errors")
    }
}
