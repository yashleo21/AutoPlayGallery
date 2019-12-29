package com.emre1s.autoplaygallery.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.emre1s.autoplaygallery.R
import com.emre1s.autoplaygallery.models.GALLERY_ITEM_TYPE
import com.emre1s.autoplaygallery.models.GalleryDataModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.image_view.view.*
import kotlinx.android.synthetic.main.video_view.view.*


class GalleryAdapter(val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var data: ArrayList<GalleryDataModel>? = null
    private var exoPlayer: ExoPlayer? = null

    override fun getItemViewType(position: Int): Int {
        return when (data?.get(position)?.type) {
            GALLERY_ITEM_TYPE.TYPE_IMAGE -> GALLERY_ITEM_TYPE.TYPE_IMAGE.pos
            GALLERY_ITEM_TYPE.TYPE_VIDEO -> GALLERY_ITEM_TYPE.TYPE_VIDEO.pos
            else -> GALLERY_ITEM_TYPE.TYPE_IMAGE.pos
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            GALLERY_ITEM_TYPE.TYPE_IMAGE.pos -> {
                ImageHolder(getView(parent,
                    R.layout.image_view
                ))
            }
            GALLERY_ITEM_TYPE.TYPE_VIDEO.pos -> {
                VideoHolder(getView(parent,
                    R.layout.video_view
                ))
            }
            else -> {
                ImageHolder(getView(parent,
                    R.layout.image_view
                ))
            }
        }
    }

    private fun getView(parent: ViewGroup, layout: Int): View {
        return LayoutInflater.from(parent.context).inflate(layout, parent, false)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VideoHolder -> {
                holder.bindView()
            }

            is ImageHolder -> {
              //  holder.bindView()
            }
        }
    }

    override fun getItemCount(): Int {
        if (data.isNullOrEmpty()) return 0
        return data?.size ?: 0
    }

    fun refreshList(data: ArrayList<GalleryDataModel>?) {
        this.data = data
        notifyDataSetChanged()
    }

    inner class VideoHolder(val view: View): RecyclerView.ViewHolder(view), com.emre1s.autoplaygallery.util.Util.AutoPlayGalleryVideoHolder {
        private var mediaSource: MediaSource? = null
        private var exoPlayerView: PlayerView? = null
        private var pos: Int = -1
        // Create a data source factory.
        var dataSourceFactory: DataSource.Factory =
            DefaultHttpDataSourceFactory(Util.getUserAgent(context, "auto-play-gallery"))


        init {
            exoPlayerView = view.pv
            mediaSource = DashMediaSource.Factory(dataSourceFactory)
            .createMediaSource(Uri.parse("https://bitmovin-a.akamaihd.net/content/MI201109210084_1/mpds/f08e80da-bf1d-4e3d-8899-f0f6155f6efa.mpd"))
        }

        fun bindView() {
            Log.d("GalleryAdapter", "CALLED bind")
            pos = adapterPosition
//            (exoPlayerView?.player as ExoPlayer).prepare(mediaSource!!)
//            (exoPlayerView?.player as ExoPlayer).playWhenReady = true
        }

        override fun setAndPrepareExoPlayer(exoPlayer: ExoPlayer) {
            //Clear previous instance
            exoPlayerView?.player = null
            Log.d("GalleryAdapter", "CALLED setExoPlayer $pos")
            exoPlayerView?.player = exoPlayer
            exoPlayer.prepare(mediaSource!!)
            exoPlayer.playWhenReady = true
        }

        override fun pauseState() {
            view.play_pause.visibility = View.VISIBLE
            view.thumbnail.visibility = View.VISIBLE
            exoPlayerView?.visibility = View.INVISIBLE
        }

        override fun resumeState() {
            view.play_pause.visibility = View.INVISIBLE
            view.thumbnail.visibility = View.INVISIBLE
            exoPlayerView?.visibility = View.VISIBLE
        }
    }

    inner class ImageHolder(val view: View): RecyclerView.ViewHolder(view) {

        fun bindView() {
            if (adapterPosition < 0) return
            view.iv_gallery.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100 + (10 * adapterPosition))
            view.requestLayout()
        }
    }
}