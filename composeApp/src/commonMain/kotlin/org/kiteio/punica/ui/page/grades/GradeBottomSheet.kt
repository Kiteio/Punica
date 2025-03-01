package org.kiteio.punica.ui.page.grades

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import org.kiteio.punica.client.academic.api.CourseGrade
import org.kiteio.punica.ui.component.ModalBottomSheet
import org.kiteio.punica.ui.component.ProvideBodyMedium
import punica.composeapp.generated.resources.*

@Composable
fun GradeBottomSheet(visible: Boolean, onDismissRequest: () -> Unit, grade: CourseGrade?) {
    ModalBottomSheet(visible, onDismissRequest) {
        if (grade != null) {
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                item {
                    // 课程名称
                    Text(grade.name)
                    ProvideBodyMedium {
                        // 课程编号
                        Text(grade.courseId)
                        // 学期
                        Text("${grade.term}")
                        // 学分
                        RowItem(
                            key = stringResource(Res.string.credits),
                            value = "${grade.credits}",
                        )
                        // 总学时
                        RowItem(
                            key = stringResource(Res.string.class_hours),
                            value = grade.hours,
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                item {
                    ProvideBodyMedium {
                        // 成绩
                        RowItem(
                            key = stringResource(Res.string.grade),
                            value = grade.score,
                        )
                        // 平时成绩
                        grade.dailyScore?.let {
                            RowItem(
                                key = stringResource(Res.string.daily_score),
                                value = it,
                            )
                        }
                        // 实验成绩
                        grade.labScore?.let {
                            RowItem(
                                key = stringResource(Res.string.lab_score),
                                value = it,
                            )
                        }
                        // 期末成绩
                        grade.finalScore?.let {
                            RowItem(
                                key = stringResource(Res.string.final_score),
                                value = it,
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        // 成绩标识
                        grade.mark?.let {
                            RowItem(
                                key = stringResource(Res.string.mark),
                                value = it,
                            )
                        }
                        // 备注
                        grade.note?.let {
                            RowItem(
                                key = stringResource(Res.string.note),
                                value = it,
                            )
                        }
                        // 考核方式
                        RowItem(
                            key = stringResource(Res.string.assessment_method),
                            value = grade.assessmentMethod,
                        )
                        // 考试性质
                        RowItem(
                            key = stringResource(Res.string.exam_type),
                            value = grade.examType,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
                item {
                    ProvideBodyMedium {
                        // 通识课分类
                        grade.electiveCategory?.let {
                            RowItem(
                                key = stringResource(Res.string.elective_category),
                                value = it,
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun RowItem(key: String, value: String) {
    Row {
        Text(key, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.width(8.dp))
        Text(value)
    }
}