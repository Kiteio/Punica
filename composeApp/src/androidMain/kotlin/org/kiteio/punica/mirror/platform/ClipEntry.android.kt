package org.kiteio.punica.mirror.platform

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry

actual fun ClipEntry(text: String) = ClipEntry(ClipData.newPlainText(text, text))