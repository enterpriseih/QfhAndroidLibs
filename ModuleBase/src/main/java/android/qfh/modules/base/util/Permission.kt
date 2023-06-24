package android.qfh.modules.base.util

import android.Manifest
import android.os.Build
import android.qfh.modules.base.R
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX

/**
 * SD卡读取权限申请
 * @param block 权限请求成功时的回调
 */
fun FragmentActivity.runWithReadPermission(
    vararg permission: String = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
    block: () -> Unit
) {
    PermissionX.init(this)
        .permissions(*permission)
        .onExplainRequestReason { scope, deniedList ->
            scope.showRequestReasonDialog(
                deniedList,
                getString(R.string.base_text_common_permission_request),
                getString(R.string.base_text_common_permission_request_ok),
                getString(R.string.base_text_common_permission_request_cancel)
            )
        }
        .onForwardToSettings { scope, deniedList ->
            scope.showForwardToSettingsDialog(
                deniedList,
                getString(R.string.base_text_common_permission_not_allow),
                getString(R.string.base_text_common_permission_request_ok),
                getString(R.string.base_text_common_permission_request_cancel)
            )
        }
        .request { allGranted, _, _ ->
            if (allGranted) {
                block()
            }
        }
}

/**
 * 获取能读取 SD 卡的真实路径读取权限
 * 如果在 Android 11 及以上，会请求[Manifest.permission.MANAGE_EXTERNAL_STORAGE]权限，
 * 否则会获取 [Manifest.permission.WRITE_EXTERNAL_STORAGE] 权限
 */
fun FragmentActivity.runWithReadSDSpace(onGetPermission: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        PermissionX.init(this)
            .permissions(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
            .explainReasonBeforeRequest()
            .onExplainRequestReason { scope, deniedList, beforeRequest ->
                scope.showRequestReasonDialog(
                    deniedList,
                    if (beforeRequest) getString(R.string.base_text_request_permission_MANAGE_EXTERNAL_STORAGE) else getString(
                        R.string.base_text_common_permission_request
                    ),
                    getString(R.string.base_text_common_permission_request_ok),
                    getString(R.string.base_text_common_permission_request_cancel)
                )
            }.request { allGranted, _, _ ->
                if (allGranted) {
                    onGetPermission()
                }
            }
    } else {
        runWithReadPermission(block = onGetPermission)
    }
}