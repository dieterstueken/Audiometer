package ditz.audio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.function.Consumer;

/*
 * file           :  $RCSfile: $
 * version        :  $Revision: $
 * created by     :  stueken
 * date created   :  07.06.2008, 18:49:51
 * last mod by    :  $Author: $
 * date last mod  :  $Date: $
 *
 */

public class Audiometer extends JPanel {

    final Generator generator;

    public Audiometer() {

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));



        generator = new Generator() {
            public void handleError(Throwable error) {
                Audiometer.this.handleError(error);
            }
        };

        SliderPanel gainControl = new SliderPanel("gain: ", " dB", -120, 0, -25) {
            public void setValue(int value) {
                super.setValue(value);
                generator.setGain(value);
            }
        };

        SliderPanel balanceControl = new SliderPanel("balance: ", " dB", -40, 40, 0) {
            public void setValue(int value) {
                super.setValue(value);
                generator.setBalance(value);
            }
        };

        SliderPanel freqControl = new SliderPanel("frequency: ", " Hz", -36, 48, 0) {
            public void setValue(int value) {
                int frequency = (int) (1000 * Math.pow(2, value / 12.0));
                super.setValue(frequency);
                generator.setFrequency(frequency);
            }
        };

        add(audiogramms(freqControl.model));

        add(gainControl);
        add(balanceControl);
        add(freqControl);

        JPanel buttons = new JPanel();
        buttons.add(checkbox("left", generator::enableLeft, true));
        buttons.add(checkbox("pulse", generator::enablePulse, false));
        buttons.add(checkbox("right", generator::enableRight, true));

        add(buttons);
    }


    private Component audiogramms(BoundedRangeModel freqModel) {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(audiogramm(freqModel), BorderLayout.LINE_START);
        panel.add(Box.createRigidArea(new Dimension(10, 0)), BorderLayout.CENTER);
        panel.add(audiogramm(freqModel), BorderLayout.LINE_END);

        return panel;
    }

    private Component audiogramm(BoundedRangeModel freqModel) {
        return new Audiogramm(freqModel);
    }

    JComponent checkbox(String name, Consumer<Boolean> enable, boolean enabled) {
        JCheckBox checkbox = new JCheckBox(name);
        checkbox.addItemListener(ev -> enable.accept(checkbox.isSelected()));
        checkbox.setSelected(enabled);
        enable.accept(enabled);
        return checkbox;
    }

    void open() {
        JFrame frame = new JFrame("Audiometer");

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                generator.stop();
            }
        });

        frame.setContentPane(this);
        frame.pack();
        frame.setVisible(true);

        generator.start();
    }

    public void handleError(Throwable error) {
        error.printStackTrace();
    }

    public static void main(String args[]) {

        final Audiometer audiometer = new Audiometer();

        SwingUtilities.invokeLater(audiometer::open);
    }
}
