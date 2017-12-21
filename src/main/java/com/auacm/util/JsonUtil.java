package com.auacm.util;

import com.google.gson.*;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;

@Component
public class JsonUtil {
    private Gson gson;

    private JsonFormat format;

    public JsonUtil() {
        gson = new GsonBuilder().setPrettyPrinting().create();
        format = new JsonFormat();
    }

    public String removeEmptyObjects(String jsonString) {
        JsonObject object = new JsonParser().parse(jsonString).getAsJsonObject();
        removeEmptyObjects(object);
        return gson.toJson(object);
    }

    private void removeEmptyObjects(JsonObject object) {
        ArrayList<String> deleteObject = new ArrayList<>();
        for (Map.Entry<String, JsonElement> element : object.entrySet()) {
            if (element.getValue().isJsonObject()) {
                JsonObject object1 = element.getValue().getAsJsonObject();
                if (object1.size() == 0) {
                    deleteObject.add(element.getKey());
                } else {
                    removeEmptyObjects(object1);
                }
            } else if (element.getValue().isJsonArray()) {
                JsonArray array = element.getValue().getAsJsonArray();
                ArrayList<JsonElement> deleteArray = new ArrayList<>();
                for (JsonElement e : array) {
                    if (e.isJsonObject()) {
                        JsonObject object1 = e.getAsJsonObject();
                        if (object1.size() == 0) {
                            deleteArray.add(object1);
                        } else {
                            removeEmptyObjects(object1);
                        }
                    }
                }
                for (JsonElement e : deleteArray) {
                    array.remove(e);
                }
            }
        }
        for (String key : deleteObject) {
            object.remove(key);
        }
    }

    public String toJson(Message message) {
        return format.printToString(message);
    }

    public JsonObject toJsonObject(Message message) {
        return new JsonParser().parse(toJson(message)).getAsJsonObject();
    }
}
