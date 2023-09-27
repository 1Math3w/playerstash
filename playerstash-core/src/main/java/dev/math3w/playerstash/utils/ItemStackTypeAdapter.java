package dev.math3w.playerstash.utils;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ItemStackTypeAdapter extends TypeAdapter<ItemStack> {
    @Override
    public void write(JsonWriter out, ItemStack itemStack) throws IOException {
        writeSerializable(out, itemStack);
    }

    private void writeSerializable(JsonWriter out, ConfigurationSerializable serializable) throws IOException {
        out.beginObject();
        for (Map.Entry<String, Object> entry : serializable.serialize().entrySet()) {
            if (entry.getValue() instanceof Optional) {
                Optional<?> optional = (Optional<?>) entry.getValue();
                if (optional.isPresent()) {
                    out.name(entry.getKey());
                    writeValue(out, optional.get());
                }
            } else {
                out.name(entry.getKey());
                writeValue(out, entry.getValue());
            }
        }
        out.endObject();
    }

    private void writeList(JsonWriter out, List<?> list) throws IOException {
        out.beginArray();
        for (Object item : list) {
            writeValue(out, item);
        }
        out.endArray();
    }

    private void writeValue(JsonWriter out, Object value) throws IOException {
        if (value instanceof Map) {
            out.beginObject();
            Map<?, ?> map = (Map<?, ?>) value;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                out.name(entry.getKey().toString());
                writeValue(out, entry.getValue());
            }
            out.endObject();
        } else if (value instanceof List) {
            writeList(out, (List<?>) value);
        } else if (value instanceof ItemStack) {
            write(out, (ItemStack) value);
        } else if (value instanceof ConfigurationSerializable) {
            writeSerializable(out, ((ConfigurationSerializable) value));
        } else if (value instanceof Integer) {
            out.value((Integer) value);
        } else if (value instanceof Long) {
            out.value((Long) value);
        } else if (value instanceof Number) {
            out.value((Number) value);
        } else if (value instanceof Boolean) {
            out.value((Boolean) value);
        } else if (value != null) {
            out.value(value.toString());
        }
    }

    @Override
    public ItemStack read(JsonReader in) throws IOException {
        Map<String, Object> serialized = new HashMap<>();
        in.beginObject();
        while (in.hasNext()) {
            String key = in.nextName();
            Object value = readValue(in);
            serialized.put(key, value);
        }
        in.endObject();

        Map<String, Object> serializedMeta = (Map<String, Object>) serialized.get("meta");

        if (serializedMeta != null) {
            try {
                String version = Bukkit.getServer().getClass().getPackage().getName().substring(23);
                Class<?> craftMentaClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftMetaItem");
                Class<?> serializableMetaClass = Arrays.stream(craftMentaClass.getClasses())
                        .filter(aClass1 -> aClass1.getName().contains("SerializableMeta"))
                        .findAny().get();
                ItemMeta meta = (ItemMeta) serializableMetaClass.getMethod("deserialize", Map.class).invoke(null, serializedMeta);
                serialized.put("meta", meta);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException |
                     IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return ItemStack.deserialize(serialized);
    }

    private Object readValue(JsonReader in) throws IOException {
        switch (in.peek()) {
            case BEGIN_OBJECT:
                Map<String, Object> map = new HashMap<>();
                in.beginObject();
                while (in.hasNext()) {
                    String key = in.nextName();
                    Object value = readValue(in);
                    map.put(key, value);
                }
                in.endObject();
                return map;
            case BEGIN_ARRAY:
                List<Object> list = new ArrayList<>();
                in.beginArray();
                while (in.hasNext()) {
                    Object value = readValue(in);
                    list.add(value);
                }
                in.endArray();
                return list;
            case STRING:
                return in.nextString();
            case NUMBER:
                return in.nextInt();
            case BOOLEAN:
                return in.nextBoolean();
            case NULL:
                in.nextNull();
                return null;
            default:
                throw new IOException("Unsupported JSON element type");
        }
    }
}