package org.kiteio.punica.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.datastore.preferences.core.edit
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.candy.launchCatch
import org.kiteio.punica.candy.limit
import org.kiteio.punica.datastore.Keys
import org.kiteio.punica.datastore.Preferences
import org.kiteio.punica.datastore.Users
import org.kiteio.punica.datastore.remove
import org.kiteio.punica.datastore.set
import org.kiteio.punica.edu.foundation.User
import org.kiteio.punica.getString
import org.kiteio.punica.ui.LocalViewModel
import org.kiteio.punica.ui.collectAsIdentifiedList
import org.kiteio.punica.ui.component.DeleteDialog
import org.kiteio.punica.ui.component.Dialog
import org.kiteio.punica.ui.component.DialogVisibility
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.NavBackTopAppBar
import org.kiteio.punica.ui.component.PasswordField
import org.kiteio.punica.ui.component.ScaffoldColumn
import org.kiteio.punica.ui.component.TextField
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route

/**
 * 账号
 */
@Composable
fun AccountScreen() {
    val coroutineScope = rememberCoroutineScope()
    val viewModel = LocalViewModel.current
    val users = Users.collectAsIdentifiedList<User>()

    var userDialogVisible by remember { mutableStateOf(false) }
    var deleteDialogVisible by remember { mutableStateOf(false) }
    var visibleUser by remember { mutableStateOf<User?>(null) }

    ScaffoldColumn(
        topBar = { NavBackTopAppBar(route = Route.Account) },
        floatingActionButton = {
            FloatingActionButton(onClick = { visibleUser = null; userDialogVisible = true }) {
                Icon(imageVector = Icons.Rounded.Add)
            }
        }
    ) {
        LazyColumn(contentPadding = PaddingValues(dp4(2))) {
            items(users) {
                ElevatedCard(
                    onClick = { visibleUser = it; userDialogVisible = true },
                    modifier = Modifier.padding(dp4(2)).animateItem()
                ) {
                    Row(
                        modifier = Modifier.padding(dp4(4)),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Title(text = it.name, modifier = Modifier.weight(1f))

                        IconButton(onClick = { visibleUser = it; deleteDialogVisible = true }) {
                            Icon(imageVector = Icons.Rounded.Close)
                        }
                    }
                }
            }
        }
    }

    UserDialog(
        visible = userDialogVisible,
        onDismiss = { userDialogVisible = false; },
        user = visibleUser
    )

    DeleteDialog(
        visible = deleteDialogVisible,
        onDismiss = { deleteDialogVisible = false },
        onConfirm = {
            coroutineScope.launchCatch {
                visibleUser?.let { user ->
                    Users.edit { it.remove(user) }
                    Preferences.edit {
                        if (it[Keys.lastUsername] == user.name) it.remove(Keys.lastUsername)
                    }
                    if(viewModel.eduSystem?.name == user.name) {
                        viewModel.onLogout()
                    }
                }
                deleteDialogVisible = false
                Toast(R.string.deleted).show()
            }
        },
        desc = visibleUser?.name
    )
}


/**
 * 用户编辑
 * @param visible
 * @param onDismiss
 * @param user
 */
@Composable
private fun UserDialog(visible: Boolean, onDismiss: () -> Unit, user: User?) {
    DialogVisibility(visible = visible) {
        val coroutineScope = rememberCoroutineScope()
        var name by remember { mutableStateOf(user?.name ?: "") }
        var pwd by remember { mutableStateOf(user?.pwd ?: "") }
        var secondClassPwd by remember { mutableStateOf(user?.secondClassPwd ?: "") }
        var campusNetPwd by remember { mutableStateOf(user?.campusNetPwd ?: "") }

        Dialog(
            title = { Text(text = getString(R.string.account_editing)) },
            text = {
                TextField(
                    value = name,
                    onValueChange = { name = it.limit(11).filter { value -> value.isDigit() } },
                    label = { Text(text = getString(R.string.student_id)) }
                )
                Spacer(modifier = Modifier.height(dp4(2)))

                PasswordField(
                    value = pwd,
                    onValueChange = { pwd = it },
                    label = { Text(text = getString(R.string.portal_password)) }
                )
                Spacer(modifier = Modifier.height(dp4(2)))

                PasswordField(
                    value = secondClassPwd,
                    onValueChange = { secondClassPwd = it },
                    label = { Text(text = getString(R.string.second_class_password)) }
                )
                Spacer(modifier = Modifier.height(dp4(2)))

                PasswordField(
                    value = campusNetPwd,
                    onValueChange = { campusNetPwd = it },
                    label = { Text(text = getString(R.string.campus_net_password)) }
                )
            },
            onDismiss = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launchCatch {
                            Users.edit {
                                it.set(
                                    User(
                                        name,
                                        pwd,
                                        secondClassPwd,
                                        campusNetPwd,
                                        if (name == user?.name) user.cookies else mutableSetOf()
                                    )
                                )
                            }
                            onDismiss()
                        }
                    },
                    enabled = name.isNotEmpty()
                ) {
                    Text(text = getString(R.string.save))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(text = getString(R.string.cancel)) }
            }
        )
    }
}