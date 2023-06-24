package android.qfh.apps.test.activity

import android.qfh.apps.test.databinding.ActivityMainBinding
import android.qfh.apps.test.viewmodel.MainViewModel
import android.qfh.modules.base.activity.BaseMvvmActivity
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseMvvmActivity<ActivityMainBinding,MainViewModel>() {

    override fun generalViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override val viewModel: MainViewModel by viewModels()
}