package io.spokestack.minecraft_android;

import android.content.Context;
import android.util.Log;
import androidx.lifecycle.Lifecycle;
import io.spokestack.minecraft_android.handler.Cookbook;
import io.spokestack.minecraft_android.handler.ErrorHandler;
import io.spokestack.minecraft_android.handler.ExitHandler;
import io.spokestack.minecraft_android.handler.HelpHandler;
import io.spokestack.minecraft_android.handler.LaunchHandler;
import io.spokestack.minecraft_android.handler.RecipeHandler;
import io.spokestack.minecraft_android.handler.RepeatHandler;
import io.spokestack.minecraft_android.handler.RequestHandler;
import io.spokestack.spokestack.OnSpeechEventListener;
import io.spokestack.spokestack.SpeechContext;
import io.spokestack.spokestack.SpeechPipeline;
import io.spokestack.spokestack.nlu.NLUResult;
import io.spokestack.spokestack.nlu.TraceListener;
import io.spokestack.spokestack.nlu.tensorflow.TensorflowNLU;
import io.spokestack.spokestack.tts.SynthesisRequest;
import io.spokestack.spokestack.tts.TTSEvent;
import io.spokestack.spokestack.tts.TTSListener;
import io.spokestack.spokestack.tts.TTSManager;
import io.spokestack.spokestack.util.EventTracer;

import java.util.Arrays;
import java.util.List;

/**
 * A single point of contact for managing
 */
public class Spokestack implements OnSpeechEventListener, TTSListener, TraceListener {
    private final String logTag = getClass().getSimpleName();

    private SpeechPipeline speechPipeline;
    private TensorflowNLU nlu;
    private DialogManager dialogManager;
    private TTSManager tts;
    private Context appContext;
    private Lifecycle appLifecycle;
    private VoiceUI uiDelegate;

    public Spokestack(Context applicationContext,
                      Lifecycle lifecycle,
                      VoiceUI uiDelegate) {
        this.appContext = applicationContext;
        this.appLifecycle = lifecycle;
        this.uiDelegate = uiDelegate;
    }

    public void start() throws Exception {
        if (this.speechPipeline == null) {
            buildPipeline();
            buildNLU();
            buildDialogManager();
            buildTTS();
        }

        this.speechPipeline.start();
    }

    public void stop() {
        if (this.speechPipeline != null) {
            this.speechPipeline.stop();
        }
    }

    public void launch() {
        if (this.dialogManager != null) {
            NLUResult launch = new NLUResult.Builder("")
                  .withIntent(LaunchHandler.LAUNCH_INTENT)
                  .build();
            handleIntent(launch);
        }
    }

    public void activateAsr() {
        if (this.speechPipeline != null) {
            this.speechPipeline.activate();
        }
    }

    public void deactivateAsr() {
        if (this.speechPipeline != null) {
            this.speechPipeline.deactivate();
        }
    }

    private void buildPipeline() {
        this.speechPipeline = new SpeechPipeline.Builder()
              .useProfile("io.spokestack.spokestack.profile.PushToTalkAndroidASR")
              .setProperty("trace-level", EventTracer.Level.DEBUG.value())
              .setAndroidContext(this.appContext)
              .addOnSpeechEventListener(this)
              .build();
    }

    private void buildNLU() {
        if (this.nlu == null) {
            String cacheDir = this.appContext.getCacheDir().getAbsolutePath();
            this.nlu = new TensorflowNLU.Builder()
                  .setProperty("nlu-model-path", cacheDir + "/nlu.tflite")
                  .setProperty("nlu-metadata-path", cacheDir + "/metadata.json")
                  .setProperty("wordpiece-vocab-path", cacheDir + "/vocab.txt")
                  .setProperty("trace-level", EventTracer.Level.DEBUG.value())
                  .addTraceListener(this)
                  .build();
        }
    }

