package network

enum class ServerStatus(var code: String) {
    UNKNOWN(""), OFFLINE(""), ONLINE(""), ERROR_CODE("")
}

fun ServerStatus.isError(): Boolean{
    return this == ServerStatus.OFFLINE || this == ServerStatus.ERROR_CODE
}