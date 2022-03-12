package com.zero.audiotrimmer

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pub.devrel.easypermissions.EasyPermissions
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private var clipEnd = 0
    private var clipStart = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val player = getAudio()

        val btnMark = findViewById<Button>(R.id.btnMarkClip)
        val btnSave = findViewById<Button>(R.id.btnSaveClip)
        val btnPlay = findViewById<ImageView>(R.id.ivPlay)
        val btnRewind5s = findViewById<ImageView>(R.id.ivRewind5s)
        val btnRewind10s = findViewById<ImageView>(R.id.ivRewind10s)
        val btnRewind30s = findViewById<ImageView>(R.id.ivRewind30s)

        btnSave.setOnClickListener {
            if (clipEnd != 0){
                if (EasyPermissions.hasPermissions(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Already have permission, do the thing
                    val date: Long = Date().time
                    val file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

                    val fileName = "$file/$date.mp3"

                    Log.d("TAG", "onCreate: $fileName")

                    copyRAWtoPhone(R.raw.podcast, fileName)
                } else {
                    // Do not have permissions, request them now
                    EasyPermissions.requestPermissions(this, "Please grant storage access to save file",
                        121, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
            }
        }

        btnMark.setOnClickListener {
            if (clipEnd == 0) {
                clipEnd = player?.currentPosition ?: 0

                if (clipEnd != 0) {
                    player?.pause()
                    btnMark.text = "Mark Clip Start"
                    Toast.makeText(this, "Clip end marked", Toast.LENGTH_SHORT).show()
                }
            }
            else{
                clipStart = player?.currentPosition ?: 0
                if (clipStart <= clipEnd) {
                    player?.pause()
                    btnMark.text = "Mark Clip Start"
                    Toast.makeText(this, "Clip start marked", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, "Clip start should be lesser than the clip end duration", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            Log.d("TAG", "onCreate: $clipStart Clip End: $clipEnd")
        }

        btnRewind5s.setOnClickListener {
            if (player != null) {
                if (player.currentPosition.minus(5000) > 0) {
                    val pos = player.currentPosition - 5000
                    player.seekTo(pos)
                }
                else{
                    Toast.makeText(this, "Cannot rewind before 0s", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnRewind10s.setOnClickListener {
            if (player != null) {
                if (player.currentPosition.minus(10000) > 0) {
                    val pos = player.currentPosition - 10000
                    player.seekTo(pos)
                }
                else{
                    Toast.makeText(this, "Cannot rewind before 0s", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnRewind30s.setOnClickListener {
            if (player != null) {
                if (player.currentPosition.minus(30000) > 0) {
                    val pos = player.currentPosition - 30000
                    player.seekTo(pos)
                }
                else {
                    Toast.makeText(this, "Cannot rewind before 0s", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnPlay.setOnClickListener {
            if (player != null) {
                Log.d("TAG", "onCreate: ${player.currentPosition}")
                if (!player.isPlaying) {
                    btnPlay.setImageResource(R.drawable.img_pause)
                    player.start()
                }
                else{
                    btnPlay.setImageResource(R.drawable.img_play)
                    player.pause()
                }
            }
        }
    }

    private fun copyRAWtoPhone(id: Int, path: String) {
        val inputStream: InputStream = resources.openRawResource(id)
        val out = FileOutputStream(path)
        val buff = ByteArray(30570)
        var read: Int
        try {
            var i = 1
            while (inputStream.read(buff).also { read = it } > 0) {
                Log.d("TAG", "copyRAWtoPhone: $read   $i")
                if (i >= (clipStart/1000) && i <= (clipEnd/1000))
                    out.write(buff, 0, read)
                i++
            }
            Toast.makeText(this, "Successfully trimmed and saved", Toast.LENGTH_LONG).show()
        } catch (e: IOException){
            Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
        } finally {
            inputStream.close()
            out.close()
        }
    }

    private fun getAudio(): MediaPlayer? {
        val resID = resources.getIdentifier("podcast", "raw", packageName)
        return MediaPlayer.create(this, resID)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
    }

}