import database.DatabaseRecord
import org.junit.Test

class DuplicatedFilesTest {

    @Test
    fun testDuplications(){
        val hashInstances = mutableListOf(
            DatabaseRecord(null, "File23", "origin/path", "hash", true),
            DatabaseRecord(null, "File24", "origin/path", "hash", true),
            DatabaseRecord(null, "File25", "origin/path", "hash", true),

            DatabaseRecord(null, "File20", "origin", "hash", true),
            DatabaseRecord(null, "File21", "origin", "hash", true),
            DatabaseRecord(null, "File22", "origin", "hash", true),

            DatabaseRecord(null, "File33", "origin/1", "hash", true)


        )

        val existingRecords = mutableListOf(
            DatabaseRecord(null, "File1", "origin", "hash", true),
            DatabaseRecord(null, "File20", "origin", "hash", true),
            DatabaseRecord(null, "File3", "origin", "hash", true),

            DatabaseRecord(null, "File34", "origin/2", "hash", true),


            DatabaseRecord(null, "File4", "origin/path", "hash", true),
            DatabaseRecord(null, "File5", "origin/path", "hash", true),
            DatabaseRecord(null, "File6", "origin/path", "hash", true)
        )


        val toRemove = mutableListOf<DatabaseRecord>()
        existingRecords.forEach {
            if (hashInstances.contains(it)){
                hashInstances.remove(it)
                toRemove.add(it) // do nothing
            }

            if (hashInstances.none { instance -> instance.path == it.path }){
                toRemove.add(it) // remove from db
            }
        }
        toRemove.forEach {
            existingRecords.remove(it)
        }


        existingRecords.forEach {
            val sameInstance = hashInstances.firstOrNull { instance -> instance.path == it.path && instance.name == it.name }
            if (sameInstance != null){
                return@forEach
            }

            val instance = hashInstances.firstOrNull { instance ->
                instance.path == it.path
            } ?: return@forEach
            it.name = instance.name // rename
            hashInstances.remove(instance)
        }

        existingRecords.addAll(hashInstances) // insert

        existingRecords.forEach {
            println(it)
        }
    }
}