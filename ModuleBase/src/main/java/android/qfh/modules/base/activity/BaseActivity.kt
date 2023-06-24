package android.qfh.modules.base.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

/**
 * 每个 module 继承时建议先自定义一个 module 级别的 baseActivity，不要直接使用此 activity
 */
abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null

    @Suppress("MemberVisibilityCanBePrivate")
    protected val binding: VB
        get() = _binding!!

    protected abstract fun generalViewBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = generalViewBinding()
        setContentView(binding.root)
        initViews()
        initListener()
        initData()
    }


    protected open fun initViews() {

    }

    protected open fun initListener() {

    }

    protected open fun initData() {

    }

}