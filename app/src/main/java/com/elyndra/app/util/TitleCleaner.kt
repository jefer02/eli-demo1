package com.elyndra.app.util

/** Turns a raw ROM filename into a display-friendly title, e.g.
 *  "Super Mario World (USA) (Rev 1).sfc" -> "Super Mario World". */
object TitleCleaner {
    private val bracketedTagRegex = Regex("""[\(\[][^)\]]*[)\]]""")
    private val whitespaceRegex = Regex("""\s+""")

    fun clean(fileName: String): String {
        val withoutExtension = fileName.substringBeforeLast('.')
        val withoutTags = bracketedTagRegex.replace(withoutExtension, "")
        return withoutTags
            .replace('_', ' ')
            .replace(whitespaceRegex, " ")
            .trim()
            .ifBlank { withoutExtension }
    }
}
