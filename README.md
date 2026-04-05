# Real-Time Price Tracker

A simple Android app that tracks live stock prices using WebSocket. Prices update every 2 seconds and the UI reflects changes with a green/red flash animation. Built with Jetpack Compose, MVVM, and Hilt.

---

## What it does

- Connects to a WebSocket server and streams price updates for 25 stocks
- Feed screen shows all prices in real time, sorted by current price
- Tap any stock to open a detail screen with company info and price history
- Flash animation highlights price movement (green = up, red = down)
- Connection status shown in the toolbar (Live / Connecting / Off)
- Supports deep links — `stocks://symbol/AAPL` opens the detail screen directly
- Light & dark theme — follows system theme automatically

---

## Tech used

- **Jetpack Compose** + Material 3 for UI
- **MVVM** architecture with ViewModel and StateFlow
- **Hilt** for dependency injection
- **OkHttp** WebSocket for real-time connection
- **Kotlin Coroutines + Flow** for async data handling
- **Navigation Compose** with deep link support
- **MockK** + **kotlinx-coroutines-test** for unit tests

---

## Project structure

```
data/
  model/         — PriceRecord, PriceUpdate, StockSymbol
  websocket/     — WebSocketManager, PriceRepository

di/              — Hilt AppModule

navigation/      — AppNavigation (NavHost + routes)

ui/
  feed/          — FeedScreen, FeedViewModel, PriceRowItem
  detail/        — DetailScreen, DetailViewModel
  theme/         — Colors, typography, theme
```

---

## How to run

1. Open in Android Studio
2. Connect a device or start an emulator (API 24+)
3. Hit Run

The app needs internet access to connect to the WebSocket server (`wss://ws.postman-echo.com/raw`).

---

## Architecture notes

The data flow goes: `WebSocketManager` → `PriceRepository` → `ViewModel` → `Compose UI`.

`WebSocketManager` is a singleton (via Hilt) so both `FeedViewModel` and `DetailViewModel` share the same connection and receive the same price stream. Price updates flow through a `SharedFlow` since multiple ViewModels need to collect simultaneously. Connection state uses `StateFlow` so any new collector always gets the current value immediately.

One thing worth mentioning — when navigating to the detail screen, it seeds the initial price from the last known value in the manager instead of waiting for the next WebSocket tick. Without this, the detail page would show a loading spinner for up to 4 seconds before the first update arrives for that specific symbol.

---

## Running tests

```bash
./gradlew testDebugUnitTest
```

Tests are pure JVM — no emulator needed.

- `PriceFeedTest` — tests the model layer: StockSymbol list, direction logic, flashTrigger, prevPrice
- `FeedViewModelTest` — tests start/stop tracking, price updates, sort order, connection state
- `DetailViewModelTest` — tests symbol filtering, seed price, direction changes across ticks

---

## Deep link

```bash
adb shell am start -W -a android.intent.action.VIEW \
  -d "stocks://symbol/AAPL" com.power.realtimepricetracker
```

Works with any of the 25 supported tickers.
