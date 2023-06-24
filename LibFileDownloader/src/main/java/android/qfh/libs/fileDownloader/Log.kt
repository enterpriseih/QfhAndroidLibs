package android.qfh.libs.fileDownloader

import android.util.Log

// ============ debug ===================//

private const val TAG = "FileDownloadManager"
private const val DEBUG = false

internal fun logD(msg: String) {
    if (DEBUG) {
        Log.d(TAG, msg)
    }
}

internal fun logW(msg: String) {
    if (DEBUG) {
        Log.w(TAG, msg)
    }
}

// ============ debug ===================//