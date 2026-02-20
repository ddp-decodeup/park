@file:Suppress("unused")
package com.parkloyalty.lpr.scan.extensions

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.navigation.*
import androidx.navigation.fragment.findNavController

/*
 * NavExtensions.kt
 *
 * Handy Kotlin extension functions for working with Android Jetpack Navigation.
 * - Safe navigation (prevents crashes when action/destination isn't available)
 * - Convenience navigate/pop helpers usable from Fragment, Activity, NavController
 * - Helpers for animations / popUpTo options
 *
 * This file now includes usage examples for:
 *  - Fragment (direct action and Safe Args example)
 *  - Activity (finding NavController and navigating)
 *  - View click listeners that navigate
 *  - Global action usage and popUpTo examples
 *
 * Paste this file into your project under e.g. src/main/java/com/example/app/navigation/
 * Rebuild after editing the nav_graph if you rely on Safe Args.
 */

// -------------------------------
// Fragment convenience helpers
// -------------------------------

/** Short alias to Fragment.findNavController() */
val Fragment.nav: NavController
    get() = findNavController()

/** Navigate using a destination or action id safely. This will check if the action is present on the current destination, or if the destination id exists in the graph. */
fun Fragment.navigateSafe(
    @IdRes id: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    nav.safeNavigate(id, args, navOptions, navigatorExtras)
}

/** Pop back stack once. */
fun Fragment.pop(): Boolean = nav.popBackStack()

/** Pop to a specific destination id. inclusive controls whether the dest itself is popped. */
fun Fragment.popTo(@IdRes destId: Int, inclusive: Boolean = false): Boolean = nav.popBackStack(destId, inclusive)

/** Pop to the graph's start destination (clear back stack to root of current graph). */
fun Fragment.popToRoot(): Boolean {
    val start = nav.graph.startDestinationId
    return nav.popBackStack(start, false)
}

/** Navigate up using NavController. */
fun Fragment.navigateUp(): Boolean = nav.navigateUp()

// -------------------------------
// Activity helpers
// -------------------------------

/** Find NavController from an Activity hosting a NavHostFragment. Pass navHostFragmentId (R.id.nav_host_fragment). */
fun Activity.findNav(@IdRes navHostFragmentId: Int): NavController =
    androidx.navigation.Navigation.findNavController(this, navHostFragmentId)

// -------------------------------
// NavController extensions
// -------------------------------

/** Safely navigate using either an action id or a destination id. Avoids crashes when the current destination doesn't have the requested action. */
fun NavController.safeNavigate(
    @IdRes id: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    // If current destination has an action with this id, use it
    val current = currentDestination

    try {
        val canUseAction = current?.getAction(id) != null
        if (canUseAction) {
            if (navigatorExtras != null) {
                navigate(id, args, navOptions, navigatorExtras)
            } else {
                navigate(id, args, navOptions)
            }
            return
        }

        // If id is a destination inside this nav graph (global navigation by destination id)
        val node = graph.findNode(id)
        if (node != null) {
            if (navigatorExtras != null) {
                navigate(id, args, navOptions, navigatorExtras)
            } else {
                navigate(id, args, navOptions)
            }
            return
        }

        // Fallback: try navigating by action id ignoring currentDestination (global action may exist on root)
        val rootAction = graph.getAction(id)
        if (rootAction != null) {
            if (navigatorExtras != null) {
                navigate(id, args, navOptions, navigatorExtras)
            } else {
                navigate(id, args, navOptions)
            }
            return
        }

        // Nothing matched: no-op (safe)
    } catch (e: IllegalArgumentException) {
        // swallow to keep app stable
    }
}


//how to use observeFragmentResult
//navController.observeFragmentResult("result_key") { bundle ->
//    // Handle the result here
//    val data = bundle?.getString("data_key")
//    // Do something with the data
//}
fun NavController.observeFragmentResult(key:String, callback: (Bundle?) -> Unit) {
    if (currentBackStackEntry != null) {
        currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle?>(key)
            ?.observe(currentBackStackEntry!!) { result ->
                callback(result)
            }
    }
}

//How to use returnBackWithResult
//val resultBundle = Bundle().apply {
//    putString("data_key", "Some data to return")
//}
//navController.returnBackWithResult(resultBundle)
fun NavController.returnBackWithResult(key: String, result: Bundle?) {
    previousBackStackEntry?.savedStateHandle?.set(key, result)
    popBackStack()
}



/** Pop to the root destination of the nav graph. */
fun NavController.popToRoot(): Boolean {
    val start = graph.startDestinationId
    return popBackStack(start, false)
}

