package game2D;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Wave filter fades the volume in and out to give a 'wave' sound effect.
 */
public class WaveFilterStream extends FilterInputStream {

    private boolean quieten;

    WaveFilterStream(InputStream in) {
        super(in);
        quieten = true;
    }

    // Get a value from the array 'buffer' at the given 'position'
    // and convert it into short big-endian format
    public short getSample(byte[] buffer, int position) {
        return (short) (((buffer[position + 1] & 0xff) << 8) |
                (buffer[position] & 0xff));
    }

    // Set a short value 'sample' in the array 'buffer' at the
    // given 'position' in little-endian format
    public void setSample(byte[] buffer, int position, short sample) {
        buffer[position] = (byte) (sample & 0xFF);
        buffer[position + 1] = (byte) ((sample >> 8) & 0xFF);
    }

    public int read(byte[] sample, int offset, int length) throws IOException {
        // Get the number of bytes in the data stream
        int bytesRead = super.read(sample, offset, length);
        // Set the rate of change in volume per sample.
        float change = 2.9f * (1.0f / (float) bytesRead);
        // Start off at full volume
        float volume = 1f;
        short amplitude;
        //	Loop through the sample 2 bytes at a time
        for (int p = 0; p < bytesRead; p = p + 2) {
            // Read the current amplitude (volume)
            amplitude = getSample(sample, p);
            // Reduce it by the relevant volume factor
            amplitude = (short) ((float) amplitude * volume);
            // Set the new amplitude value
            setSample(sample, p, amplitude);
            // Decrease the volume
            if (volume <= 0.2f) {
                quieten = false;
            } else if (volume >= 1f) {
                quieten = true;
            }
            if (quieten) {
                volume = volume - change;
            } else {
                volume = volume + change;
            }
        }
        return length;
    }

}
