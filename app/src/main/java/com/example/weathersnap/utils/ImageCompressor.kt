package com.example.weathersnap.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream

/**
 * Result returned by [ImageCompressor.compress].
 */
data class CompressionResult(
    /** The compressed image file saved to the cache directory. */
    val compressedFile: File,
    /** Size of the original captured image in bytes. */
    val originalSizeBytes: Long,
    /** Size of the compressed image in bytes. */
    val compressedSizeBytes: Long
) {
    /** Human-readable original size string (e.g., "3.45 MB"). */
    val originalSizeFormatted: String get() = formatBytes(originalSizeBytes)

    /** Human-readable compressed size string (e.g., "512.00 KB"). */
    val compressedSizeFormatted: String get() = formatBytes(compressedSizeBytes)

    /** Compression ratio as a percentage saved (e.g., "72% smaller"). */
    val savingsPercent: Int
        get() = if (originalSizeBytes > 0)
            ((1.0 - compressedSizeBytes.toDouble() / originalSizeBytes) * 100).toInt()
        else 0

    private fun formatBytes(bytes: Long): String = when {
        bytes >= 1_000_000 -> "%.2f MB".format(bytes / 1_000_000.0)
        bytes >= 1_000     -> "%.2f KB".format(bytes / 1_000.0)
        else               -> "$bytes B"
    }
}

/**
 * Utility object that compresses a JPEG image file using [BitmapFactory] + [Bitmap.compress].
 *
 * The compressor:
 *  1. Calculates the original file size before any modification.
 *  2. Down-samples the bitmap via [BitmapFactory.Options.inSampleSize] to avoid OOM errors
 *     on high-resolution CameraX captures.
 *  3. Re-encodes to JPEG at [DEFAULT_QUALITY] (60) into a new file in the cache directory.
 *  4. Returns a [CompressionResult] with both sizes so the UI can display them.
 */
object ImageCompressor {

    /** JPEG quality (0–100). 60 gives a good balance of size vs visual quality. */
    private const val DEFAULT_QUALITY = 60

    /** Max dimension (px) used when down-sampling large CameraX captures. */
    private const val MAX_DIMENSION = 1920

    /**
     * Compresses [originalFile] and returns a [CompressionResult].
     *
     * @param context   Used to resolve the cache directory.
     * @param originalFile  The raw image file from CameraX.
     * @param quality   JPEG quality (0–100). Defaults to [DEFAULT_QUALITY].
     * @throws IllegalArgumentException if the file cannot be decoded.
     */
    fun compress(
        context: Context,
        originalFile: File,
        quality: Int = DEFAULT_QUALITY
    ): CompressionResult {
        val originalSizeBytes = originalFile.length()

        // ── Step 1: Read image dimensions without loading pixels ──────────────
        val boundsOptions = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(originalFile.absolutePath, boundsOptions)

        // ── Step 2: Calculate a safe down-sample ratio ────────────────────────
        val sampleSize = calculateInSampleSize(boundsOptions, MAX_DIMENSION, MAX_DIMENSION)

        // ── Step 3: Decode the full bitmap using the sample size ──────────────
        val decodeOptions = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        val bitmap = BitmapFactory.decodeFile(originalFile.absolutePath, decodeOptions)
            ?: throw IllegalArgumentException(
                "ImageCompressor: unable to decode ${originalFile.absolutePath}"
            )

        // ── Step 4: Write compressed JPEG to a new cache file ─────────────────
        val compressedFile = File(
            context.cacheDir,
            "WEATHERSNAP_COMPRESSED_${System.currentTimeMillis()}.jpg"
        )
        FileOutputStream(compressedFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        }
        bitmap.recycle()

        return CompressionResult(
            compressedFile = compressedFile,
            originalSizeBytes = originalSizeBytes,
            compressedSizeBytes = compressedFile.length()
        )
    }

    /**
     * Computes the largest power-of-2 sample size such that both dimensions
     * stay >= the requested [reqWidth] × [reqHeight] target.
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (rawHeight, rawWidth) = options.outHeight to options.outWidth
        var inSampleSize = 1
        if (rawHeight > reqHeight || rawWidth > reqWidth) {
            val halfHeight = rawHeight / 2
            val halfWidth  = rawWidth  / 2
            while (halfHeight / inSampleSize >= reqHeight &&
                halfWidth  / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
