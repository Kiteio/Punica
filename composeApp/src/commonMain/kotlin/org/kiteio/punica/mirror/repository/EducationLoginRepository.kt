package org.kiteio.punica.mirror.repository

import io.ktor.client.statement.*
import io.ktor.http.*
import jakarta.inject.Singleton
import org.kiteio.punica.mirror.modal.User
import org.kiteio.punica.mirror.platform.readCaptcha
import org.kiteio.punica.mirror.service.EducationService
import org.kiteio.punica.mirror.storage.AppDatabase
import org.kiteio.punica.mirror.storage.Preferences

/**
 * 教务系统登录存储库。
 *
 * @param service 教务系统服务
 * @param database 数据库
 */
@Singleton
fun getEducationLoginRepository(
    service: EducationService,
    database: AppDatabase,
): EducationLoginRepository {
    return EducationLoginRepositoryImpl(service, database)
}

/**
 * 教务系统登录存储库。
 */
interface EducationLoginRepository {
    /**
     * 自动登录教务系统，如果登录失败，会抛出异常。
     */
    suspend fun autoLogin(userId: String)

    /**
     * 登录教务系统，如果登录失败，会抛出异常。
     */
    suspend fun login(
        userId: String,
        password: String,
        secondClassPwd: String,
    )
}

// --------------- 实现 ---------------

private class EducationLoginRepositoryImpl(
    private val service: EducationService,
    database: AppDatabase,
) : EducationLoginRepository {
    private val userDao = database.userDao()

    override suspend fun autoLogin(userId: String) {
        val user = userDao.getById(userId)
        check(user != null)
        loginAndSave(user)
    }

    override suspend fun login(
        userId: String,
        password: String,
        secondClassPwd: String,
    ) {
        val oldUser = userDao.getById(userId)
        val newUser = User(
            userId,
            password,
            secondClassPwd,
            oldUser?.cookies ?: emptyList(),
        )
        loginAndSave(newUser)
    }

    /**
     * 登录教务系统。
     *
     * @param user 用户
     */
    private suspend fun loginAndSave(user: User) {
        try {
            // 保存用户
            Preferences.changeUserId(user.id)
            userDao.insert(user)

            // 请求验证码
            val response = service.getCaptcha(user.cookies)
            // 读取验证码
            val captcha = response.readRawBytes().readCaptcha()
            require(captcha.length == 4) { "验证码错误!!" }

            // 登录
            service.login(user.id, user.password, captcha)

            // 保存 Cookie
            val cookies = response.setCookie()
            if (cookies.isNotEmpty()) {
                val names = cookies.map { it.name }
                val newCookies = mutableListOf<Cookie>().apply {
                    addAll(cookies)
                    addAll(cookies.filter { it.name !in names })
                }
                userDao.insert(user.copy(cookies = newCookies))
            }
        } catch (e: Exception) {
            if (e.message == "验证码错误!!") {
                // 验证码错误
                loginAndSave(user)
            } else {
                throw e
            }
        }
    }
}