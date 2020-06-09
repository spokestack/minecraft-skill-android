package io.spokestack.minecraft_android.handler;

import io.spokestack.minecraft_android.Response;
import io.spokestack.minecraft_android.Responses;

/**
 * Responds to help requests.
 */
public class HelpHandler implements RequestHandler {

    private static final String INTENT = "AMAZON.HelpIntent";

    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.getIntent().equals(INTENT);
    }

    @Override
    public Response handle(HandlerInput handlerInput) {
        String prompt = Responses.HELP_MESSAGE.formatPrompt();
        return new Response(prompt);
    }
}
