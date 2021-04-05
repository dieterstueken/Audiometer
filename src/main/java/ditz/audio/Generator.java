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

    float frequency = 0;
    float gain = 1;
    float pan = 1;

    boolean left = true;
    boolean right = true;
    boolean pulse = false;

    long count = 0;
    float phase = 0;

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public void setGain(float dB) {
        this.gain = (float)Math.pow(10, dB/20);
    }

    public void setBalance(float dB) {
        this.pan = (float)Math.pow(10, dB/20);
    }

    public void enableLeft(boolean enable) {
        left = enable;
    }

    public void enableRight(boolean enable) {
        right = enable;
    }

    public void enablePulse(boolean enable) {
        pulse = enable;
    }

    AudioPlayer player = AudioPlayer.open();

    Thread thread = null;
    
    public Generator() {
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

    double sinus(double p) {
        return Math.sin(p*2*Math.PI);
    }

    void play() {

        int sampleFrequency = player.sampleFrequency();
        ++count;

        if(pulse && (count % sampleFrequency > sampleFrequency/2)) {
            player.write(0,0);
            return;
        }

        double value = gain*sinus(phase);
        double left = value;
        double right = value;

        if(pan>1)
            left /= pan;
        else
            right *= pan;

        player.write(this.left?left:0, this.right?right:0);

        float next = phase + frequency / sampleFrequency;
        next -= Math.floor(next);
        phase = next;
    }
}
