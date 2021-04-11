package ditz.audio;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: stueken
 * Date: 06.04.21
 * Time: 13:35
 */
class Audiogramm extends JPanel {

    static final int[] FREQS = {125, 250, 500, 1000, 2000, 3000, 4000, 6000, 8000, 11000, 16000};

    // # of tick lines
    static final int NX = 7;
    static final int NY = 14;

    static final int width = 48 * NX;
    static final int height = 30 * NY;

    static final Insets IN = new Insets(30, 30, 70, 35);

    int pix(float freq) {
        double lf = Math.log(freq/1000)/Math.log(2) + 3;
        double x = IN.left + width*lf/NX;
        return (int) x;
    }

    int piy(float db) {
        float y = IN.top + height*(db/10+1)/NY;
        return (int) y;
    }

    Audiogramm(BoundedRangeModel freqModel) {
        super(null);

        setPreferredSize(new Dimension(width+IN.left+IN.right, height+IN.top+IN.bottom));

        Border border = BorderFactory.createLineBorder(Color.BLACK);
        setBorder(border);

        freqSlider(freqModel);
        lossSlider();

        for (int freq : FREQS) {
            String text = freq<1000 ? String.format("%d", freq) : String.format("%dk", freq/1000);
            JLabel label = new JLabel(text);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            add(label);
            label.setBounds(pix(freq)-12, IN.top-18, 24, 10);
        }

        for(int i=0; i<=NY; ++i) {
            int db = 10*i-10;
            String text = String.format("%d", db);
            JLabel label = new JLabel(text);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            add(label);
            int ix = 0;
            int iy = piy(db)-6;
            label.setBounds(ix, iy, 24, 10);
        }
    }

    private JSlider freqSlider(BoundedRangeModel freqModel) {
        JSlider slider = new JSlider(freqModel);
        slider.setBounds(IN.left-7, IN.top+height+8, width+16, 20);
        add(slider);

        JLabel label = new JLabel();
        //label.setPreferredSize(new Dimension(160, 10));
        label.setHorizontalAlignment(JLabel.RIGHT);
        label.setBounds(IN.left, IN.top+height+30, 55, 15);
        
        ChangeListener cl = ev -> {
            int value = freqModel.getValue();
            double freq = 1000 * Math.pow(2, value / 12.0);
            String text = String.format("%5.0f Hz", freq);
            label.setText(text);
        };
        freqModel.addChangeListener(cl);
        cl.stateChanged(null);
        add(label);

        return slider;
    }

    private JSlider lossSlider() {
        JSlider slider = new JSlider(JSlider.VERTICAL, -130, 10, -10);
        slider.setBounds(IN.left+width+10, IN.top-7, 20, height+16);
        add(slider);

        JLabel label = new JLabel();
        //label.setPreferredSize(new Dimension(160, 10));
        label.setHorizontalAlignment(JLabel.RIGHT);
        label.setBounds(IN.left+100, IN.top+height+30, 55, 15);

        ChangeListener cl = ev -> {
            int value = slider.getValue();
            String text = String.format("%d dB", -value);
            label.setText(text);
        };
        slider.addChangeListener(cl);
        cl.stateChanged(null);
        add(label);

        return slider;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D gr = (Graphics2D) g;

        gr.setColor(Color.GRAY);

        int iy = piy(-10);
        int jy = piy(130);
        for (int freq : FREQS) {
            int ix = pix(freq);
            gr.drawLine(ix, iy, ix, jy);
            // draw label
        }

        int ix = pix(125);
        int jx = pix(16000);
        for(int i=0; i<=NY; ++i) {
            int db = 10*i-10;
            iy = piy(db);
            gr.drawLine(ix, iy, jx, iy);
        }
    }
}
