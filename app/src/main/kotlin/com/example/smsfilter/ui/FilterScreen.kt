package com.example.smsfilter.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smsfilter.data.FilterRuleRepository
import com.example.smsfilter.domain.FilterCategory
import com.example.smsfilter.domain.FilterResult
import com.example.smsfilter.domain.FilterRules
import com.example.smsfilter.domain.SmsFilterEngine

private val sampleMessages = listOf(
    "【快递通知】您的包裹已到驿站，请凭取件码领取。",
    "本周限时优惠，全场折扣，立即购买！",
    "您的账户异常，请点击链接立即验证并提交银行卡密码。",
)

@Composable
fun FilterScreen(ruleRepository: FilterRuleRepository) {
    var message by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<FilterResult?>(null) }
    var saveMessage by remember { mutableStateOf<String?>(null) }
    var rules by remember { mutableStateOf(ruleRepository.loadRules()) }
    var scamKeywords by remember { mutableStateOf(rules.scamKeywords.joinToString("\n")) }
    var marketingKeywords by remember { mutableStateOf(rules.marketingKeywords.joinToString("\n")) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("短信过滤", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text(
            "本地关键词规则演示，不代表安全结论。短信内容不会上传。",
            style = MaterialTheme.typography.bodyMedium,
        )

        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("短信内容") },
            placeholder = { Text("粘贴短信内容进行验证") },
            minLines = 4,
        )
        Button(
            onClick = { result = SmsFilterEngine(rules).classify(message) },
            enabled = message.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("开始过滤")
        }

        Text("快速样例", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        sampleMessages.forEachIndexed { index, sample ->
            TextButton(
                onClick = {
                    message = sample
                    result = SmsFilterEngine(rules).classify(sample)
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("${index + 1}. ${sample.take(32)}")
            }
        }

        result?.let { FilterResultCard(it) }

        Spacer(Modifier.height(4.dp))
        Text("过滤规则", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("每行一个关键词；疑似诈骗规则优先于营销规则。修改后保存到本机。")
        OutlinedTextField(
            value = scamKeywords,
            onValueChange = {
                scamKeywords = it
                saveMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("疑似诈骗关键词") },
            minLines = 3,
        )
        OutlinedTextField(
            value = marketingKeywords,
            onValueChange = {
                marketingKeywords = it
                saveMessage = null
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("营销关键词") },
            minLines = 3,
        )
        Button(
            onClick = {
                val updatedRules = FilterRules(
                    scamKeywords = scamKeywords.lines(),
                    marketingKeywords = marketingKeywords.lines(),
                )
                ruleRepository.saveRules(updatedRules)
                rules = ruleRepository.loadRules()
                scamKeywords = rules.scamKeywords.joinToString("\n")
                marketingKeywords = rules.marketingKeywords.joinToString("\n")
                saveMessage = "规则已保存在本机"
                result = if (message.isNotBlank()) SmsFilterEngine(rules).classify(message) else null
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("保存规则")
        }
        saveMessage?.let { Text(it, color = MaterialTheme.colorScheme.primary) }

        Text(
            "当前为手动测试 MVP，不会读取、拦截或收发系统短信，也不是默认短信应用。",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun FilterResultCard(result: FilterResult) {
    val categoryColor = when (result.category) {
        FilterCategory.NORMAL -> MaterialTheme.colorScheme.primary
        FilterCategory.MARKETING -> MaterialTheme.colorScheme.tertiary
        FilterCategory.SUSPECTED_SCAM -> MaterialTheme.colorScheme.error
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text("分类：${result.category.label}", color = categoryColor, fontWeight = FontWeight.Bold)
            result.reasons.forEach { reason ->
                Text("原因：$reason", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
