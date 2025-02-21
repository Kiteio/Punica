package org.kiteio.punica

import android.content.Context
import androidx.startup.Initializer

/**
 * 全局 [Context]。
 *
 * [参阅](https://funkymuse.dev/posts/create-data-store-kmp/)。
 */
internal lateinit var applicationContext: Context
    private set


class ContextInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        applicationContext = context.applicationContext
    }


    override fun dependencies() = emptyList<Class<out Initializer<*>>>()
}