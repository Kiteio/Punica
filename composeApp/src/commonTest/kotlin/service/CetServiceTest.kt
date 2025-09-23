package service

import kotlinx.coroutines.runBlocking
import org.kiteio.punica.mirror.service.CetService
import kotlin.test.Test

class CetServiceTest {
    private val service = CetService()

    @Test
    fun shouldGetExam() = runBlocking {
        println(service.getExam())
    }
}