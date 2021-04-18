package ditz.audio;

import javax.swing.*;
import javax.swing.event.ChangeEvent;

/**
 * Created by IntelliJ IDEA.
 * User: stueken
 * Date: 18.04.21
 * Time: 19:35
 */
public class FrequencyModel extends DefaultBoundedRangeModel {

    public static final int KHZ = 1000;
    public static final int CHANNELS_PER_KHZ = 12;
    public static final int KHZ_CHANNEL = 3*CHANNELS_PER_KHZ;
    public static final int NUM_CHANNELS = 7*CHANNELS_PER_KHZ;

    public static float toFreq(int channel) {
        return FrequencyModel.KHZ * (float) Math.pow(2, (float)(channel- FrequencyModel.KHZ_CHANNEL) / FrequencyModel.CHANNELS_PER_KHZ);
    }

    public static int toChannel(float freq) {
        double channel = FrequencyModel.CHANNELS_PER_KHZ * Math.log(freq/ FrequencyModel.KHZ)/Math.log(2) + FrequencyModel.KHZ_CHANNEL;
        return (int) channel;
    }

    final Generator generator;

    public FrequencyModel(Generator generator) {
        super(toChannel(generator.frequency), 0, 0, NUM_CHANNELS);
        this.generator = generator;
        addChangeListener(this::updateFrequency);
        generator.setFrequency(getFreq());
    }

    public int numChannels() {
        return NUM_CHANNELS;
    }

    public int getChannel() {
        return getValue();
    }

    public float getFreq() {
        return toFreq(getChannel());
    }

    private void updateFrequency(ChangeEvent ev) {
        generator.setFrequency(getFreq());
    }
}
