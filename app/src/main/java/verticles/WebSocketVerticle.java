package verticles;


import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.handler.CorsHandler;

public class WebSocketVerticle extends AbstractVerticle {

	@Override
	public void start() throws Exception {
		/*Router router = Router.router(vertx);

        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        router.route("/ws/*").handler(sockJSHandler);
        
        
		vertx.createHttpServer()
			.webSocketHandler(ws -> ws.handler(ws::writeBinaryMessage)).requestHandler(req -> {
			if (req.uri().equals("/ws"))
				req.response().send("hello");
		}).listen(8080);*/
		
		Router router = Router.router(vertx);

        // Configurar CORS
        CorsHandler corsHandler = CorsHandler.create("*") // Permitir desde cualquier origen
                .allowedMethod(io.vertx.core.http.HttpMethod.GET)
                .allowedMethod(io.vertx.core.http.HttpMethod.POST)
                .allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
                .allowedHeader("Access-Control-Allow-Method")
                .allowedHeader("Access-Control-Allow-Origin")
                .allowedHeader("Access-Control-Allow-Headers")
                .allowCredentials(true);

        router.route().handler(corsHandler);
		HttpServer server = vertx.createHttpServer(new HttpServerOptions().setPort(8080));

        server.webSocketHandler(this::handleWebSocket);

        server.listen(ar -> {
            if (ar.succeeded()) {
                System.out.println("Servidor HTTP escuchando en el puerto " + ar.result().actualPort());
            } else {
                System.out.println("Error al iniciar el servidor HTTP: " + ar.cause().getMessage());
            }
        });
	}
	
	 private void handleWebSocket(ServerWebSocket webSocket) {
	        System.out.println("Nueva conexión WebSocket establecida");

	        // Manejar mensajes entrantes desde el cliente
	        webSocket.textMessageHandler(message -> {
	            System.out.println("Mensaje recibido desde el cliente: " + message);

	            // Enviar un mensaje de vuelta al cliente
	            webSocket.writeTextMessage("Mensaje del servidor: " + message);
	        });

	        // Manejar cierre de conexión
	        webSocket.closeHandler(close -> {
	            System.out.println("Conexión WebSocket cerrada");
	        });
	    }

}
