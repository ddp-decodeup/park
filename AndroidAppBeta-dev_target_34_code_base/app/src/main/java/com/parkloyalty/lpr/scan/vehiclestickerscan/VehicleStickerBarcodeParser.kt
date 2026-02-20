package com.parkloyalty.lpr.scan.vehiclestickerscan

import com.parkloyalty.lpr.scan.vehiclestickerscan.model.VehicleInfoModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object VehicleStickerBarcodeParser {

    // Common normalizations / lookups
    private val makeLookup = mapOf(
        // common abbreviations you see in barcodes or stickers
        "PORSC" to "Porsche", "POR" to "Porsche", "TOYT" to "Toyota", "TOYO" to "Toyota",
        "HOND" to "Honda", "MB" to "Mercedes-Benz", "MERZ" to "Mercedes-Benz",
        "BMW" to "BMW", "FORD" to "Ford", "GM" to "General Motors", "CHEV" to "Chevrolet",
        "VW" to "Volkswagen", "VOLK" to "Volkswagen", "SUBA" to "Subaru", "AUDI" to "Audi",
        "HYUN" to "Hyundai", "KIA" to "Kia", "NISS" to "Nissan", "TESL" to "Tesla"
    )

    private val bodyLookup = mapOf(
        "SDN" to "Sedan", "SUBN" to "SUV", "SUV" to "SUV", "HBK" to "Hatchback",
        "CP" to "Coupe", "CPE" to "Coupe", "CNV" to "Convertible", "PK" to "Pickup",
        "PICK" to "Pickup", "VAN" to "Van", "WGN" to "Wagon"
    )

    private val plateTypeLookup = mapOf(
        "PAS" to "Passenger", "COM" to "Commercial", "LIV" to "Livery", "LOAN" to "Loaner",
        "GOV" to "Government", "FARM" to "Farm"
    )

    // Patterns
    private val VIN = Regex("(?<![A-Z0-9])[A-HJ-NPR-Z0-9]{17}(?![A-Z0-9])")
    private val DATE_ANY = listOf(
        // mm/dd/yyyy, mm/dd/yy, yyyy-mm-dd, yyyymmdd
        Regex("\\b(0?[1-9]|1[0-2])/(0?[1-9]|[12]\\d|3[01])/(\\d{4})\\b"),
        Regex("\\b(0?[1-9]|1[0-2])/(0?[1-9]|[12]\\d|3[01])/(\\d{2})\\b"),
        Regex("\\b(20\\d{2})-(0?[1-9]|1[0-2])-(0?[1-9]|[12]\\d|3[01])\\b"),
        Regex("\\b(20\\d{2})(0?[1-9]|1[0-2])(0?[1-9]|[12]\\d|3[01])\\b")
    )
    private val YEAR = Regex("\\b(19\\d{2}|20\\d{2})\\b")
    private val STATE =
        Regex("\\b(A[LKZR]|C[AOT]|D[EC]|F[LM]|G[AU]|H[I]|I[ADLN]|K[SY]|L[A]|M[ADEHINOPST]|N[CDEHJMVY]|O[HKR]|P[AWR]|R[I]|S[CD]|T[NX]|U[T]|V[AIT]|W[AIVY])\\b")
    private val PLATE_SIMPLE = Regex("\\b[A-Z0-9]{5,8}\\b")

    // Known tag variations in AAMVA-style vehicle registration payloads (states vary a lot)
    private val tagMap = mapOf(
        // Possible tags you might see on different states
        "VIN" to "vin", "VRVIN" to "vin", "VNV" to "vin",
        "LIC" to "plate", "PLATE" to "plate", "LPN" to "plate", "REGNBR" to "plate",
        "EXP" to "expiry", "EXPDATE" to "expiry", "VREXP" to "expiry", "REGEXP" to "expiry",
        "ST" to "state", "STATE" to "state", "REGSTATE" to "state",
        "YR" to "year", "YEAR" to "year", "VRYEAR" to "year",
        "MAKE" to "make", "MK" to "make",
        "MODEL" to "model", "MDL" to "model",
        "BODY" to "bodystyle", "BODYCLASS" to "bodystyle", "BDY" to "bodystyle",
        "COLOR" to "color", "COL" to "color", "CLR" to "color",
        "PLATETYPE" to "platetype", "PT" to "platetype"
    )

    /**
     * Main entry: returns parsed info from a raw barcode payload
     */
    fun parse(raw: String?): VehicleInfoModel {
        if (raw.isNullOrBlank()) return VehicleInfoModel()
        val clean = raw.trim().replace("\u0000", " ").replace("\r", "\n")
        val tokens = tokenize(clean)

        // 1) Try tagged parsing (AAMVA-like / key:value / key<sep>value)
        val tagged = parseTagged(tokens)

        // 2) Heuristics fallback (free text like "2025 PORSC SUBN LPL4200")
        val heur = parseHeuristics(tokens, clean)

        // 3) Merge (prefer tagged when present, then heuristic)
        return merge(tagged, heur).copy(rawPdf417 = raw)
    }

    /**
     * Merge two infos, preferring 'a' when it has a non-null field.
     */
    fun merge(a: VehicleInfoModel, b: VehicleInfoModel): VehicleInfoModel {
        fun pick(x: String?, y: String?) = x ?: y
        val expiryDate = normalizeDate(pick(a.expiryDate, b.expiryDate))
        // Prefer explicit components; otherwise derive from expiryDate
        val year = pick(a.expiryYear, b.expiryYear)
        val month = pick(a.expiryMonth, b.expiryMonth)
        val day = pick(a.expiryDay, b.expiryDay)
        val (yy, mm, dd) = if (year == null || month == null || day == null)
            splitDate(expiryDate) else Triple(year, month, day)

        return VehicleInfoModel(
            vin = pick(a.vin, b.vin),
            make = pick(a.make, b.make),
            model = pick(a.model, b.model),
            year = pick(a.year, b.year),
            expiryDate = expiryDate,
            expiryYear = yy,
            expiryMonth = mm,
            expiryDay = dd,
            plateNumber = pick(a.plateNumber, b.plateNumber),
            state = pick(a.state, b.state)?.uppercase(Locale.US),
            plateType = pick(a.plateType, b.plateType),
            bodyStyle = pick(a.bodyStyle, b.bodyStyle),
            color = pick(a.color, b.color),
            rawPdf417 = pick(a.rawPdf417, b.rawPdf417),
            raw1D = pick(a.raw1D, b.raw1D)
        )
    }

    // ---------- Internals ----------

    private fun tokenize(s: String): List<String> =
        s.split('\n', '\r', '\t', ' ').filter { it.isNotBlank() }

//    private fun parseTagged(tokens: List<String>): VehicleInfo {
//        var vin: String? = null
//        var plate: String? = null
//        var exp: String? = null
//        var state: String? = null
//        var year: String? = null
//        var make: String? = null
//        var model: String? = null
//        var body: String? = null
//        var color: String? = null
//        var plateType: String? = null
//
//        // Build pairs of (tag,value) when we see TAG:VALUE or TAG=VALUE or TAG VALUE
//        val joined = tokens.joinToString(" ")
//        val kvRegex = Pattern.compile("\\b([A-Z]{2,12})[:=\\s]+([^|\\n\\r]+?)\\b(?=\\s+[A-Z]{2,12}[:=\\s]|$)")
//        val m = kvRegex.matcher(joined)
//        while (m.find()) {
//            val tag = m.group(1)?.uppercase(Locale.US) ?: continue
//            val value = m.group(2)?.trim() ?: continue
//            when (tagMap[tag]) {
//                "vin" -> vin = VIN.find(value)?.value ?: vin
//                "plate" -> plate = PLATE_SIMPLE.find(value)?.value ?: plate
//                "expiry" -> exp = findFirstDate(value) ?: exp
//                "state" -> state = STATE.find(value.uppercase(Locale.US))?.value ?: state
//                "year" -> year = YEAR.find(value)?.value ?: year
//                "make" -> make = normalizeMake(value)
//                "model" -> model = value.take(32)
//                "bodystyle" -> body = normalizeBody(value)
//                "color" -> color = value.take(32)
//                "platetype" -> plateType = normalizePlateType(value)
//            }
//        }
//
//        return VehicleInfo(
//            vin = vin, make = make, model = model, year = year,
//            expiryDate = normalizeDate(exp), plateNumber = plate, state = state,
//            plateType = plateType, bodyStyle = body, color = color
//        )
//    }

//    private fun parseTagged(tokens: List<String>): VehicleInfo {
//        var vin: String? = null
//        var plate: String? = null
//        var exp: String? = null
//        var state: String? = null
//        var year: String? = null
//        var make: String? = null
//        var model: String? = null
//        var body: String? = null
//        var color: String? = null
//        var plateType: String? = null
//
//        val joined = tokens.joinToString(" ")
//
//        // ✅ New: direct regexes for DMV-style fields
//        vin = Regex("VADW([A-HJ-NPR-Z0-9]{17})").find(joined)?.groupValues?.get(1)
//        plate = Regex("RAM([A-Z0-9]{5,8})").find(joined)?.groupValues?.get(1)
//        exp = Regex("RAG(\\d{8})").find(joined)?.groupValues?.get(1)?.let {
//            "${it.substring(0,4)}-${it.substring(4,6)}-${it.substring(6,8)}"
//        }
//        plateType = Regex("RAL([A-Z]+)").find(joined)?.groupValues?.get(1)
//        year = Regex("ZVA(\\d{4})").find(joined)?.groupValues?.get(1)
//        make = Regex("ZVB([A-Z]+)").find(joined)?.groupValues?.get(1)
//            ?: Regex("VAK([A-Z]+)").find(joined)?.groupValues?.get(1)
//        model = Regex("ZVC([A-Z0-9]+)").find(joined)?.groupValues?.get(1)
//
//        // Map abbreviations → friendly names
//        make = when (make) {
//            "PORSC", "PORS" -> "Porsche"
//            else -> make
//        }
//        model = when (model) {
//            "SUBN" -> "SUV"
//            else -> model
//        }
//        plateType = when (plateType) {
//            "PAS" -> "Passenger"
//            else -> plateType
//        }
//
//        return VehicleInfo(
//            vin = vin,
//            make = make,
//            model = model,
//            year = year,
//            expiryDate = exp,
//            plateNumber = plate,
//            state = state ?: "NY",   // default if you know the sticker is NY
//            plateType = plateType,
//            bodyStyle = model,       // model=SUBN → SUV
//            color = color
//        )
//    }

    private fun parseTagged(tokens: List<String>): VehicleInfoModel {
        var vin: String? = null
        var plate: String? = null
        var exp: String? = null
        var state: String? = null
        var year: String? = null
        var make: String? = null
        var model: String? = null
        var body: String? = null
        var color: String? = null
        var plateType: String? = null
        var serial: String? = null
        var stickerSerial: String? = null   // NEW

        val joined = tokens.joinToString(" ")

        // VIN
        vin = Regex("VAD([A-HJ-NPR-Z0-9]{17})").find(joined)?.groupValues?.get(1)

//        // DMV internal serial (VH…)
//        serial = Regex("\\bVH([0-9A-Z]+)\\b").find(joined)?.groupValues?.get(1)
//
//        // Sticker serial (JF412821)
//        stickerSerial = Regex("\\b([A-Z]{2}[0-9]{6,})\\b")
//            .find(joined)?.groupValues?.get(1)

        // DMV internal serial (VH00670058 → "00670058")
        serial = Regex("VH(\\d{6,})").find(joined)?.groupValues?.get(1)

        // Sticker serial (JF412821 style: 2 letters + 6 digits, appears at very start)
        stickerSerial = Regex("\\b([A-Z]{2}[0-9]{6,})\\b").find(joined)?.groupValues?.get(1)

        // Plate
        plate = Regex("RAM([A-Z0-9]{5,8})").find(joined)?.groupValues?.get(1)

        // Expiry
        exp = Regex("RAG(\\d{8})").find(joined)?.groupValues?.get(1)?.let {
            "${it.substring(0, 4)}-${it.substring(4, 6)}-${it.substring(6, 8)}"
        }

        // Plate type
        plateType = Regex("RAL([A-Z]+)").find(joined)?.groupValues?.get(1)

        // Year
        year = Regex("ZVA(\\d{4})").find(joined)?.groupValues?.get(1)

        // Make
        make = Regex("VAK([A-Z]+)").find(joined)?.groupValues?.get(1)
            ?: Regex("ZVB([A-Z]+)").find(joined)?.groupValues?.get(1)

        // Model
        model = Regex("ZVC([A-Z0-9]+)").find(joined)?.groupValues?.get(1)
        body = model

//        // State
//        state = Regex("\\*([A-Z]{2})MA\\*").find(joined)?.groupValues?.get(1)
//            ?: if (joined.contains("NY")) "NY" else null

        // State — from *NYMA* or fallback
        state = Regex("\\*([A-Z]{2})MA\\*").find(joined)?.groupValues?.get(1)
            ?: if (joined.contains("NY")) "NY" else "NA"


        //TODO JANAK Normalize
        make = makeLookup[make] ?: make
        body = bodyLookup[body] ?: body
        plateType = plateTypeLookup[plateType] ?: plateType

//        val expDateRaw = Regex("RAG(\\d{8})").find(joined)?.groupValues?.get(1)?.let {
//            "${it.substring(0,4)}-${it.substring(4,6)}-${it.substring(6,8)}"
//        }

        val (expYear, expMonth, expDay) = splitDate(exp)

        return VehicleInfoModel(
            vin = vin,
            make = make,
            model = model,
            year = year,
            expiryDate = exp,
            expiryYear = expYear,
            expiryMonth = expMonth,
            expiryDay = expDay,
            plateNumber = plate,
            state = state,
            plateType = plateType,
            bodyStyle = body,
            color = color,
            serialNumber = serial,
            stickerSerialNumber = stickerSerial  // NEW
        )
    }


    private fun parseHeuristics(tokens: List<String>, full: String): VehicleInfoModel {
        var vin = VIN.find(full)?.value
        var year = tokens.firstOrNull { YEAR.matches(it) }
        var state =
            tokens.firstOrNull { STATE.matches(it.uppercase(Locale.US)) }?.uppercase(Locale.US)

        // Find plate: pick an 5–8 char alnum that is NOT the VIN, avoid obvious words
        val plate = tokens.firstOrNull {
            it.length in 5..8 &&
                    it.uppercase(Locale.US) != vin &&
                    it.uppercase(Locale.US).all { ch -> ch.isLetterOrDigit() } &&
                    it.uppercase(Locale.US) !in setOf(
                "NEW",
                "YORK",
                "VEHICLE",
                "REGISTRATION",
                "EXP",
                "YEAR",
                "MONTH"
            )
        }

        // Expiry date: any friendly date detected
        val expRaw = findFirstDate(full)
        val normExp = normalizeDate(expRaw)
        val (expY, expM, expD) = splitDate(normExp)

        // Make / body style from common abbreviations, then fallback to dictionary scan
        var make: String? = null
        var body: String? = null
        var model: String? = null

        tokens.forEach { t ->
            val up = t.uppercase(Locale.US)
            if (make == null) make = makeLookup[up] ?: makeFromToken(up)
            if (body == null) body = bodyLookup[up]
        }

        // Simple model hint: word following make, or known body code
        if (model == null && make != null) {
            val idx = tokens.indexOfFirst {
                makeEquals(
                    it,
                    make!!
                ) || makeLookup[it.uppercase(Locale.US)] == make
            }
            if (idx in 0 until tokens.lastIndex) {
                val nxt = tokens[idx + 1]
                if (!YEAR.matches(nxt) && nxt.length in 2..12) model = normalizeModel(nxt)
            }
        }

        return VehicleInfoModel(
            vin = vin, make = make, model = model, year = year,
            expiryDate = normExp,
            expiryYear = expY,
            expiryMonth = expM,
            expiryDay = expD, plateNumber = plate, state = state,
            bodyStyle = body
        )
    }

    private fun makeFromToken(t: String): String? =
        when {
            t.length >= 3 && t in makeLookup -> makeLookup[t]
            t == "PORSCHE" -> "Porsche"
            t == "MERCEDES" || t == "MERCEDES-BENZ" || t == "MERCEDESBENZ" -> "Mercedes-Benz"
            else -> null
        }

    private fun makeEquals(token: String, make: String) =
        token.equals(make, true) || makeLookup[token.uppercase(Locale.US)] == make

    private fun normalizeModel(s: String?) = s?.replace(Regex("[^A-Za-z0-9-]"), "")?.uppercase(
        Locale.US
    )

    private fun normalizeBody(s: String?): String? {
        if (s.isNullOrBlank()) return null
        val up = s.uppercase(Locale.US)
        return bodyLookup[up] ?: up
    }

    private fun normalizePlateType(s: String?): String? {
        if (s.isNullOrBlank()) return null
        val up = s.uppercase(Locale.US)
        return plateTypeLookup[up] ?: up.replace("_", " ").replace("-", " ").lowercase(Locale.US)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }
    }

    private fun normalizeMake(s: String?): String? {
        if (s.isNullOrBlank()) return null
        val up = s.uppercase(Locale.US).trim()
        return makeLookup[up] ?: up.replace("_", " ").replace("-", " ")
            .lowercase(Locale.US).replaceFirstChar { it.titlecase(Locale.US) }
    }

    private fun normalizeDate(inp: String?): String? {
        val d = inp ?: return null
        // Try to coerce to yyyy-MM-dd
        val candidates = listOf(
            "MM/dd/yyyy", "M/d/yyyy", "MM/dd/yy", "M/d/yy", "yyyy-MM-dd", "yyyyMMdd"
        )
        for (pat in candidates) {
            try {
                val sdfIn = SimpleDateFormat(pat, Locale.US); sdfIn.isLenient = false
                val dt = sdfIn.parse(d) ?: continue
                return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(dt)
            } catch (_: Exception) {
            }
        }
        return d // fallback raw
    }

    private fun findFirstDate(s: String): String? {
        for (rx in DATE_ANY) {
            val m = rx.find(s) ?: continue
            return m.value
        }
        return null
    }

    private fun splitDate(inp: String?): Triple<String?, String?, String?> {
        if (inp == null) return Triple(null, null, null)
        val formats = listOf(
            "MM/dd/yyyy", "M/d/yyyy", "MM/dd/yy", "M/d/yy", "yyyy-MM-dd", "yyyyMMdd"
        )
        for (pat in formats) {
            try {
                val sdfIn = SimpleDateFormat(pat, Locale.US); sdfIn.isLenient = false
                val dt = sdfIn.parse(inp) ?: continue
                val cal = Calendar.getInstance().apply { time = dt }
                val year = cal.get(Calendar.YEAR).toString()
                val month = (cal.get(Calendar.MONTH) + 1).toString().padStart(2, '0')
                val day = cal.get(Calendar.DAY_OF_MONTH).toString().padStart(2, '0')
                return Triple(year, month, day)
            } catch (_: Exception) {
            }
        }
        return Triple(null, null, null)
    }
}