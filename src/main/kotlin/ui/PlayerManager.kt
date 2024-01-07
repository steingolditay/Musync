package ui

import com.goxr3plus.streamplayer.enums.Status
import com.goxr3plus.streamplayer.stream.StreamPlayer
import com.goxr3plus.streamplayer.stream.StreamPlayerEvent
import com.goxr3plus.streamplayer.stream.StreamPlayerListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import utils.FileUtils.getFullPath
import utils.FileUtils.isAudioFile
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.math.roundToLong


object PlayerManager: StreamPlayer(), StreamPlayerListener {

    var scope: CoroutineScope? = null

    init {
        addStreamPlayerListener(this)
    }

    private val _playlist = MutableStateFlow(listOf<String>())
    val playlist = _playlist.asStateFlow()

    private val _playerState = MutableStateFlow(Status.NOT_SPECIFIED)
    val playerState = _playerState.asStateFlow()

    private val _currentPlayingIndex: MutableStateFlow<Int?> = MutableStateFlow(null)
    val currentPlayingIndex = _currentPlayingIndex.asStateFlow()

    private val _currentPlayingTime: MutableStateFlow<Long?> = MutableStateFlow(null)
    val currentPlayingTime = _currentPlayingTime.asStateFlow()

    private val _currentPlayingDuration: MutableStateFlow<Long?> = MutableStateFlow(null)
    val currentPlayingDuration = _currentPlayingDuration.asStateFlow()

    private val _currentPlayingProgress: MutableStateFlow<Float?> = MutableStateFlow(null)
    val currentPlayingProgress = _currentPlayingProgress.asStateFlow()

    private var totalFrames = 0L


    fun addToPlaylist(filePath: String){
        _playlist.value = playlist.value.toMutableList().apply { add(filePath) }
    }

    fun removeFromPlaylist(index: Int){
        _currentPlayingIndex.value?.let { currentPlayingIndex ->
            when {
                index == currentPlayingIndex ->  return
                index < currentPlayingIndex -> _currentPlayingIndex.value = currentPlayingIndex - 1
            }
        }
        _playlist.value = playlist.value.toMutableList().apply { removeAt(index) }
    }


    override fun opened(dataSource: Any?, properties: MutableMap<String, Any>?) {
        properties ?: return

        (properties["duration"] as? Long)?.let { durationInMicroseconds ->
            _currentPlayingDuration.value = TimeUnit.MICROSECONDS.toSeconds(durationInMicroseconds)
        }
        (properties["audio.length.frames"] as? Int) ?.let {
            totalFrames = it.toLong()
        }

    }

    override fun progress(encodedBytes: Int, microsecondPosition: Long, pcmData: ByteArray?, properties: MutableMap<String, Any>?) {
        val progress = if (encodedBytes > 0 && totalBytes > 0) encodedBytes.toFloat() / totalBytes else -1f
        val seconds = (durationInSeconds * progress).roundToLong()
        _currentPlayingTime.value = seconds
        _currentPlayingProgress.value = progress
    }

    override fun statusUpdated(event: StreamPlayerEvent?) {
        event?.let {
            _playerState.value = it.playerStatus
            if (it.playerStatus == Status.EOM){
                handleNext()
            }
        }
    }

    fun playForIndex(trackNumber: Int){
        val file = File(playlist.value[trackNumber])
        if (file.exists()){
            stop()
            open(file)
            play()
            _currentPlayingIndex.value = trackNumber
            _currentPlayingTime.value = 0
        }
    }

    fun playSingleFile(file: File){
        stop()
        open(file)
        play()
        _playlist.value = listOf(file.absolutePath)
        _currentPlayingIndex.value = 0
        _currentPlayingTime.value = 0
    }

    fun playDirectory(file: File){
        val audioList = file.listFiles()?.filter { it.isAudioFile() }
        if (audioList.isNullOrEmpty()){
            return
        }

        stop()
        open(audioList.first())
        play()
        _playlist.value = audioList.map { it.getFullPath() }
        _currentPlayingIndex.value = 0
        _currentPlayingTime.value = 0
    }

    fun playNext(){
        _currentPlayingIndex.value?.let {
            val nextIndex = it + 1
            if (nextIndex == _playlist.value.size){
                return
            }
            playForIndex(nextIndex)
        }
    }

    fun playPrevious(){
        _currentPlayingIndex.value?.let {
            if (it <= 0){
                return
            }
            playForIndex(it - 1)
        }
    }

    private fun handleNext(){
        scope?.launch(Dispatchers.IO) {
            stop()
            delay(1000)
            _currentPlayingIndex.value?.let { currentPlayingIndex ->
                if (_playlist.value.size > currentPlayingIndex.plus(1)) {
                    val nextIndex = currentPlayingIndex + 1
                    val file = File(_playlist.value[nextIndex])
                    open(file)
                    play()
                    _currentPlayingIndex.value = nextIndex
                    _currentPlayingTime.value = 0
                } else {
                    run {
                        _currentPlayingIndex.value = -1
                        _currentPlayingTime.value = 0
                        _currentPlayingDuration.value = 0
                        _currentPlayingProgress.value = 0f
                    }
                }
            }
        }

    }

}