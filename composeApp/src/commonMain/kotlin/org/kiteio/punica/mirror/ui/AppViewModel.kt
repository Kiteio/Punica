package org.kiteio.punica.mirror.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.kiteio.punica.mirror.modal.User
import org.kiteio.punica.mirror.repository.EducationLoginRepository
import org.kiteio.punica.mirror.service.EducationService
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.error_name_is_empty
import punica.composeapp.generated.resources.error_password_is_empty

/** App Koin 模块 */
val appModule = module {
    singleOf(::EducationService)
    singleOf(::EducationLoginRepository)
    singleOf(::AppViewModel)
}

/**
 * App ViewModel。
 */
class AppViewModel(
    val toast: Toast,
    val repository: EducationLoginRepository,
) : ViewModel(),
    MVI<AppUiState, AppIntent> {
    private val _uiState = MutableStateFlow<AppUiState>(AppUiState.Init)
    override val uiState = _uiState.asStateFlow()

    private val handler = CoroutineExceptionHandler { _, throwable ->
        _uiState.value = AppUiState.Error(throwable)
        toast.show(throwable)
    }

    override fun dispatch(intent: AppIntent) {
        when (intent) {
            is AppIntent.Login -> with(intent) {
                login(userId, password, secondClassPwd)
            }
        }
    }

    /**
     * 登录教务系统。
     */
    private fun login(
        userId: String,
        password: String,
        secondClassPwd: String,
    ) {
        viewModelScope.launch(handler) {
            require(userId.isNotBlank()) { getString(Res.string.error_name_is_empty) }
            require(password.isNotBlank()) { getString(Res.string.error_password_is_empty) }
            val user = repository.login(userId, password, secondClassPwd)
            _uiState.emit(AppUiState.Init)
            _uiState.emit(AppUiState.LoggedIn(user))
        }
    }
}

/**
 * App 状态。
 */
sealed class AppUiState {
    data object Init : AppUiState()

    data class LoggedIn(val user: User) : AppUiState()

    data class Error(val e: Throwable) : AppUiState()
}

/**
 * App 意图。
 */
sealed class AppIntent {
    data class Login(
        val userId: String,
        val password: String,
        val secondClassPwd: String,
    ) : AppIntent()
}