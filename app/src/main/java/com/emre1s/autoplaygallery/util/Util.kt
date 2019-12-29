package com.emre1s.autoplaygallery.util
import android.content.Context
import android.graphics.Point
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.ExoPlayer


object Util {
    private val TAG = Util::class.java.simpleName
    private var videoSurfaceDefaultWidth: Int = 0
    private var screenDefaultHeight: Int = 1
    private var activeViewPosition: Int = -1
    private var recyclerOrientation = RecyclerView.HORIZONTAL


    class GalleryScrollListener(context: Context, val exoPlayer: ExoPlayer,
                                orientation: Int = RecyclerView.HORIZONTAL): RecyclerView.OnScrollListener() {

        init {
            recyclerOrientation = orientation

            val display =
                (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            val point = Point()
            display.getSize(point)
            videoSurfaceDefaultWidth = point.x
            screenDefaultHeight = point.y
        }
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            Log.d(TAG, "force scroll for pos 0")
            var currentPosition = -1
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                currentPosition = calculateActivePosition(recyclerView)
                Log.d(TAG, "force scroll for pos 0 curret pos $currentPosition")
                if (currentPosition < 0) return

                if (activeViewPosition == currentPosition) return

                recyclerView.requestLayout()
                exoPlayer.playWhenReady = false
                //Find view type
                val currentViewholder = recyclerView.findViewHolderForAdapterPosition(currentPosition)

                if (currentViewholder != null) {
                    Log.d(TAG, "Condition met")
                    activeViewPosition = currentPosition
                    if (currentViewholder is AutoPlayGalleryVideoHolder) {
                        Log.d(TAG, "Condition met AutoPlayGalleryVideoHolder")
                        currentViewholder.setAndPrepareExoPlayer(exoPlayer = exoPlayer)
                    }

                } else {
                    //Video view gone
                    activeViewPosition = -1
                }
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
        }
    }

    private fun calculateActivePosition(recyclerView: RecyclerView): Int {
        val manager = recyclerView.layoutManager as LinearLayoutManager
        var targetPosition = -1
        val startPosition = manager.findFirstVisibleItemPosition()
        var endPosition = manager.findLastVisibleItemPosition()

        if (endPosition - startPosition > 1) {
            endPosition = startPosition + 1
        }

        if (startPosition < 0 || endPosition < 0) {
            return targetPosition
        }

        if (startPosition != endPosition) {
            val startPositionVideoWidth = getVisibleVideoSurface(recyclerView, startPosition)
            val endPositionVideoWidth = getVisibleVideoSurface(recyclerView, endPosition)

            targetPosition = if (startPositionVideoWidth > endPositionVideoWidth) startPosition else endPosition
        } else {
            targetPosition = startPosition
        }

        return targetPosition
    }

    private fun getVisibleVideoSurface(recyclerView: RecyclerView, playPosition: Int): Int {
        val manager = recyclerView.layoutManager as LinearLayoutManager
        val at =
            playPosition - manager.findFirstVisibleItemPosition()

        val child: View = recyclerView.getChildAt(at) ?: return 0
        val location = IntArray(2)
        child.getLocationInWindow(location)
        return if (recyclerOrientation == RecyclerView.HORIZONTAL) {
            if (location[0] < 0) {
                location[0] + videoSurfaceDefaultWidth
            } else {
                videoSurfaceDefaultWidth - location[0]
            }
        } else {
            if (location[1] < 0) {
                location[1] + videoSurfaceDefaultWidth
            } else {
                videoSurfaceDefaultWidth - location[1]
            }
        }
    }

    fun addExoPlayerToFirstViewIfVideo(exoPlayer: ExoPlayer, recyclerView: RecyclerView) {
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val holder = recyclerView.findViewHolderForAdapterPosition(0)
                if (holder is AutoPlayGalleryVideoHolder) {
                    holder.setAndPrepareExoPlayer(exoPlayer)
                }
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    interface AutoPlayGalleryVideoHolder {
        fun setAndPrepareExoPlayer(exoPlayer: ExoPlayer)
    }
}