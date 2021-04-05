package ditz.audio;

/**
 * Created by IntelliJ IDEA.
 * User: stueken
 * Date: 04.04.21
 * Time: 14:43
 */
public class Generator implements Runnable {

    float frequency = 440;
    float gain = 1;
    float pan = 1;

    boolean left = true;
    boolean right = true;
    boolean pulse = false;

    long step = 0;
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

    static final int NPI = AudioPlayer.SAMPLE_FREQUENCY/20;
    float[] data = new float[2*NPI];

    AudioPlayer player = AudioPlayer.open();

    Thread thread = null;
    
    public Generator() {
        for(int i=0; i<2*NPI; ++i) {
            double value = Math.sin(i*Math.PI/NPI);
            data[i] = (float)value;
        }
    }

    public void start() {
        if(thread==null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if(thread!=null) {
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
        try {
            player.start();
            while (!Thread.currentThread().isInterrupted()) {
                play();
            }
        } catch (Throwable error) {
            handleError(error);
        } finally {
            player.stop();
        }
    }

    double sinus(double p) {
        return Math.sin(p*Math.PI/NPI);
    }

    void play() {

        int sampleFrequency = player.sampleFrequency();
        ++step;

        if(pulse && (step% sampleFrequency > sampleFrequency/2)) {
            player.write(0,0);
            return;
        }

        double value = gain*sinus(phase * NPI);
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
