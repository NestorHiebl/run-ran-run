package Audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class AudioPlayer {

    private Clip clip;

    public AudioPlayer(String s) {
        try {
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File(s));

            AudioFormat baseFormat = inputStream.getFormat();
            AudioFormat decodeFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    baseFormat.getSampleRate(),
                    16,
                    baseFormat.getChannels(),
                    baseFormat.getChannels() * 2,
                    baseFormat.getSampleRate(),
                    false);
            AudioInputStream decodedInputStream = AudioSystem.getAudioInputStream(decodeFormat, inputStream);

            clip = AudioSystem.getClip();
            clip.open(decodedInputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void play() {
        if (clip == null) {
            return;
        }
        stop();
        clip.setFramePosition(0);
        clip.start();
    }

    public void stop() {
        if (clip.isRunning()) {
            clip.stop();
        }
    }

    public void close() {
        stop();
        clip.close();
    }
}
