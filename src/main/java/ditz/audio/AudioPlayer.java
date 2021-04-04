package ditz.audio;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * Created by IntelliJ IDEA.
 * User: stueken
 * Date: 04.04.21
 * Time: 15:13
 */
public class AudioPlayer {

    public static final int SAMPLE_FREQUENCY = 44100;
    static final int SAMPLE_RATE = SAMPLE_FREQUENCY*2;
    static final int RANGE = Short.MAX_VALUE - Short.MIN_VALUE;

    final AudioFormat format;
    final SourceDataLine line;
    long sample = 0;
    long buffers = 0;

    final byte[] bits;
    final ShortBuffer buffer;

    public static AudioPlayer open() {
        try {
            return new AudioPlayer();
        } catch (LineUnavailableException error) {
            throw new RuntimeException(error);
        }
    }

    public int sampleFrequency() {
        return SAMPLE_FREQUENCY;
    }

    public void start() {
        line.start();
    }

    public void stop() {
        line.stop();
    }

    public AudioPlayer() throws LineUnavailableException {

        this.format = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                (float)SAMPLE_RATE, // sample rate
                16, // bits
                2,  // channels
                4,  // frameSize
                (float)SAMPLE_RATE,
                ByteOrder.nativeOrder()==ByteOrder.LITTLE_ENDIAN); // endian
        DataLine.Info info = new DataLine.Info(
                SourceDataLine.class,
                format);
        int bufferSize; // = SAMPLE_RATE*frameSize/10;
        line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(format);
        bufferSize = line.getBufferSize();
        bits = new byte[bufferSize];
        buffer = ByteBuffer.wrap(bits).asShortBuffer();
        //line.start();
    }

    public void flush() {
        int samples = buffer.position();
        line.write(this.bits, 0, samples*Short.BYTES);
        buffer.rewind();
        ++buffers;
    }

    // valid range is between [+1,-1]
    public void write(double left, double right) {

        if(Math.max(Math.abs(left), Math.abs(right))>1.0)
            throw new IllegalStateException();

        short bits = (short) Math.floor(left*RANGE/2);
        buffer.put(bits);

        bits = (short) Math.floor(right*RANGE/2);
        buffer.put(bits);

        if(!buffer.hasRemaining())
            flush();

        ++sample;
    }
}
