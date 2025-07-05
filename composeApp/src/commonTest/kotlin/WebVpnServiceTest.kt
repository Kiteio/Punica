import kotlinx.coroutines.runBlocking
import org.kiteio.punica.mirror.service.WebVpnService
import util.readTotp
import util.readUser
import kotlin.test.Test

class WebVpnServiceTest {
    private val service = WebVpnService()
    private val user = readUser()
    private val totp = readTotp()

    /**
     * WebVpn 登录测试。
     */
    @Test
    fun shouldLogin() = runBlocking {
        service.login(user.id, user.password, totp)
    }
}