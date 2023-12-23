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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

object ClientApi {

    const val PORT = 8080

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
        }
    }

    private val _serverStatus = MutableStateFlow(ServerStatus.UNKNOWN)
    val serverStatus = _serverStatus.asStateFlow()

    suspend fun isServerAlive() {
        try {
            val response = client.request("/alive") {
                method = HttpMethod.Get
                port = PORT
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

    suspend fun syncFiles(records: List<FileRecord>): List<Int>? {
        try {
            val response = client.request("/sync"){
                contentType(ContentType.Application.Json)
                method = HttpMethod.Post
                port = PORT
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


fun HttpResponse.isOk(): Boolean {
    return this.status.value in 200..299
}
