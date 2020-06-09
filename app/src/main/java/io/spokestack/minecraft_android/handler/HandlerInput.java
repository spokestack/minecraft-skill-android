package io.spokestack.minecraft_android.handler;

import io.spokestack.minecraft_android.Session;
import io.spokestack.spokestack.nlu.NLUResult;
import io.spokestack.spokestack.nlu.Slot;

import java.util.Map;

/**
 * A wrapper class for user requests, including intents, slots, and
 * an anemic form of conversation state.
 */
public class HandlerInput {
    private String intent;
    private Map<String, Slot> slots;
    private Session session;

    public HandlerInput(NLUResult nluResult, Session session) {
        this.intent = nluResult.getIntent();
        this.slots = nluResult.getSlots();
        this.session = session;
    }

    public String getIntent() {
        return intent;
    }

    public Map<String, Slot> getSlots() {
        return slots;
    }

    public Session getSession() {
        return session;
    }
}
