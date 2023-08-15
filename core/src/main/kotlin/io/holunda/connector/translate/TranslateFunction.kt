package io.holunda.connector.translate

import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.module.kotlin.*
import io.camunda.connector.api.annotation.*
import io.camunda.connector.api.error.*
import io.camunda.connector.api.outbound.*
import io.holunda.connector.common.*
import io.holunda.connector.compose.*
import io.holunda.connector.extract.*
import org.apache.commons.text.*
import org.slf4j.*
import java.util.*

@OutboundConnector(
  name = "gpt-translate",
  inputVariables = ["inputJson", "language", "model"],
  type = "io.holunda.connector.translate:1"
)
class TranslateFunction : OutboundConnectorFunction {

  @Throws(Exception::class)
  override fun execute(context: OutboundConnectorContext): Any {
    LOG.info("Executing TranslateFunction")
    val connectorRequest = context.variables.readFromJson<TranslateRequest>()
    //val connectorRequest = context.bindVariables(TranslateRequest::class.java)
    LOG.info("Request: {}", connectorRequest)
    return executeConnector(connectorRequest)
  }

  private fun executeConnector(request: TranslateRequest): TranslateResult {
    val result = LLMServiceClient.run("translate",
        TranslateTask(
          request.model.modelId,
          request.inputJson,
          request.language
        )
    )

    LOG.info("TranslateFunction result: $result")

    return TranslateResult(result)
  }

  data class TranslateTask(
    val model: String,
    val input: JsonNode,
    val target_language: String,
  )

  companion object {
    private val LOG = LoggerFactory.getLogger(TranslateFunction::class.java)
  }
}
