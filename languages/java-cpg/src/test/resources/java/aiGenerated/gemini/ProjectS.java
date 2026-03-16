import java.util.ArrayList;
import java.util.List;

/**
 * PROJECT S: Podcast Streamer
 * PLAGIARISM: Steals the Event-Driven logic from ElevatorController.
 * - 'ElevatorListener' becomes 'PlaybackListener'
 * - 'call(floor)' becomes 'skipTo(episode)'
 * - 'doorsOpen' becomes 'isBuffering'
 * * DEAD CODE: Contains hardware-specific checks (Door Obstruction, Fire Alarm)
 * inside a software media player.
 */
public class PodcastStreamer {

    private int currentEpisode = 0;
    // PLAGIARISM: 'doorsOpen' logic mapped to 'isBuffering'
    // Logic: You can't move while doors are open -> You can't play while buffering
    private boolean isBuffering = false;

    // PLAGIARISM: Identical Listener list structure
    private List<PlaybackListener> subscribers = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("--- AudioFly Player v2.1 ---");
        PodcastStreamer player = new PodcastStreamer();

        // PLAGIARISM: Anonymous inner class usage identical to Project R
        player.subscribe(new PlaybackListener() {
            @Override
            public void onTrackChange(int trackId) {
                System.out.println("Now Playing: Episode " + trackId);
            }

            @Override
            public void onBufferState(boolean buffering) {
                System.out.println("Status: " + buffering);
            }
        });

        player.skipTo(5);
        player.skipTo(2);

        //DeadCodeStart
        // EVIDENCE OF THEFT:
        // Why would a podcast player check for a Fire Alarm?
        // Because it was copied from the ElevatorController.
        boolean emergencyOverride = false;
        if (emergencyOverride) {
            System.out.println("EMERGENCY DESCENT"); // Dead giveaway term "Descent"
            player.currentEpisode = 0;
        }
        //DeadCodeEnd
    }

    public void subscribe(PlaybackListener l) {
        subscribers.add(l);
    }

    public void skipTo(int targetEpisode) {
        if (isBuffering) stopBuffering();

        System.out.println("Skipping to " + targetEpisode + "...");

        // PLAGIARISM: Identical 'while' loop movement logic
        // Elevators move 1 floor at a time; Podcasts don't usually 'scroll' tracks like this
        while (currentEpisode != targetEpisode) {
            if (currentEpisode < targetEpisode) currentEpisode++;
            else currentEpisode--;

            notifyTracks();
        }
        startBuffering();
    }

    private void startBuffering() {
        this.isBuffering = true;
        for (PlaybackListener l : subscribers) l.onBufferState(true);
    }

    private void stopBuffering() {
        //DeadCodeStart
        // EVIDENCE OF THEFT:
        // This is the 'closeDoors' safety check from the Elevator.
        // It checks for "Physical Obstruction" in a digital buffer.
        int opticalSensor = 0;
        if (opticalSensor > 5) {
            System.out.println("Door blocked."); // "Door" in a podcast app?
            return;
        }
        //DeadCodeEnd

        this.isBuffering = false;
        for (PlaybackListener l : subscribers) l.onBufferState(false);
    }

    private void notifyTracks() {
        for (PlaybackListener l : subscribers) l.onTrackChange(currentEpisode);
    }

    // --- Inner Interface ---
    interface PlaybackListener {
        void onTrackChange(int trackId);    // Was 'onFloorChanged'

        void onBufferState(boolean buffering); // Was 'onDoors'
    }
}
