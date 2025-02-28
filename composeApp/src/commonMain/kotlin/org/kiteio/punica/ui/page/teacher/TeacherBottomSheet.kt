package org.kiteio.punica.ui.page.teacher

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.api.TCourse
import org.kiteio.punica.client.academic.api.Teacher
import org.kiteio.punica.client.academic.api.TeacherProfile
import org.kiteio.punica.client.academic.api.getTeacherProfile
import org.kiteio.punica.ui.widget.ModalBottomSheet
import org.kiteio.punica.ui.widget.ProvideBodyMedium
import org.kiteio.punica.ui.widget.SpaceBetween
import org.kiteio.punica.wrapper.launchCatching
import punica.composeapp.generated.resources.*

/**
 * 教师模态对话框。
 */
@Composable
fun TeacherBottomSheet(visible: Boolean, onDismissRequest: () -> Unit, teacher: Teacher?) {
    if (visible) {
        val teacherProfile by produceState<TeacherProfile?>(null, AppVM.academicSystem) {
            launchCatching {
                teacher?.run {
                    value = AppVM.academicSystem?.getTeacherProfile(id)
                }
            }
        }

        ModalBottomSheet(
            teacherProfile != null,
            onDismissRequest = onDismissRequest,
        ) {
            TeacherProfile(teacherProfile!!)
        }
    }
}


/**
 * 教师信息。
 */
@Composable
private fun TeacherProfile(teacherProfile: TeacherProfile) = with(teacherProfile) {
    LazyColumn(contentPadding = PaddingValues(16.dp)) {
        item {
            // 姓名
            Text(name)
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 联系方式
        item {
            ProvideBodyMedium {
                // 手机号
                phoneNumber?.let {
                    RowItem(
                        key = stringResource(Res.string.phone_number),
                        value = it,
                    )
                }
                // QQ
                qq?.let {
                    RowItem(
                        key = stringResource(Res.string.qq),
                        value = it,
                    )
                }
                // 微信
                weChat?.let {
                    RowItem(
                        key = stringResource(Res.string.wechat),
                        value = it,
                    )
                }
                // 邮箱
                email?.let {
                    RowItem(
                        key = stringResource(Res.string.email),
                        value = it,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            ProvideBodyMedium {
                // 性别
                gender?.let {
                    RowItem(
                        key = stringResource(Res.string.gender),
                        value = it,
                    )
                }
                // 政治面貌
                politics?.let {
                    RowItem(
                        key = stringResource(Res.string.politics),
                        value = it,
                    )
                }
                // 民族
                nation?.let {
                    RowItem(
                        key = stringResource(Res.string.nation),
                        value = it,
                    )
                }
                // 职务
                duty?.let {
                    RowItem(
                        key = stringResource(Res.string.duty),
                        value = it,
                    )
                }
                // 职称
                title?.let {
                    RowItem(
                        key = stringResource(Res.string.title),
                        value = it,
                    )
                }
                // 教职工类别
                category?.let {
                    RowItem(
                        key = stringResource(Res.string.staff_category),
                        value = it,
                    )
                }
                // 部门（院系）
                faculty?.let {
                    RowItem(
                        key = stringResource(Res.string.faculty),
                        value = it,
                    )
                }
                // 科室（系）
                office?.let {
                    RowItem(
                        key = stringResource(Res.string.office),
                        value = it,
                    )
                }
                // 最高学历
                qualification?.let {
                    RowItem(
                        key = stringResource(Res.string.qualification),
                        value = it,
                    )
                }
                // 学位
                degree?.let {
                    RowItem(
                        key = stringResource(Res.string.degree),
                        value = it,
                    )
                }
                // 研究方向
                field?.let {
                    RowItem(
                        key = stringResource(Res.string.field),
                        value = it,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            // 近四个学期主讲课程
            if (taught.isNotEmpty()) {
                TeachCourses(
                    key = stringResource(Res.string.taught_course),
                    tCourses = taught,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            // 下学期计划开设课程
            if (teaching.isNotEmpty()) {
                TeachCourses(
                    key = stringResource(Res.string.teaching_course),
                    tCourses = teaching,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        item {
            ProvideBodyMedium {
                // 个人简介
                biography?.let {
                    ColumnItem(
                        key = stringResource(Res.string.biography),
                        value = it
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // 教学理念
                philosophy?.let {
                    ColumnItem(
                        key = stringResource(Res.string.philosophy),
                        value = it
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                // 最想对学生说的话
                slogan?.let {
                    ColumnItem(
                        key = stringResource(Res.string.slogan),
                        value = it
                    )
                }
            }
        }
    }
}


/**
 * 横向信息。
 */
@Composable
private fun RowItem(key: String, value: String) {
    SpaceBetween {
        Text(key)
        Text(value)
    }
}


/**
 * 竖向信息
 */
@Composable
private fun ColumnItem(key: String, value: String) {
    Text(
        key,
        fontWeight = FontWeight.SemiBold,
    )
    Text(value)
}


/**
 * 教授课程。
 */
@Composable
private fun TeachCourses(key: String, tCourses: List<TCourse>) {
    ProvideBodyMedium {
        // 名称
        Text(key, fontWeight = FontWeight.SemiBold)
        HorizontalDivider(
            modifier = Modifier.padding(2.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
        )
        // 课程
        tCourses.forEach {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                // 课程名称
                Text(
                    it.name,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
                VerticalDivider(modifier = Modifier.padding(2.dp).height(12.dp))
                // 分类
                Text(
                    it.category,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
                VerticalDivider(modifier = Modifier.padding(2.dp).height(12.dp))
                // 学期
                Text(
                    "${it.term}",
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(2.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
            )
        }
    }
}