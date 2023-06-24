package android.qfh.modules.base.fragment


import android.qfh.modules.base.viewmodel.BaseViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseMvvmDialogFragment<VB : ViewBinding, VM : BaseViewModel> :
    BaseDialogFragment<VB>() {

    protected abstract val viewModel: VM

}