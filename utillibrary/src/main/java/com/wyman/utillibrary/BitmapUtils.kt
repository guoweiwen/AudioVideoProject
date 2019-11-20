package com.wyman.utillibrary

import android.content.ContentProviderClient
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PixelFormat
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.RemoteException
import android.provider.MediaStore
import android.util.Log

import java.io.BufferedOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer

/**
 * Created by cain on 2017/7/9.
 */

object BitmapUtils {

    private val TAG = "BitmapUtils"

    val EXIF_TAGS = arrayOf("FNumber",
            ExifInterface.TAG_DATETIME,
            "ExposureTime",
            ExifInterface.TAG_FLASH,
            ExifInterface.TAG_FOCAL_LENGTH,
            "GPSAltitude", "GPSAltitudeRef",
            ExifInterface.TAG_GPS_DATESTAMP,
            ExifInterface.TAG_GPS_LATITUDE,
            ExifInterface.TAG_GPS_LATITUDE_REF,
            ExifInterface.TAG_GPS_LONGITUDE,
            ExifInterface.TAG_GPS_LONGITUDE_REF,
            ExifInterface.TAG_GPS_PROCESSING_METHOD,
            ExifInterface.TAG_GPS_TIMESTAMP,
            ExifInterface.TAG_IMAGE_LENGTH,
            ExifInterface.TAG_IMAGE_WIDTH,
            "ISOSpeedRatings",
            ExifInterface.TAG_MAKE,
            ExifInterface.TAG_MODEL,
            ExifInterface.TAG_WHITE_BALANCE
    )

