package org.kiteio.punica.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.buildAnnotatedString
import org.kiteio.punica.candy.appendClickable

/**
 * [RadioButton]
 * @param selected
 * @param onClick
 * @param label 文字标签
 * @param enabled
 */
@Composable
fun RadioButton(
    selected: Boolean,
    onClick: () -> Unit,
    label: String? = null,
    enabled: Boolean = true
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected, onClick = onClick, enabled = enabled)
        label?.let {
            Text(text = buildAnnotatedString { appendClickable(label, onClick = onClick) })
        }
    }
}