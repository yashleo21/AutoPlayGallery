package com.emre1s.autoplaygallery

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emre1s.autoplaygallery.models.GALLERY_ITEM_TYPE
import com.emre1s.autoplaygallery.models.GalleryDataModel
import com.emre1s.autoplaygallery.models.GalleryDataObject
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private var layoutManager: LinearLayoutManager? = null
    private var galleryAdapter: GalleryAdapter? = null
    private var videoSurfaceDefaultWidth: Int = 0
    private var screenDefaultHeight: Int = 1
    private var activeViewPosition: Int = -1

    private var exoPlayer: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Init exoplayer instance once
        exoPlayer = SimpleExoPlayer.Builder(this).build()

        val display =
            (this.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val point = Point()
        display.getSize(point)
        videoSurfaceDefaultWidth = point.x
        screenDefaultHeight = point.y
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        galleryAdapter = GalleryAdapter(this)
        rv_gallery.layoutManager = layoutManager
        rv_gallery.adapter = galleryAdapter

        rv_gallery.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                Log.d(TAG, "SCROLLED")
                var currentPosition = -1
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    currentPosition = calculateActivePosition()
                    Log.d(TAG, "IDLE STTE $currentPosition")
                    if (currentPosition < 0) return
                    Log.d(TAG, "activeViewPosition $activeViewPosition")
                    if (activeViewPosition == currentPosition) return

                    exoPlayer?.playWhenReady = false
                    //Find view type
                    val currentViewholder = rv_gallery?.findViewHolderForAdapterPosition(currentPosition)
                    if (currentViewholder != null && currentViewholder is GalleryAdapter.VideoHolder) {
                        activeViewPosition = currentPosition
                        Log.d(TAG, "Not null viewholder")
                        currentViewholder.setAndPrepareExoPlayer(exoPlayer = exoPlayer!!)
                    } else {
                        //Video view gone
                        activeViewPosition = -1
                    }
                }
            }
        })



        val data = arrayListOf<GalleryDataModel>(GalleryDataModel(GALLERY_ITEM_TYPE.TYPE_IMAGE, GalleryDataObject("")),
            GalleryDataModel(GALLERY_ITEM_TYPE.TYPE_VIDEO, GalleryDataObject("")),
            GalleryDataModel(GALLERY_ITEM_TYPE.TYPE_IMAGE, GalleryDataObject("")),
            GalleryDataModel(GALLERY_ITEM_TYPE.TYPE_VIDEO, GalleryDataObject("")),
        GalleryDataModel(GALLERY_ITEM_TYPE.TYPE_IMAGE, GalleryDataObject("")))

        galleryAdapter?.refreshList(data)
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.stop()
    }

    fun calculateActivePosition(): Int {
        var targetPosition = -1
        val startPosition = layoutManager!!.findFirstVisibleItemPosition()
        var endPosition = layoutManager!!.findLastVisibleItemPosition()

        if (endPosition - startPosition > 1) {
            endPosition = startPosition + 1
        }

        if (startPosition < 0 || endPosition < 0) {
            return targetPosition
        }

        if (startPosition != endPosition) {
            val startPositionVideoWidth = getVisibleVideoSurfaceWidth(startPosition)
            val endPositionVideoWidth = getVisibleVideoSurfaceWidth(endPosition)

            targetPosition = if (startPositionVideoWidth > endPositionVideoWidth) startPosition else endPosition
        } else {
            targetPosition = startPosition
        }

        return targetPosition
    }

    private fun getVisibleVideoSurfaceWidth(playPosition: Int): Int {
        val at =
            playPosition - layoutManager!!.findFirstVisibleItemPosition()
        Log.d(TAG, "getVisibleVideoSurfaceHeight: at: $at")
        val child: View = rv_gallery.getChildAt(at) ?: return 0
        val location = IntArray(2)
        child.getLocationInWindow(location)
        return if (location[0] < 0) {
            location[0] + videoSurfaceDefaultWidth
        } else {
            videoSurfaceDefaultWidth - location[0]
        }
    }
}
