package com.parkloyalty.lpr.scan.util.app_performance

import android.os.Handler
import android.os.Looper
import android.util.Log


class PerformanceMonitor {
    private val tag = "PerformanceMonitor"
    private var startTime = 0L
    private val memoryUsageHistory = mutableListOf<MemorySnapshot>()
    private var handler: Handler? = null
    private val monitoringInterval = 30000L // 30 seconds

    data class MemorySnapshot(
        val timestamp: Long,
        val usedMemory: Long,
        val maxMemory: Long,
        val checkpoint: String
    )

    fun startMonitoring() {
        startTime = System.currentTimeMillis()
        Log.i(tag, "📊 Starting continuous memory monitoring (every ${monitoringInterval / 1000}s)")

        logMemoryUsage("Monitoring Started")
        startPeriodicMemoryCheck()
    }

    private fun startPeriodicMemoryCheck() {
        handler = android.os.Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                logMemoryUsage("Periodic Check")
                handler?.postDelayed(this, monitoringInterval)
            }
        }
        handler?.post(runnable)
    }

    fun logMemoryUsage(checkpoint: String) {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val maxMemory = runtime.maxMemory()
        val availableMemory = maxMemory - usedMemory

        val snapshot = MemorySnapshot(
            timestamp = System.currentTimeMillis(),
            usedMemory = usedMemory,
            maxMemory = maxMemory,
            checkpoint = checkpoint
        )

        memoryUsageHistory.add(snapshot)

        // Keep only last 50 snapshots to avoid memory issues
        if (memoryUsageHistory.size > 50) {
            memoryUsageHistory.removeAt(0)
        }

        val trend = getMemoryTrend()
        val memoryPressure = getMemoryPressure(usedMemory, maxMemory)

        Log.d(
            tag, """
            🧠 === Memory Usage at $checkpoint ===
            📈 Used: ${usedMemory / 1024 / 1024} MB
            🏠 Max: ${maxMemory / 1024 / 1024} MB  
            💚 Available: ${availableMemory / 1024 / 1024} MB
            ⏰ Runtime: ${(System.currentTimeMillis() - startTime) / 1000}s
            📊 Trend: $trend
            ⚠️  Pressure: $memoryPressure
        """.trimIndent()
        )
    }

    private fun getMemoryTrend(): String {
        if (memoryUsageHistory.size < 3) return "📊 Insufficient data"

        val recent = memoryUsageHistory.takeLast(5)
        val isIncreasing = recent.zipWithNext().count { (a, b) -> b.usedMemory > a.usedMemory } >= 3
        val isDecreasing = recent.zipWithNext().count { (a, b) -> b.usedMemory < a.usedMemory } >= 3

        return when {
            isIncreasing -> "📈 INCREASING (potential leak!)"
            isDecreasing -> "📉 DECREASING (good)"
            else -> "➡️  STABLE"
        }
    }

    private fun getMemoryPressure(used: Long, max: Long): String {
        val percentage = (used.toDouble() / max.toDouble()) * 100
        return when {
            percentage > 90 -> "🔴 CRITICAL (${percentage.toInt()}%)"
            percentage > 75 -> "🟡 HIGH (${percentage.toInt()}%)"
            percentage > 50 -> "🟢 MODERATE (${percentage.toInt()}%)"
            else -> "✅ LOW (${percentage.toInt()}%)"
        }
    }

    fun getMemorySummary(): String {
        if (memoryUsageHistory.isEmpty()) return "No memory data available"

        val latest = memoryUsageHistory.last()
        val first = memoryUsageHistory.first()
        val growth = latest.usedMemory - first.usedMemory

        return """
            Memory Summary:
            📊 Snapshots taken: ${memoryUsageHistory.size}
            📈 Memory growth: ${growth / 1024 / 1024} MB
            🔄 Current trend: ${getMemoryTrend()}
            ⚠️  Current pressure: ${getMemoryPressure(latest.usedMemory, latest.maxMemory)}
        """.trimIndent()
    }

    fun stopMonitoring() {
        handler?.removeCallbacksAndMessages(null)
        handler = null
        Log.i(tag, "🛑 Memory monitoring stopped")
    }
}