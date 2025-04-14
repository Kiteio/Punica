package org.kiteio.punica.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking

/**
 * [remember] + [runBlocking]。
 */
@Composable
fun <T> rememberRunBlocking(block: suspend CoroutineScope.() -> T) =
    remember { runBlocking(block = block) }