import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.http.content.ByteArrayContent
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.json.Json

@Suppress("unused")
class ApiClient() {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 15000
            connectTimeoutMillis = 5000
            socketTimeoutMillis = 15000
        }
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
            filter { request ->
                request.url.toString().contains("/api/assets")
            }
        }



        expectSuccess = true
    }
    private val log = KtorSimpleLogger("ApiClient")

    suspend fun post(
        url: Url,
        headers: Map<String, Any>?,
        body: Any?
    ): HttpResponse? = try {
        client.post(
            url = url,
        ) {
            headers {
                headers?.forEach { (key, value) ->
                    header(key, value)
                }
            }

            setBody(body)
        }
    } catch (e: ClientRequestException) {
        log.error(e.response.bodyAsText())
        null
    }

    @OptIn(InternalAPI::class)
    suspend fun delete(
        url: Url,
        headers: Map<String, Any>?,
        body: Any?
    ): HttpResponse? = try {
        client.delete(url = url) {
            headers?.forEach { (key, value) ->
                header(key, value)
            }

            setBody(body)
        }
    } catch (e: ClientRequestException) {
        log.error(e.response.bodyAsText())
        null
    }

    suspend fun put(
        url: Url,
        headers: Map<String, Any>?,
        body: Any?
    ): HttpResponse? = try {
        client.put(
            url = url,
        ) {
            headers {
                headers?.forEach { (key, value) ->
                    header(key, value)
                }
            }

            setBody(body)
        }
    } catch (e: ClientRequestException) {
        log.error(e.response.bodyAsText())
        null
    }

    suspend fun get(
        url: Url,
        headers: Map<String, Any>?,
        body: Any?
    ): HttpResponse? = try {
        client.get(
            url = url
        ) {
            headers {
                headers?.forEach { (key, value) ->
                    header(key, value)
                }
            }

            setBody(body)
        }
    } catch (e: ClientRequestException) {
        log.error(e.response.bodyAsText())
        null
    }
}