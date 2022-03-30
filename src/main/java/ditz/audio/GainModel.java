package ditz.audio;

import javax.swing.*;
import java.awt.event.ItemEvent;

/**
 * Created by IntelliJ IDEA.
 * User: stueken
 * Date: 05.05.21
 * Time: 10:31
 */
public class GainModel extends DefaultBoundedRangeModel {

    public GainModel() {
        super(110, 0, 0, 130);
    }

    @Override
    public void setValue(int n) {
        if(coupled)
            bias -= n - getValue();

        super.setValue(n);
    }

    boolean coupled = false;

    public void couple(boolean coupled) {
        this.coupled = coupled;
    }

    public void couple(ItemEvent ev) {
        couple(ev.getStateChange() == ItemEvent.SELECTED);
    }

    // loss value offset.
    int bias = 0;

    public String getLabel() {
        return String.format("%d dB", -1*getValue());
    }
}
