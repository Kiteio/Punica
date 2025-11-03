package service

import kotlinx.coroutines.runBlocking
import org.kiteio.punica.mirror.service.getCetService
import kotlin.test.Test

class CetServiceTest {
    private val service = getCetService()

    @Test
    fun shouldGetExam() = runBlocking {
        println(service.getExam())
    }
}