import androidx.compose.runtime.rememberCoroutineScope
import database.FileRecord
import database.DatabaseService
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.Test

class DuplicatedFilesTest {

    @Test
    fun testDuplications(){
        val database = DatabaseService.mockDatabase

        val scope = rememberCoroutineScope()

        transaction(database) {
            SchemaUtils.create(DatabaseService.MusicFileDao)
        }


        scope.launch {
            DatabaseService.dbQuery {
                DatabaseService.MusicFileDao.insert {
                    it[id] = 1
                    it[name] = "1"
                    it[path] = "path"
                    it[hash] = "hash1"
                    it[sync] = true
                }
                DatabaseService.MusicFileDao.insert {
                    it[id] = 2
                    it[name] = "2"
                    it[path] = "path"
                    it[hash] = "hash2"
                    it[sync] = true
                }
            }

            val hashInstances = mutableListOf(
                FileRecord(null, "1", "path", "hash1", true),
                FileRecord(null, "2", "path", "hash1", true)
            )

            val existingRecords = DatabaseService.dbQuery { DatabaseService.getAll() }


        }
    }
}