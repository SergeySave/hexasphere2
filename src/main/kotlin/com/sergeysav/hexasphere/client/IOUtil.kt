package com.sergeysav.hexasphere.client

import mu.KotlinLogging
import org.lwjgl.system.MemoryUtil
import java.io.BufferedInputStream
import java.io.IOException
import java.net.URL
import java.nio.ByteBuffer

object IOUtil {

    private val logger = KotlinLogging.logger {  }

    @Throws(IOException::class)
    fun loadResource(resourceName: String): ByteBuffer {
        val url: URL = IOUtil::class.java.getResource(resourceName) ?: throw IOException("Resource not found: $resourceName")
        logger.trace { "Loading Resource: $resourceName" }
        val resourceSize: Int = url.openConnection().contentLength
        val resource: ByteBuffer = MemoryUtil.memAlloc(resourceSize)
        BufferedInputStream(url.openStream()).use { bis ->
            var b: Int
            do {
                b = bis.read()
                if (b != -1) {
                    resource.put(b.toByte())
                }
            } while (b != -1)
        }
        resource.flip()
        return resource
    }
}