package io.spokestack.minecraft_android;

import io.spokestack.minecraft_android.handler.Cookbook;
import io.spokestack.spokestack.nlu.Slot;

import java.util.Map;

/**
 * A data class representing the current conversation state.
 */
public class Session {

    private Cookbook cookbook;
    private Response lastResponse;
    private String lastIntent;
    private Map<String, Slot> lastSlots;

    public Session(Cookbook cookbook) {
        this.cookbook = cookbook;
    }

    public Cookbook getCookbook() {
        return cookbook;
    }

    public Response getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(Response lastResponse) {
        this.lastResponse = lastResponse;
    }

    public String getLastIntent() {
        return lastIntent;
    }

    public void setLastIntent(String lastIntent) {
        this.lastIntent = lastIntent;
    }

    public Map<String, Slot> getLastSlots() {
        return lastSlots;
    }

    public void setLastSlots(Map<String, Slot> lastSlots) {
        this.lastSlots = lastSlots;
    }
}
