package org.kiteio.punica.ui.screen

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.godaddy.android.colorpicker.ClassicColorPicker
import com.godaddy.android.colorpicker.HsvColor
import com.materialkolor.PaletteStyle
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Gitee
import compose.icons.simpleicons.Github
import org.kiteio.punica.R
import org.kiteio.punica.Toast
import org.kiteio.punica.candy.*
import org.kiteio.punica.datastore.*
import org.kiteio.punica.getString
import org.kiteio.punica.openUri
import org.kiteio.punica.ui.component.*
import org.kiteio.punica.ui.dp4
import org.kiteio.punica.ui.navigation.Route
import org.kiteio.punica.ui.subduedContentColor
import org.kiteio.punica.ui.toColor
import org.kiteio.punica.ui.toHexString

/**
 * 设置
 */
@Composable
fun SettingsScreen() {
    val coroutineScope = rememberCoroutineScope()
    val preferences by Preferences.data.collectAsState()

    var keysBottomSheetVisible by remember { mutableStateOf(false) }
    var visibleStore by remember { mutableStateOf<DataStore<Preferences>?>(null) }
    var colorPickerVisible by remember { mutableStateOf(false) }

    ScaffoldBox(topBar = { NavBackTopAppBar(route = Route.Settings) }) {
        LazyColumn {
            title(getString(R.string.theme))

            item {
                val options = listOf(
                    R.string.from_avatar,
                    R.string.material_you,
                    R.string.random_color,
                    R.string.from_color
                )
                val selectedIndex by remember {
                    derivedStateOf { preferences?.get(Keys.themeColorSource) ?: 0 }
                }

                OptionsSetting(
                    text = { Text(text = getString(R.string.color)) },
                    options = options,
                    toString = { getString(it) },
                    selectedIndex = selectedIndex,
                    onSelect = { index ->
                        coroutineScope.launchCatch {
                            Preferences.edit { it[Keys.themeColorSource] = index }
                        }
                        if (index == 3) colorPickerVisible = true
                    },
                    leadingIcon = { Icon(imageVector = Icons.Rounded.ColorLens) },
                    desc = if (selectedIndex == 3) {
                        {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(dp4(2))
                                        .clip(CircleShape)
                                        .background(
                                            preferences
                                                ?.get(Keys.themeColor)
                                                ?.toColor() ?: Color.Transparent
                                        )
                                        .border(BorderStroke(1.dp, subduedContentColor()))
                                )
                                Spacer(modifier = Modifier.width(dp4(2)))
                                Text(text = preferences?.get(Keys.themeColor) ?: "")
                            }
                        }
                    } else null
                )
            }

            item {
                val options = PaletteStyle.entries.map { it.name }

                val selectedIndex by remember {
                    derivedStateOf { preferences?.get(Keys.themeStyle) ?: 0 }
                }

                OptionsSetting(
                    text = { Text(text = getString(R.string.style)) },
                    options = options,
                    toString = { it },
                    selectedIndex = selectedIndex,
                    enabled = (preferences?.get(Keys.themeColorSource) ?: 0) == 0,
                    onSelect = { index ->
                        coroutineScope.launchCatch {
                            Preferences.edit { it[Keys.themeStyle] = index }
                        }
                    },
                    leadingIcon = { Icon(imageVector = Icons.Rounded.ScatterPlot) }
                )
            }

            title(getString(R.string.contact))

            item {
                val clipboardManager = LocalClipboardManager.current

                Setting(
                    text = { Text(text = getString(R.string.email)) },
                    onClick = {
                        clipboardManager.setText("17875765201@163.com")
                        Toast(R.string.copied).show()
                    },
                    leadingIcon = { Icon(imageVector = Icons.Rounded.MailOutline) },
                    desc = { Text(text = "17875765201@163.com") }
                )
            }

            item {
                Setting(
                    text = { Text(text = "Github") },
                    onClick = { openUri(URLs.PUNICA_GITHUB) },
                    leadingIcon = { Icon(imageVector = SimpleIcons.Github) }
                )
            }

            item {
                Setting(
                    text = { Text(text = "Gitee") },
                    onClick = { openUri(URLs.PUNICA_GITEE) },
                    leadingIcon = { Icon(imageVector = SimpleIcons.Gitee) }
                )
            }

            title(getString(R.string.storage))

            item {
                StoreSetting(
                    resId = R.string.timetable,
                    onClick = { visibleStore = Timetables; keysBottomSheetVisible = true },
                    icon = Icons.Rounded.CalendarMonth
                )
            }

            val items = listOf(
                Route.Module.ExamPlan to ExamPlans,
                Route.Module.SchoolReport to SchoolReports,
                Route.Module.LevelReport to LevelReports,
                Route.Module.SecondClass to SecondClassReports,
                Route.Module.TimetableAll to TimetableAlls,
                Route.Module.Plan to Plans,
                Route.Module.Progress to Progresses
            )

            items(items) {
                StoreSetting(
                    onClick = { visibleStore = it.second; keysBottomSheetVisible = true },
                    route = it.first
                )
            }

            title(getString(R.string.others))

            item {
                Setting(
                    text = { Text(text = getString(R.string.debug)) },
                    onClick = {
                        coroutineScope.launchCatch {
                            Preferences.edit { it[Keys.debug] = !(it[Keys.debug] ?: false) }
                        }
                    },
                    leadingIcon = { Icon(Icons.Rounded.BugReport) },
                    end = {
                        Switch(
                            checked = preferences?.get(Keys.debug) ?: false,
                            onCheckedChange = { value ->
                                coroutineScope.launchCatch { Preferences.edit { it[Keys.debug] = value } }
                            }
                        )
                    }
                )
            }
        }

        KeysBottomSheet(
            visible = keysBottomSheetVisible,
            onDismiss = { keysBottomSheetVisible = false },
            store = visibleStore
        )

        ColorPicker(
            visible = colorPickerVisible,
            onDismiss = { colorPickerVisible = false },
            hex = preferences?.get(Keys.themeColor)
        )
    }
}


