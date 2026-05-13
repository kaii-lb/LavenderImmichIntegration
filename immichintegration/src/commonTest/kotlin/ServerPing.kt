import io.github.kaii_lb.lavender.immichintegration.Auth
import io.github.kaii_lb.lavender.immichintegration.clients.ApiClient
import io.github.kaii_lb.lavender.immichintegration.clients.ServerClient
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class ServerPing {
    @Test
    fun ping() {
        val client = ServerClient(
            endpoint = TestConfig.SERVER_URL,
            auth = Auth.None,
            client = ApiClient(debugMode = true)
        )

        runBlocking {
            assertEquals(client.ping(), true)
        }
    }
}