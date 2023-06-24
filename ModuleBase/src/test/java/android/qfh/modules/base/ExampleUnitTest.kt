package android.qfh.modules.base

import android.app.Service
import android.content.ContentValues
import android.content.Intent
import android.os.IBinder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import org.junit.Test
import kotlin.math.roundToInt

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        11.5.roundToInt()
    }
    class A :Service() {
        override fun onBind(intent: Intent?): IBinder? {
            TODO("Not yet implemented")
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            return super.onStartCommand(intent, flags, startId)
        }
    }
}