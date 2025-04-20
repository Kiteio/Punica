package org.kiteio.punica.client.office

import org.kiteio.punica.http.HttpClient
import org.kiteio.punica.http.HttpClientWrapper

/**
 * 教务处。
 */
interface AcademicOffice: HttpClientWrapper {
    val baseUrl: String
}


/**
 * 返回教务处客户端。
 */
fun AcademicOffice() = object: AcademicOffice {
    override val baseUrl = "https://jwc.gdufe.edu.cn"
    override val httpClient = HttpClient(baseUrl)
}