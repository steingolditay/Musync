package models

data class FileRecord(var id: Int? = null, var name: String, var path: String, val hash: String, var sync: Boolean)
