import io.github.kaii_lb.lavender.immichintegration.Auth
import io.github.kaii_lb.lavender.immichintegration.clients.ApiClient
import io.github.kaii_lb.lavender.immichintegration.clients.LoginClient
import kotlinx.coroutines.runBlocking
import kotlin.experimental.ExperimentalNativeApi
import kotlin.test.Test

@OptIn(ExperimentalNativeApi::class)
class Auth {
    private val apiClient = ApiClient(debugMode = true)
    private val loginClient = LoginClient(
        client = apiClient,
        endpoint = TestConfig.SERVER_URL,
        auth = Auth.ApiKey(apiKey = TestConfig.API_KEY)
    )

    @Test
    fun testValidateToken() = runBlocking {
        assert(loginClient.validate())
    }
}