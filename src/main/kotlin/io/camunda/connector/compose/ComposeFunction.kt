package io.camunda.connector.compose

import com.aallam.openai.api.*
import com.google.gson.reflect.*
import io.camunda.connector.api.annotation.*
import io.camunda.connector.api.outbound.*
import io.camunda.connector.common.*
import io.camunda.connector.common.json.*
import io.camunda.connector.common.openai.*
import io.camunda.connector.common.prompt.*
import org.slf4j.*
import java.util.*

@OptIn(BetaOpenAI::class)
@OutboundConnector(
    name = "gpt-compose",
    inputVariables = ["inputJson", "description", "style", "tone", "language", "sender", "model", "apiKey"],
    type = "gpt-compose"
)
class ComposeFunction : OutboundConnectorFunction {

    @Throws(Exception::class)
    override fun execute(context: OutboundConnectorContext): Any {
        LOG.info("Executing ComposeFunction")
        val connectorRequest = context.getVariablesAsType(ComposeRequest::class.java)
        LOG.info("Request: {}", connectorRequest)
        context.validate(connectorRequest)
        context.replaceSecrets(connectorRequest)
        return executeConnector(connectorRequest)
    }

    private fun executeConnector(request: ComposeRequest): String {
        val openAIClient = OpenAIClient(request.apiKey ?: throw RuntimeException("No OpenAI apiKey set"))

        val prompt = ComposePrompt(
            request.description!!,
            request.style!!,
            request.tone!!,
            request.language!!,
            request.sender!!,
            request.inputJson!!,
        )

        LOG.info("ComposeFunction prompt: ${prompt.buildPrompt()}")

        val completedChatHistory = openAIClient.chatCompletion(prompt.buildPrompt())
        val result = completedChatHistory.completionContent().trim()

        LOG.info("ComposeFunction result: $result")

        return result
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(ComposeFunction::class.java)
    }
}
