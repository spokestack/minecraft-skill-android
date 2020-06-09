package io.spokestack.minecraft_android.handler;

import io.spokestack.minecraft_android.Response;
import io.spokestack.minecraft_android.Responses;

/**
 * TODO: Document me
 */
public class ExitHandler implements RequestHandler {

    private static final String INTENT = "ExitIntent";

    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.getIntent().equals(INTENT);
    }

    @Override
    public Response handle(HandlerInput handlerInput) {
        String prompt = Responses.STOP_MESSAGE.formatPrompt();
        return new Response(prompt);
    }
}
