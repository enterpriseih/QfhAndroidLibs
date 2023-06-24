package android.qfh.libs.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.qfh.libs.utils.CrashUtil.processThrowable
import android.qfh.libs.utils.CrashUtil.reStartApp
import android.qfh.libs.utils.CrashUtil.setCrashHandlerEnable
import android.qfh.libs.utils.CrashUtil.setOnCatchThrowableAction
import java.io.PrintWriter
import java.io.StringWriter

/**
 * 全局异常捕捉
 *
 * 通过[setCrashHandlerEnable]函数设置是否全局捕获异常，如果参数为false，则相当于不设置异常捕获，当程序抛出异常时就会正常崩溃。
 * 如果设置为true，则开启了异常捕获，此时当程序抛出异常时，会交给我们自己处理，通过[setOnCatchThrowableAction]方法设置程序异常时的
 * 处理逻辑。
 * [setCrashHandlerEnable]和[setOnCatchThrowableAction]是一起用的。
 *
 * @suppress 当[setCrashHandlerEnable]函数设置为 true 时，必须调用[setOnCatchThrowableAction]处理,否则当程序出现未捕获的异常时会
 * 直接卡死（因为我们接管了处理，但是没有设置处理的逻辑）。此时第一步可以根据传递过来的Throwable对象获取异常信息，然后将该错误信息写入本地文
 * 件或者数据库内。接下来第二步的方案有两种，一种是不处理该异常，让其继续崩溃退出[processThrowable];另一种为调用[reStartApp]重启并跳往指定的Activity。
 *
 * @suppress [reStartApp]方法内部逻辑为跳往指定的Activity，然后销毁当前进程。需要注意的是进程被销毁了，但是Activity的栈记录并没有被销毁(注意是栈的记录而不是栈，
 * 当前进程销毁时，栈内属于该进程的Activity也会被直接销毁，当进程重建时栈内的Activity全部被创建)，所以就会造成如下现象：
 * 1、主界面在A界面，跳往B界面，B界面崩溃，在崩溃捕捉中调用[reStartApp]跳往B界面,此时的具体逻辑为:当前栈为A-B，然后B崩溃，进程销毁，进程重建，原先栈内的结构A被重建(B因为崩溃不会被重建)，然后创建B并添加到当前栈顶)
 * 2、主界面在A界面，跳往B界面，B界面崩溃，在崩溃捕捉中调用[reStartApp]跳往A界面,此时的具体逻辑为:当前栈为A-B，然后B崩溃，进程销毁，进程重建，原先栈内的结构A被重建(B因为崩溃不会被重建)，然后创建A并添加到当前栈顶,
 * 此时如果A界面的launchMode指定为默认，那么此时栈内有2个界面A，如果指定为singleTask,栈内则只保留一个界面A).
 * @suppress 1、按照官方文档所说，动态设置 launchMode 和清单文件内设置 launchMode 同时存在时，以动态设置为主，但此处测试发现动态设置的 launchMode 无法生效，必须以静态注册的方式才能生效)
 * @suppress 2、最好捕获到异常后调用[processThrowable]让程序崩溃，如果必须要重启，重启的界面的 launchMode 需要静态设置为 singleTask，防止重复实例化。
 */
object CrashUtil {
    private val defaultUncaughtExceptionHandler: Thread.UncaughtExceptionHandler =
        Thread.getDefaultUncaughtExceptionHandler()
            ?: throw NullPointerException("the defaultUncaughtExceptionHandler is null")

    //当错误被捕捉时触发的回调
    private var mOnCatchThrowableAction: ((Thread, Throwable) -> Unit)? = null

    /**
     * 设置是否启用异常捕捉
     * @param enabled 是否启用
     * @suppress 设置开启后，必须调用[setOnCatchThrowableAction]函数设置异常处理逻辑,且处理逻辑最后只能重启或者关闭程序(否则会出现卡死现象)
     */
    fun setCrashHandlerEnable(enabled: Boolean) {
        if (enabled) {
            Thread.setDefaultUncaughtExceptionHandler { thread, e ->
                mOnCatchThrowableAction?.let { it(thread, e) }
            }
        } else {
            Thread.setDefaultUncaughtExceptionHandler(defaultUncaughtExceptionHandler)
        }
    }

    /**
     * 设置当捕获到异常时对Throwable的处理逻辑
     * @param action 对捕捉到的Throwable的处理代码块
     * @suppress 只有[setCrashHandlerEnable]为true后才会生效
     */
    fun setOnCatchThrowableAction(action: (Thread, Throwable) -> Unit) {
        mOnCatchThrowableAction = action
    }

    /**
     * 重启App并跳往泛型指定的Activity,可以携带参数
     */
    inline fun <reified C : Activity> reStartApp(context: Context, bundle: Bundle? = null) {
        val intent = Intent(context, C::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        bundle?.let {
            intent.putExtras(it)
        }
        context.startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    /**
     * 继续将此异常抛出，让程序崩溃
     * 可以在[setOnCatchThrowableAction]捕获到异常做一些额外操作后(比如讲错误日志写入文件)，然后在调用此方法让程序
     * 恢复原有的轨迹上(抛出异常，程序崩溃)
     */
    fun processThrowable(thread: Thread, throwable: Throwable) {
        defaultUncaughtExceptionHandler.uncaughtException(thread, throwable)
    }
}

/**
 * 根据错误获取其完整调用栈信息
 */
fun Throwable.getCompleteInfo(): String {
    val stringWriter = StringWriter()
    val printWriter = PrintWriter(stringWriter)
    this.printStackTrace(printWriter)
    printWriter.close()
    return stringWriter.toString()
}
