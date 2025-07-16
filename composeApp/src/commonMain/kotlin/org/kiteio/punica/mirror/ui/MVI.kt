package org.kiteio.punica.mirror.ui

import kotlinx.coroutines.flow.StateFlow

/**
 * MVI 架构。
 */
interface MVI<State, Intent> {
    /** UI 状态 */
    val uiState: StateFlow<State>

    /**
     * 分发意图。
     */
    fun dispatch(intent: Intent)
}