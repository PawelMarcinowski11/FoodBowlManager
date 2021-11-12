package marcinowski.pawel.foodmanager

sealed class NavigationItem(var route: Int, var icon: Int, var title: Int) {
    object Scan : NavigationItem(0, R.drawable.ic_baseline_document_scanner_24, R.string.Scan)
    object Home : NavigationItem(1, R.drawable.ic_baseline_fastfood_24, R.string.Home)
    object Settings : NavigationItem(2, R.drawable.ic_baseline_settings_24, R.string.Settings)
}