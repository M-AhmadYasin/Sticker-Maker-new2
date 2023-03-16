package com.pro.stickermaker.emojitobitmap

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.util.TypedValue
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.Px
import com.pro.stickermaker.emojitobitmap.Bitmapji.createBitmap

/**
 * Allows you to load emoji as a Bitmap! See [createBitmap].
 */
object Bitmapji {

    /**
     * Create a bitmap given the emoji.
     * @param context The context
     * @param emoji An emoji (or any String, really)
     * @param size The size to make the bitmap. The largest size possible is 93dp (not sure why).
     */
    @JvmStatic
    fun createBitmap(context: Context, emoji: String, @Px size: Float): Bitmap {
        val captureView = CaptureTextView(context, size)
        return captureView.capture(emoji)
    }

    @JvmStatic
    fun createTextBitmap(
        context: Context,
        text: String,
        @Px size: Float,
        textColor: Color,
        backgroundColor: Color,
        strokeColor: Color? = null
    ): Bitmap {
        val captureView = CaptureTextView(context, size)
        return captureView.capture(text)
    }

    /*#03FFFFFF*/
    @SuppressLint("AppCompatCustomView")
    private class CaptureTextView(
        context: Context,
        var size: Float,
        @ColorInt var text_Color: Int = Color.WHITE,
        @ColorInt var background_Color: Int = Color.parseColor("#03FFFFFF"),
        var stroke_Color: Color? = null
    ) : TextView(context) {

        fun capture(emoji: String?): Bitmap {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, size)
            setTextColor(text_Color)
            setBackgroundColor(background_Color)
            text = emoji
            measure(
                MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            )
            layout(0, 0, measuredWidth, measuredHeight)
            val b = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val c = Canvas(b)
            /*c.drawRect(left.toFloat(),top.toFloat(),right.toFloat(),bottom.toFloat(), Paint().apply {
                this.color = Color.parseColor("#000000")
                style = Paint.Style.STROKE

            })*/
            draw(c)
            return b
        }
    }
}