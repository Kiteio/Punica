import kotlinx.coroutines.runBlocking
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import org.kiteio.punica.mirror.platform.readText
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * OCR 测试。
 */
class OCRTest {
    @Test
    fun shouldRecognizeText(): Unit = runBlocking {
        val path = Path("src/commonTest/resources/captcha.png")
        val byteArray = SystemFileSystem.source(path).buffered().readByteArray()

        val dataPath = "src/commonTest/resources/tessdata/"
        assertEquals(byteArray.readText(dataPath), "zvnm")
    }
}