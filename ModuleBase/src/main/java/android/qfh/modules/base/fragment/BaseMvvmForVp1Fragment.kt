package android.qfh.modules.base.fragment

import android.qfh.modules.base.viewmodel.BaseViewModel
import android.widget.Toast
import androidx.viewbinding.ViewBinding

abstract class BaseMvvmForVp1Fragment<VB : ViewBinding, VM : BaseViewModel> :
    BaseForVp1Fragment<VB>() {

    protected abstract val viewModel: VM

    override fun initViews() {
        super.initViews()
        viewModel.errorInfo.observe(this) {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
}