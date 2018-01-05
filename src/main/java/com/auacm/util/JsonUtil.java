package com.auacm.util;

import com.google.gson.*;
import com.google.protobuf.Message;
import com.googlecode.protobuf.format.JsonFormat;
import org.springframework.security.access.method.P;
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
        JsonObject object = new JsonParser().parse(format.printToString(message)).getAsJsonObject();
        JsonObject newObject = formatMaps(object);
        removeEmptyObjects(newObject);
        return newObject.toString();
    }

    public JsonObject formatMaps(JsonObject object) {
        JsonObject newObject = new JsonObject();
        for (Map.Entry<String, JsonElement> temp : object.entrySet()) {
            if (temp.getValue().isJsonObject()) {
                JsonObject current = temp.getValue().getAsJsonObject();
                if (current.size() == 1 && current.has("list") && current.get("list").isJsonArray()) {
                    JsonArray replaceArray = new JsonArray();
                    JsonArray currentArray = current.get("list").getAsJsonArray();
                    for (JsonElement e : currentArray) {
                        if (e.isJsonObject()) {
                            replaceArray.add(formatMaps(e.getAsJsonObject()));
                        } else {
                            replaceArray.add(e);
                        }
                    }
                    newObject.add(temp.getKey(), replaceArray);
                } else {
                    newObject.add(temp.getKey(), formatMaps(current));
                }
            } else if (temp.getValue().isJsonArray()) {
                JsonArray array = temp.getValue().getAsJsonArray();
                JsonArray replaceArray = new JsonArray();
                JsonObject replace = new JsonObject();
                if (array.get(0).isJsonObject()) {
                    JsonObject firstObject = array.get(0).getAsJsonObject();
                    if (firstObject.has("key") && firstObject.has("value") && firstObject.size() == 2) {
                        for (JsonElement e : array) {
                            JsonObject current = e.getAsJsonObject();
                            replace.add(current.get("key").getAsString(), current.get("value"));
                        }
                        newObject.add(temp.getKey(), formatMaps(replace));
                    } else {
                        for (JsonElement e : array) {
                            JsonObject current = e.getAsJsonObject();
                            replaceArray.add(formatMaps(current));
                        }
                        newObject.add(temp.getKey(), replaceArray);
                    }
                } else {
                    newObject.add(temp.getKey(), array);
                }
            } else {
                newObject.add(temp.getKey(), temp.getValue());
            }
        }
        return newObject;
    }

    public JsonObject toJsonObject(Message message) {
        return new JsonParser().parse(toJson(message)).getAsJsonObject();
    }
}
