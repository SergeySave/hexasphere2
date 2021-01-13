package com.sergeysav.hexasphere.client.localization

import com.sergeysav.hexasphere.client.IOUtil
import java.nio.file.FileSystems
import java.util.Scanner

object L10n {

    private val mapping = mutableMapOf<String, String>()

    fun load(locale: String) {
        mapping.clear()
        val locFilePath = FileSystems.getDefault().getPath(IOUtil.getResourcePath("/localization/$locale.loc"))
        Scanner(locFilePath).use { scan ->
            while (scan.hasNextLine()) {
                val line = scan.nextLine()
                if (line.startsWith("#")) continue

                val splitPoint = line.indexOf('=')
                if (splitPoint < 0 || splitPoint >= line.length) continue

                val key = line.substring(0, splitPoint)
                val value = line.substring(splitPoint + 1)
                mapping[key] = value
            }
        }
    }

    fun localize(string: String) = mapping[string] ?: string

    operator fun get(string: String) = localize(string)
    operator fun invoke(string: String) = localize(string)
}