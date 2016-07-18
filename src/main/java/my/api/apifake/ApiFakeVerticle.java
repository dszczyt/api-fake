package my.api.apifake;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;

import java.util.concurrent.ThreadLocalRandom;

public class ApiFakeVerticle extends AbstractVerticle {

    private void getProducts(RoutingContext r) {
        r.response().end(new JsonObject().put("productId", 42).encode());
    }

    private void getProductsSlow(RoutingContext r) {
        vertx.setTimer(
                ThreadLocalRandom.current().nextInt(1, 3_000),
                id -> this.getProducts(r)
        );
    }

    @Override
    public void start(Future<Void> fut) {
        Router router = Router.router(vertx);

        router.get("/slow_products")
                .handler(this::getProductsSlow);

        router.get("/products")
                .handler(this::getProducts);

        HttpServer server = vertx
                .createHttpServer()
                .requestHandler(router::accept)
                .listen(
                        config().getInteger("http.port", 8000),
                        result -> {
                            if(result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );

    }
}
