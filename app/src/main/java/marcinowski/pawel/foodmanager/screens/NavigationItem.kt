package marcinowski.pawel.foodmanager.screens

import marcinowski.pawel.foodmanager.R

/**
 * Navigation items, used for the navigation bar
 *
 */
sealed class NavigationItem(var route: Int, var icon: Int, var title: Int) {
    object Scan : NavigationItem(0, R.drawable.ic_baseline_document_scanner_24, R.string.label_scan)
    object Home : NavigationItem(1, R.drawable.ic_baseline_fastfood_24, R.string.label_home)
    object Settings : NavigationItem(2, R.drawable.ic_baseline_settings_24, R.string.label_settings)
}