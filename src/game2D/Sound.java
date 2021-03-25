package game2D;

import javax.sound.sampled.*;
import java.io.File;

public class Sound extends Thread {

    private final String FILENAME;    // The name of the file to play
    private boolean loop;
    private boolean stopMusic;    // A flag showing that the thread has finished
    private final boolean PAUSABLE;
    private final boolean FILTERED;

    private boolean launched = false;
    boolean playing;

    Clip clip;

    public Sound(String fname, boolean pausable, boolean filtered) {
        this.FILTERED = filtered;
        FILENAME = fname;
        stopMusic = false;
        //playing = false;
        this.PAUSABLE = pausable;

        if (pausable) {
            playing = false;
        }
}

    /**
     * run will play the actual sound but you should not call it directly.
     * You need to call the 'start' method of your sound object (inherited
     * from Thread, you do not need to declare your own). 'run' will
     * eventually be called by 'start' when it has been scheduled by
     * the process scheduler.
     */
    public void run() {
        try {
            launched = true;
            stopMusic = false;

            File file = new File(FILENAME);
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            AudioFormat format = stream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, format);

            if (FILTERED) {
                WaveFilterStream filtered = new WaveFilterStream(stream);
                long streamFrameLength = stream.getFrameLength();
                stream = new AudioInputStream(filtered, format, streamFrameLength);

            }

            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();

            setPlaying(true);

            Thread.sleep(100);

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY); // loop music constantly
            }

            if(stopMusic) {
                clip.close();
            }

        } catch (Exception e) {
			System.out.println(e+"\nThere was a problem when playing the sound: "+ FILENAME);
        }
    }

    /**
     * Utilises Clip.stop() to stop the playback of the clip.
     */
    public void pauseClip() {
        if (PAUSABLE) {
            if (isPlaying()) {
                System.out.println("pausing");
                clip.stop();
                setPlaying(false);
            }
        } else
            System.err.println("Can't pause an unpausable Sound.");
    }

    /**
     * Utilises Clip.start() to start the playback of the clip.
     */
    public void playClip() {
        if (PAUSABLE) {
            if (!isPlaying()) {
                System.out.println("playing");
                clip.start();
                if (loop) {
                    clip.loop(Clip.LOOP_CONTINUOUSLY); // loop music constantly
                }
                setPlaying(true);
            }

        } else
            System.err.println("Can't toggle play an unpausable Sound.");
    }

    public boolean isLaunched() {
        return launched;
    }

    public boolean isPlaying() {
        return playing;
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public boolean isStopMusic() {
        return stopMusic;
    }

    public void setStopMusic(boolean stopMusic){
        this.stopMusic = stopMusic;
    }
}
