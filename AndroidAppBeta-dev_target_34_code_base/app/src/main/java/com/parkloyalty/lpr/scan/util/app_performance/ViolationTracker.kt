package com.parkloyalty.lpr.scan.util.app_performance

import android.os.strictmode.Violation
import android.util.Log

class ViolationTracker {
    private val tag = "ViolationTracker"
    private val violations = mutableListOf<ViolationRecord>()

    data class ViolationRecord(
        val timestamp: Long,
        val type: String,
        val violationType: String,
        val message: String,
        val activity: String,
        val thread: String
    )

    fun recordViolation(type: String, violation: Violation, activity: String) {
        val record = ViolationRecord(
            timestamp = System.currentTimeMillis(),
            type = type,
            violationType = violation.javaClass.simpleName,
            message = violation.message ?: "Unknown",
            activity = activity,
            thread = Thread.currentThread().name
        )

        violations.add(record)

        Log.w(tag, "📝 Recorded $type violation from $activity: ${record.violationType}")
    }

    fun getSummary(): String {
        if (violations.isEmpty()) return "✅ No violations recorded"

        val grouped = violations.groupBy { it.type }
        val summary = StringBuilder("Violation Summary:\n")

        grouped.forEach { (type, records) ->
            val typeCount = records.size
            val uniqueViolations = records.groupBy { it.violationType }

            summary.append("🚨 $type: $typeCount violations\n")
            uniqueViolations.forEach { (violationType, instances) ->
                summary.append("   └─ $violationType: ${instances.size} times\n")
            }
        }

        return summary.toString()
    }

    fun getViolationsByActivity(): Map<String, List<ViolationRecord>> {
        return violations.groupBy { it.activity }
    }
}