package ditz.audio;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: stueken
 * Date: 04.04.21
 * Time: 14:43
 */
public class Generator implements Runnable {

    static final Logger LOGGER = Logger.getLogger(Generator.class.getName());
    static final Level LEVEL = Level.FINE;

    static double sinus(double p) {
        return Math.sin(p*2*Math.PI);
    }

    public class Channel implements AudioChannel {

        final String name;

        boolean enabled = false;
        float gain = 1;

        public Channel(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }

        @Override
        public void enable(boolean enabled) {
            this.enabled = enabled;
        }

        @Override
        public float getFrequency() {
            return Generator.this.frequency;
        }

        public void setFrequency(float frequency) {
            Generator.this.setFrequency(frequency);
        }

        @Override
        public float getGain() {
            return gain;
        }

        @Override
        public void setGain(float gain) {
            this.gain = gain;
        }

        double value() {
            return enabled ? gain * sinus(phase) : 0;
        }
    }

    float frequency = 0;

    final Channel left= new Channel("left"), right= new Channel("right");

    long count = 0;
    float phase = 0;

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    AudioPlayer player = AudioPlayer.open();

    Thread thread = null;
    
    public Generator(float frequency, boolean enabled) {
        this.frequency = frequency;
        left.enabled = right.enabled = enabled;
    }

    public void start() {
        LOGGER.log(LEVEL,"start");

        if(thread==null) {
            thread = new Thread(this, "Generator");
            thread.start();
        }
    }

    public void stop() {
        LOGGER.log(LEVEL,"stop");

        Thread thread = this.thread;
        if(thread!=null) {
            this.thread = null;
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException interrupt) {
                interrupt.printStackTrace();
            }
        }
    }

    public void handleError(Throwable error) {

    }

    public void run() {
        LOGGER.log(LEVEL,"run");

        try {
            player.start();
            Thread current = Thread.currentThread();
            while (this.thread==current && !current.isInterrupted()) {
                play();
            }
        } catch (Throwable error) {
            handleError(error);
        } finally {
            player.stop();
            LOGGER.log(LEVEL,"stopped");
        }
    }

    void play() {

        int sampleFrequency = player.sampleFrequency();
        player.write(left.value(), right.value());

        float next = phase + frequency / sampleFrequency;
        next -= Math.floor(next);
        phase = next;
        ++count;
    }
}
