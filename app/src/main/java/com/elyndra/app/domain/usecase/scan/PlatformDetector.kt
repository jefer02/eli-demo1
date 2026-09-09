package com.elyndra.app.domain.usecase.scan

import com.elyndra.app.util.PlatformCatalog

/**
 * Resolves a scanned file to a platform id. Folder name is checked first since
 * it disambiguates shared extensions (iso/bin/cue/chd) that map to several
 * platforms; an unambiguous extension is used as a fallback.
 */
object PlatformDetector {
    fun detect(extension: String, parentFolderName: String): String? {
        PlatformCatalog.folderAliasToPlatformId[parentFolderName.trim().lowercase()]?.let { return it }
        val candidates = PlatformCatalog.extensionToPlatformIds[extension].orEmpty()
        return candidates.singleOrNull()
    }
}
