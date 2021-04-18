package ditz.audio;

import java.util.Arrays;
import java.util.function.IntUnaryOperator;

/**
 * Created by IntelliJ IDEA.
 * User: stueken
 * Date: 18.04.21
 * Time: 12:26
 */
public class AudioModel {

    final FrequencyModel freqModel;

    final AudioChannel channel;

    final int[] audiogramm;

    AudioModel(FrequencyModel freqModel, AudioChannel channel) {
        this.channel = channel;
        this.freqModel = freqModel;
        this.audiogramm = new int[freqModel.numChannels()];

        channel.setGain(getLoss());
    }

    public String getName() {
        return channel.getName();
    }

    public void enable(boolean enabled) {
        channel.enable(enabled);
    }

    public boolean isEnabled() {
        return channel.isEnabled();
    }

    public FrequencyModel getFreqModel() {
        return freqModel;
    }

    public float getFreq() {
        return channel.getFrequency();
    }

    public int getLoss(int channel) {
        return audiogramm[channel];
    }

    public void setAudiogramm(IntUnaryOperator data) {
        Arrays.setAll(this.audiogramm, data);
    }

    public int getLoss() {
        int channel = freqModel.getChannel();
        return getLoss(channel);
    }

    public void setLoss(int channel, int loss) {
        audiogramm[channel] = loss;
    }

    public void setLoss(int loss) {
        int channel = freqModel.getChannel();
        setLoss(channel, loss);
        setGain(loss);
    }

    private void setGain(int loss) {
        float gain = (float)Math.pow(10, -(loss+10)/20.0);
        this.channel.setGain(gain);
    }
}
