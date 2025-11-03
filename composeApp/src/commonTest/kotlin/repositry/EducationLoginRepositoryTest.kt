package repositry

import kotlinx.coroutines.runBlocking
import org.kiteio.punica.mirror.repository.getEducationLoginRepository
import org.kiteio.punica.mirror.service.getEducationService
import org.kiteio.punica.mirror.storage.getAppDatabase
import util.readUser
import kotlin.test.Test

class EducationLoginRepositoryTest {
    private val service = getEducationService()
    private var database = getAppDatabase()
    private val repository = getEducationLoginRepository(service, database)
    private val user = readUser()

    @Test
    fun shouldAutoLogin() = runBlocking {
        println(repository.login(user.id, user.password, user.secondClassPwd))
    }
}