package network

import database.FileRecord
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object ClientApi {

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
        }
    }
    val serverPort = 8080
    var scope: CoroutineScope? = null

    private val _serverStatus = MutableStateFlow(ServerStatus.UNKNOWN)
    val serverStatus = _serverStatus.asStateFlow()

    fun setServerStatus(status: ServerStatus) {
        _serverStatus.value = status
    }

    fun isServerAlive() {
        scope?.launch(Dispatchers.IO) {
            try {
                val response = client.request("/alive") {
                    method = HttpMethod.Get
                    port = serverPort
                    headers {
                        append(HttpHeaders.Accept, "application/json")
                    }
                }
                _serverStatus.value = when {
                    response.isOk() && response.body<GeneralResponse>().isSuccessful -> ServerStatus.ONLINE
                    !response.isOk() -> ServerStatus.ERROR_CODE.apply { code = response.status.value.toString() }
                    else -> ServerStatus.UNKNOWN
                }
            } catch (e: Exception) {
                _serverStatus.value = ServerStatus.OFFLINE
            }
        }
    }

    suspend fun syncFiles(records: List<FileRecord>): List<Int>? {
        try {
            val response = client.request("/sync"){
                contentType(ContentType.Application.Json)
                method = HttpMethod.Post
                port = serverPort
                setBody(records)
                headers {
                    append(HttpHeaders.Accept, "application/json")
                }
            }
            if (response.isOk()){
                return response.body()
            }
        } catch (e: Exception){
            println("error: $e")
        }
        return listOf()
    }
}


private fun HttpResponse.isOk(): Boolean {
    return this.status.value in 200..299
}

enum class ApiResponse(var message: String) {
    OK("success"),
    ERROR_CODE(""),
    SERVER_OFFLINE("server offline"),
    UNKNOWN_ERROR("unknown error"),
    UNKNOWN_STATE("server status is unknown")
}

enum class ServerStatus(var code: String) {
    UNKNOWN(""), OFFLINE(""), ONLINE(""), ERROR_CODE("")
}

fun ServerStatus.isError(): Boolean{
    return this == ServerStatus.OFFLINE || this == ServerStatus.ERROR_CODE
}