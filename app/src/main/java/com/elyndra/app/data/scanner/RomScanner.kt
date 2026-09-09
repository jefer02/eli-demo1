package com.elyndra.app.data.scanner

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import com.elyndra.app.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** A ROM file found while walking a SAF tree, not yet matched to a platform. */
data class ScannedFile(
    val documentUri: Uri,
    val fileName: String,
    val extension: String,
    val sizeBytes: Long,
    val parentFolderName: String,
)

/**
 * Recursively walks a SAF (`content://`) tree looking for files whose extension
 * is a known ROM/disc-image type. Uses direct [DocumentsContract] queries rather
 * than [androidx.documentfile.provider.DocumentFile] to avoid the extra per-file
 * IPC round-trip that API makes, which matters once a library reaches thousands
 * of files.
 */
class RomScanner @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    suspend fun scanTree(treeUri: Uri): List<ScannedFile> = withContext(Dispatchers.IO) {
        val results = mutableListOf<ScannedFile>()
        val rootDocumentId = DocumentsContract.getTreeDocumentId(treeUri)
        scanDocument(treeUri, rootDocumentId, parentFolderName = "", results)
        results
    }

    private fun scanDocument(
        treeUri: Uri,
        parentDocumentId: String,
        parentFolderName: String,
        results: MutableList<ScannedFile>,
    ) {
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, parentDocumentId)
        val projection = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_SIZE,
        )

        // Buffer rows locally: recursing while a Cursor from the same provider is
        // still open is unreliable across DocumentsProvider implementations.
        data class Child(val documentId: String, val name: String, val mimeType: String?, val size: Long)
        val children = mutableListOf<Child>()

        runCatching {
            context.contentResolver.query(childrenUri, projection, null, null, null)?.use { cursor ->
                val idIdx = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DOCUMENT_ID)
                val nameIdx = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
                val mimeIdx = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_MIME_TYPE)
                val sizeIdx = cursor.getColumnIndexOrThrow(DocumentsContract.Document.COLUMN_SIZE)
                while (cursor.moveToNext()) {
                    val name = cursor.getString(nameIdx) ?: continue
                    children += Child(
                        documentId = cursor.getString(idIdx),
                        name = name,
                        mimeType = cursor.getString(mimeIdx),
                        size = cursor.getLong(sizeIdx),
                    )
                }
            }
        }

        for (child in children) {
            if (child.mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
                scanDocument(treeUri, child.documentId, child.name, results)
            } else {
                val extension = child.name.substringAfterLast('.', missingDelimiterValue = "").lowercase()
                if (extension.isNotEmpty() && extension in Constants.KNOWN_ROM_EXTENSIONS) {
                    results += ScannedFile(
                        documentUri = DocumentsContract.buildDocumentUriUsingTree(treeUri, child.documentId),
                        fileName = child.name,
                        extension = extension,
                        sizeBytes = child.size,
                        parentFolderName = parentFolderName,
                    )
                }
            }
        }
    }
}
