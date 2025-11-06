package org.kiteio.punica.mirror.ui.screen.modules.notices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.kiteio.punica.mirror.repository.NoticeDetailRepository
import org.kiteio.punica.mirror.ui.MVI
import org.kiteio.punica.mirror.ui.Toast
import org.kiteio.punica.mirror.ui.show
import org.koin.android.annotation.KoinViewModel

@KoinViewModel
class NoticeDetailViewModel(
    private val noticeDetailRepository: NoticeDetailRepository,
    toast: Toast,
) : ViewModel(), MVI<NoticeDetailUiState, NoticeDetailIntent> {
    private val _uiState = MutableStateFlow<NoticeDetailUiState>(
        NoticeDetailUiState.Loading
    )
    override val uiState = _uiState.asStateFlow()

    /** 错误处理 */
    private val handler = CoroutineExceptionHandler { _, throwable ->
        toast.show(throwable)
        _uiState.update { NoticeDetailUiState.Error(throwable) }
    }

    override fun dispatch(intent: NoticeDetailIntent) {
        when (intent) {
            // 加载教学通知详情
            is NoticeDetailIntent.Load -> viewModelScope.launch(handler) {
                _uiState.update { NoticeDetailUiState.Loading }
                val html = noticeDetailRepository.getNoticeDetailHtml(intent.urlString)
                _uiState.update { NoticeDetailUiState.Success(html) }
            }
        }
    }
}

/**
 * 教学通知详情状态。
 */
sealed class NoticeDetailUiState {
    /** 加载中 */
    data object Loading : NoticeDetailUiState()

    /**
     * 加载成功。
     *
     * @property noticeHtml 通知 Html 字符串
     */
    data class Success(val noticeHtml: String) : NoticeDetailUiState()

    /** 加载失败 */
    data class Error(val e: Throwable) : NoticeDetailUiState()
}

/**
 * 教学通知详情意图。
 */
sealed class NoticeDetailIntent {
    /**
     * 加载通知。
     *
     * @property urlString 通知 Url
     */
    data class Load(val urlString: String) : NoticeDetailIntent()
}