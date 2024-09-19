package org.kiteio.punica.ui.screen.bottom

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ContentPaste
import androidx.compose.material.icons.rounded.EditLocationAlt
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import coil.compose.rememberAsyncImagePainter
import compose.icons.TablerIcons
import compose.icons.tablericons.CalendarEvent
import kotlinx.coroutines.delay
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.candy.collectAsState
import org.kiteio.punica.candy.launchCatch
import org.kiteio.punica.copyToFiles
import org.kiteio.punica.datastore.Keys
import org.kiteio.punica.datastore.Preferences
import org.kiteio.punica.edu.foundation.Campus
import org.kiteio.punica.getString
import org.kiteio.punica.ui.LocalNavController
import org.kiteio.punica.ui.LocalViewModel
import org.kiteio.punica.ui.avatarPainter
import org.kiteio.punica.ui.component.DatePickerDialog
import org.kiteio.punica.ui.component.Dialog
import org.kiteio.punica.ui.component.DialogVisibility
import org.kiteio.punica.ui.component.Icon
import org.kiteio.punica.ui.component.Image
import org.kiteio.punica.ui.component.RadioButton
import org.kiteio.punica.ui.component.ScaffoldColumn
import org.kiteio.punica.ui.component.SubduedText
import org.kiteio.punica.ui.component.Title
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.navigation.navigateTo
import java.time.LocalDate

/**
 * 我
 */
@Composable
fun MeScreen() {
    val navController = LocalNavController.current

    val avatarPainter = avatarPainter

    var contentVisible by remember { mutableStateOf(false) }
    var avatarDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = Unit) {
        delay(50)
        contentVisible = true
    }

    ScaffoldColumn(contentWindowInsets = WindowInsets.captionBar) {
        AnimatedVisibility(
            visible = contentVisible,
            enter = slideInVertically { -it },
            modifier = Modifier.weight(0.35f)
        ) {
            UserCard(
                painter = avatarPainter,
                onClick = { navController.navigateTo(Route.Login) },
                onAvatarClick = { avatarDialogVisible = true }
            )
        }

        Spacer(modifier = Modifier.height(dp4(4)))

        AnimatedVisibility(
            visible = contentVisible,
            enter = slideInVertically { it },
            modifier = Modifier.weight(0.55f)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Settings(
                    modifier = Modifier
                        .padding(dp4(5))
                        .clip(CardDefaults.shape)
                )
            }
        }
    }

    AvatarDialog(
        visible = avatarDialogVisible,
        onDismiss = { avatarDialogVisible = false },
        initialAvatar = avatarPainter
    )
}


/**
 * 用户卡片（附带背景）
 * @param painter 头像 [Painter]
 * @param onClick 卡片点击事件
 * @param onAvatarClick 头像点击事件
 * @param modifier
 */
@Composable
private fun UserCard(
    painter: Painter,
    onClick: () -> Unit,
    onAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel = LocalViewModel.current

    val avatarSize = dp4(18)
    val space = dp4(5)

    Box(modifier = modifier.run { if (this == Modifier) height(dp4(60) + avatarSize / 2) else this }) {
        // 背景
        Column {
            Image(
                painter = painter,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .blur(dp4(2))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(0.4f))
            )
            Spacer(modifier = Modifier.height(avatarSize / 2))
        }

        // 用户卡片
        Column {
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(0.9f),
                shadowElevation = 2.dp,
                modifier = Modifier
                    .padding(horizontal = space)
                    .clip(MaterialTheme.shapes.extraLarge)
                    .clickable(onClick = onClick)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Avatar(
                        painter = painter,
                        onClick = onAvatarClick,
                        modifier = Modifier.size(avatarSize)
                    )
                    Spacer(modifier = Modifier.width(dp4(4)))
                    Column {
                        Title(
                            text = viewModel.eduSystem?.name ?: getString(R.string.click_to_login),
                            style = MaterialTheme.typography.titleMedium
                        )
                        SubduedText(text = getString(R.string.punica_poem))
                    }
                }
            }
        }
    }
}


/**
 * 头像
 * @param painter 头像 [Painter]
 * @param onClick 头像点击事件
 * @param modifier
 */
