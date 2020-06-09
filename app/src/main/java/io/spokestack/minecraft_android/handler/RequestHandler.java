package io.spokestack.minecraft_android.handler;

import io.spokestack.minecraft_android.Response;

/**
 * A simple interface for classes that are capable of generating responses
 * to user requests.
 */
public interface RequestHandler {

    public boolean canHandle(HandlerInput handlerInput);
    public Response handle(HandlerInput handlerInput);
}
