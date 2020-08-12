package net.genin.christophe.secure.fileupload;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.reactivex.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Jsons {

    public static List<String> to_String(JsonArray arr) {
        if (Objects.isNull(arr)) {
            return new ArrayList<>();
        }
        return arr.stream()
                .map(Object::toString)
                .collect(Collectors.toList());
    }

    private final RoutingContext rc;
    private int status = 200;

    public Jsons(RoutingContext rc) {
        this.rc = rc;
    }

    public Jsons withStatus(int status) {
        this.status = status;
        return this;
    }

    public void json(JsonObject obj) {
        json(obj.encode());
    }

    public void json(JsonArray obj) {
        json(obj.encode());
    }

    public void json(String encode) {
        rc.response().setStatusCode(status).putHeader("Content-Type", "application/json").end(encode);
    }
}
