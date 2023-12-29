package network

data class SyncResponse(val isSuccessful: Boolean, val uploads: List<String>)