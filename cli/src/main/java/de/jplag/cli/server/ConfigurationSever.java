package de.jplag.cli.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import de.jplag.cli.server.configurationDTO.SubmissionData;
import de.jplag.cli.server.configurationDTO.SubmissionParsing;

import java.io.IOException;
import java.util.Map;

public class ConfigurationSever extends WebServer {
    private static final String UI_RESOURCE_PREFIX = "configuration-ui";
    private static final String INDEX_PATH = "index.html";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String RESULT_PATH = "results.jplag";

    /**
     * Launches a locally hosted configuration ui.
     * @param port The port to use for the server. You can use 0 to use any free port.
     * @throws IOException If the result file cannot be read
     */
    public ConfigurationSever(int port) throws IOException {
        super(port);

        this.routingTree.insertRouting("", new RoutingResources(UI_RESOURCE_PREFIX).or(new RoutingAlias(INDEX_PATH)));
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpRequestMethod method = HttpRequestMethod.fromName(exchange.getRequestMethod());
        if (method == HttpRequestMethod.POST) {
            String path = exchange.getRequestURI().getPath();
            if (path.endsWith("submissions")) {
                SubmissionData data = objectMapper.readValue(exchange.getRequestBody(), SubmissionData.class);

                SubmissionParsing parsedData = new SubmissionParsing(data.newSubmissions().size() * 50,
                        data.oldSubmissions().size() * 50, Map.of("java", data.newSubmissions().size() * 50 +
                        data.oldSubmissions().size() * 50));
                exchange.sendResponseHeaders(200, 0);
                exchange.getResponseBody().write(objectMapper.writeValueAsString(parsedData).getBytes());
                return;
            }
            if (path.endsWith("languages")) {

            }
            if (path.endsWith("comparisonOptions")) {

            }
            if (path.endsWith("reportFileOptions")) {
            }
        }
        super.handle(exchange);
    }
}
