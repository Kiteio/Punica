package org.kiteio.punica.ui.widget

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.*
import androidx.glance.material3.ColorProviders
import androidx.glance.text.Text
import com.materialkolor.dynamicColorScheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import org.kiteio.punica.R
import org.kiteio.punica.client.academic.api.Timetable
import org.kiteio.punica.client.academic.foundation.Campus
import org.kiteio.punica.client.academic.foundation.Term
import org.kiteio.punica.serialization.PrefsKeys
import org.kiteio.punica.serialization.Stores
import org.kiteio.punica.serialization.get
import org.kiteio.punica.wrapper.now

class TimetableWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = TimetableWidget()
}


class TimetableWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val userId = Stores.prefs.data.map { it[PrefsKeys.ACADEMIC_USER_ID] }.first()
        val timetable = Stores.timetable.data
            .map { prefs -> userId?.let { prefs.get<Timetable>("$it${Term.current}") } }
            .first()
        val date = LocalDate.now()
        val dayOfWeekOrdinal = date.dayOfWeek.ordinal
        val index = dayOfWeekOrdinal * 6
        val subTimetable = timetable?.cells?.subList(index, index + 6)

        val week = Stores.prefs.data.map { it[PrefsKeys.WEEK] ?: 1 }.first()
        val campus = Stores.prefs.data.map { Campus.entries[it[PrefsKeys.CAMPUS] ?: 0] }.first()
        val colors = ColorProviders(
            dynamicColorScheme(
                primary = Color(0xFF5783E0),
                isDark = false,
                isAmoled = true,
            )
        )
        val daysOfWeek = context.resources.getStringArray(R.array.days_of_week)

        provideContent {
            GlanceTheme(colors = colors) {
                Scaffold(
                    modifier = GlanceModifier.padding(horizontal = 4.dp, vertical = 8.dp),
                ) {
                    Column {
                        subTimetable?.let { list ->
                            Row(modifier = GlanceModifier.fillMaxWidth()) {
                                Row(modifier = GlanceModifier.defaultWeight()) {
                                    // 日期
                                    Text("${date.monthNumber}-${date.dayOfMonth}")
                                    Spacer(modifier = GlanceModifier.width(8.dp))
                                    // 星期
                                    Text(daysOfWeek[dayOfWeekOrdinal])
                                }
                                // 周次
                                Text(context.getString(R.string.week_of, week))
                            }
                            Spacer(modifier = GlanceModifier.height(8.dp))
                            var flag = false
                            LazyColumn {
                                list.forEachIndexed { index, cell ->
                                    cell?.firstOrNull { it.weeks.contains(week) }?.let { course ->
                                        flag = true
                                        item {
                                            Row(modifier = GlanceModifier.fillMaxWidth()) {
                                                val modifier = GlanceModifier.defaultWeight()

                                                // 课程名称
                                                Text(course.name, modifier = modifier, maxLines = 1)
                                                Spacer(modifier = GlanceModifier.width(8.dp))
                                                // 时间
                                                Text(
                                                    campus.schedule[index].run { "$start ~ $endInclusive" },
                                                    modifier = modifier,
                                                    maxLines = 1,
                                                )
                                                Spacer(modifier = GlanceModifier.width(8.dp))
                                                // 教室
                                                Text(course.classroom ?: "", modifier = modifier, maxLines = 1)
                                            }
                                        }
                                    }
                                }
                            }
                            if (!flag) {
                                Box(
                                    modifier = GlanceModifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center,
                                ) { Text(context.getString(R.string.holiday)) }
                            }
                        } ?: Box(
                            modifier = GlanceModifier.fillMaxSize(),
                            contentAlignment = Alignment.Center,
                        ) { Text(context.getString(R.string.no_timetable)) }
                    }
                }
            }
        }
    }
}