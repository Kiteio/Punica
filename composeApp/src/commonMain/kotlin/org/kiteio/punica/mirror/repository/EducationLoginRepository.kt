package org.kiteio.punica.mirror.repository

import org.kiteio.punica.mirror.modal.User
import org.kiteio.punica.mirror.platform.readCaptcha
import org.kiteio.punica.mirror.service.EducationService

/**
 * 教务系统登录存储库。
 *
 * @param service 教务系统服务
 */
fun EducationLoginRepository(
    service: EducationService,
): EducationLoginRepository {
    return EducationLoginRepositoryImpl(service)
}

/**
 * 教务系统登录存储库。
 */
interface EducationLoginRepository {
    /**
     * 尝试使用本地用户登录教务系统，如果登录失败，会抛出异常。
     */
    suspend fun login(): User

    /**
     * 登录教务系统，如果登录失败，会抛出异常。
     */
    suspend fun login(
        userId: String,
        password: String,
        secondClassPwd: String,
    ): User
}

// --------------- 实现 ---------------

private class EducationLoginRepositoryImpl(
    private val service: EducationService,
) : EducationLoginRepository {
    override suspend fun login(): User {
        TODO("在本地获取用户")
    }

    override suspend fun login(
        userId: String,
        password: String,
        secondClassPwd: String,
    ): User {
        // TODO: 本地获取 Cookie
        val captcha = service.getCaptcha(null).readCaptcha()
        service.login(userId, password, captcha)
        return User(userId, password, secondClassPwd)
    }
}