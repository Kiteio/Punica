package org.kiteio.punica.mirror.platform

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry

@OptIn(ExperimentalComposeUiApi::class)
actual fun ClipEntry(text: String) = ClipEntry.withPlainText(text)