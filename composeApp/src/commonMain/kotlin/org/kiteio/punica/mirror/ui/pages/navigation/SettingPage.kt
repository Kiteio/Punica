package org.kiteio.punica.mirror.ui.pages.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.window.core.layout.WindowSizeClass
import coil3.compose.AsyncImage
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Kotlin
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.kiteio.punica.Build
import org.kiteio.punica.mirror.ui.AppUiState
import org.kiteio.punica.mirror.ui.AppViewModel
import org.kiteio.punica.mirror.ui.PunicaExpressiveTheme
import org.kiteio.punica.mirror.ui.component.animation.DotLottieAnimation
import org.kiteio.punica.mirror.ui.pages.LoginRoute
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.app_name
import punica.composeapp.generated.resources.avatar
import punica.composeapp.generated.resources.extract
import punica.composeapp.generated.resources.jetpackcompose
import punica.composeapp.generated.resources.not_logged_in
import punica.composeapp.generated.resources.punica
import punica.composeapp.generated.resources.wallpaper
import kotlin.random.Random

/**
 * 设置页面路由。
 */
@Serializable
data object SettingRoute

/**
 * 设置页面目的地。
 */
fun NavGraphBuilder.settingDestination() {
    composable<SettingRoute> {
        val appViewModel = koinInject<AppViewModel>()
        val appUiState by appViewModel.uiState.collectAsStateWithLifecycle()
        val settingViewModel = koinViewModel<SettingViewModel>()
        val navController = koinInject<NavHostController>()
        val uiState by settingViewModel.uiState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            settingViewModel.dispatch(SettingIntent.LoadWallpaper)
        }

        SettingPage(
            uiState = uiState,
            userId = (appUiState as? AppUiState.LoggedIn)?.user?.id,
            isLoggedIn = (appUiState as? AppUiState.LoggedIn)?.user != null,
            navController = navController,
        )
    }
}

@Preview
@Composable
private fun SettingPagePreview() {
    PunicaExpressiveTheme {
        SettingPage(
            uiState = SettingUiState.Error(Exception()),
            userId = stringResource(Res.string.app_name),
            isLoggedIn = false,
            navController = rememberNavController(),
        )
    }
}

/**
 * 设置页面。
 *
 * @param uiState UI 状态
 * @param userId 学号
 * @param isLoggedIn 是否已经登录
 */
@Composable
private fun SettingPage(
    uiState: SettingUiState,
    userId: String?,
    isLoggedIn: Boolean,
    navController: NavHostController,
) {
    val adaptiveInfo = currentWindowAdaptiveInfo()
    val isWindowWidthSizeAtLeastDpMedium = adaptiveInfo
        .windowSizeClass.isWidthAtLeastBreakpoint(
            WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND
        )

    Scaffold { innerPadding ->
        Row(modifier = Modifier.padding(innerPadding)) {
            // 主窗格
            MainPane(
                userId = userId,
                isLoggedIn = isLoggedIn,
                onUserCardClick = {
                    navController.navigate(LoginRoute)
                },
                wallpaperUrl = if (uiState is SettingUiState.Success)
                    uiState.wallpaperUrl else null,
                wallpaperLoadError = uiState is SettingUiState.Error,
                modifier = Modifier.weight(1f),
            )
            // 辅助窗格
            if (isWindowWidthSizeAtLeastDpMedium) {
                Surface(
                    modifier = Modifier.width(240.dp),
                    shadowElevation = 2.dp,
                ) {
                    SupportingPane()
                }
            }
        }
    }
}

/**
 * 主窗格。
 *
 * @param userId 学号
 * @param isLoggedIn 是否已经登录
 * @param wallpaperUrl 壁纸
 * @param wallpaperLoadError 壁纸是否加载错误
 */
@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
private fun MainPane(
    userId: String?,
    isLoggedIn: Boolean,
    onUserCardClick: () -> Unit,
    wallpaperUrl: Any?,
    wallpaperLoadError: Boolean,
    modifier: Modifier = Modifier,
) {
    val hazeState = rememberHazeState()

    Column(modifier = modifier) {
        Box(modifier = Modifier.fillMaxHeight(.35f)) {
            // 背景图
            AsyncImage(
                wallpaperUrl,
                contentDescription = stringResource(Res.string.wallpaper),
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 36.dp)
                    .hazeSource(hazeState),
                error = if (wallpaperLoadError)
                    painterResource(Res.drawable.punica) else null,
                contentScale = ContentScale.Crop,
            )

            Column(
                modifier = Modifier.matchParentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // 空白区域
                Spacer(Modifier.weight(1f))
                // 用户卡片
                UserCard(
                    userId = userId,
                    isLoggedIn = isLoggedIn,
                    onClick = onUserCardClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                        .widthIn(max = 560.dp)
                        .clip(CircleShape)
                        .hazeEffect(hazeState, HazeMaterials.ultraThin())
                )
            }
        }

        // 设置
        LazyVerticalGrid(
            columns = GridCells.Adaptive(144.dp),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Setting(
                    name = stringResource(Res.string.app_name),
                    value = { Text(stringResource(Res.string.app_name)) },
                    onClick = {},
                    leadingIcon = Icons.Outlined.DateRange,
                )
            }
            item {
                Setting(
                    name = stringResource(Res.string.app_name),
                    value = { Text(stringResource(Res.string.app_name)) },
                    onClick = {},
                    leadingIcon = Icons.Outlined.DateRange,
                )
            }
        }
    }
}

