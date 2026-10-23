package com.example.smsfilter.data

import android.content.Context
import com.example.smsfilter.domain.DEFAULT_MARKETING_KEYWORDS
import com.example.smsfilter.domain.DEFAULT_SCAM_KEYWORDS
import com.example.smsfilter.domain.FilterRules

class FilterRuleRepository(context: Context) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun loadRules(): FilterRules = FilterRules(
        scamKeywords = readKeywords(SCAM_KEYWORDS, DEFAULT_SCAM_KEYWORDS),
        marketingKeywords = readKeywords(MARKETING_KEYWORDS, DEFAULT_MARKETING_KEYWORDS),
    )

    fun saveRules(rules: FilterRules) {
        preferences.edit()
            .putString(SCAM_KEYWORDS, rules.scamKeywords.toStoredValue())
            .putString(MARKETING_KEYWORDS, rules.marketingKeywords.toStoredValue())
            .apply()
    }

    private fun readKeywords(key: String, defaults: List<String>): List<String> {
        val storedValue = preferences.getString(key, null) ?: return defaults
        return storedValue.split('\n').map(String::trim).filter(String::isNotEmpty)
    }

    private fun List<String>.toStoredValue(): String =
        asSequence().map(String::trim).filter(String::isNotEmpty).distinct().joinToString("\n")

    private companion object {
        const val PREFERENCES_NAME = "filter_rules"
        const val SCAM_KEYWORDS = "scam_keywords"
        const val MARKETING_KEYWORDS = "marketing_keywords"
    }
}
