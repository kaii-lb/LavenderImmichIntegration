import com.kaii.lavender.immichintegration.clients.ApiClient
import com.kaii.lavender.immichintegration.clients.ServerClient
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ServerPing {
    @Test
    fun ping() {
        val client = ServerClient(
            baseUrl = "http://localhost:2283",
            client = ApiClient()
        )

        runBlocking {
            assertEquals(client.ping(), true)
        }
    }
}