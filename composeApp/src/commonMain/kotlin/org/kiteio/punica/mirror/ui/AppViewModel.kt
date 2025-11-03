package org.kiteio.punica.mirror.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jakarta.inject.Singleton
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.kiteio.punica.mirror.repository.EducationLoginRepository
import org.kiteio.punica.mirror.storage.Preferences
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.error_password_is_empty
import punica.composeapp.generated.resources.error_user_id_is_11_digits
import punica.composeapp.generated.resources.login_successful

/**
 * App [ViewModel]。
 */
@Singleton
class AppViewModel(
    private val educationLoginRepository: EducationLoginRepository,
    private val toast: Toast,
) : ViewModel() {
    private val _userState = MutableStateFlow<UserState>(UserState.None)

    val userState = _userState.asStateFlow()

    /**
     * 登录教务系统。
     */
    fun login(userId: String, password: String, secondClassPwd: String) {
        val handler = loginExceptionHandler(userId)

        viewModelScope.launch(handler) {
            require(userId.length == 11) {
                getString(Res.string.error_user_id_is_11_digits)
            }
            require(password.isNotBlank()) {
                getString(Res.string.error_password_is_empty)
            }
            // 更新状态为登录中
            _userState.update { UserState.LoggingIn(userId) }
            educationLoginRepository.login(
                userId,
                password,
                secondClassPwd,
            )
            // 更新状态为登录成功
            _userState.update { UserState.LoggedIn(userId) }
            toast.show(getString(Res.string.login_successful))
        }
    }

    /**
     * 自动登录教务系统。
     */
    fun autoLogin() {
        viewModelScope.launch {
            // 本地获取学号
            val userId = Preferences.userId.first()
            if (userId != null) {
                _userState.update { UserState.NotLogin(userId) }
                val handler = loginExceptionHandler(userId)
                launch(handler) {
                    educationLoginRepository.autoLogin(userId)
                    // 更新状态为登录成功
                    _userState.update { UserState.LoggedIn(userId) }
                }
            }
        }
    }

    /**
     * 登录异常处理。
     *
     * @param userId 学号
     */
    private fun loginExceptionHandler(userId: String?): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, throwable ->
            toast.show(throwable)
            _userState.update {
                if (userId == null) {
                    UserState.None
                } else {
                    UserState.LoggingIn(userId)
                }
            }
        }
    }
}

sealed class UserState {
    abstract val userId: String?

    data object None : UserState() {
        override val userId = null
    }

    data class NotLogin(override val userId: String) : UserState()

    data class LoggingIn(override val userId: String) : UserState()

    data class LoggedIn(override val userId: String) : UserState()
}