@Composable
private fun Avatar(painter: Painter, onClick: () -> Unit, modifier: Modifier = Modifier) {
    OutlinedCard(
        onClick = onClick,
        elevation = CardDefaults.elevatedCardElevation(),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Image(
            painter = painter,
            modifier = modifier.aspectRatio(1f),
            contentScale = ContentScale.Crop
        )
    }
}


/**
 * 头像更改弹窗
 * @param visible
 * @param onDismiss
 * @param initialAvatar 初始头像 [Painter]
 */
@Composable
private fun AvatarDialog(
    visible: Boolean,
    onDismiss: () -> Unit,
    initialAvatar: Painter
) {
    DialogVisibility(visible = visible) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val clipboardManager = LocalClipboardManager.current
        var isReset by remember { mutableStateOf(false) }

        var uri by remember { mutableStateOf<String?>(null) }
        val launcher = rememberLauncherForActivityResult(contract = PickVisualMedia()) {
            it?.let { uri = context.copyToFiles(it, "images", "avatar.webp");isReset = false }
        }

        Dialog(
            title = { Text(text = getString(R.string.customize_avatar)) },
            text = {
                Avatar(
                    painter = uri?.let {
                        rememberAsyncImagePainter(
                            model = it,
                            onError = { Toast(R.string.this_is_not_a_valid_link).show() }
                        )
                    } ?: if (isReset) painterResource(id = R.drawable.punica) else initialAvatar,
                    onClick = {},
                    modifier = Modifier.size(dp4(24))
                )
                Spacer(modifier = Modifier.height(dp4(6)))

                Row {
                    IconButton(onClick = { uri = clipboardManager.getText().toString() }) {
                        Icon(imageVector = Icons.Rounded.ContentPaste)
                    }
                    Spacer(modifier = Modifier.width(dp4(6)))

                    IconButton(onClick = { uri = null; isReset = true }) {
                        Icon(imageVector = Icons.Rounded.Replay)
                    }
                    Spacer(modifier = Modifier.width(dp4(6)))

                    IconButton(
                        onClick = {
                            launcher.launch(
                                PickVisualMediaRequest.Builder()
                                    .setMediaType(PickVisualMedia.ImageOnly)
                                    .build()
                            )
                        }
                    ) {
                        Icon(imageVector = Icons.Rounded.Image)
                    }
                }
                Spacer(modifier = Modifier.height(dp4(4)))

                Column(Modifier.fillMaxWidth()) {
                    Tip(
                        leadingText = getString(R.string.left),
                        text = getString(R.string.paste_the_url)
                    )
                    Tip(
                        leadingText = getString(R.string.middle),
                        text = getString(R.string.restore_the_default)
                    )
                    Tip(
                        leadingText = getString(R.string.right),
                        text = getString(R.string.pick_a_local_picture)
                    )
                }
            },
            onDismiss = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launchCatch {
                            Preferences.edit {
                                uri?.let { uri -> it[Keys.avatarUri] = uri }
                                    ?: it.remove(Keys.avatarUri)
                            }
                            onDismiss()
                        }
                    }
                ) { Text(text = getString(R.string.save)) }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(text = getString(R.string.cancel)) }
            }
        )
    }
}


/**
 * 功能提示
 * @param leadingText 功能
 * @param text 功能描述
 */
