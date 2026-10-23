package com.example.smsfilter.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SmsFilterEngineTest {
    private val engine = SmsFilterEngine(FilterRules())

    @Test
    fun classifiesUnmatchedMessageAsNormal() {
        val result = engine.classify("明天下午三点开会，请准时参加。")

        assertEquals(FilterCategory.NORMAL, result.category)
        assertTrue(result.reasons.single().contains("未匹配"))
    }

    @Test
    fun classifiesMarketingMessageAndReportsMatchedKeyword() {
        val result = engine.classify("本周限时优惠，会员享折扣。")

        assertEquals(FilterCategory.MARKETING, result.category)
        assertTrue(result.reasons.single().contains("优惠"))
        assertTrue(result.reasons.single().contains("限时"))
    }

    @Test
    fun scamRuleTakesPriorityOverMarketingRule() {
        val result = engine.classify("限时优惠！点击链接验证账户异常。")

        assertEquals(FilterCategory.SUSPECTED_SCAM, result.category)
        assertTrue(result.reasons.single().contains("账户异常"))
        assertTrue(result.reasons.single().contains("点击链接"))
    }

    @Test
    fun ignoresBlankKeywordsAndMatchesEnglishWithoutCaseSensitivity() {
        val customEngine = SmsFilterEngine(
            FilterRules(scamKeywords = listOf("", "  "), marketingKeywords = listOf("deal")),
        )

        val result = customEngine.classify("TODAY'S DEAL")

        assertEquals(FilterCategory.MARKETING, result.category)
        assertTrue(result.reasons.single().contains("deal"))
    }
}
