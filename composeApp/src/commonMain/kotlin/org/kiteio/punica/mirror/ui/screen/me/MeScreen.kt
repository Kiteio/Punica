package org.kiteio.punica.mirror.ui.screen.me

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditCalendar
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.AsyncImage
import com.github.skydoves.colorpicker.compose.AlphaSlider
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.mirror.modal.Gender
import org.kiteio.punica.mirror.modal.bing.Wallpaper
import org.kiteio.punica.mirror.modal.education.Campus
import org.kiteio.punica.mirror.storage.Preferences
import org.kiteio.punica.mirror.ui.animation.dotLottiePainterResource
import org.kiteio.punica.mirror.ui.navigation.BottomNavKey
import org.kiteio.punica.mirror.ui.screen.settings.SettingsRoute
import org.kiteio.punica.mirror.ui.theme.punicaColor
import org.kiteio.punica.mirror.util.syncFirst
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import punica.composeapp.generated.resources.*

/**
 * 我的页入口。
 */
fun EntryProviderBuilder<NavKey>.meEntry() {
    entry<MeRoute> { MeScreen() }
}

/**
 * 我的页路由。
 */
@Serializable
data object MeRoute : BottomNavKey {
    private val gender = Preferences.gender.syncFirst()
    private var _icon = mutableStateOf(gender.avatar)
    private var _selectedIcon = mutableStateOf(gender.selectedAvatar)

    override val strRes = Res.string.me
    override val icon by _icon
    override val selectedIcon by _selectedIcon

    fun changeGender(gender: Gender) {
        _icon.value = gender.avatar
        _selectedIcon.value = gender.selectedAvatar
    }
}

@OptIn(ExperimentalHazeMaterialsApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun MeScreen() {
    val backStack = koinInject<NavBackStack<NavKey>>()

    val viewModel = koinViewModel<MeViewModel>()

    var username by remember { mutableStateOf("12345678910") }

    // 壁纸
    val wallpaper by viewModel.wallpaper.collectAsState(null)
    // 壁纸是否正在加载
    val isWallpaperLoading by viewModel.isWallpaperLoading.collectAsState()

    // 校区
    val campus by viewModel.campus.collectAsState(Campus.Default)

    // 周次
    val week by viewModel.week.collectAsState(0)

    // 主色调
    val primaryColor by viewModel.primaryColor.collectAsState(punicaColor)

    // 性别
    val gender by viewModel.gender.collectAsState(Gender.Default)

    val hazeState = rememberHazeState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(
                bottom = innerPadding.calculateBottomPadding(),
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box {
                // 壁纸
                Wallpaper(
                    wallpaper = wallpaper,
                    isWallpaperLoading = isWallpaperLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.35f)
                        .padding(bottom = 48.dp)
                        .hazeSource(hazeState),
                    contentScale = ContentScale.Crop,
                )

                // 用户卡片
                UserCard(
                    name = username,
                    onAvatarClick = {},
                    onNameCardClick = {},
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 8.dp)
                        .height(96.dp)
                        .clip(CircleShape)
                        .hazeEffect(
                            hazeState,
                            HazeMaterials.ultraThin(
                                MaterialTheme.colorScheme.surfaceContainerLow,
                            ),
                        ),
                )
            }

            // 条状标签设置
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val itemModifier = Modifier.padding(horizontal = 8.dp)

                // 校区设置
                CampusSetting(
                    campus = campus,
                    onCampusChange = viewModel::changeCampus,
                    modifier = itemModifier,
                )

                // 性别设置
                GenderSetting(
                    gender = gender,
                    onGenderChange = {
                        MeRoute.changeGender(it)
                        viewModel.changeGender(it)
                    },
                    modifier = itemModifier,
                )

                // 主色调设置
                PrimaryColorSetting(
                    primaryColor = primaryColor,
                    onPrimaryColorChange = viewModel::changePrimaryColor,
                    modifier = itemModifier,
                )

                // 周次设置
                WeekSetting(
                    week = week,
                    onWeekChange = viewModel::changeWeek,
                    modifier = itemModifier,
                )

                // 更多设置
                MoreSettings(
                    onClick = {
                        backStack.add(SettingsRoute)
                    },
                    modifier = itemModifier,
                )
            }
        }
    }
}

