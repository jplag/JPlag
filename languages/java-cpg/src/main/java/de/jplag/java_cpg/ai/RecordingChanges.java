package de.jplag.java_cpg.ai;

/**
 * Class to keep track of whether changes are being recorded between multiple ai Instances.
 * @author ujiqk
 * @version 1.0
 */
public class RecordingChanges {

    private boolean recording;

    /**
     * Constructor for RecordingChanges value holder.
     * @param recordingChanges whether changes are being recorded.
     */
    public RecordingChanges(boolean recordingChanges) {
        this.recording = recordingChanges;
    }

    /**
     * @return whether changes are being recorded.
     */
    public boolean isRecording() {
        return recording;
    }

    /**
     * @param recordingChanges sets whether changes are being recorded.
     */
    public void setRecording(boolean recordingChanges) {
        this.recording = recordingChanges;
    }

}
