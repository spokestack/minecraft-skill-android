package io.spokestack.minecraft_android;

/**
 * Interface representing voice events that should be reflected in the UI.
 */
public interface VoiceUI {

    /**
     * ASR has been activated, so the app is actively listening to the user.
     */
    void asrActivated();

    /**
     * ASR has been deactivated, so the app is no longer actively listening.
     */
    void asrDeactivated();

    /**
     * ASR has returned a transcript ready to be displayed.
     */
    void transcriptAvailable(String transcript);

    /**
     * Our dialog manager has produced a formatted prompt that can be displayed
     * while it is read aloud.
     */
    void promptAvailable(String prompt);
}