/**
 * 壁纸。
 *
 * @param wallpaper 壁纸
 * @param isWallpaperLoading 是否正在加载壁纸
 */
@Composable
private fun Wallpaper(
    wallpaper: Wallpaper?,
    isWallpaperLoading: Boolean,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
) {
    Surface(
        modifier = modifier,
        shadowElevation = 2.dp,
    ) {
        if (isWallpaperLoading || wallpaper != null) {
            AsyncImage(
                wallpaper?.url,
                contentDescription = stringResource(Res.string.wallpaper),
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
            )
        } else {
            Image(
                painterResource(Res.drawable.punica),
                contentDescription = stringResource(Res.string.logo),
                modifier = Modifier.fillMaxSize().blur(4.dp),
                contentScale = contentScale,
            )
        }
    }
}

/**
 * 用户卡片。
 *
 * @param name 名字
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun UserCard(
    name: String,
    onAvatarClick: () -> Unit,
    onNameCardClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isWidthAtLeastMediumLowerBound = adaptiveInfo.windowSizeClass
        .isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND,
        )

    // 头像和名字卡片水平间距
    val horizontalSpace = if (isWidthAtLeastMediumLowerBound) 8.dp else 4.dp
    // 名字卡片形状
    val nameCardShape = if (isWidthAtLeastMediumLowerBound) {
        MaterialTheme.shapes.large.run {
            CircleShape.copy(
                topEnd = topEnd,
                bottomStart = bottomStart,
            )
        }
    } else {
        MaterialShapes.Slanted.toShape()
    }
    // 名字卡片边距
    val nameCardPadding = PaddingValues(
        vertical = if (isWidthAtLeastMediumLowerBound) 16.dp else 8.dp,
    )
    // 名字卡片内边距
    val nameCardInnerPadding = PaddingValues(
        horizontal = if (isWidthAtLeastMediumLowerBound) 16.dp else 24.dp,
        vertical = 8.dp,
    )

    // 名字颜色
    val nameColors = listOf(
        MaterialTheme.colorScheme.primaryFixedDim,
        MaterialTheme.colorScheme.inversePrimary,
    )

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpace),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                onClick = onAvatarClick,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 16.dp)
                    .aspectRatio(1f),
                contentColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape,
                shadowElevation = 2.dp,
            ) {
                Image(
                    dotLottiePainterResource(
                        "files/animation/multiple_circles.lottie",
                    ),
                    contentDescription = stringResource(Res.string.avatar),
                    modifier = Modifier.scale(2f),
                )

                Image(
                    painterResource(Res.drawable.punica),
                    contentDescription = stringResource(Res.string.logo),
                    modifier = Modifier.scale(0.8f),
                )
            }

            Card(
                onClick = onNameCardClick,
                modifier = Modifier
                    .weight(1f)
                    .padding(nameCardPadding),
                shape = nameCardShape,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(nameCardInnerPadding),
                    verticalArrangement = Arrangement.Center,
                ) {
                    // 名字
                    Text(
                        name,
                        fontWeight = FontWeight.Black,
                        style = LocalTextStyle.current.copy(
                            brush = Brush.linearGradient(
                                colors = nameColors,
                            )
                        ),
                    )
                    Spacer(Modifier.height(4.dp))
                    // 摘抄
                    Text(
                        stringResource(Res.string.extract),
                        modifier = Modifier.basicMarquee(Int.MAX_VALUE),
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

/**
 * 校区设置。
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun CampusSetting(
    campus: Campus,
    onCampusChange: (Campus) -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenuChipSetting(
        headline = {
            Text(stringResource(campus.strRes))
        },
        modifier = modifier,
        leadingIcon = {
            Icon(
                Icons.Outlined.School,
                contentDescription = stringResource(Res.string.campus),
            )
        },
        dropdownMenuItems = Campus.entries.map { stringResource(it.strRes) },
        onItemClick = { onCampusChange(Campus.entries[it]) }
    )
}

/**
 * 性别设置。
 */
