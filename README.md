# Recyclerview Auto Media Playback Management (For Exoplayer)

[![](https://jitpack.io/v/yashleo21/AutoPlayGallery.svg)](https://jitpack.io/#yashleo21/AutoPlayGallery)

### Add it in your root build.gradle at the end of repositories
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  ```
  
###  Add the dependency
  ```
  dependencies {
	        implementation 'com.github.yashleo21:AutoPlayGallery:Tag'
	}
  ```
  
  The purpose of this library is to handle play/pause of videos by using Exoplayer in Recyclerview which also supports **multiple** view types.
  You can have a Viewholder with only image and another with videos and this library will handle auto play/pause of videos for you.
  
  ## Some key instructions
  
  * ### Keep only **one** instance of Exoplayer in your activity/fragment and handle its pause/resume states in onStop/onResume and onDestroy.
  
  * ### When onBindViewHolder() is called for a Viewholder with exoplayer in it, prepare the media source. Everything else related to playing the video happens inside the interface callback provided in this library. (Explained below)
  
  
  * Add a scroll listener to your recycler view by creating an instance of GalleryScrollListener(context: Context, val exoPlayer: ExoPlayer, recyclerView: RecyclerView,
    orientation: Int = RecyclerView.HORIZONTAL) and pass all the required parameters.
  * Once your recycler view's adapter has been created and data has been passed, call addExoPlayerToFirstViewIfVideo(exoPlayer: ExoPlayer, recyclerView: RecyclerView) function.
  * Implement the interface AutoPlayGalleryVideoHolder in your Recyclerview's Viewholder **which is suppose to play videos/has exoplayer**
    * Inside setAndPrepareExoPlayer(exoPlayer: ExoPlayer) function, do the following:
      1. Attach the view holder's PlayerView to the supplied exoplayer
      2. Prepare the exoplayer by providing it with a media source
      3. Set exoplayer to play whenever it is ready. (exoplayer.playwhenready = true)
    * For pauseState(), show a loading view, thumbnail or whatever you might want to in paused state. This callback is also called when video is loading.
    * In resumeState(), show the exoplayer and hide thumbnails, etc.
    
    
    The next release is going to further streamline the playback process by handling media states as well. Right now, the video resets and starts from beginning.
    
    Here to help!
