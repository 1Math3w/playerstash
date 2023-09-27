package dev.math3w.playerstash.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.inventory.ItemStack;

public class ItemSerializationUtils {
    private static final Gson gson;

    static {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter());
        gson = gsonBuilder.create();
    }

    private ItemSerializationUtils() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }

    /**
     * Serialize an ItemStack to JSON.
     *
     * @param itemStack The ItemStack to serialize.
     * @return The JSON representation of the ItemStack.
     */
    public static String serializeItemStackToJson(ItemStack itemStack) {
        return gson.toJson(itemStack, ItemStack.class);
    }

    /**
     * Deserialize JSON back to an ItemStack.
     *
     * @param json The JSON representation of the ItemStack.
     * @return The deserialized ItemStack.
     */
    public static ItemStack deserializeItemStackFromJson(String json) {
        return gson.fromJson(json, ItemStack.class);
    }
}