/**
 * 设置分类
 * @receiver [LazyListScope]
 * @param text
 */
@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.title(text: String) {
    stickyHeader {
        Surface(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = dp4(4), bottom = dp4(), start = dp4(4))
            )
        }
    }
}


/**
 * 有选项的设置
 * @param text
 * @param options
 * @param toString
 * @param selectedIndex
 * @param onSelect
 * @param modifier
 * @param enabled
 * @param leadingIcon
 * @param desc
 */
@Composable
private fun <T> OptionsSetting(
    text: @Composable () -> Unit,
    options: List<T>,
    toString: (T) -> String,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    desc: (@Composable () -> Unit)? = null,
) {
    var expanded by remember { mutableStateOf(false) }

    Setting(
        text = text,
        onClick = { expanded = true },
        modifier = modifier,
        enabled = enabled,
        leadingIcon = leadingIcon,
        desc = desc,
        end = {
            Box {
                Text(text = toString(options[selectedIndex]))
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    options.forEachIndexed { index, item ->
                        DropdownMenuItem(
                            text = { Text(text = toString(item)) },
                            onClick = { onSelect(index); expanded = false },
                            selected = index == selectedIndex
                        )
                    }
                }
            }
        }
    )
}


/**
 * Store 键编辑项
 * @param onClick
 * @param route
 */
@Composable
private fun StoreSetting(onClick: () -> Unit, route: Route) {
    StoreSetting(resId = route.nameResId, onClick = onClick, icon = route.icon)
}


/**
 * Store 键编辑项
 * @param resId
 * @param onClick
 * @param icon
 */
@Composable
private fun StoreSetting(@StringRes resId: Int, onClick: () -> Unit, icon: ImageVector) {
    Setting(
        text = { Text(text = getString(resId)) },
        onClick = onClick,
        leadingIcon = { Icon(imageVector = icon) }
    )
}


/**
 * 设置
 * @param text 标题
 * @param onClick
 * @param modifier
 * @param enabled
 * @param leadingIcon
 * @param desc
 * @param end
 * @param bottom
 */
