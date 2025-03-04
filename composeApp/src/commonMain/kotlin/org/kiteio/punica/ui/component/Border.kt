package org.kiteio.punica.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun BorderStroke() = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(0.2f))