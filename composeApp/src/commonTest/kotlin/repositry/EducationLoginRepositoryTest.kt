package repositry

import kotlinx.coroutines.runBlocking
import org.kiteio.punica.mirror.repository.EducationLoginRepository
import org.kiteio.punica.mirror.service.EducationService
import util.readUser
import kotlin.test.Test

class EducationLoginRepositoryTest {
    private val service = EducationService()
    private val repository = EducationLoginRepository(service)
    private val user = readUser()

    @Test
    fun shouldLogin() = runBlocking {
        println(repository.login(user.id, user.password, user.secondClassPwd))
    }
}