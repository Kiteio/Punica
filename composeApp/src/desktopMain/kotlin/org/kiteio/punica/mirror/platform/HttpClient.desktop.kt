package org.kiteio.punica.mirror.platform

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

actual fun platformHttpClient(
    block: HttpClientConfig<*>.() -> Unit,
): HttpClient = HttpClient(OkHttp) {
    engine {
        config {
            sslSocketFactory(
                SpecificDomainTrustManager.sslSocketFactory,
                SpecificDomainTrustManager,
            )
            hostnameVerifier(SpecificHostnameVerifier)
        }
    }
    block(this)
}

@Suppress("CustomX509TrustManager")
object SpecificDomainTrustManager : X509TrustManager {
    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()

    override fun checkClientTrusted(
        chain: Array<X509Certificate>,
        authType: String,
    ) = Unit

    override fun checkServerTrusted(
        chain: Array<X509Certificate>,
        authType: String,
    ) {
        val certificate = chain.first()
        val subjectDN = certificate.subjectX500Principal.name
        if (!subjectDN.contains("*.neea.edu.cn")) {
            throw java.security.cert.CertificateException()
        }
    }

    val sslSocketFactory: javax.net.ssl.SSLSocketFactory by lazy {
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf<TrustManager>(this), null)
        sslContext.socketFactory
    }
}

object SpecificHostnameVerifier : HostnameVerifier {
    private val trustedHosts = listOf("resource.neea.edu.cn")

    override fun verify(hostname: String, session: javax.net.ssl.SSLSession): Boolean {
        return trustedHosts.any { hostname.endsWith(it) }
    }
}