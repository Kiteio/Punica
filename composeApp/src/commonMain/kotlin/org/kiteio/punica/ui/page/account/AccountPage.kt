package org.kiteio.punica.ui.page.account

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Login
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.foundation.User
import org.kiteio.punica.ui.compositionlocal.LocalWindowSizeClass
import org.kiteio.punica.ui.page.account.AccountCategory.*
import org.kiteio.punica.ui.widget.NavBackAppBar
import org.kiteio.punica.ui.widget.ProvideNotNull
import org.kiteio.punica.ui.widget.showToast
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.*

/**
 * 账号路由。
 *
 * @param categoryOrdinal [AccountCategory] ordinal（因 Desktop 暂不支持嵌套类，无法直接使用枚举类）
 */
@Serializable
data class AccountRoute(val categoryOrdinal: Int)


/**
 * 账号页面。
 */
@Composable
fun AccountPage(route: AccountRoute) = viewModel {
    val category = when (route.categoryOrdinal) {
        SecondClass.ordinal -> SecondClass
        Network.ordinal -> Network
        else -> Academic
    }

    AccountVM(category)
}.Content()


@Composable
private fun AccountVM.Content() {
    val scope = rememberCoroutineScope()
    val windowSizeClass = LocalWindowSizeClass.current

    // 用户列表
    val users by users.collectAsState(null)
    // 当前用户学号
    val userId by when (category) {
        Academic -> AppVM.academicUserId
        SecondClass -> AppVM.secondClassUserId
        Network -> AppVM.networkUserId
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
            TopBar(onAddUser = { loginWhenSave = true; accountBottomSheetVisible = true })
        }
    ) { innerPadding ->
        ProvideNotNull(
            users,
            isLoading = users == null,
            modifier = Modifier.padding(innerPadding),
        ) { users ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(
                    when (windowSizeClass.widthSizeClass) {
                        WindowWidthSizeClass.Compact -> 1
                        WindowWidthSizeClass.Medium -> 2
                        WindowWidthSizeClass.Expanded -> 3
                        else -> TODO("Not yet implemented")
                    }
                ),
                contentPadding = PaddingValues(4.dp),
            ) {
                items(users.sortedBy { it.id != userId }, key = { it.id }) {
                    UserCard(
                        user = it,
                        userId = userId,
                        onClick = {
                            user = it
                            loginWhenSave = false
                            accountBottomSheetVisible = true
                        },
                        onSetupCurrentAccount = {
                            scope.launchCatching { setupCurrentAccount(it.id) }
                        },
                        onRemoveCurrentAccount = {
                            scope.launchCatching { removeCurrentAccount() }
                        },
                        onDeleteAccount = {
                            user = it
                            deleteDialogVisible = true
                        },
                        modifier = Modifier.fillMaxWidth().padding(4.dp).animateItem(),
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
        onConfirm = {
            scope.launchCatching {
                saveAccount(it)
                accountBottomSheetVisible = false
                user = null
            }
        },
        category = category,
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
                    AppVM.deleteUser(category, user.id)
                    showToast(getString(Res.string.delete_successful))
                }
                deleteDialogVisible = false
                user = null
            }
        },
    )
}


/**
 * 顶部导航栏。
 */
@Composable
private fun AccountVM.TopBar(onAddUser: () -> Unit) {
    NavBackAppBar(
        title = { Text("${stringResource(category.nameRes)}${stringResource(Res.string.account)}") },
        actions = {
            IconButton(onClick = onAddUser) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = stringResource(Res.string.add),
                )
            }
        },
    )
}


/**
 * 用户。
 */
@Composable
private fun UserCard(
    user: User,
    userId: String?,
    onClick: () -> Unit,
    onSetupCurrentAccount: () -> Unit,
    onRemoveCurrentAccount: () -> Unit,
    onDeleteAccount: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier,
    ) {
        ListItem(
            headlineContent = {
                Text(
                    user.id,
                    color = if (user.id == userId) MaterialTheme.colorScheme.primary
                    else LocalContentColor.current,
                )
            },
            trailingContent = {
                TrailingContent(
                    isCurrentAccount = user.id == userId,
                    onSetupCurrentAccount = onSetupCurrentAccount,
                    onRemoveCurrentAccount = onRemoveCurrentAccount,
                    onDeleteAccount = onDeleteAccount,
                )
            }
        )
    }
}


@Composable
private fun TrailingContent(
    isCurrentAccount: Boolean,
    onSetupCurrentAccount: () -> Unit,
    onRemoveCurrentAccount: () -> Unit,
    onDeleteAccount: () -> Unit,
) {
    Row {
        // 设为、移除当前账号
        IconButton(
            onClick = if (isCurrentAccount) onRemoveCurrentAccount
            else onSetupCurrentAccount,
        ) {
            Icon(
                if (isCurrentAccount) Icons.AutoMirrored.Outlined.Logout
                else Icons.AutoMirrored.Outlined.Login,
                contentDescription = stringResource(
                    if (isCurrentAccount) Res.string.remove_current_account
                    else Res.string.set_up_current_account,
                ),
            )
        }
        // 删除账号
        IconButton(onClick = onDeleteAccount) {
            Icon(
                Icons.Outlined.DeleteOutline,
                contentDescription = stringResource(Res.string.delete),
            )
        }
    }
}