package com.emre1s.autoplaygallery.models

data class GalleryDataModel(var type: GALLERY_ITEM_TYPE, var data: GalleryDataObject)

data class GalleryDataObject(var source: String?)

enum class GALLERY_ITEM_TYPE(val pos: Int) {
    TYPE_IMAGE(0),
    TYPE_VIDEO(1)
}