    private void buildDialogManager() {
        String cacheDir = this.appContext.getCacheDir().getAbsolutePath();

        List<? extends RequestHandler> handlers = Arrays.asList(
              new LaunchHandler(),
              new RecipeHandler(),
              new HelpHandler(),
              new RepeatHandler(),
              new ExitHandler(),
              new ErrorHandler()
        );
        Cookbook cookbook =
              new Cookbook(cacheDir + "/minecraft-recipe-metaphones.json");
        this.dialogManager = new DialogManager(handlers, cookbook);
    }

    private void buildTTS() throws Exception {
        if (this.tts == null) {
            this.tts = new TTSManager.Builder()
                  .setTTSServiceClass("io.spokestack.spokestack.tts.SpokestackTTSService")
                  .setOutputClass("io.spokestack.spokestack.tts.SpokestackTTSOutput")
                  .setProperty("spokestack-id", "f0bc990c-e9db-4a0c-a2b1-6a6395a3d97e")
                  .setProperty(
                        "spokestack-secret",
                        "5BD5483F573D691A15CFA493C1782F451D4BD666E39A9E7B2EBE287E6A72C6B6"
                  )
                  .addTTSListener(this)
                  .setAndroidContext(this.appContext)
                  .setLifecycle(appLifecycle)
                  .build();
        }
    }

    // OnSpeechEventListener

    @Override
    public void onEvent(SpeechContext.Event event, SpeechContext context) throws Exception {
        switch (event) {
            case ACTIVATE:
                Log.v(logTag, "ASR activated");
                this.uiDelegate.asrActivated();
                break;
            case DEACTIVATE:
                Log.v(logTag, "ASR deactivated");
                this.uiDelegate.asrDeactivated();
                break;
            case TIMEOUT:
                Log.i(logTag, "ASR timeout");
                break;
            case ERROR:
                Log.e(logTag, "ASR error", context.getError());
                break;
            case RECOGNIZE:
                String utterance = context.getTranscript();
                Log.v(logTag, "utterance: " + utterance);
                this.uiDelegate.transcriptAvailable(utterance);
                // the speech event occurs off the main thread, so it's fine
                // to treat this like a blocking call
                NLUResult result = this.nlu.classify(utterance).get();
                handleIntent(result);
                break;
            case TRACE:
                break;
        }
    }

    private void handleIntent(NLUResult result) {
        Response response = this.dialogManager.handleIntent(result);
        Log.v(logTag, "response: " + response);
        String thinking = appContext.getString(R.string.thinking_text);
        this.uiDelegate.promptAvailable(thinking);
        speak(response.getPrompt());
    }

    public void speak(String text) {
        SynthesisRequest request = new SynthesisRequest.Builder(text).build();
        this.tts.synthesize(request);
    }

    // NLU TraceListener

    @Override
    public void onTrace(EventTracer.Level level, String message) {
        switch (level) {
            case DEBUG:
                Log.d(logTag, message);
                break;
            case PERF:
                Log.v(logTag, message);
                break;
            case INFO:
                Log.i(logTag, message);
                break;
            case WARN:
                Log.w(logTag, message);
                break;
            case ERROR:
                Log.e(logTag, message);
                break;
        }
    }

    // TTSListener

    @Override
    public void eventReceived(TTSEvent event) {
        switch (event.type) {
            // it might take a second or two to get the audio player set up and
            // playing, so delay displaying the prompt until we have audio
            case PLAYBACK_COMPLETE:
                Response response =
                      this.dialogManager.getSession().getLastResponse();
                if (response.isMicOpen()) {
                    activateAsr();
                }
                break;
            case AUDIO_AVAILABLE:
                response =
                      this.dialogManager.getSession().getLastResponse();
                this.uiDelegate.promptAvailable(response.getPrompt());
                break;
            case ERROR:
                Log.e(logTag, "TTS error: "
                      + event.getError().getLocalizedMessage());
        }
    }
}
