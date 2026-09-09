package com.elyndra.app.util

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.UUID

/**
 * Copies a user-picked image (from the system photo picker) into app-private
 * storage. Picker grants are session-scoped, unlike a persisted SAF tree
 * permission, so the source Uri can't be relied on to still be readable the
 * next time the app starts - the local copy is what gets stored on the [Game].
 */
object LocalImageStore {

    /** [kind] namespaces the copy, e.g. "covers" or "backgrounds". Returns a `file://` path, or null on failure. */
    suspend fun importImage(context: Context, sourceUri: Uri, kind: String): String? =
        withContext(Dispatchers.IO) {
            val targetDir = File(context.filesDir, "images/$kind").apply { mkdirs() }
            val targetFile = File(targetDir, "${UUID.randomUUID()}.jpg")
            try {
                val input = context.contentResolver.openInputStream(sourceUri) ?: return@withContext null
                input.use { stream -> targetFile.outputStream().use { output -> stream.copyTo(output) } }
                Uri.fromFile(targetFile).toString()
            } catch (e: IOException) {
                null
            }
        }
}
