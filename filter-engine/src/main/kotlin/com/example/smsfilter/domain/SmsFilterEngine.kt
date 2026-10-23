package com.example.smsfilter.domain

import java.util.Locale

enum class FilterCategory(val label: String) {
    NORMAL("正常"),
    MARKETING("营销"),
    SUSPECTED_SCAM("疑似诈骗"),
}

data class FilterRules(
    val scamKeywords: List<String> = DEFAULT_SCAM_KEYWORDS,
    val marketingKeywords: List<String> = DEFAULT_MARKETING_KEYWORDS,
)

data class FilterResult(
    val category: FilterCategory,
    val reasons: List<String>,
)

class SmsFilterEngine(private val rules: FilterRules) {
    fun classify(message: String): FilterResult {
        val scamMatches = findMatches(message, rules.scamKeywords)
        if (scamMatches.isNotEmpty()) {
            return FilterResult(
                category = FilterCategory.SUSPECTED_SCAM,
                reasons = listOf("命中疑似诈骗关键词：${scamMatches.joinToString("、")}"),
            )
        }

        val marketingMatches = findMatches(message, rules.marketingKeywords)
        if (marketingMatches.isNotEmpty()) {
            return FilterResult(
                category = FilterCategory.MARKETING,
                reasons = listOf("命中营销关键词：${marketingMatches.joinToString("、")}"),
            )
        }

        return FilterResult(
            category = FilterCategory.NORMAL,
            reasons = listOf("未匹配到营销或疑似诈骗关键词"),
        )
    }

    private fun findMatches(message: String, keywords: List<String>): List<String> {
        val normalizedMessage = message.lowercase(Locale.ROOT)
        return keywords
            .asSequence()
            .map(String::trim)
            .filter(String::isNotEmpty)
            .distinctBy { it.lowercase(Locale.ROOT) }
            .filter { normalizedMessage.contains(it.lowercase(Locale.ROOT)) }
            .toList()
    }
}

val DEFAULT_SCAM_KEYWORDS = listOf(
    "验证码",
    "转账",
    "账户异常",
    "点击链接",
    "立即验证",
    "银行卡",
    "密码",
    "中奖",
    "退款",
)

val DEFAULT_MARKETING_KEYWORDS = listOf(
    "优惠",
    "促销",
    "折扣",
    "限时",
    "免费领取",
    "立即购买",
    "订阅",
    "广告",
)
