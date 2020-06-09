package io.spokestack.minecraft_android.handler;

import io.spokestack.minecraft_android.Recipe;
import io.spokestack.minecraft_android.Response;
import io.spokestack.minecraft_android.Responses;

/**
 * Handles launch requests, returning a welcome message.
 */
public class LaunchHandler implements RequestHandler {

    public static final String LAUNCH_INTENT = "LaunchIntent";

    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.getIntent().equals(LAUNCH_INTENT);
    }

    @Override
    public Response handle(HandlerInput handlerInput) {
        Cookbook cookbook = handlerInput.getSession().getCookbook();
        Recipe recipe = cookbook.randomRecipe();
        String prompt = Responses.WELCOME.formatPrompt(recipe.getName());
        return new Response(prompt);
    }
}
