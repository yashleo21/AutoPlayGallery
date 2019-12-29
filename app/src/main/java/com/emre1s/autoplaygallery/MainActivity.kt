package com.emre1s.autoplaygallery

import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.emre1s.autoplaygallery.adapters.GalleryAdapter
import com.emre1s.autoplaygallery.models.GALLERY_ITEM_TYPE
import com.emre1s.autoplaygallery.models.GalleryDataModel
import com.emre1s.autoplaygallery.models.GalleryDataObject
import com.emre1s.autoplaygallery.util.IndicatorDecoration
import com.emre1s.autoplaygallery.util.Util
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    private var layoutManager: LinearLayoutManager? = null
    private var galleryAdapter: GalleryAdapter? = null
    private var videoSurfaceDefaultWidth: Int = 0
    private var screenDefaultHeight: Int = 1
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

        val indicatorDecoration = IndicatorDecoration()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        galleryAdapter = GalleryAdapter(this)
        rv_gallery.layoutManager = layoutManager
        rv_gallery.adapter = galleryAdapter
        rv_gallery.addItemDecoration(indicatorDecoration)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(rv_gallery)

        rv_gallery.addOnScrollListener(Util.GalleryScrollListener(this, exoPlayer!!, rv_gallery, GALLERY_ITEM_TYPE.TYPE_VIDEO.pos))

        val data = arrayListOf<GalleryDataModel>(GalleryDataModel(GALLERY_ITEM_TYPE.TYPE_VIDEO, GalleryDataObject("")),
            GalleryDataModel(GALLERY_ITEM_TYPE.TYPE_IMAGE, GalleryDataObject("")),
            GalleryDataModel(GALLERY_ITEM_TYPE.TYPE_VIDEO, GalleryDataObject("")),
            GalleryDataModel(GALLERY_ITEM_TYPE.TYPE_IMAGE, GalleryDataObject("")),
            GalleryDataModel(GALLERY_ITEM_TYPE.TYPE_VIDEO, GalleryDataObject("")),
        GalleryDataModel(GALLERY_ITEM_TYPE.TYPE_IMAGE, GalleryDataObject("")))

        galleryAdapter?.refreshList(data)
        Util.addExoPlayerToFirstViewIfVideo(exoPlayer!!, rv_gallery)
    }

    override fun onStop() {
        super.onStop()
        exoPlayer?.playWhenReady = false
    }

    override fun onResume() {
        super.onResume()
        exoPlayer?.playWhenReady = true
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.stop()
    }
}
