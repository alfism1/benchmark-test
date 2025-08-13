import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class Main {
	public static void main(String[] args) throws IOException {
		HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
		server.createContext("/", Main::handleRoot);
		server.setExecutor(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
		server.start();
	}

	private static void handleRoot(HttpExchange exchange) throws IOException {
		byte[] response = "Hello World".getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
		exchange.sendResponseHeaders(200, response.length);
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(response);
		}
	}
} 