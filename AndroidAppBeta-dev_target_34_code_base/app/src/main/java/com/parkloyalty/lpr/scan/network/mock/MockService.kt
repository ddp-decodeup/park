package com.parkloyalty.lpr.scan.network.mock

import android.content.Context
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.parkloyalty.lpr.scan.utils.ObjectMapperProvider
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import javax.inject.Singleton
import kotlin.math.ceil

/**
 * Debug-friendly MockService for executeGetGeneticHitListAPI(...)
 * Uses Thread.sleep inside fromCallable so breakpoints behave predictably.
 *
 * Place your JSON at: app/src/main/assets/genetic_hit_list.json
 */
@Singleton
class MockService(private val ctx: Context, private val networkDelayMs: Long = 2000L) {

    private val fullJsonObj: ObjectNode? = loadJsonSafe()

    private fun loadJsonSafe(): ObjectNode? {
        return try {
            val text = ctx.assets.open("genetic_hit_list.json").bufferedReader().use { it.readText() }
            ObjectMapperProvider.instance.readTree(text) as? ObjectNode
        } catch (t: Throwable) {
            t.printStackTrace()
            null
        }
    }

    /**
     * Matches your Service signature called from the ViewModel.
     * page & limit are String because your ViewModel passes Strings.
     */
    fun executeGetGeneticHitListAPIOnlyLpr(
        typeOfHit: String?,
        lprNumber: String?,
        page: String?,
        limit: String?
    ): Observable<JsonNode?> {

        return Observable.fromCallable<JsonNode?> {
            // simulate latency
            try {
                Thread.sleep(networkDelayMs)
            } catch (_: InterruptedException) {
            }

            // Prepare base JSON & source array
            val base = fullJsonObj?.deepCopy() ?: ObjectMapperProvider.instance.createObjectNode().apply {
                put("status", false)
                put("message", "mock json missing")
                put("total_count", 0)
                set<ArrayNode>("data", ObjectMapperProvider.instance.createArrayNode())
            }

            val originalArray: ArrayNode =
                (base.get("data") as? ArrayNode) ?: ObjectMapperProvider.instance.createArrayNode()

            // Normalize filter inputs
            val typeFilter = typeOfHit?.trim()?.takeIf { it.isNotEmpty() }?.lowercase()
            val lprFilter = lprNumber?.trim()?.takeIf { it.isNotEmpty() }?.uppercase()

            // Build filtered array (apply filters first)
            val filtered = ObjectMapperProvider.instance.createArrayNode()
            for (elem in originalArray) {
                if (!elem.isObject) continue
                val obj = elem as ObjectNode

                // get values safely
                val itemType = obj.get("type_of_hit")?.asText()?.trim()?.lowercase()
                val itemLp = obj.get("lp_number")?.asText()?.trim()?.uppercase()

                // check filters
                var matches = true
                typeFilter?.let { tf -> matches = matches && (itemType != null && itemType == tf) }
                lprFilter?.let { lf -> matches = matches && (itemLp != null && itemLp == lf) }

                if (matches) filtered.add(obj)
            }

            // If no filters provided, filtered will equal originalArray
            val effectiveArray =
                if (typeFilter == null && lprFilter == null) originalArray else filtered

            val totalCount = effectiveArray.size()

            // parse page & limit safely (1-based page)
            val pageNum = try {
                page?.toInt() ?: 1
            } catch (_: Exception) {
                1
            }
            val pageSize = try {
                limit?.toInt() ?: totalCount.takeIf { it > 0 } ?: 1
            } catch (_: Exception) {
                totalCount.takeIf { it > 0 } ?: 1
            }

            val totalPages =
                if (pageSize <= 0) 1 else ceil(totalCount.toDouble() / pageSize.toDouble()).toInt()

            val fromIndex = ((pageNum - 1) * pageSize).coerceAtLeast(0)
            val toIndex = (fromIndex + pageSize).coerceAtMost(effectiveArray.size())

            val sliced = ObjectMapperProvider.instance.createArrayNode()
            if (fromIndex < effectiveArray.size()) {
                for (i in fromIndex until toIndex) sliced.add(effectiveArray.get(i))
            }

            // Build response JSON (filtered total_count + sliced data)
            val resp = ObjectMapperProvider.instance.createObjectNode()
            resp.put("status", true)
            resp.put("message", "Genetic hit list fetched successfully (mock)")
            resp.put("total_count", totalCount)
            resp.set<ArrayNode>("data", sliced)

            resp as JsonNode
        }
            .subscribeOn(Schedulers.io())
    }