/**
 * 用户卡片。
 *
 * @param userId 学号
 * @param isLoggedIn 是否已经登录
 */
@Composable
private fun UserCard(
    userId: String?,
    isLoggedIn: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer.copy(.1f),
        contentColor = MaterialTheme.colorScheme.primaryContainer,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surfaceVariant.copy(.5f),
        ),
    ) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurface,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 头像
                DotLottieAnimation(
                    "files/multiple_circles.lottie",
                    contentDescription = stringResource(Res.string.avatar),
                    modifier = Modifier.size(64.dp).scale(1.5f),
                )
                Spacer(Modifier.width(8.dp))

                Column {
                    // 学号
                    Text(
                        userId ?: stringResource(Res.string.not_logged_in),
                        color = MaterialTheme.colorScheme.run {
                            if (isLoggedIn) primary else onSurface
                        },
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    // 摘抄
                    Text(
                        stringResource(Res.string.extract),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }
}

/**
 * 设置。
 *
 * @param name 设置名称
 * @param value 设置值
 * @param onClick 点击事件
 * @param leadingIcon 图标
 */
@Composable
private fun Setting(
    name: String,
    value: @Composable () -> Unit,
    onClick: () -> Unit,
    leadingIcon: ImageVector,
) {
    var clicked by remember { mutableStateOf(false) }

    // 点击时变色
    val tint by animateColorAsState(
        MaterialTheme.colorScheme.run {
            if (clicked) primary else onSurface
        }
    )
    // 点击时旋转
    val degrees by animateFloatAsState(
        if (clicked) Random.nextFloat() * 720f - 360f else 0f,
    )
    // 点击时缩放
    val size by animateDpAsState(
        if (clicked) 28.dp else 24.dp,
    )

    LaunchedEffect(clicked) {
        // 重置 clicked
        if (clicked) {
            delay(400)
            clicked = false
        }
    }

    Surface(
        onClick = {
            clicked = true
            onClick()
        },
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.surfaceVariant,
        ),
        contentColor = MaterialTheme.colorScheme.primary,
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        ListItem(
            headlineContent = { Text(name) },
            supportingContent = value,
            leadingContent = {
                Box(
                    modifier = Modifier.size(28.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        leadingIcon,
                        contentDescription = name,
                        modifier = Modifier.size(size).rotate(degrees),
                        tint = tint,
                    )
                }
            },
        )
    }
}

/**
 * 辅助窗格。
 */
@Composable
private fun SupportingPane(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Logo
        Image(
            painterResource(Res.drawable.punica),
            contentDescription = stringResource(Res.string.app_name),
            modifier = Modifier.size(96.dp),
        )
        // App 名称
        Text(
            stringResource(Res.string.app_name),
            style = MaterialTheme.typography.titleMedium,
        )

        CompositionLocalProvider(
            LocalTextStyle provides MaterialTheme.typography.bodySmall
        ) {
            // App 版本
            Text("v${Build.versionName}")
            Spacer(Modifier.height(8.dp))

            // KMP
            Text("Power by Kotlin Multiplatform")
            Spacer(Modifier.fillMaxHeight(.1f))

            // Kotlin 版本
            LeadingIconSpaceBetweenText(
                leadingIcon = {
                    Icon(
                        SimpleIcons.Kotlin,
                        contentDescription = SimpleIcons.Kotlin.name,
                        tint = Color(0xFF7F52FF),
                        modifier = Modifier
                            .padding(horizontal = 2.dp)
                            .size(16.dp),
                    )
                },
                key = "Kotlin",
                value = "v${KotlinVersion.CURRENT}",
                modifier = Modifier.fillMaxWidth(.8f),
            )
            Spacer(Modifier.height(8.dp))

            // Compose 版本
            LeadingIconSpaceBetweenText(
                leadingIcon = {
                    Icon(
                        painterResource(Res.drawable.jetpackcompose),
                        contentDescription = "Jetpack Compose",
                        tint = Color(0xFF4285F4),
                        modifier = Modifier.size(20.dp),
                    )
                },
                key = "Compose",
                value = "v${Build.composeVersion}",
                modifier = Modifier.fillMaxWidth(.8f),
            )
            Spacer(Modifier.height(48.dp))
        }
    }
}

/**
 * 带图标的两边布局文本。
 */
@Composable
private fun LeadingIconSpaceBetweenText(
    leadingIcon: @Composable () -> Unit,
    key: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // 图标 + 文字
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            leadingIcon()
            Spacer(Modifier.width(4.dp))
            Text(key)
        }
        // 文字
        Text(value)
    }
}