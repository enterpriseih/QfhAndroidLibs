package android.qfh.apps.test.start

import android.content.Context
import android.qfh.modules.base.start.ModuleBaseInitializer
import androidx.startup.Initializer

class TestInitializer : Initializer<TestInitializer> {
    override fun create(context: Context): TestInitializer {
        return TestInitializer()
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf(ModuleBaseInitializer::class.java)
    }
}