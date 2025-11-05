package org.kiteio.punica.mirror.ui.screen.modules.cet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kiteio.punica.mirror.modal.cet.CetExam
import org.kiteio.punica.mirror.repository.CetRepository
import org.kiteio.punica.mirror.ui.MVI
import org.kiteio.punica.mirror.ui.Toast
import org.kiteio.punica.mirror.ui.show
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class CetViewModel(
    private val cetRepository: CetRepository,
    private val toast: Toast,
) : ViewModel(), MVI<CetUiState, CetIntent> {
    private val _uiState = MutableStateFlow<CetUiState>(CetUiState.Loading)
    override val uiState = _uiState.asStateFlow()

    /** 错误处理 */
    private val handler = CoroutineExceptionHandler { _, throwable ->
        toast.show(throwable)
        _uiState.update { CetUiState.Error(throwable) }
    }

    override fun dispatch(intent: CetIntent) {
        when (intent) {
            CetIntent.Load -> viewModelScope.launch(handler) {
                // 加载四六级考试
                val cetExam = cetRepository.getExam()
                _uiState.update { CetUiState.Success(cetExam) }
            }
        }
    }
}

/**
 * Cet 状态。
 */
sealed class CetUiState {
    /** 加载中 */
    data object Loading : CetUiState()

    /** 加载成功 */
    data class Success(val cetExam: CetExam) : CetUiState()

    /** 加载失败 */
    data class Error(val e: Throwable) : CetUiState()
}

/**
 * Cet 意图。
 */
sealed class CetIntent {
    /** 加载 */
    data object Load : CetIntent()
}