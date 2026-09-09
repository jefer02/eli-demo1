package com.elyndra.app.ui

import android.content.res.Configuration
import android.os.LocaleList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import com.elyndra.app.domain.model.AppLanguage
import java.util.Locale

/**
 * Applies the user's language choice to everything drawn inside [content].
 *
 * Rather than pulling in appcompat just for per-app locales, this overrides the
 * two things `stringResource` reads - [LocalContext] (for its Resources) and
 * [LocalConfiguration] (which is what actually triggers recomposition when the
 * choice changes). [AppLanguage.SYSTEM] passes the host context straight
 * through, so "follow the device" needs no special-casing downstream.
 */
@Composable
fun AppLocaleProvider(language: AppLanguage, content: @Composable () -> Unit) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    if (language == AppLanguage.SYSTEM) {
        content()
        return
    }

    val localized = remember(language, configuration) {
        val locale = Locale.forLanguageTag(language.tag)
        val config = Configuration(configuration).apply {
            setLocales(LocaleList(locale))
            setLayoutDirection(locale)
        }
        config to context.createConfigurationContext(config)
    }

    CompositionLocalProvider(
        LocalConfiguration provides localized.first,
        LocalContext provides localized.second,
        content = content,
    )
}
