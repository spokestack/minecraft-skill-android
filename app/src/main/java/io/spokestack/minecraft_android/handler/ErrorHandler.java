package io.spokestack.minecraft_android.handler;

import io.spokestack.minecraft_android.Response;
import io.spokestack.minecraft_android.Responses;

/**
 * The fallback request handler for when nothing else works.
 */
public class ErrorHandler implements RequestHandler {
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return true;
    }

    @Override
    public Response handle(HandlerInput handlerInput) {
        String prompt = Responses.ERROR.formatPrompt();
        return new Response(prompt);
    }
}
