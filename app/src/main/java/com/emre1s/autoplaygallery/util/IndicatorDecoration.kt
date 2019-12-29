package com.emre1s.autoplaygallery.util

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class IndicatorDecoration: RecyclerView.ItemDecoration() {

    private val activeColor = (0xFFFFFFFF).toInt()
    private val inactiveColor = 0x66FFFFFF

    private val DP = Resources.getSystem().displayMetrics.density

    //Height of indicator item view
    private val indicatorHeight = (DP * 16).toInt()

    //Stroke width
    private val indicatorStrokeWidth = (DP * 2)

    //Indicator width
    private val indicatorItemLength = (DP * 16)

    private val indicatorItemPadding = (DP * 4)

    //Animation interpolation
    private val mInterpolator = AccelerateDecelerateInterpolator()

    private val paint = Paint()

    init {
        paint.strokeCap = Paint.Cap.ROUND
        paint.strokeWidth = indicatorStrokeWidth
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val itemCount = parent.adapter?.itemCount ?: 0

        // center horizontally, calculate width and subtract half from center
        val totalLength = indicatorItemLength * itemCount
        val paddingBetweenItems = 0.coerceAtLeast(itemCount - 1) * indicatorItemPadding
        val indicatorTotalLength = totalLength + paddingBetweenItems
        val indicatorStartX = (parent.width - indicatorTotalLength) / 2F

        // center vertically in the allotted space
        val indicatorPosY = parent.height - indicatorHeight / 2F

        drawInactiveIndicators(c, indicatorStartX, indicatorPosY, itemCount)

        //Find active page
        val manager = parent.layoutManager as LinearLayoutManager
        val activePosition = manager.findFirstVisibleItemPosition()
        if (activePosition == RecyclerView.NO_POSITION) return

        //Find active page offset
        val activeChild = manager.findViewByPosition(activePosition)
        val left = activeChild!!.left
        val width = activeChild.width

        // on swipe the active item will be positioned from [-width, 0]
        // interpolate offset for smooth animation

        val progress = mInterpolator.getInterpolation(left * -1 / width.toFloat())

        drawHighlights(c, indicatorStartX, indicatorPosY, activePosition, progress, itemCount)

    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = - indicatorHeight
    }

    private fun drawInactiveIndicators(c: Canvas, startPosX: Float, yPos: Float, itemCount: Int) {
        paint.color = inactiveColor

        val itemWidth = indicatorItemLength + indicatorItemPadding

        var startPos = startPosX
        for (i in 0 until  itemCount) {
            c.drawLine(startPos, yPos, startPos + indicatorItemLength, yPos, paint)
            startPos += itemWidth
        }
    }

    private fun drawHighlights(c: Canvas, startPosX: Float, yPos: Float, highlightPosition: Int, progress: Float, itemCount: Int) {
        paint.color = activeColor

        val itemWidth = indicatorItemLength + indicatorItemPadding

        if (progress == 0F) {
            //Normal draw, no swipe detected
            val highlightStartPos = startPosX + itemWidth * highlightPosition
            c.drawLine(highlightStartPos, yPos, highlightStartPos + indicatorItemLength, yPos, paint)
        } else {
            var highlightStartPos = startPosX + itemWidth * highlightPosition
            //Calculate partial highlight since swipe in progress
            val partialLength = indicatorItemLength * progress

            c.drawLine(highlightStartPos + partialLength, yPos, highlightStartPos + indicatorItemLength, yPos, paint)

            //Draw highlight overlapping next item as well
            if (highlightPosition < itemCount - 1) {
                highlightStartPos += itemWidth
                c.drawLine(highlightStartPos, yPos, highlightStartPos + partialLength, yPos, paint)
            }
        }
    }
}