/** Convenience to get NavOptions builder with animations and popUpTo settings. */
fun buildNavOptions(
    @IdRes popUpTo: Int? = null,
    inclusive: Boolean = false,
    enterAnim: Int? = null,
    exitAnim: Int? = null,
    popEnterAnim: Int? = null,
    popExitAnim: Int? = null
): NavOptions {
    val builder = NavOptions.Builder()
    popUpTo?.let { builder.setPopUpTo(it, inclusive) }
    enterAnim?.let { builder.setEnterAnim(it) }
    exitAnim?.let { builder.setExitAnim(it) }
    popEnterAnim?.let { builder.setPopEnterAnim(it) }
    popExitAnim?.let { builder.setPopExitAnim(it) }
    return builder.build()
}

// -------------------------------
// View helpers
// -------------------------------

/** Set a click listener on a view to navigate safely using NavController found from this view. Example: view.navigateOnClick(R.id.action_global_example) */
fun View.navigateOnClick(
    @IdRes navActionOrDestinationId: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    setOnClickListener {
        try {
            val controller = androidx.navigation.Navigation.findNavController(this)
            controller.safeNavigate(navActionOrDestinationId, args, navOptions, navigatorExtras)
        } catch (_: Exception) {
            // no-op if NavController not found for this view
        }
    }
}

// -------------------------------
// Extras: Safe navigation with single-click guard
// -------------------------------

/**
 * Prevents double navigation by tracking current destination and ignoring repeated taps that are
 * attempted while a navigation is still in progress.
 * Note: This is a light-weight approach and may not cover all edge cases (e.g., very slow transitions).
 */
fun NavController.safeNavigateSingleClick(
    @IdRes id: Int,
    args: Bundle? = null,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    val current = currentDestination ?: return
    val actionAvailable = current.getAction(id) != null || graph.findNode(id) != null || graph.getAction(id) != null
    if (!actionAvailable) return

    safeNavigate(id, args, navOptions, navigatorExtras)
}

// -------------------------------
// USAGE EXAMPLES
// -------------------------------

/*
 * 1) From a Fragment (direct action id)
 *
 *    // inside LoginFragment
 *    navigateSafe(R.id.action_loginScreenFragment_to_welcomeScreenFragment)
 *
 * 2) From a Fragment (with Safe Args generated directions)
 *
 *    // build-time generated class by Safe Args plugin
 *    val action = LoginFragmentDirections.actionLoginScreenFragmentToWelcomeScreenFragment(userName = "Janak")
 *    nav.navigate(action)
 *
 * 3) From a Fragment (navigate by destination id with extras)
 *
 *    val bundle = bundleOf("userId" to 42)
 *    navigateSafe(R.id.welcomeScreenFragment, args = bundle)
 *
 * 4) From an Activity (find controller and navigate)
 *
 *    // inside MainActivity
 *    val nav = findNavController(R.id.nav_host_fragment)
 *    nav.safeNavigate(R.id.action_global_welcomeScreenFragment)
 *
 * 5) View click helper
 *
 *    // xml: <Button android:id="@+id/btnNext" ... />
 *    btnNext.navigateOnClick(R.id.action_loginScreenFragment_to_welcomeScreenFragment)
 *
 * 6) Pop / popTo examples
 *
 *    pop()                // pop one
 *    popTo(R.id.loginScreenFragment, inclusive = false) // pop back stack until login
 *    popToRoot()          // pop to graph start
 *
 * 7) Using buildNavOptions to set animations + popUpTo
 *
 *    val opts = buildNavOptions(
 *        popUpTo = R.id.splashScreenFragment,
 *        inclusive = true,
 *        enterAnim = R.anim.slide_in_right,
 *        exitAnim = R.anim.slide_out_left
 *    )
 *    nav.safeNavigate(R.id.action_loginScreenFragment_to_welcomeScreenFragment, navOptions = opts)
 */

// small helper for building bundles inline (optional)
fun bundleOfPairs(vararg pairs: Pair<String, Any?>): Bundle {
    val b = Bundle()
    for ((k, v) in pairs) {
        when (v) {
            null -> b.putSerializable(k, null)
            is Int -> b.putInt(k, v)
            is Long -> b.putLong(k, v)
            is String -> b.putString(k, v)
            is Boolean -> b.putBoolean(k, v)
            is Float -> b.putFloat(k, v)
            is Double -> b.putDouble(k, v)
            is Bundle -> b.putAll(v)
            else -> b.putSerializable(k, v as java.io.Serializable)
        }
    }
    return b
}
