package net.genin.christophe.secure.fileupload.verticles;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.reactivex.core.AbstractVerticle;
import io.vertx.reactivex.core.buffer.Buffer;
import io.vertx.reactivex.core.eventbus.Message;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Objects;

public class GzVerticle extends AbstractVerticle {


    private static final Logger LOG = LoggerFactory.getLogger(GzVerticle.class);
    public static final String GET = GzVerticle.class.getName() + ".get";


    @Override
    public void start() {
        LoadingCache<String, Buffer> cache = Caffeine.newBuilder()
                .build(this::getGzFile);
        vertx.eventBus().<String>consumer(GET, msg ->
                Single.just(msg)
                        .map(Message::body)
                        .map(path -> Objects.requireNonNull(cache.get(path)))
                        // Bug Reactive buffer not managed in codec ?
                        .map(Buffer::getDelegate)
                        .subscribeOn(Schedulers.io())
                        .subscribe(
                                msg::reply,
                                t -> {
                                    LOG.warn("Error in " + msg, t);
                                    msg.fail(404, "Not found");
                                })
        );
    }

    private Buffer getGzFile(@NonNull String path) {
        String resource = "webroot/wc/" + path + ".gz";
        return vertx.fileSystem().readFileBlocking(resource);
    }
}
