package marcinowski.pawel.foodmanager

sealed class NavigationItem(var route: String, var icon: Int, var title: Int) {
    object Scan : NavigationItem("scan", R.drawable.ic_baseline_document_scanner_24, R.string.Scan)
    object Home : NavigationItem("home", R.drawable.ic_baseline_fastfood_24, R.string.Home)
    object Settings : NavigationItem("setting", R.drawable.ic_baseline_settings_24, R.string.Settings)
}