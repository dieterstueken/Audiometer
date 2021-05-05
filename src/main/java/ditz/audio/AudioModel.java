package ditz.audio;

import javax.swing.*;
import java.awt.event.ItemEvent;

/**
 * Created by IntelliJ IDEA.
 * User: stueken
 * Date: 18.04.21
 * Time: 12:26
 */
public class AudioModel extends DefaultBoundedRangeModel {

    final FrequencyModel freqModel;

    final AudioChannel channel;

    final GainModel gainModel;

    final int[] audiogramm;

    AudioModel(FrequencyModel freqModel, GainModel gainModel, AudioChannel channel) {
        super(0, 0, -130, 10);
        this.audiogramm = new int[freqModel.numChannels()];

        this.channel = channel;
        this.gainModel = gainModel;
        this.freqModel = freqModel;

        freqModel.addChangeListener(this::dataChanged);
        gainModel.addChangeListener(this::dataChanged);

        addChangeListener(this::valueChanged);

        updateGain();
    }

    public String getName() {
        return channel.getName();
    }

    public void enable(boolean enabled) {
        channel.enable(enabled);
    }

    public void enable(ItemEvent ev) {
        enable(ev.getStateChange() == ItemEvent.SELECTED);
    }

    public boolean isEnabled() {
        return channel.isEnabled();
    }

    public FrequencyModel getFreqModel() {
        return freqModel;
    }

    public float getFreq() {
        return freqModel.getFreq();
    }

    public int getLimit() {
        return gainModel.getValue();
    }
    
    @Override
    public void setValueIsAdjusting(boolean b) {
        super.setValueIsAdjusting(b);
    }

    @Override
    public void setValue(int n) {
        super.setValue(n);
    }

    public int getLoss() {
        int channel = freqModel.getChannel();
        return getLoss(channel);
    }

    public int getLoss(int channel) {
        return audiogramm[channel] - gainModel.bias;
    }

    public void setLoss(int channel, int loss) {
        audiogramm[channel] = loss + gainModel.bias;
        updateGain();
    }

    /**
     * Update the slider value after data changed.
     * @param dummy possible change event
     */
    public void dataChanged(Object dummy) {
        updateGain();
        int channel = freqModel.getChannel();
        int value = -getLoss(channel);
        setValue(value);
    }

    /**
     * Update the loss and gain if the slider was moved.
     * @param dummy possible change event
     */
    public void valueChanged(Object dummy) {
        int channel = freqModel.getChannel();
        int loss = -getValue();
        setLoss(channel, loss);
    }

    public void incrementLoss(int delta) {
        int channel = freqModel.getChannel();
        int loss = getLoss(channel);
        setLoss(channel, loss+delta);
        dataChanged(null);
    }

    public void incrementFreq(int delta) {
        int channel = freqModel.getValue();
        freqModel.setValue(channel+delta);
    }

    public void updateGain() {
        setGain(getLoss());
    }

    private void setGain(int loss) {
        float gain = (float)Math.pow(10, (loss - this.gainModel.getValue())/20.0);
        this.channel.setGain(gain);
    }

}
