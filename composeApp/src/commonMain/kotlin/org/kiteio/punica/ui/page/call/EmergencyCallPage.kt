package org.kiteio.punica.ui.page.call

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.foundation.Campus
import org.kiteio.punica.ui.component.CardListItem
import org.kiteio.punica.ui.component.HorizontalTabPager
import org.kiteio.punica.ui.component.NavBackAppBar
import org.kiteio.punica.ui.component.showToast
import org.kiteio.punica.ui.page.modules.ModuleRoute
import org.kiteio.punica.ui.rememberRunBlocking
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.Res
import punica.composeapp.generated.resources.campus_alarm
import punica.composeapp.generated.resources.campus_police_office
import punica.composeapp.generated.resources.copy
import punica.composeapp.generated.resources.copy_successful
import punica.composeapp.generated.resources.emergency_call
import punica.composeapp.generated.resources.first_aid
import punica.composeapp.generated.resources.guanzhou_police_station
import punica.composeapp.generated.resources.hours_24

/**
 * 紧急电话页面路由。
 */
@Serializable
object EmergencyCallRoute : ModuleRoute {
    override val nameRes = Res.string.emergency_call
    override val icon = Icons.Outlined.Call
}


/**
 * 紧急电话页面。
 */
@Composable
fun EmergencyCallPage() = Content()


@Composable
private fun Content() {
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

    val state = rememberPagerState { Campus.entries.size }
    val campus = rememberRunBlocking { AppVM.campus.first() }

    Scaffold(
        topBar = {
            NavBackAppBar(
                title = { Text(stringResource(EmergencyCallRoute.nameRes)) },
                shadowElevation = 0.dp,
            )
        }
    ) { innerPadding ->
        HorizontalTabPager(
            state,
            tabContent = {
                val stringResource = when {
                    it == 0 -> campus
                    campus.ordinal == 0 -> Campus.FO_SHAN
                    else -> Campus.CANTON
                }.nameRes

                // 校区名称
                Text(stringResource(stringResource))
            },
            modifier = Modifier.padding(innerPadding)
        ) { page ->
            LazyVerticalGrid(
                columns = GridCells.Adaptive(232.dp),
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize(),
            ) {
                separateCampus(
                    page = page,
                    campus = campus,
                    onCopy = {
                        scope.launchCatching {
                            val text = buildAnnotatedString { append(it) }
                            clipboardManager.setText(text)
                            showToast(getString(Res.string.copy_successful))
                        }
                    },
                )
            }
        }
    }
}


/**
 * 区分校区。
 */
private fun LazyGridScope.separateCampus(page: Int, campus: Campus, onCopy: (String) -> Unit) {
    val mCampus = when {
        page == 0 -> campus
        campus.ordinal == 0 -> Campus.FO_SHAN
        else -> Campus.CANTON
    }
    when (mCampus) {
        Campus.CANTON -> cantonCall(onCopy)
        Campus.FO_SHAN -> foshanCall(onCopy)
    }
}


/**
 * 广州校区电话。
 */
private fun LazyGridScope.cantonCall(onCopy: (String) -> Unit) {
    item {
        Call(
            name = stringResource(Res.string.first_aid),
            phoneNumber = "13112234297",
            onCopy = onCopy,
        )
    }
    item {
        Call(
            name = stringResource(Res.string.campus_alarm),
            phoneNumber = "020-84096060",
            onCopy = onCopy,
        )
    }
    item {
        Call(
            name = stringResource(Res.string.campus_police_office),
            phoneNumber = "020-84096110",
            workingHours = "8:30 - 17:30",
            onCopy = onCopy,
        )
    }
    item {
        Call(
            name = stringResource(Res.string.guanzhou_police_station),
            phoneNumber = "020-84092782",
            onCopy = onCopy,
        )
    }
}


/**
 * 佛山校区电话。
 */
private fun LazyGridScope.foshanCall(onCopy: (String) -> Unit) {
    item {
        Call(
            name = stringResource(Res.string.first_aid),
            phoneNumber = "18566890063",
            onCopy = onCopy,
        )
    }
    item {
        Call(
            name = stringResource(Res.string.campus_alarm),
            phoneNumber = "0757-87828110",
            onCopy = onCopy,
        )
    }
}


/**
 * 电话。
 *
 * @param name 名称
 * @param phoneNumber 电话
 * @param workingHours 工作时间
 */
@Composable
private fun Call(
    name: String,
    phoneNumber: String,
    workingHours: String = stringResource(Res.string.hours_24),
    onCopy: (String) -> Unit,
) {
    CardListItem(
        // 名称
        headlineContent = {
            Text(name, color = MaterialTheme.colorScheme.primary)
        },
        onClick = {},
        modifier = Modifier.padding(8.dp),
        // 电话和工作时间
        supportingContent = {
            Column {
                Text(phoneNumber)
                Text(workingHours)
            }
        },
        // 复制
        trailingContent = {
            IconButton(onClick = { onCopy(phoneNumber) }) {
                Icon(
                    Icons.Outlined.ContentCopy,
                    contentDescription = stringResource(Res.string.copy),
                )
            }
        },
    )
}