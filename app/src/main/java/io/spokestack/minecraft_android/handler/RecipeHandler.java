package io.spokestack.minecraft_android.handler;

import io.spokestack.minecraft_android.Levenshtein;
import io.spokestack.minecraft_android.Recipe;
import io.spokestack.minecraft_android.Response;
import io.spokestack.minecraft_android.Responses;
import io.spokestack.spokestack.nlu.Slot;
import org.apache.commons.codec.language.DoubleMetaphone;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * The intent handler for resolving recipes from user searches.
 */
public class RecipeHandler implements RequestHandler {

    private static final String INTENT = "RecipeIntent";
    private final DoubleMetaphone encoder;

    public RecipeHandler() {
        this.encoder = new DoubleMetaphone();
        // predetermined based on the max length of our encoded recipe names
        this.encoder.setMaxCodeLen(13);
    }

    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.getIntent().equals(INTENT);
    }

    @Override
    public Response handle(HandlerInput handlerInput) {
        String prompt = Responses.RECIPE_NOT_FOUND_WITHOUT_ITEM_NAME
              .formatPrompt();

        Slot itemSlot = handlerInput.getSlots().get("Item");
        // If we don't have a value for the Item slot, we can't repeat the
        // search term in our response, so bail now.
        if (itemSlot == null || itemSlot.getRawValue() == null) {
            return new Response(prompt);
        }

        String recipeName = (String) itemSlot.getValue();
        Cookbook cookbook = handlerInput.getSession().getCookbook();

        // The interesting search logic is in getRecipe; see below.
        String recipe = getRecipe(cookbook, recipeName);

        // We have a search term, but nothing was phonetically close enough
        // to what the ASR gave us to reliably turn into a recipe name;
        // tell the user what we heard but that we couldn't make sense of it.
        if (recipe == null) {
            prompt = Responses.RECIPE_NOT_FOUND_WITH_ITEM_NAME
                  .formatPrompt(recipeName);
            return new Response(prompt);
        }

        // Success! Give the user their recipe and close the mic.
        return new Response(recipe, false);
    }

    private String getRecipe(Cookbook cookbook, String recipeName) {
        String normalized = recipeName.toLowerCase();
        List<Recipe> candidates = cookbook.lookup(normalized);
        if (candidates != null) {
            // exact match; our job is easy
            return candidates.get(0).getRecipe();
        }

        // We didn't get an exact match, so try to get as close as possible.
        // in order to do this, we'll use the Double Metaphone representation
        // of the ASR result.
        // (see https://en.wikipedia.org/wiki/Metaphone)
        //
        // We're removing spaces in order to collapse geminate (doubled)
        // consonants - for example, the Minecraft invented term
        // "dark prismarine" is often recognized as "dark prism Marine" by
        // ASRs, and we want to get "TRKPRSMRN" as the metaphone, excluding the
        // extra "m" sound returned by the ASR.
        String metaphone = encoder.doubleMetaphone(
              normalized.replaceAll(" ", ""));
        candidates = cookbook.lookup(metaphone);

        // If we can find a phonetic match, we'll go through the full names
        // that reduce to that metaphone and look for the closest match using
        // Levenshtein edit distance between the search term as recognized by
        // ASR and the original recipe names.
        // (see https://en.wikipedia.org/wiki/Levenshtein_distance)
        if (candidates != null) {
            SortedMap<Integer, Recipe> distanceMap = new TreeMap<>();

            for (Recipe recipe : candidates) {
                int distance =
                      Levenshtein.distance(normalized, recipe.getName());
                distanceMap.put(distance, recipe);
            }
            int lowest = distanceMap.firstKey();
            return distanceMap.get(lowest).getRecipe();
        }

        // We didn't find even a phonetic match. Bummer.
        return null;
    }
}
