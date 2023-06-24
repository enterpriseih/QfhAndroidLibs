package android.qfh.modules.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    private var _binding: VB? = null

    @Suppress("MemberVisibilityCanBePrivate")
    protected val binding get() = _binding!!

    private var firstLoadData = true

    protected abstract fun generalViewBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = generalViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firstLoadData = true
        initViews()
        initListener()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        if (firstLoadData) {
            firstLoadData = false
            initData()
        }

    }

    protected open fun initViews() {

    }

    protected open fun initListener() {

    }

    protected open fun initData() {

    }

    protected fun <D> Flow<D>.asLiveDataFromFragment(actions: (D) -> Unit) {
        distinctUntilChanged().asLiveData(viewLifecycleOwner.lifecycleScope.coroutineContext)
            .observe(viewLifecycleOwner, actions)
    }
}