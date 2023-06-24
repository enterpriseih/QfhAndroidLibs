package android.qfh.libs.utils

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Base64
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.nio.ByteBuffer
import kotlin.math.roundToInt

object ImageUtil {
    /**
     * 根据指定的宽高从数据流中解析图片,如果图片大于指定的宽高则会被压缩，直到指定的宽高能包含压缩后的图片
     * @param resId 资源Id,如果从资源文件中解析图片，这个字段不为空
     * @param res Resources 对象，如果从资源文件中解析图片，这个字段不为空
     * @param inputStream 数据流,从数据流中解析图片此字段不为空;内部会关闭改流(不需要在外界关闭)
     * @param byteArray 字节数组,从字节数组中加载图片时此字段不能为空
     * @param file 文件,从文件中解析图片此字段不为空
     * @param aliveWidth 图片的最大宽度，大于这个值图片会被压缩
     * @param aliveHeight 图片的最大高度，大于这个值图片会被压缩
     */
    fun getScaleBitmap(
        resId: Int = -1,
        res: Resources? = null,
        inputStream: InputStream? = null,
        byteArray: ByteArray? = null,
        file: File? = null,
        aliveWidth: Float,
        aliveHeight: Float
    ): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var tempByteArray: ByteArray? = null
        when {
            resId != -1 -> BitmapFactory.decodeResource(res, resId, options)
            inputStream != null -> {
                tempByteArray = inputStream.use { it.readBytes() }
                BitmapFactory.decodeByteArray(tempByteArray, 0, tempByteArray.size, options)
            }
            byteArray != null -> BitmapFactory.decodeByteArray(
                byteArray,
                0,
                byteArray.size,
                options
            )
            file != null -> BitmapFactory.decodeFile(file.absolutePath, options)
        }
        if (options.outWidth <= 0 || options.outHeight <= 0) {
            return null
        }
        options.inJustDecodeBounds = false
        if (options.outWidth > aliveWidth || options.outHeight > aliveHeight) {
            val widthScale = (options.outWidth.toFloat() / aliveWidth).roundToInt()
            val heightScale = (options.outHeight.toFloat() / aliveHeight).roundToInt()
            options.inSampleSize = widthScale.coerceAtLeast(heightScale)
        }
        return when {
            resId != -1 -> BitmapFactory.decodeResource(res, resId, options)
            inputStream != null -> BitmapFactory.decodeByteArray(
                tempByteArray,
                0,
                tempByteArray!!.size,
                options
            )
            file != null -> BitmapFactory.decodeFile(file.absolutePath, options)
            byteArray != null -> BitmapFactory.decodeByteArray(
                byteArray,
                0,
                byteArray.size,
                options
            )
            else -> throw RuntimeException("error")
        }
    }

    /**
     * bitmap 转 base64
     */
    fun bitmapToBase64(bitmap: Bitmap?): String? {
        var result: String? = null
        var baos: ByteArrayOutputStream? = null
        try {
            if (bitmap != null) {
                baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
                baos.flush()
                baos.close()
                val bitmapBytes: ByteArray = baos.toByteArray()
                result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (baos != null) {
                    baos.flush()
                    baos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return result
    }

    fun imageToBitmap(image: Image): Bitmap {
        val width = image.width
        val height = image.height
        val planes = image.planes
        val buffer: ByteBuffer = planes[0].buffer
        //两个像素的距离
        val pixelStride = planes[0].pixelStride
        //整行的距离
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * width
        val bitmap =
            Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        image.close()
        return bitmap
    }

}