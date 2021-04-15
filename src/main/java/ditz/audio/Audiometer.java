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

        BoundedRangeModel freqModel = new DefaultBoundedRangeModel(0, 0, -36, 48);
        freqModel.addChangeListener(ev->setFrequency(freqModel.getValue()));
        setFrequency(freqModel.getValue());
        
        add(audiogramms(freqModel));
    }

    private void setFrequency(int value) {
        int frequency = (int) (1000 * Math.pow(2, value / 12.0));
        generator.setFrequency(frequency);
    }


    private Component audiogramms(BoundedRangeModel freqModel) {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(new Audiogramm(freqModel, "left",
                generator::enableLeft, generator::setLeftGain), BorderLayout.LINE_START);

        panel.add(Box.createRigidArea(new Dimension(10, 0)), BorderLayout.CENTER);

        panel.add(new Audiogramm(freqModel, "right",
                generator::enableRight, generator::setRightGain), BorderLayout.LINE_END);

        return panel;
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
