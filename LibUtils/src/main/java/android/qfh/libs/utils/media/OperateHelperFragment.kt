package android.qfh.libs.utils.media

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

class OperateHelperFragment : Fragment() {

    private lateinit var launchForChooseDirCallback: (Uri) -> Unit
    private val launchForChooseDir =
        registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
            it?.let { it1 -> launchForChooseDirCallback(it1) }
        }

    private lateinit var launchForChooseFileCallback: (Uri) -> Unit
    private val launchForChooseFile =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            it?.let { it1 -> launchForChooseFileCallback(it1) }
        }

    // 使用户选择文件夹
    fun chooseDir(onChoose: (Uri) -> Unit) {
        launchForChooseDirCallback = onChoose
        launchForChooseDir.launch(null)
    }

    fun chooseFile(onChoose: (Uri) -> Unit) {
        launchForChooseFileCallback = onChoose
        launchForChooseFile.launch(null)
    }
}