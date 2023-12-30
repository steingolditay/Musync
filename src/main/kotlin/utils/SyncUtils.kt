package utils

import enums.SyncProgress
import Constants.ImageResources

object SyncUtils {


    fun SyncProgress.getImageResourceForProgress(): String {
        return when (this){
            SyncProgress.INDEXING -> ImageResources.indexing
            SyncProgress.PROCESSING -> ImageResources.processing
            SyncProgress.FINALIZING -> ImageResources.finalizing
            SyncProgress.FETCHING -> ImageResources.fetching
            SyncProgress.READY -> ImageResources.ready
            SyncProgress.UPLOADING -> ImageResources.uploading
            SyncProgress.COMPLETE -> ImageResources.complete
        }
    }
}