@Composable
private fun GenderSetting(
    gender: Gender,
    onGenderChange: (Gender) -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenuChipSetting(
        headline = { Text(stringResource(gender.strRes)) },
        leadingIcon = {
            Icon(
                gender.avatar,
                contentDescription = stringResource(Res.string.campus),
            )
        },
        dropdownMenuItems = Gender.entries.map { stringResource(it.strRes) },
        onItemClick = { onGenderChange(Gender.entries[it]) },
        modifier = modifier,
    )
}

/**
 * 主色调设置。
 */
@Composable
private fun PrimaryColorSetting(
    primaryColor: Color,
    onPrimaryColorChange: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    val controller = rememberColorPickerController()
    var visible by remember { mutableStateOf(false) }

    ChipSetting(
        headline = {
            Text(stringResource(Res.string.primary_color))
        },
        onClick = {
            visible = true
        },
        modifier = modifier,
        leadingIcon = {
            // 颜色预览
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(primaryColor),
            )
        },
    ) {
        if (visible) {
            // 颜色选择对话框
            AlertDialog(
                onDismissRequest = { visible = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onPrimaryColorChange(controller.selectedColor.value)
                            visible = false
                        },
                    ) {
                        Text(stringResource(Res.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { visible = false }) {
                        Text(stringResource(Res.string.dismiss))
                    }
                },
                title = { Text(stringResource(Res.string.primary_color)) },
                text = {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // 取色盘
                        HsvColorPicker(
                            modifier = Modifier
                                .size(240.dp)
                                .padding(10.dp),
                            controller = controller,
                            initialColor = primaryColor,
                        )
                        // alpha 滑块
                        AlphaSlider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp),
                            controller = controller,
                            borderRadius = 8.dp,
                            borderSize = 4.dp,
                            wheelRadius = 12.dp,
                            tileSize = 8.dp,
                            initialColor = primaryColor,
                        )
                    }
                },
            )
        }
    }
}

/**
 * 周次设置。
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun WeekSetting(
    week: Int,
    onWeekChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    DropdownMenuChipSetting(
        headline = { Text(stringResource(Res.string.week_of, week)) },
        leadingIcon = {
            Icon(
                Icons.Outlined.EditCalendar,
                contentDescription = stringResource(Res.string.campus),
            )
        },
        dropdownMenuItems = (0..20).map { stringResource(Res.string.week_of, it) },
        onItemClick = { onWeekChange(it) },
        modifier = modifier,
    )
}

/**
 * 下拉菜单条状标签设置。
 *
 * @param headline 标题
 * @param leadingIcon 图标
 * @param dropdownMenuItems 下拉菜单项
 */
@Composable
private fun DropdownMenuChipSetting(
    headline: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit),
    dropdownMenuItems: List<String>,
    onItemClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    ChipSetting(
        headline = headline,
        onClick = { expanded = true },
        modifier = modifier,
        leadingIcon = leadingIcon,
        popup = {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.heightIn(max = 280.dp),
            ) {
                dropdownMenuItems.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            onItemClick(index)
                            expanded = false
                        },
                    )
                }
            }
        },
    )
}

/**
 * 条状标签设置。
 *
 * @param headline 名称
 * @param leadingIcon 图标
 * @param popup 弹窗
 */
@Composable
private fun ChipSetting(
    headline: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    popup: @Composable (() -> Unit)? = null,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        shadowElevation = 2.dp,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp,
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 图标
            leadingIcon?.invoke()
            // 名称
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Black,
                ),
                content = headline,
            )
        }
        popup?.invoke()
    }
}

/**
 * 更多设置。
 */
@Composable
private fun MoreSettings(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = CircleShape,
        shadowElevation = 2.dp,
    ) {
        IconButton(onClick = onClick) {
            Icon(
                Icons.Outlined.Settings,
                contentDescription = stringResource(Res.string.settings),
            )
        }
    }
}