package org.kiteio.punica.tool

import dev.turingcomplete.kotlinonetimepassword.HmacAlgorithm
import dev.turingcomplete.kotlinonetimepassword.TimeBasedOneTimePasswordConfig
import dev.turingcomplete.kotlinonetimepassword.TimeBasedOneTimePasswordGenerator
import java.util.concurrent.TimeUnit

actual fun TOTP(secret: String) = object : TOTP {
    private val config = TimeBasedOneTimePasswordConfig(
        codeDigits = 6,
        hmacAlgorithm = HmacAlgorithm.SHA1,
        timeStep = 30,
        timeStepUnit = TimeUnit.SECONDS
    )

    private val generator = TimeBasedOneTimePasswordGenerator(
        secret.decodeBase32Bytes(),
        config
    )

    override fun generate() = generator.generate()
}