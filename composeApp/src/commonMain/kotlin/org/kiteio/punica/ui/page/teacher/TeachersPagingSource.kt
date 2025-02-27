package org.kiteio.punica.ui.page.teacher

import org.kiteio.punica.AppVM
import org.kiteio.punica.client.academic.api.Teacher
import org.kiteio.punica.client.academic.api.getTeachers
import org.kiteio.punica.wrapper.IntPagingSource

class TeachersPagingSource(private val name: String) : IntPagingSource<Teacher>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Teacher> {
        return try {
            val index = params.key ?: 1
            val teachers = AppVM.academicSystem?.getTeachers(name)

            requireNotNull(teachers)

            LoadResult.Page(
                data = teachers.teachers,
                prevKey = params.key?.let { it - 1 },
                nextKey = if (index == teachers.pageCount) null else index + 1,
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}