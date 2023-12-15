package verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.TemplateHandler;
import io.vertx.ext.web.templ.mvel.MVELTemplateEngine;
import static io.vertx.ext.web.handler.TemplateHandler.DEFAULT_CONTENT_TYPE;

public class HttpServerVerticle extends AbstractVerticle {
	private HttpServer server;
	private int port = 9999;

	@Override
	public void start(Promise<Void> startPromise) throws Exception {

		Router router = Router.router(vertx);
		
		
		// Serve the dynamic pages
		MVELTemplateEngine templateEngine = MVELTemplateEngine.create(vertx);
		TemplateHandler templateHandler = TemplateHandler.create(templateEngine, "templates",
				DEFAULT_CONTENT_TYPE);
		
		router.route("/dynamic/*")
	      .handler(ctx -> {
	        // put the context into the template render context
	        ctx.put("context", ctx);
	        ctx.next();
	      })
	      .handler(templateHandler);
		
		
		// Serve the static pages
		router.route().handler(StaticHandler.create("webroot"));

		server = vertx.createHttpServer().requestHandler(router);

		// Now bind the server:
		server.listen(port, res -> {
			if (res.succeeded()) {
				System.out.println("HttpServer start at " + port);
				startPromise.complete();

			} else {
				startPromise.fail(res.cause());
			}
		});
	}
}
