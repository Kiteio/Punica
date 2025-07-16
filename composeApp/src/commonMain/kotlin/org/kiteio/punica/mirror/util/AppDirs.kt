package org.kiteio.punica.mirror.util

/**
 * App 目录。
 */
expect object AppDirs {
    /**
     * 文件目录。
     */
    fun filesDir(path: String): String

    /**
     * 下载目录。
     */
    fun downloadsDir(path: String): String

    /**
     * 缓存目录。
     */
    fun cacheDir(path: String): String
}