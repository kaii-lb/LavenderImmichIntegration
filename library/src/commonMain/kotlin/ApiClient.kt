import io.ktor.client.HttpClient
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.logging.KtorSimpleLogger
import kotlinx.serialization.json.Json

class ApiClient {
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

    suspend fun get(url: Url): HttpResponse =
        client.get(
            url = url
        )
}