@Composable
private fun Tip(leadingText: String, text: String) {
    Text(
        text = buildAnnotatedString {
            withStyle(
                LocalTextStyle.current.toSpanStyle().copy(
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            ) {
                append(leadingText)
            }
            append(" $text")
        }
    )
}


/**
 * 设置
 * @param modifier
 */
@Composable
private fun Settings(modifier: Modifier = Modifier) {
    val coroutineScope = rememberCoroutineScope()
    val preferences by Preferences.data.collectAsState()
    val schoolStart by remember {
        derivedStateOf { preferences?.get(Keys.schoolStart)?.let { LocalDate.parse(it) } }
    }

    var datePickerDialogVisible by remember { mutableStateOf(false) }
    var campusDialogVisible by remember { mutableStateOf(false) }
    var dropdownMenuExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.padding(1.dp)) {
        SettingItem(route = Route.Account)
        Spacer(modifier = Modifier.height(dp4(2)))

        SettingItem(
            nameResId = R.string.school_start,
            icon = TablerIcons.CalendarEvent,
            onClick = { datePickerDialogVisible = true },
            value = {
                Text(
                    text = schoolStart?.toString() ?: getString(R.string.not_set)
                )
            }
        )
        Spacer(modifier = Modifier.height(dp4(2)))

        SettingItem(
            nameResId = R.string.campus,
            icon = Icons.Rounded.EditLocationAlt,
            onClick = { dropdownMenuExpanded = true },
            value = {
                Text(
                    text = getString(
                        (preferences?.get(Keys.campusId)?.let { Campus.getById(it) }
                            ?: Campus.Canton).nameResId
                    )
                )
                DropdownMenu(
                    expanded = dropdownMenuExpanded,
                    onDismissRequest = { dropdownMenuExpanded = false }
                ) {
                    Campus.values.forEach { campus ->
                        DropdownMenuItem(
                            text = { Text(text = getString(campus.nameResId)) },
                            onClick = {
                                coroutineScope.launchCatch {
                                    Preferences.edit {
                                        it[Keys.campusId] = campus.id
                                    }
                                    delay(80)
                                    dropdownMenuExpanded = false
                                }
                            }
                        )
                    }
                }
            }
        )
        Spacer(modifier = Modifier.height(dp4(2)))

        SettingItem(route = Route.Version)
        Spacer(modifier = Modifier.height(dp4(2)))

        SettingItem(route = Route.Settings)
    }

    DatePickerDialog(
        visible = datePickerDialogVisible,
        onDismiss = { datePickerDialogVisible = false },
        onConfirm = { localDate ->
            coroutineScope.launchCatch {
                Preferences.edit { it[Keys.schoolStart] = localDate.toString() }
                datePickerDialogVisible = false
            }
        },
        title = { Text(text = getString(R.string.school_start)) },
        initialDate = schoolStart
    )
    CampusDialog(
        visible = campusDialogVisible,
        onDismiss = { campusDialogVisible = false },
        initialCampus = preferences?.get(Keys.campusId)
    )
}


/**
 * 设置项
 * @param route
 */
@Composable
private fun SettingItem(route: Route) {
    val navController = LocalNavController.current
    with(route) {
        SettingItem(
            nameResId = nameResId,
            icon = icon,
            onClick = { navController.navigateTo(this) }
        )
    }
}


/**
 * 设置项
 * @param nameResId 名称
 * @param icon 前部图标
 * @param onClick
 * @param value 当前值
 */
@Composable
private fun SettingItem(
    @StringRes nameResId: Int,
    icon: ImageVector,
    onClick: () -> Unit,
    value: @Composable (() -> Unit)? = null
) {
    ElevatedCard(
        onClick = onClick,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dp4(4)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Icon(imageVector = icon)
                Spacer(modifier = Modifier.width(dp4(2)))
                Text(text = getString(nameResId))
            }

            value?.apply { Box { invoke() } }
        }
    }
}


/**
 * 更改校区
 * @param visible
 * @param onDismiss
 * @param initialCampus
 */
@Composable
private fun CampusDialog(visible: Boolean, onDismiss: () -> Unit, initialCampus: Int?) {
    DialogVisibility(visible = visible) {
        val coroutineScope = rememberCoroutineScope()
        var campusId by remember { mutableIntStateOf(initialCampus ?: Campus.Canton.id) }

        Dialog(
            title = { Text(text = getString(R.string.change_campus)) },
            text = {
                Campus.values.forEach { campus ->
                    RadioButton(
                        selected = campus.id == campusId,
                        onClick = {
                            campusId = campus.id
                            coroutineScope.launchCatch {
                                Preferences.edit {
                                    it[Keys.campusId] = campusId
                                }
                                delay(80)
                                onDismiss()
                            }
                        },
                        label = getString(campus.nameResId)
                    )
                }
            },
            onDismiss = onDismiss,
            contentHorizontalAlignment = Alignment.Start
        )
    }
}