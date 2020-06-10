package io.spokestack.minecraft_android;

import org.jetbrains.annotations.NotNull;

/**
 * A simple class representing a system response to a user request.
 */
public class Response {

    private final String prompt;
    private final boolean micOpen;

    /**
     * Create a new response. Leaves the mic open after the prompt by default.
     * @param prompt The prompt to deliver to the user.
     */
    public Response(String prompt) {
        this(prompt, true);
    }

    /**
     * Create a new response.
     * @param prompt The prompt to deliver to the user.
     * @param micOpen Flag for whether the mic should be left open (and ASR
     *                active) after the prompt is read.
     */
    public Response(String prompt, boolean micOpen) {
        this.prompt = prompt;
        this.micOpen = micOpen;
    }

    public String getPrompt() {
        return prompt;
    }

    public boolean isMicOpen() {
        return micOpen;
    }

    @NotNull
    @Override
    public String toString() {
        return "Response{" +
              "prompt='" + prompt + '\'' +
              "micOpen='" + micOpen + '\'' +
              '}';
    }
}
