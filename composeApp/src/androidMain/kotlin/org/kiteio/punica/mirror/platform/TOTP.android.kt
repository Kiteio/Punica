package org.kiteio.punica.mirror.platform

import dev.turingcomplete.kotlinonetimepassword.HmacAlgorithm
import dev.turingcomplete.kotlinonetimepassword.TimeBasedOneTimePasswordConfig
import dev.turingcomplete.kotlinonetimepassword.TimeBasedOneTimePasswordGenerator
import org.kiteio.punica.mirror.util.decodeBase32Bytes
import java.util.concurrent.TimeUnit

actual fun TOTP(secret: String): TOTP = TotpImpl(secret)

private class TotpImpl(secret: String) : TOTP {
    private val generator by lazy {
        val config = TimeBasedOneTimePasswordConfig(
            codeDigits = 6,
            hmacAlgorithm = HmacAlgorithm.SHA1,
            timeStep = 30,
            timeStepUnit = TimeUnit.SECONDS
        )

        TimeBasedOneTimePasswordGenerator(
            secret.decodeBase32Bytes(),
            config
        )
    }

    override fun generate() = generator.generate()
}