package com.power.realtimepricetracker.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.power.realtimepricetracker.ui.detail.DetailScreen
import com.power.realtimepricetracker.ui.detail.DetailViewModel
import com.power.realtimepricetracker.ui.feed.FeedScreen
import com.power.realtimepricetracker.ui.feed.FeedViewModel

object Routes {
    const val FEED = "feed"
    const val SYMBOL_DETAILS = "symbol_details/{symbol}"

    fun detailRoute(symbol: String) = "symbol_details/$symbol"
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.FEED
    ) {
        composable(route = Routes.FEED) {
            val vm: FeedViewModel = hiltViewModel()
            FeedScreen(
                viewModel = vm,
                onSymbolClick = { symbol ->
                    navController.navigate(Routes.detailRoute(symbol))
                }
            )
        }

        composable(
            route = Routes.SYMBOL_DETAILS,
            arguments = listOf(
                navArgument("symbol") { type = NavType.StringType }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "stocks://symbol/{symbol}" }
            )
        ) {
            val vm: DetailViewModel = hiltViewModel()
            DetailScreen(
                viewModel = vm,
                onBack = { navController.popBackStack() }
            )
        }
    }
}