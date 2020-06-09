package io.spokestack.minecraft_android;

/**
 * A simple data class to aid JSON deserialization.
 */
public class Recipe {
    private String name;
    private String recipe;

    public Recipe(String name, String recipe) {
        this.name = name;
        this.recipe = recipe;
    }

    public String getName() {
        return name;
    }

    public String getRecipe() {
        return recipe;
    }
}
