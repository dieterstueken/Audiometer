package ditz.audio;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;

abstract class SliderPanel extends JPanel
{

    final JLabel vLabel;

    SliderPanel(String title, String unit, int min, int max, int value) {
        super(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;

        JLabel label = new JLabel(title, JLabel.CENTER);
        add(label, c);

        vLabel = new JLabel();
        vLabel.setPreferredSize(new Dimension(40, 10));
        vLabel.setHorizontalAlignment(JLabel.RIGHT);
        c.gridx = 1;
        add(vLabel, c);

        label = new JLabel(unit, JLabel.LEFT);
        c.gridx = 2;
        add(label, c);

        JSlider slider = new  JSlider(JSlider.HORIZONTAL, min, max, value);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        slider.addChangeListener(this::stateChanged);

        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        add(slider, c);

        setValue(slider.getValue());
    }

    public void stateChanged(ChangeEvent e) {
        JSlider slider = (JSlider) e.getSource();
        int value = slider.getValue();
        vLabel.setText(Integer.toString(value));
        setValue(value);
    }

    public void setValue(int value) {
        vLabel.setText(Integer.toString(value));
    }
}
