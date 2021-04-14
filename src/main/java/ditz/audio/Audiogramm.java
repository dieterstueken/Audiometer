package ditz.audio;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.function.Consumer;

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

    final JCheckBox checkbox;

    final JLabel lossLabel = new JLabel();
    final JSlider lossSlider;

    final JLabel freqLabel = new JLabel();
    final BoundedRangeModel freqModel;

    final int[] audiogramm = new int[7*12];

    private void setLoss(int loss) {
        String text = String.format("%d dB", -loss);
        lossLabel.setText(text);

        int i = getFreqIndex();
        audiogramm[i] = loss;

        repaint();
    }

    private void setFreq(int value) {
        double freq = 1000 * Math.pow(2, value / 12.0);
        String text = String.format("%5.0f Hz", freq);
        freqLabel.setText(text);

        repaint();
    }

    private int getFreqIndex() {
        return freqModel.getValue() + 36;
    }

    int pix(float freq) {
        double lf = Math.log(freq/1000)/Math.log(2) + 3;
        double x = IN.left + width*lf/NX;
        return (int) x;
    }

    int piy(float db) {
        float y = IN.top + height*(db/10+1)/NY;
        return (int) y;
    }

    Audiogramm(BoundedRangeModel freqModel, String name, Consumer<Boolean> enable) {
        super(null);

        setPreferredSize(new Dimension(width+IN.left+IN.right, height+IN.top+IN.bottom));

        Border border = BorderFactory.createLineBorder(Color.BLACK);
        setBorder(border);

        this.checkbox = new JCheckBox(name);
        checkbox.setBounds(IN.left, IN.top+height+30, 60, 15);
        add(checkbox);

        this.freqModel = freqModel;

        this.lossSlider = lossSlider();

        freqSlider();

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


    private JSlider freqSlider() {

        freqLabel.setHorizontalAlignment(JLabel.RIGHT);
        freqLabel.setBounds(IN.left+65, IN.top+height+30, 55, 15);
        add(freqLabel);

        JSlider slider = new JSlider(freqModel);
        slider.setBounds(IN.left-7, IN.top+height+8, width+16, 20);
        add(slider);

        freqModel.addChangeListener(ev->setFreq(slider.getValue()));
        setFreq(slider.getValue());

        return slider;
    }

    private JSlider lossSlider() {

        lossLabel.setHorizontalAlignment(JLabel.RIGHT);
        lossLabel.setBounds(IN.left+150, IN.top+height+30, 55, 15);
        add(lossLabel);

        JSlider slider = new JSlider(JSlider.VERTICAL, -130, 10, 0);
        slider.setBounds(IN.left+width+10, IN.top-7, 20, height+16);

        slider.addChangeListener(ev->setLoss(slider.getValue()));

        add(slider);

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

        jx = getFreqIndex();

        for(int i=0; i<12*NX; ++i) {
            int db = audiogramm[i];

            ix = IN.left + 4*i;
            iy = IN.top + 3*(10-db);

            if(i==jx) {
                gr.setColor(Color.RED);
                gr.drawLine(ix, piy(130), ix, piy(-10));
            } else {
                gr.setColor(Color.BLACK);

            }

            gr.fillArc(ix-2, iy-2, 4, 4, 0, 360);
        }
    }
}
