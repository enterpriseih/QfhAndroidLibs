package android.qfh.modules.base.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

open class BaseViewModel : ViewModel() {

    // 比较严重的错误
    val errorInfo = MutableLiveData<String>()

    protected fun runWithException(
        onSendLoading: () -> Unit = {},
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {},
        action: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                onSendLoading()
                action()
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "")
            }
        }
    }
}