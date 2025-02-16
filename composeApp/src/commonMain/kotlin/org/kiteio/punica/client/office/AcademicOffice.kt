package org.kiteio.punica.client.office

import org.kiteio.punica.http.HttpClient
import org.kiteio.punica.http.HttpClientWrapper

/**
 * 教务处。
 */
interface AcademicOffice: HttpClientWrapper


/**
 * 返回教务处客户端。
 */
fun AcademicOffice() = object: AcademicOffice {
    override val httpClient = HttpClient("https://jwc.gdufe.edu.cn")
}