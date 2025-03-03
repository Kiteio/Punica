package org.kiteio.punica.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.updateAll
import androidx.glance.layout.*
import androidx.glance.material3.ColorProviders
import androidx.glance.text.Text
import com.materialkolor.dynamicColorScheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate
import kotlinx.datetime.daysUntil
import org.kiteio.punica.R
import org.kiteio.punica.client.academic.api.Timetable
import org.kiteio.punica.client.academic.foundation.Campus
import org.kiteio.punica.client.academic.foundation.Term
import org.kiteio.punica.serialization.PrefsKeys
import org.kiteio.punica.serialization.Stores
import org.kiteio.punica.serialization.get
import org.kiteio.punica.wrapper.launchCatching
import org.kiteio.punica.wrapper.now

class TimetableWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = TimetableWidget()
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    var initialized = false

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        if (initialized) coroutineScope.launchCatching {
            glanceAppWidget.updateAll(context)
        }
        else initialized = true
    }
}


class TimetableWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val colors = ColorProviders(
            dynamicColorScheme(
                primary = Color(0xFF5783E0),
                isDark = false,
                isAmoled = true,
            )
        )
        val daysOfWeek = context.resources.getStringArray(R.array.days_of_week)
        // 当前日期
        val now = LocalDate.now()
        val dayOfWeekOrdinal = now.dayOfWeek.ordinal
        val index = dayOfWeekOrdinal * 6

        provideContent {
            // 学号
            val userId by Stores.prefs.data.map { it[PrefsKeys.ACADEMIC_USER_ID] }.collectAsState(null)
            // 课表
            val timetable by Stores.timetable.data
                .map { prefs -> userId?.let { prefs.get<Timetable>("$it${Term.current}") } }
                .collectAsState(null)

            val subTimetable = timetable?.cells?.subList(index, index + 6)

            // 周次
            val week by Stores.prefs.data.map { prefs ->
                val date = prefs[PrefsKeys.TERM_START_DATE]?.let {
                    LocalDate.parse(it)
                } ?: now
                date.run {
                    (daysUntil(now) + (dayOfWeek.ordinal - now.dayOfWeek.ordinal)).toInt() / 7
                }.coerceIn(0..20)
            }.collectAsState(0)
            // 校区
            val campus by Stores.prefs.data.map {
                Campus.entries[it[PrefsKeys.CAMPUS] ?: 0]
            }.collectAsState(Campus.CANTON)

            GlanceTheme(colors = colors) {
                Scaffold(
                    modifier = GlanceModifier.padding(horizontal = 4.dp, vertical = 8.dp),
                ) {
                    Column {
                        subTimetable?.let { list ->
                            Row(modifier = GlanceModifier.fillMaxWidth()) {
                                Row(modifier = GlanceModifier.defaultWeight()) {
                                    // 日期
                                    Text("${now.monthNumber}-${now.dayOfMonth}")
                                    Spacer(modifier = GlanceModifier.width(8.dp))
                                    // 星期
                                    Text(daysOfWeek[dayOfWeekOrdinal])
                                }
                                // 周次
                                Text(context.getString(R.string.week_of, week))
                            }
                            Spacer(modifier = GlanceModifier.height(8.dp))
                            list.forEachIndexed { index, cell ->
                                cell?.firstOrNull { it.weeks.contains(week) }?.let { course ->
                                    Row(modifier = GlanceModifier.fillMaxWidth()) {
                                        val modifier = GlanceModifier.defaultWeight()

                                        // 课程名称
                                        Text(course.name, modifier = modifier, maxLines = 1)
                                        Spacer(modifier = GlanceModifier.width(8.dp))
                                        // 时间
                                        Text(
                                            "${
                                                campus.schedule[course.sections.min() - 1].start
                                            }-${
                                                campus.schedule[course.sections.max() - 1].endInclusive
                                            }",
                                            modifier = modifier,
                                            maxLines = 1,
                                        )
                                        Spacer(modifier = GlanceModifier.width(8.dp))
                                        // 教室
                                        Text(course.classroom ?: "", modifier = modifier, maxLines = 1)
                                    }
                                    Spacer(modifier = GlanceModifier.height(4.dp))
                                }
                            }
                            if (list.all { courses -> courses?.all { it.weeks.contains(week) } != true } == true) {
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