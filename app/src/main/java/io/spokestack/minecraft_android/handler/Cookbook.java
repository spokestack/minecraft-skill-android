package io.spokestack.minecraft_android.handler;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import io.spokestack.minecraft_android.Recipe;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A class that provides centralized access to our recipe dataset.
 */
public class Cookbook {
    private Map<String, List<Recipe>> recipes;
    private String[] names;

    public Cookbook(String cookbookPath) {
        this.recipes = load(cookbookPath);
        this.names = this.recipes.keySet().toArray(new String[0]);
    }

    public List<Recipe> lookup(String name) {
        return this.recipes.get(name);
    }

    public Recipe randomRecipe() {
        Random random = new Random();
        int nameIdx = random.nextInt(recipes.size());
        String name = this.names[nameIdx];
        List<Recipe> recipes = this.recipes.get(name);
        return recipes.get(random.nextInt(recipes.size()));
    }

    private Map<String, List<Recipe>> load(String cookbookPath) {
        // We've preprocessed our data to include both full recipe names
        // and a phonetic search equivalent as keys to make search quicker.
        // This has been done in a naive fashion that makes the file larger,
        // but we're not dealing with big data here.
        Map<String, List<Recipe>> recipes = new HashMap<>();
        try (FileReader fileReader = new FileReader(cookbookPath);
             JsonReader reader = new JsonReader(fileReader)) {
            Gson gson = new Gson();
            Type jsonType =
                  new TypeToken<Map<String, List<Recipe>>>() {}.getType();
            recipes = gson.fromJson(reader, jsonType);

        } catch (IOException e) {
            // no fancy error handling here; this should be hardened before
            // actual use
            e.printStackTrace();
        }
        return recipes;
    }
}
