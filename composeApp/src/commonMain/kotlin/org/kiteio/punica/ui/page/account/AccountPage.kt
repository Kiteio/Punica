package org.kiteio.punica.ui.page.account

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.foundation.User
import org.kiteio.punica.ui.component.LoadingNotNullOrEmpty
import org.kiteio.punica.ui.component.NavBackAppBar
import org.kiteio.punica.ui.page.account.PasswordType.Academic
import org.kiteio.punica.ui.page.account.PasswordType.OTP
import org.kiteio.punica.ui.page.account.PasswordType.SecondClass
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.add

/**
 * 账号页面路由。
 *
 * @param typeOrdinal [PasswordType] ordinal（因 Desktop 暂不支持嵌套类，无法直接使用枚举类）
 */
@Serializable
data class AccountRoute(val typeOrdinal: Int)


/**
 * 账号页面。
 */
@Composable
fun AccountPage(route: AccountRoute) = viewModel {
    AccountVM(PasswordType.entries[route.typeOrdinal])
}.Content()


@Composable
private fun AccountVM.Content() {
    val scope = rememberCoroutineScope()

    // 用户列表
    val users by users.collectAsState(null)
    // 当前学号
    val userId by when (type) {
        Academic -> AppVM.academicUserId
        SecondClass -> AppVM.secondClassUserId
        else -> flowOf(null)
    }.collectAsState(null)

    // 账号模态对话框可见性
    var accountBottomSheetVisible by remember { mutableStateOf(false) }
    // 保存时登录
    var loginWhenSave by remember { mutableStateOf(false) }
    // 删除对话框可见性
    var deleteDialogVisible by remember { mutableStateOf(false) }
    // 选中用户
    var user by remember { mutableStateOf<User?>(null) }

    Scaffold(
        topBar = {
            NavBackAppBar(
                title = { Text(stringResource(type.nameRes)) },
                actions = {
                    // 添加账号
                    IconButton(
                        onClick = {
                            loginWhenSave = type != OTP
                            accountBottomSheetVisible = true
                        }
                    ) {
                        Icon(
                            Icons.Outlined.Add,
                            contentDescription = stringResource(Res.string.add),
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        LoadingNotNullOrEmpty(
            users,
            isLoading = users == null,
            modifier = Modifier.padding(innerPadding),
        ) { users ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(256.dp),
                contentPadding = PaddingValues(8.dp),
            ) {
                items(users.sortedBy { it.id != userId }, key = { it.id }) {
                    if (type == OTP) OTPUser(
                        user = it,
                        onClick = {
                            user = it
                            loginWhenSave = false
                            accountBottomSheetVisible = true
                        },
                        modifier = Modifier.padding(8.dp).animateItem()
                    ) else User(
                        user = it,
                        isCurrentAccount = it.id == userId,
                        onClick = {
                            user = it
                            loginWhenSave = false
                            accountBottomSheetVisible = true
                        },
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }

    // 账号模态对话框
    AccountBottomSheet(
        accountBottomSheetVisible,
        onDismissRequest = {
            accountBottomSheetVisible = false
            user = null
        },
        initialUser = user,
        initialLoginWhenSave = loginWhenSave,
    )

    // 删除对话框
    DeleteDialog(
        deleteDialogVisible,
        onDismissRequest = { deleteDialogVisible = false },
        onConfirm = {
            scope.launchCatching {
                user?.let { user ->
                    // 删除账号
                    AppVM.deleteUser(type, user.id)
                }
                deleteDialogVisible = false
                user = null
            }
        },
    )
}