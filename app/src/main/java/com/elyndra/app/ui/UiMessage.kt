package com.elyndra.app.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.res.stringResource

/**
 * A message a ViewModel wants shown, kept as a resource id + args instead of a
 * finished String.
 *
 * ViewModels have no Context, and the one they could reach (the application's)
 * follows the *device* locale - which is wrong the moment the user picks a
 * language in Settings. Resolving in the composable instead means the message
 * is formatted with the locale [AppLocaleProvider] installed.
 */
data class UiMessage(@StringRes val resId: Int, val args: List<Any> = emptyList()) {
    constructor(@StringRes resId: Int, vararg args: Any) : this(resId, args.toList())
}

@Composable
@ReadOnlyComposable
fun UiMessage.resolve(): String = stringResource(resId, *args.toTypedArray())
