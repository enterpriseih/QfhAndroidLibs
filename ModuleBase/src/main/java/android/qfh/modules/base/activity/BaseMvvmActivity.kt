package android.qfh.modules.base.activity

import android.qfh.modules.base.viewmodel.BaseViewModel
import androidx.viewbinding.ViewBinding

abstract class BaseMvvmActivity<VB : ViewBinding, VM : BaseViewModel> : BaseActivity<VB>() {

    protected abstract val viewModel: VM
}