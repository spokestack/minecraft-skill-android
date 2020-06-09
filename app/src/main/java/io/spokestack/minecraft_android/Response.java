package io.spokestack.minecraft_android;

import org.jetbrains.annotations.NotNull;

/**
 * A simple class representing a system response to a user request.
 */
public class Response {

    private final String prompt;

    /**
     * Create a new response.
     * @param prompt The prompt to deliver to the user.
     */
    public Response(String prompt) {
        this.prompt = prompt;
    }

    public String getPrompt() {
        return prompt;
    }

    @NotNull
    @Override
    public String toString() {
        return "Response{" +
              "prompt='" + prompt + '\'' +
              '}';
    }
}
