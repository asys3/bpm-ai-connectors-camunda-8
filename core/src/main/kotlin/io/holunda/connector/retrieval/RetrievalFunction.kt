package io.holunda.connector.retrieval

import io.camunda.connector.api.annotation.*
import io.camunda.connector.api.outbound.*
import io.holunda.connector.common.*
import mu.*

@OutboundConnector(
    name = "gpt-retrieval",
    inputVariables = [
        "query",
        "outputSchema",
        "database",
        "databaseUrl",
        "embeddingProvider",
        "embeddingModel",
        "mode",
        "model"
    ],
    type = "io.holunda:connector-retrieval:1"
)
class RetrievalFunction : OutboundConnectorFunction {

    override fun execute(context: OutboundConnectorContext): Any {
        logger.info("Executing RetrievalFunction")
        val connectorRequest = context.variables.readFromJson<RetrievalRequest>()
        logger.info("RetrievalFunction request: $connectorRequest")
        return executeRequest(connectorRequest)
    }

    private fun executeRequest(request: RetrievalRequest): RetrievalResult {
        val result = LLMServiceClient.run("retrieval", RetrievalTask.fromRequest(request))
        logger.info("RetrievalFunction result: $result")
        return RetrievalResult(result)
    }

    companion object : KLogging()
}
