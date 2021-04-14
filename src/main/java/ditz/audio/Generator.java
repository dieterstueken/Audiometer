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

    float gainLeft = 1;
    float gainRight = 1;

    boolean left = true;
    boolean right = true;

    long count = 0;
    float phase = 0;

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public void setLeftGain(float dB) {
        this.gainLeft = (float)Math.pow(10, dB/20);
    }

    public void enableLeft(boolean enable) {
        left = enable;
    }

    public void setRightGain(float dB) {
        this.gainRight = (float)Math.pow(10, dB/20);
    }

    public void enableRight(boolean enable) {
        right = enable;
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

        double value = sinus(phase);
        double leftValue = this.left ? value * gainLeft : 0;
        double rightValue = this.right? value * gainRight : 0;

        player.write(leftValue, rightValue);

        float next = phase + frequency / sampleFrequency;
        next -= Math.floor(next);
        phase = next;
    }
}