    /**
     * 从Buffer中创建Bitmap
     * @param buffer
     * @param width
     * @param height
     * @param flipX
     * @param flipY
     * @return
     */
    @JvmOverloads
    fun getBitmapFromBuffer(buffer: ByteBuffer?, width: Int, height: Int,
                            flipX: Boolean = false, flipY: Boolean = false): Bitmap? {
        if (buffer == null) {
            return null
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(buffer)
        return if (flipX || flipY) {
            flipBitmap(bitmap, flipX, flipY, true)
        } else {
            bitmap
        }
    }

    /**
     * 从普通文件中读入图片
     * @param fileName
     * @return
     */
    fun getBitmapFromFile(fileName: String): Bitmap? {
        var bitmap: Bitmap?
        val file = File(fileName)
        if (!file.exists()) {
            return null
        }
        try {
            bitmap = BitmapFactory.decodeFile(fileName)
        } catch (e: Exception) {
            Log.e(TAG, "getBitmapFromFile: ", e)
            bitmap = null
        }

        return bitmap
    }

    /**
     * 加载Assets文件夹下的图片
     * @param context
     * @param fileName
     * @return
     */
    fun getImageFromAssetsFile(context: Context, fileName: String): Bitmap? {
        var bitmap: Bitmap? = null
        val manager = context.resources.assets
        try {
            val `is` = manager.open(fileName)
            bitmap = BitmapFactory.decodeStream(`is`)
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }

    /**
     * 加载Assets文件夹下的图片
     * @param context
     * @param fileName
     * @return
     */
    fun getImageFromAssetsFile(context: Context, fileName: String, inBitmap: Bitmap?): Bitmap? {
        var bitmap: Bitmap? = null
        val manager = context.resources.assets
        try {
            val `is` = manager.open(fileName)
            if (inBitmap != null && !inBitmap.isRecycled) {
                val options = BitmapFactory.Options()
                // 使用inBitmap时，inSampleSize得设置为1
                options.inSampleSize = 1
                // 这个属性一定要在inBitmap之前使用，否则会弹出一下异常
                // BitmapFactory: Unable to reuse an immutable bitmap as an image decoder target.
                options.inMutable = true
                options.inBitmap = inBitmap
                bitmap = BitmapFactory.decodeStream(`is`, null, options)
            } else {
                bitmap = BitmapFactory.decodeStream(`is`)
            }
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bitmap
    }

    /**
     * 计算 inSampleSize的值
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    fun calculateInSampleSize(options: BitmapFactory.Options,
                              reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }

            var totalPixels = (width * height / inSampleSize).toLong()
            val totalReqPixelsCap = (reqWidth * reqHeight * 2).toLong()

            while (totalPixels > totalReqPixelsCap) {
                inSampleSize *= 2
                totalPixels /= 2
            }
        }
        return inSampleSize
    }


    /**
     * 从文件读取Bitmap
     * @param dst       目标路径
     * @param maxWidth  读入最大宽度, 为0时，直接读入原图
     * @param maxHeight 读入最大高度，为0时，直接读入原图
     * @return
     */
    fun getBitmapFromFile(dst: File?, maxWidth: Int, maxHeight: Int): Bitmap? {
        if (null != dst && dst.exists()) {
            var opts: BitmapFactory.Options? = null
            if (maxWidth > 0 && maxHeight > 0) {
                opts = BitmapFactory.Options()
                opts.inJustDecodeBounds = true
                BitmapFactory.decodeFile(dst.path, opts)
                // 计算图片缩放比例
                opts.inSampleSize = calculateInSampleSize(opts, maxWidth, maxHeight)
                opts.inJustDecodeBounds = false
                opts.inInputShareable = true
                opts.inPurgeable = true
            }
            try {
                return BitmapFactory.decodeFile(dst.path, opts)
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
            }

        }
        return null
    }

    /**
     * 从文件读取Bitmap
     * @param dst                   目标路径
     * @param maxWidth              读入最大宽度，为0时，直接读入原图
     * @param maxHeight             读入最大高度，为0时，直接读入原图
     * @param processOrientation    是否处理图片旋转角度
     * @return
     */
    fun getBitmapFromFile(dst: File?, maxWidth: Int, maxHeight: Int, processOrientation: Boolean): Bitmap? {
        if (null != dst && dst.exists()) {
            var opts: BitmapFactory.Options? = null
            if (maxWidth > 0 && maxHeight > 0) {
                opts = BitmapFactory.Options()
                opts.inJustDecodeBounds = true
                BitmapFactory.decodeFile(dst.path, opts)
                // 计算图片缩放比例
                opts.inSampleSize = calculateInSampleSize(opts, maxWidth, maxHeight)
                opts.inJustDecodeBounds = false
                opts.inInputShareable = true
                opts.inPurgeable = true
            }
            try {
                val bitmap = BitmapFactory.decodeFile(dst.path, opts)
                if (!processOrientation) {
                    return bitmap
                }
                val orientation = getOrientation(dst.path)
                if (orientation == 0) {
                    return bitmap
                } else {
                    val matrix = Matrix()
                    matrix.postRotate(orientation.toFloat())
                    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                }
            } catch (e: OutOfMemoryError) {
                e.printStackTrace()
            }

        }
        return null
    }

    /**
     * 从Drawable中获取Bitmap图片
     * @param drawable
     * @return
     */
    fun getBitmapFromDrawable(drawable: Drawable): Bitmap {
        val w = drawable.intrinsicWidth
        val h = drawable.intrinsicHeight
        val config = if (drawable.opacity != PixelFormat.OPAQUE)
            Bitmap.Config.ARGB_8888
        else
            Bitmap.Config.RGB_565
        val bitmap = Bitmap.createBitmap(w, h, config)
        // 在View或者SurfaceView里的canvas.drawBitmap会看不到图，需要用以下方式处理
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, w, h)
        drawable.draw(canvas)

        return bitmap
    }

    /**
     * 图片等比缩放
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @param isRecycled
     * @return
     */
    fun zoomBitmap(bitmap: Bitmap?, newWidth: Int, newHeight: Int, isRecycled: Boolean): Bitmap? {
        var bitmap: Bitmap? = bitmap ?: return null
        val width = bitmap!!.width
        val height = bitmap.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        val matrix = Matrix()
        if (scaleWidth < scaleHeight) {
            matrix.postScale(scaleWidth, scaleWidth)
        } else {
            matrix.postScale(scaleHeight, scaleHeight)
        }
        val result = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        if (!bitmap.isRecycled && isRecycled) {
            bitmap.recycle()
            bitmap = null
        }
        return result
    }

    /**
     * 保存图片
     * @param context
     * @param path
     * @param bitmap
     * @param addToMediaStore
     */
    @JvmOverloads
    fun saveBitmap(context: Context, path: String, bitmap: Bitmap,
                   addToMediaStore: Boolean = true) {
        val file = File(path)
        if (!file.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        var fOut: FileOutputStream? = null
        try {
            fOut = FileOutputStream(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var compress = true
        if (path.endsWith(".png")) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut)
        } else if (path.endsWith(".jpeg") || path.endsWith(".jpg")) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
        } else { // 除了png和jpeg之外的图片格式暂时不支持
            compress = false
        }
        try {
            fOut!!.flush()
            fOut.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        // 添加到媒体库
        if (addToMediaStore && compress) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.DATA, path)
            values.put(MediaStore.Images.Media.DISPLAY_NAME, file.name)
            context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }
    }

    /**
     * 保存图片
     * @param filePath
     * @param buffer
     * @param width
     * @param height
     */
    fun saveBitmap(filePath: String, buffer: ByteBuffer, width: Int, height: Int) {
        var bos: BufferedOutputStream? = null
        try {
            bos = BufferedOutputStream(FileOutputStream(filePath))
            var bitmap: Bitmap? = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap!!.copyPixelsFromBuffer(buffer)
            bitmap = BitmapUtils.rotateBitmap(bitmap, 180, true)
            bitmap = BitmapUtils.flipBitmap(bitmap, true)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            bitmap.recycle()
            bitmap = null
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } finally {
            if (bos != null)
                try {
                    bos.close()
                } catch (e: IOException) {
                    // do nothing
                }

        }
    }


    /**
     * 获取图片旋转角度
     * @param path
     * @return
     */
    fun getOrientation(path: String?): Int {
        var rotation = 0
        try {
            val file = File(path!!)
            val exif = ExifInterface(file.absolutePath)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL)
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> rotation = 90

                ExifInterface.ORIENTATION_ROTATE_180 -> rotation = 180

                ExifInterface.ORIENTATION_ROTATE_270 -> rotation = 270

                else -> rotation = 0
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return rotation
    }

    /**
     * 获取Uri路径图片的旋转角度
     * @param context
     * @param uri
     * @return
     */
    fun getOrientation(context: Context, uri: Uri): Int {
        val scheme = uri.scheme
        var provider: ContentProviderClient? = null
        if (scheme == null || ContentResolver.SCHEME_FILE == scheme) {
            return getOrientation(uri.path)
        } else if (scheme == ContentResolver.SCHEME_CONTENT) {
            try {
                provider = context.contentResolver.acquireContentProviderClient(uri)
            } catch (e: SecurityException) {
                return 0
            }

            if (provider != null) {
                val cursor: Cursor?
                try {
                    cursor = provider.query(uri, arrayOf(MediaStore.Images.ImageColumns.ORIENTATION, MediaStore.Images.ImageColumns.DATA), null, null, null)
                } catch (e: RemoteException) {
                    e.printStackTrace()
                    return 0
                }

                if (cursor == null) {
                    return 0
                }

                val orientationIndex = cursor
                        .getColumnIndex(MediaStore.Images.ImageColumns.ORIENTATION)
                val dataIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)

                try {
                    if (cursor.count > 0) {
                        cursor.moveToFirst()

                        var rotation = 0

                        if (orientationIndex > -1) {
                            rotation = cursor.getInt(orientationIndex)
                        }

                        if (dataIndex > -1) {
                            val path = cursor.getString(dataIndex)
                            rotation = rotation or getOrientation(path)
                        }
                        return rotation
                    }
                } finally {
                    cursor.close()
                }
            }
        }
        return 0
    }

