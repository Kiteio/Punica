package org.kiteio.punica.mirror.platform

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalComposeUiApi::class)
actual fun ClipEntry(text: String) = ClipEntry(StringSelection(text))