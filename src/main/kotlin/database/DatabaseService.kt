package database

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import utils.FileUtils.getPathOnly
import java.io.File


object DatabaseService {

    private val database = Database.connect(
        url = "jdbc:h2:file:./build/db",
        driver = "org.h2.Driver"
    )
    object MusicFileDao: Table() {
        val id: Column<Int> = integer("id").autoIncrement()
        val hash: Column<String> = varchar("hash", 32)
        val name: Column<String> = varchar("name", 256)
        val path: Column<String> = varchar("path", 512)
        val sync: Column<Boolean> = bool("sync")

        override val primaryKey: PrimaryKey get() = PrimaryKey(id)
    }

    init {
        transaction(database){
            SchemaUtils.create(MusicFileDao)
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.Default) { block() }

    suspend fun insert(file: FileRecord) {
        dbQuery {
            MusicFileDao.insert {
                it[name] = file.name
                it[path] = file.path
                it[hash] = file.hash
                it[sync] = true
            }
        }

    }

    suspend fun update(record: FileRecord) {
        val recordId = record.id ?: return
        dbQuery {
            MusicFileDao.update({ MusicFileDao.id.eq(recordId)}) {
                it[name] = record.name
                it[path] = record.path
            }
        }

    }

    suspend fun updateSync(record: FileRecord){
        val recordId = record.id ?: return
        dbQuery {
            MusicFileDao.update( {MusicFileDao.id.eq(recordId)} ) {
                it[sync] = record.sync
            }
        }
    }

    suspend fun delete(recordId: Int?) {
        recordId ?: return
        dbQuery {
            MusicFileDao.deleteWhere { id.eq(recordId) }
        }
    }

    suspend fun deleteAll(){
        dbQuery {
            MusicFileDao.deleteAll()
        }
    }

    suspend fun drop(){
        dbQuery {
            SchemaUtils.drop(MusicFileDao)
            SchemaUtils.create(MusicFileDao)
        }
    }

    suspend fun get(file: File): FileRecord? {
        return dbQuery {
            MusicFileDao
                .select(MusicFileDao.path.eq(file.getPathOnly()).and(MusicFileDao.name.eq(file.name)))
                .map { FileRecord(it[MusicFileDao.id], it[MusicFileDao.name], it[MusicFileDao.path], it[MusicFileDao.hash], it[MusicFileDao.sync]) }
                .firstOrNull()
        }
    }

    suspend fun getAll(): List<FileRecord> {
        return dbQuery {
            MusicFileDao.selectAll()
                .map {
                    FileRecord(it[MusicFileDao.id], it[MusicFileDao.name], it[MusicFileDao.path], it[MusicFileDao.hash], it[MusicFileDao.sync])
                }
        }
    }
}

data class FileRecord(var id: Int? = null, var name: String, var path: String, val hash: String, var sync: Boolean)
fun FileRecord.dataEqualsTo(fileRecord: FileRecord): Boolean {
    return this.name == fileRecord.name && this.path == fileRecord.path && this.hash == fileRecord.hash
}
