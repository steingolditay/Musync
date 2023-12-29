import database.FileRecord
import org.junit.Test

class Tests {


    @Test
    fun duplicationTest(){
        val oldFiles = listOf(
            FileRecord(null, "/folder1", "file1.txt", "hash1", true),
            FileRecord(null, "/folder1", "file2.txt", "hash1", true),
            FileRecord(null, "/folder1", "file3.txt", "hash3", true),
            FileRecord(null, "/folder1/subfolder", "file4.txt", "hash1", true),
            FileRecord(null, "/folder1/subfolder", "file5.txt", "hash1", true),
            FileRecord(null, "/folder1/subfolder", "file6.txt", "hash1", true),
            FileRecord(null, "/folder1/subfolder", "file7.txt", "hash7", true)
        )

//        val newFiles = listOf(
//            FileRecord(null, "/folder1", "file1.txt", "hash1", true), // file stayed the same
//            FileRecord(null, "/folder1", "file22.txt", "hash1", true), // file2 was renamed to this
//            FileRecord(null, "/folder1", "file3.txt", "hash2", true), // this is a new file since it has a new hash, but it was replacing an old file that doesn't exist anymore
//            FileRecord(null, "/folder1/subfolder2", "file4.txt", "hash1", true), // this file has moved
//            FileRecord(null, "/folder1", "file5.txt", "hash1", true), // this file has moved
//            FileRecord(null, "/folder1/subfolder", "file8.txt", "hash8", true),
//            FileRecord(null, "/folder1/subfolder", "file9.txt", "hash9", true),  // this is a new file
//        )

        val newFiles = listOf(
            FileRecord(null, "/folder1", "file1.txt", "hash1", true),
            FileRecord(null, "/folder1", "file2.txt", "hash1", true),
            FileRecord(null, "/folder1", "file21.txt", "hash1", true),
            FileRecord(null, "/folder1", "file3.txt", "hash3", true),
            FileRecord(null, "/folder1/subfolder", "file4.txt", "hash1", true),
            FileRecord(null, "/folder1/subfolder", "file5.txt", "hash1", true),
            FileRecord(null, "/folder1/subfolder", "file6.txt", "hash1", true),
            FileRecord(null, "/folder1/subfolder", "file8.txt", "hash8", true),
            FileRecord(null, "/folder1/subfolder2", "file8.txt", "hash8", true)


        )

        sort(oldFiles, newFiles).forEach {
            println(it)
        }
    }

    private fun sort(old: List<FileRecord>, new: List<FileRecord>): List<FileRecord>{
        val listToReturn = mutableListOf<FileRecord>()

        val duplicates = new.groupBy { it.hash }
        duplicates.forEach { map ->
            val newDuplicates = map.value
            val mutableNewDuplicates = newDuplicates.toMutableSet()
            val oldDuplicates = old.filter { it.hash ==  map.key}.toMutableSet()

            newDuplicates.forEach duplicates@{ newRecord ->
                if (oldDuplicates.contains(newRecord)){
                    listToReturn.add(newRecord)
                    mutableNewDuplicates.remove(newRecord)
                    oldDuplicates.remove(newRecord)
                    return@duplicates
                }
                val renamed = oldDuplicates.firstOrNull { it.hash == newRecord.hash && it.path == newRecord.path }
                renamed?.let {
                    oldDuplicates.remove(it)
                    it.name = newRecord.name
                    listToReturn.add(renamed)
                    mutableNewDuplicates.remove(newRecord)
                    return@duplicates
                }
                val moved = oldDuplicates.firstOrNull { it.hash == newRecord.hash && it.name == newRecord.name }
                moved?.let {
                    oldDuplicates.remove(it)
                    it.path = newRecord.path
                    listToReturn.add(it)
                    mutableNewDuplicates.remove(newRecord)
                    return@duplicates
                }

                val movedAndRenamed = oldDuplicates.firstOrNull { it.hash == newRecord.hash }
                movedAndRenamed?.let {
                    oldDuplicates.remove(it)
                    it.path = newRecord.path
                    it.name = newRecord.name
                    listToReturn.add(it)
                    mutableNewDuplicates.remove(newRecord)
                    return@duplicates
                }
            }

            mutableNewDuplicates.forEach {
                listToReturn.add(it)
            }
        }

        return listToReturn
    }
}