    /**
     * 获取图片大小
     * @param path
     * @return
     */
    fun getBitmapSize(path: String): Point {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, options)
        return Point(options.outWidth, options.outHeight)
    }

    /**
     * 将Bitmap图片旋转一定角度
     * @param data
     * @param rotate
     * @return
     */
    @JvmOverloads
    fun rotateBitmap(data: ByteArray, rotate: Int = 90): Bitmap {
        val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
        val matrix = Matrix()
        matrix.reset()
        matrix.postRotate(rotate.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width,
                bitmap.height, matrix, true)
        bitmap.recycle()
        System.gc()
        return rotatedBitmap
    }

    /**
     * 将Bitmap图片旋转90度
     * @param bitmap
     * @param isRecycled
     * @return
     */
    fun rotateBitmap(bitmap: Bitmap, isRecycled: Boolean): Bitmap? {
        return rotateBitmap(bitmap, 90, isRecycled)
    }

    /**
     * 将Bitmap图片旋转一定角度
     * @param bitmap
     * @param rotate
     * @param isRecycled
     * @return
     */
    fun rotateBitmap(bitmap: Bitmap?, rotate: Int, isRecycled: Boolean): Bitmap? {
        var bitmap: Bitmap? = bitmap ?: return null
        val matrix = Matrix()
        matrix.reset()
        matrix.postRotate(rotate.toFloat())
        val rotatedBitmap = Bitmap.createBitmap(bitmap!!, 0, 0, bitmap.width,
                bitmap.height, matrix, true)
        if (!bitmap.isRecycled && isRecycled) {
            bitmap.recycle()
            bitmap = null
        }
        return rotatedBitmap
    }

    /**
     * 镜像翻转图片
     * @param bitmap
     * @param isRecycled
     * @return
     */
    fun flipBitmap(bitmap: Bitmap?, isRecycled: Boolean): Bitmap? {
        return flipBitmap(bitmap, true, false, isRecycled)
    }

    /**
     * 翻转图片
     * @param bitmap
     * @param flipX
     * @param flipY
     * @param isRecycled
     * @return
     */
    fun flipBitmap(bitmap: Bitmap?, flipX: Boolean, flipY: Boolean, isRecycled: Boolean): Bitmap? {
        var bitmap: Bitmap? = bitmap ?: return null
        val matrix = Matrix()
        matrix.setScale((if (flipX) -1 else 1).toFloat(), (if (flipY) -1 else 1).toFloat())
        matrix.postTranslate(bitmap!!.width.toFloat(), 0f)
        val result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width,
                bitmap.height, matrix, false)
        if (isRecycled && bitmap != null && !bitmap.isRecycled) {
            bitmap.recycle()
            bitmap = null
        }
        return result
    }

    /**
     * 裁剪
     * @param bitmap
     * @param x
     * @param y
     * @param width
     * @param height
     * @param isRecycled
     * @return
     */
    fun cropBitmap(bitmap: Bitmap?, x: Int, y: Int, width: Int, height: Int, isRecycled: Boolean): Bitmap? {
        var bitmap: Bitmap? = bitmap ?: return null

        val w = bitmap!!.width
        val h = bitmap.height
        // 保证裁剪区域
        if (w - x < width || h - y < height) {
            return null
        }
        val result = Bitmap.createBitmap(bitmap, x, y, width, height, null, false)
        if (!bitmap.isRecycled && isRecycled) {
            bitmap.recycle()
            bitmap = null
        }
        return result
    }

    /**
     * 获取Exif参数
     * @param path
     * @param bundle
     * @return
     */
    fun loadExifAttributes(path: String, bundle: Bundle): Boolean {
        val exifInterface: ExifInterface
        try {
            exifInterface = ExifInterface(path)
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

        for (tag in EXIF_TAGS) {
            bundle.putString(tag, exifInterface.getAttribute(tag))
        }
        return true
    }

    /**
     * 保存Exif属性
     * @param path
     * @param bundle
     * @return 是否保存成功
     */
    fun saveExifAttributes(path: String, bundle: Bundle): Boolean {
        val exif: ExifInterface
        try {
            exif = ExifInterface(path)
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

        for (tag in EXIF_TAGS) {
            if (bundle.containsKey(tag)) {
                exif.setAttribute(tag, bundle.getString(tag))
            }
        }
        try {
            exif.saveAttributes()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }

        return true
    }
}
/**
 * 从Buffer中创建Bitmap
 * @param buffer
 * @param width
 * @param height
 * @return
 */
/**
 * 保存图片
 * @param context
 * @param path
 * @param bitmap
 */
/**
 * 将Bitmap图片旋转90度
 * @param data
 * @return
 */
