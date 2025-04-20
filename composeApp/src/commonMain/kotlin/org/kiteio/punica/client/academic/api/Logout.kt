package org.kiteio.punica.client.academic.api

import io.ktor.client.request.parameter
import kotlinx.datetime.Clock
import org.kiteio.punica.client.academic.AcademicSystem

/**
 * 退出。
 */
suspend fun AcademicSystem.logout() {
    get("/jsxsd/xk/LoginToXk") {
        parameter("method", "exit")
        parameter("tktime", Clock.System.now().toEpochMilliseconds())
    }
}