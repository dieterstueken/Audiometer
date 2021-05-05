package ditz.audio;

/**
 * Created by IntelliJ IDEA.
 * User: stueken
 * Date: 18.04.21
 * Time: 19:16
 */
public interface AudioChannel {

    String getName();

    boolean isEnabled();

    void enable(boolean enabled);

    float getGain();

    void setGain(float gain);
}
