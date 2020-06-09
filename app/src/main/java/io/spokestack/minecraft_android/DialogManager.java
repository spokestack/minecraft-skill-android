package io.spokestack.minecraft_android;

import io.spokestack.minecraft_android.handler.Cookbook;
import io.spokestack.minecraft_android.handler.HandlerInput;
import io.spokestack.minecraft_android.handler.RequestHandler;
import io.spokestack.spokestack.nlu.NLUResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsible for passing user requests through a series of candidate request
 * handlers, selecting the first one that claims to be able to produce a
 * response for the request, and returning that response.
 */
public class DialogManager {

    private List<? extends RequestHandler> requestHandlers;
    private Session session;

    public DialogManager(List<? extends RequestHandler> handlers,
                         Cookbook cookbook) {
        super();
        this.session = new Session(cookbook);
        this.requestHandlers = new ArrayList<>(handlers);
    }

    public Session getSession() {
        return session;
    }

    public Response handleIntent(NLUResult nluResult) {
        HandlerInput handlerInput = new HandlerInput(nluResult, this.session);
        Response response = null;
        for (RequestHandler handler : this.requestHandlers) {
            if (handler.canHandle(handlerInput)) {
                response = handler.handle(handlerInput);
                break;
            }
        }
        updateSession(nluResult, response);
        return response;
    }

    private void updateSession(NLUResult nluResult, Response systemResponse) {
        if (systemResponse != null) {
            this.session.setLastResponse(systemResponse);
        }
        this.session.setLastIntent(nluResult.getIntent());
        this.session.setLastSlots(nluResult.getSlots());
    }
}