@Composable
private fun Setting(
    text: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    desc: (@Composable () -> Unit)? = null,
    end: (@Composable () -> Unit)? = null,
    bottom: (@Composable () -> Unit)? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RectangleShape,
        enabled = enabled,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(horizontal = dp4(4), vertical = dp4(5))) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                leadingIcon?.run {
                    invoke()
                    Spacer(modifier = Modifier.width(dp4(2)))
                }

                Column(modifier = Modifier.weight(1f)) {
                    text()

                    CompositionLocalProvider(
                        value = LocalTextStyle provides MaterialTheme.typography.titleSmall.copy(
                            color = subduedContentColor()
                        )
                    ) { desc?.invoke() }
                }
                Spacer(modifier = Modifier.width(dp4(2)))

                end?.invoke()
            }

            bottom?.run {
                Spacer(modifier = Modifier.height(dp4(2)))
                invoke()
            }
        }
    }
}


/**
 * 键预览
 * @param visible
 * @param onDismiss
 * @param store
 */
@Composable
private fun KeysBottomSheet(
    visible: Boolean,
    onDismiss: () -> Unit,
    store: DataStore<Preferences>?
) {
    BottomSheet(visible = visible, onDismiss = onDismiss, skipPartiallyExpanded = true) {
        store?.run {
            val coroutineScope = rememberCoroutineScope()
            val preferences by data.collectAsState()
            val keys by remember { derivedStateOf { preferences?.keys() ?: emptyList() } }

            var deleteDialogVisible by remember { mutableStateOf(false) }
            var visibleKey by remember { mutableStateOf<String?>(null) }

            LazyColumn(
                contentPadding = PaddingValues(dp4(2)),
                modifier = Modifier.fillMaxHeight(0.5f)
            ) {
                item {
                    Title(
                        text = getString(R.string.key_editing),
                        modifier = Modifier.padding(dp4(2))
                    )
                    Spacer(modifier = Modifier.height(dp4(2)))
                }
                items(keys) {
                    ElevatedCard(onClick = {}, modifier = Modifier.padding(dp4(2))) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(dp4(4))
                        ) {
                            Title(text = it, modifier = Modifier.weight(1f))
                            IconButton(onClick = { visibleKey = it; deleteDialogVisible = true }) {
                                Icon(imageVector = Icons.Rounded.Close)
                            }
                        }
                    }
                }
            }

            DeleteDialog(
                visible = deleteDialogVisible,
                onDismiss = { deleteDialogVisible = false },
                onConfirm = {
                    coroutineScope.launchCatch {
                        visibleKey?.let { key ->
                            edit { it.remove(stringPreferencesKey(key)) }
                        }
                        deleteDialogVisible = false
                    }
                },
                desc = visibleKey
            )
        }
    }
}


/**
 * 颜色选取
 * @param visible
 * @param onDismiss
 * @param hex
 */
@Composable
private fun ColorPicker(visible: Boolean, onDismiss: () -> Unit, hex: String?) {
    DialogVisibility(visible = visible) {
        val coroutineScope = rememberCoroutineScope()
        val clipboardManager = LocalClipboardManager.current

        val initColor = remember { hex?.toColor() ?: Color.Transparent }
        var color by remember { mutableStateOf(HsvColor.from(initColor)) }
        var hexString by remember { mutableStateOf(initColor.toHexString()) }

        Dialog(
            text = {
                ClassicColorPicker(
                    color = color,
                    onColorChanged = { color = it; hexString = it.toColor().toHexString() },
                    modifier = Modifier.aspectRatio(1f)
                )

                Spacer(modifier = Modifier.height(dp4(2)))

                TextField(
                    value = hexString,
                    onValueChange = {
                        hexString = it
                        catchUnit { color = HsvColor.from(hexString.toColor()) }
                    },
                    label = { Text(text = getString(R.string.hex_color)) },
                    leadingIcon = { Icon(imageVector = Icons.Rounded.Numbers) },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                hexString = clipboardManager.getText().toString()
                                catchUnit { color = HsvColor.from(hexString.toColor()) }
                            }
                        ) { Icon(imageVector = Icons.Rounded.ContentPaste) }
                    }
                )
            },
            onDismiss = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        coroutineScope.launchCatch {
                            Preferences.edit {
                                it[Keys.themeColor] = color.toColor().toHexString()
                            }
                            onDismiss()
                        }
                    }
                ) {
                    Text(text = getString(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(text = getString(R.string.cancel)) }
            }
        )
    }
}