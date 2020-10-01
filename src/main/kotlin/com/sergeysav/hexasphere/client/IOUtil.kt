package com.sergeysav.hexasphere.client

import com.sergeysav.hexasphere.common.hexasphere.Hexasphere
import mu.KotlinLogging
import org.lwjgl.system.MemoryUtil
import java.io.BufferedInputStream
import java.io.IOException
import java.net.URL
import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Paths

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

    fun getResourcePath(resource: String): String {
        val path = Paths.get(resource)
        return if (Files.isReadable(path)) {
            path.toAbsolutePath().toString()
        } else {
            Hexasphere::class.java.getResource(resource).path
        }
    }

    fun doesResourceExist(resource: String): Boolean = try {
        getResourcePath(resource)
        true
    } catch (e: IllegalStateException) {
        false
    }
}