package org.kiteio.punica.client.academic.api

import io.ktor.client.request.parameter
import org.kiteio.punica.client.academic.AcademicSystem
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 退出。
 */
@OptIn(ExperimentalTime::class)
suspend fun AcademicSystem.logout() {
    get("/jsxsd/xk/LoginToXk") {
        parameter("method", "exit")
        parameter("tktime", Clock.System.now().toEpochMilliseconds())
    }
}