    fun executeGetGeneticHitListAPI(
        typeOfHit: String?,
        lprNumber: String?,
        page: String?,
        limit: String?
    ): Observable<JsonNode?> {

        return Observable.fromCallable<JsonNode?> {
            // simulate latency
            try {
                Thread.sleep(networkDelayMs)
            } catch (_: InterruptedException) {
            }

            // Prepare base JSON & source array
            val base = fullJsonObj?.deepCopy() ?: ObjectMapperProvider.instance.createObjectNode().apply {
                put("status", false)
                put("message", "mock json missing")
                put("total_count", 0)
                set<ArrayNode>("data", ObjectMapperProvider.instance.createArrayNode())
            }

            val originalArray: ArrayNode =
                (base.get("data") as? ArrayNode) ?: ObjectMapperProvider.instance.createArrayNode()

            // Normalize filter inputs and treat empty/"null"/"all" as no-filter
            fun String?.normalizeFilterForType(): String? {
                val s = this?.trim()
                if (s.isNullOrEmpty()) return null
                val low = s.lowercase()
                if (low == "null" || low == "all") return null
                return low
            }

            fun String?.normalizeFilterForLp(): String? {
                val s = this?.trim()
                if (s.isNullOrEmpty()) return null
                val up = s.uppercase()
                if (up == "NULL") return null
                return up
            }

            val typeFilter = typeOfHit.normalizeFilterForType()   // lowercase or null
            val lprFilter = lprNumber.normalizeFilterForLp()      // uppercase or null

            // Build filtered array (apply filters first)
            val filtered = ObjectMapperProvider.instance.createArrayNode()
            for (elem in originalArray) {
                if (!elem.isObject) continue
                val obj = elem as ObjectNode

                // get values safely
                val itemType = obj.get("type_of_hit")?.asText()?.trim()?.lowercase()
                val itemLp = obj.get("lp_number")?.asText()?.trim()?.uppercase()

                // check filters
                var matches = true
                typeFilter?.let { tf ->
                    // exact match on type (case-insensitive). If you prefer contains, change to contains()
                    matches = matches && (itemType != null && itemType == tf)
                }
                lprFilter?.let { lf ->
                    // exact match on LP number (case-insensitive)
                    matches = matches && (itemLp != null && itemLp == lf)
                }

                if (matches) filtered.add(obj)
            }

            // If no filters provided, effectiveArray = originalArray
            val effectiveArray =
                if (typeFilter == null && lprFilter == null) originalArray else filtered

            val totalCount = effectiveArray.size()

            // parse page & limit safely (1-based page)
            val pageNum = try {
                page?.toInt() ?: 1
            } catch (_: Exception) {
                1
            }
            val pageSize = try {
                limit?.toInt() ?: totalCount.takeIf { it > 0 } ?: 1
            } catch (_: Exception) {
                totalCount.takeIf { it > 0 } ?: 1
            }

            val totalPages =
                if (pageSize <= 0) 1 else ceil(totalCount.toDouble() / pageSize.toDouble()).toInt()

            val fromIndex = ((pageNum - 1) * pageSize).coerceAtLeast(0)
            val toIndex = (fromIndex + pageSize).coerceAtMost(effectiveArray.size())

            val sliced = ObjectMapperProvider.instance.createArrayNode()
            if (fromIndex < effectiveArray.size()) {
                for (i in fromIndex until toIndex) sliced.add(effectiveArray.get(i))
            }

            // Build response JSON (filtered total_count + sliced data)
            val resp = ObjectMapperProvider.instance.createObjectNode()
            resp.put("status", true)
            resp.put("message", "Genetic hit list fetched successfully (mock)")
            resp.put("total_count", totalCount)
            resp.set<ArrayNode>("data", sliced)

            resp as JsonNode
        }
            .subscribeOn(Schedulers.io())
    }

    // Implement other Service methods if required by your interface, or throw.
}
