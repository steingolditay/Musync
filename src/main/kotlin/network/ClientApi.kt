package network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*

import io.ktor.serialization.jackson.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import models.FileRecord
import utils.FileUtils.getContentType
import java.io.File

object ClientApi {

    const val PORT = 8080

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            jackson()
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    print("Ktor: $message\n")
                }

            }
            level = LogLevel.ALL
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

    suspend fun syncFiles(records: List<FileRecord>): List<String>? {
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
            return if (response.isOk()){
                response.body<SyncResponse>().uploads
            } else {
                null
            }
        } catch (e: Exception){
            return null
        }
    }

    suspend fun uploadFiles(files: List<FileRecord>,
                            onFileUploadProgress: (progress: Float) -> Unit,
                            onFileUploaded: (hash: String) -> Unit) {
        try {
            files.forEach { fileToUpload ->
                val file = File("${fileToUpload.path}\\${fileToUpload.name}")
                val fileBytes = file.readBytes()


                val response = client.post("http://localhost:$PORT/upload?hash=${fileToUpload.hash}"){
                    setBody(
                        MultiPartFormDataContent(
                            formData {
                                append("file", fileBytes, Headers.build {
                                    append(HttpHeaders.ContentType, file.getContentType())
                                    append(HttpHeaders.ContentDisposition, "filename=${file.name}")
                                })
                            }
                        )
                    )
                    onUpload { bytesSentTotal, contentLength ->
                        onFileUploadProgress(bytesSentTotal.toFloat() / contentLength.toFloat())
                    }
                }


                if (response.isOk()){
                    onFileUploaded(response.body<UploadResponse>().file)
                    response.body<UploadResponse>().file
                }
            }

        } catch (e: Exception){
            println(e)
        }
    }
}


fun HttpResponse.isOk(): Boolean {
    return this.status.value in 200..299
}
