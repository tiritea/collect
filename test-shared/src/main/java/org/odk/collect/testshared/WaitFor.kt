package org.odk.collect.testshared

import junit.framework.AssertionFailedError
import java.util.concurrent.Callable

object WaitFor {

    @JvmStatic
    fun <T> waitFor(callable: Callable<T>): T {
        var counter = 0
        var failure: Throwable? = null

        // Try 20 times/for 5 seconds
        while (counter < 20) {
            failure = try {
                return callable.call()
            } catch (throwable: Exception) {
                throwable
            } catch (throwable: AssertionFailedError) {
                throwable
            } catch (throwable: AssertionError) {
                throwable
            }

            wait250ms()
            counter++
        }

        throw failure!!
    }

    @JvmStatic
    fun wait250ms() {
        try {
            Thread.sleep(250)
        } catch (ignored: InterruptedException) {
            // ignored
        }
    }

    @JvmStatic
    @JvmOverloads
    fun tryAgainOnFail(maxTimes: Int = 2, action: Runnable) {
        var failure: Throwable? = null
        for (i in 0 until maxTimes) {
            try {
                action.run()
                return
            } catch (e: Throwable) {
                failure = e
                wait250ms()
            }
        }

        throw RuntimeException("tryAgainOnFail failed", failure)
    }
}
