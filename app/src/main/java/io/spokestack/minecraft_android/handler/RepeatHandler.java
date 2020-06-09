package io.spokestack.minecraft_android.handler;

import io.spokestack.minecraft_android.Response;

/**
 * Responds to requests to repeat the previous response.
 */
public class RepeatHandler implements RequestHandler {

    private static final String INTENT = "AMAZON.RepeatIntent";

    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.getIntent().equals(INTENT);
    }

    @Override
    public Response handle(HandlerInput handlerInput) {
        String prompt = handlerInput.getSession().getLastResponse().getPrompt();
        return new Response(prompt);
    }
}
