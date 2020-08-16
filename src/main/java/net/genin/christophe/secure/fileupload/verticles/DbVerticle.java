package net.genin.christophe.secure.fileupload.verticles;

import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.redis.client.*;
import io.vertx.redis.client.RedisOptions;
import io.vertx.redis.client.RedisRole;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class DbVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(DbVerticle.class);
    public static final String SAVE = DbVerticle.class.getName() + ".save";
    public static final String GET = DbVerticle.class.getName() + ".get";
    public static final String PING = DbVerticle.class.getName() + ".ping";
    public static final String EXIST = DbVerticle.class.getName() + ".exist";
    public static final String DELETE = DbVerticle.class.getName() + ".delete";


    @Override
    public void start() throws Exception {
        final String redisEndpoint = config().getString("REDIS_ENDPOINT", "redis://localhost:6379");
        // TODO export env variables
        final RedisOptions options = new RedisOptions().addConnectionString(redisEndpoint)
                .setMaxPoolSize(8)
                .setMaxWaitingHandlers(32)
                .setRole(RedisRole.MASTER);
        LOG.info("Redis used :" + redisEndpoint);
        final Redis client = Redis.createClient(vertx, options);
        client
                .rxSend(Request.cmd(Command.PING))
                .subscribe(r -> LOG.info("Redis ping Ok."), t -> LOG.error("Redis ping KO.", t));
        vertx.eventBus()
                .<JsonObject>consumer(SAVE, msg -> {
                    final JsonObject body = msg.body();
                    final String key = body.getString("key");
                    final String value = body.getJsonObject("value").encode();
                    final Request minimaRequest = Request.cmd(Command.SET).arg(key).arg(value);
                    final RedisAPI api = RedisAPI.api(client);

                    Optional.ofNullable(body.getInteger("expire"))
                            .map(expire -> api.rxSetex(key, value, expire.toString()))
                            .orElseGet(() -> api.rxSet(Arrays.asList(key, value)))
                            .subscribe(r -> {
                                final boolean result = "OK".equals(r.toString());
                                msg.reply(result);
                            }, throwable -> {
                                LOG.error("Error for key " + key, throwable);
                                msg.fail(500, "Error");
                            });
                });
        vertx.eventBus().<String>consumer(EXIST, mmsg -> {
            final String key = mmsg.body();

            final RedisAPI api = RedisAPI.api(client);
            api
                    .rxExists(Collections.singletonList(key))
                    .map(Response::toBoolean)
                    .subscribe(mmsg::reply, t -> {
                        LOG.error("Get error for " + key, t);
                        mmsg.reply(false);
                    });
        });
        vertx.eventBus().<String>consumer(PING, mmsg -> {
            RedisAPI.api(client)
                    .rxPing(Collections.singletonList("TRUE"))
                    .map(Response::toString)
                    .subscribe(
                            b -> mmsg.reply("TRUE".equals(b)),
                            t -> mmsg.reply(false)
                    );
        });
        vertx.eventBus().<String>consumer(GET, mmsg -> {
            final String key = mmsg.body();

            final RedisAPI api = RedisAPI.api(client);
            api.get(key, r -> {
                if (r.succeeded() && Objects.nonNull(r.result())) {
                    final String s = r.result().toString();
                    final JsonObject o = new JsonObject(s);
                    mmsg.reply(o);
                    return;
                }
                final Throwable t = Optional.ofNullable(r.cause()).orElse(new IllegalArgumentException("Not found : " + key));
                LOG.error("Get error for " + key, t);
                mmsg.fail(400, "Not found " + key);
            });
        });

        vertx.eventBus().<String>consumer(DELETE, mmsg -> {
            final String key = mmsg.body();

            RedisAPI.api(client)
                    .rxDel(Collections.singletonList(key))
                    .map(Response::toInteger)
                    .map(nbDeleted -> nbDeleted == 1)
                    .subscribe(
                            mmsg::reply,
                            t -> {
                                LOG.error("Get error for " + key, t);
                                mmsg.fail(400, "Not found " + key);
                            });
        });

        LOG.info("started.");
    }
}
