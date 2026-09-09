package com.elyndra.app.util

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.zip.CRC32
import javax.inject.Inject

/** Streams a SAF document through CRC32 without loading it into memory - needed
 *  because scraper matching is far more reliable with a hash than a filename alone. */
class Crc32Calculator @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun compute(uri: Uri): String? = withContext(Dispatchers.IO) {
        runCatching {
            val crc = CRC32()
            val buffer = ByteArray(8 * 1024)
            context.contentResolver.openInputStream(uri)?.use { input ->
                while (true) {
                    val read = input.read(buffer)
                    if (read < 0) break
                    crc.update(buffer, 0, read)
                }
            } ?: return@runCatching null
            crc.value.toString(16).uppercase().padStart(8, '0')
        }.getOrNull()
    }
}
