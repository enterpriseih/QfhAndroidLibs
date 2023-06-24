package android.qfh.modules.base.fragment

import android.qfh.modules.base.viewmodel.BaseViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseMvvmFragment<VB : ViewBinding, VM : BaseViewModel> : BaseFragment<VB>() {

    protected abstract val viewModel: VM
}