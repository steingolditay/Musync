package enums

enum class SyncProgress(val title: String, val stepNumber: Int) {
    INDEXING("Indexing files", 1),
    PROCESSING("Processing files - %d / %d", 2),
    FINALIZING("Updating database", 3),
    FETCHING("Fetching data from server", 4),
    READY("Ready to upload", 5),
    UPLOADING("Uploading files - %d / %d", 6),
    COMPLETE("You are all set!", 7)
}

fun SyncProgress.relativeProgress(currentState: SyncProgress): SyncRelativeState {
    return when {
        stepNumber == currentState.stepNumber -> SyncRelativeState.ACTIVE
        stepNumber < currentState.stepNumber  -> SyncRelativeState.PASSED
        else -> SyncRelativeState.UPCOMING
    }
}
