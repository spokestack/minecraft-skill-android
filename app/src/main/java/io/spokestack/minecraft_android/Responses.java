package io.spokestack.minecraft_android;

/**
 * A hardcoded list of response templates.
 */
public enum Responses {

    WELCOME(
          "Welcome to Minecraft Helper. You can ask a question like, \"What's the recipe for a %s?\" What can I help you with?"),
    ERROR(
          "Sorry, I can't understand the command. Please say it again."),
    HELP_MESSAGE(
          "You can ask questions such as, what's the recipe for a %s, or, you can say exit. Now, what can I help you with?"),
    STOP_MESSAGE("Goodbye!"),
    RECIPE_NOT_FOUND_WITH_ITEM_NAME(
          "I'm sorry, I currently do not know the recipe for %s."),
    RECIPE_NOT_FOUND_WITHOUT_ITEM_NAME(
          "I'm sorry, I currently do not know that recipe.");

    private String prompt;

    Responses(String prompt) {
        this.prompt = prompt;
    }

    public String formatPrompt(String ... params) {
        return String.format(this.prompt, (Object[]) params);
    }
}
