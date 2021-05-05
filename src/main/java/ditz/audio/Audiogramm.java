package ditz.audio;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * User: stueken
 * Date: 06.04.21
 * Time: 13:35
 */
class Audiogramm extends JPanel {

    static final int[] FREQS = {125, 250, 500, 1000, 2000, 3000, 4000, 6000, 8000, 11000, 16000};

    static final Insets IN = new Insets(30, 30, 70, 35);

    // pixel per channel
    static int CHX = 4;

    // pixel per db
    static int DbY = 3;
    static int DbL = -10;
    static int DbH = 130;

    final AudioModel model;

    final int width;
    final int height;

    final JCheckBox checkbox;
    final JSlider lossSlider;
    final JSlider freqSlider;

    final JLabel lossLabel;
    final JLabel freqLabel;

    String getLossLabel() {
        return String.format("%d dB", model.getLoss());
    }

    void updateLossLabel(Object dummy) {
        lossLabel.setText(getLossLabel());
    }

    String getFreqLabel() {
        return String.format("%5d Hz", (int) model.getFreq());
    }

    void updateFreqLabel(Object dummy) {
        freqLabel.setText(getFreqLabel());
    }

    int chx(int channel) {
        return IN.left + CHX*channel;
    }

    int chy(int channel) {
        return piy(model.getLoss(channel));
    }

    int pix(float freq) {
        return chx(FrequencyModel.toChannel(freq));
    }

    int piy(int loss) {
        return IN.top + DbY * (loss - DbL);
    }

    public int numChannels() {
        return model.freqModel.numChannels();
    }

    public int getChannel() {
        return model.freqModel.getChannel();
    }

    Audiogramm(AudioModel model) {
        super(null);

        this.model = model;

        this.width = CHX*numChannels();
        this.height = DbY*(DbH - DbL);

        setPreferredSize(new Dimension(width+IN.left+IN.right, height+IN.top+IN.bottom));

        Border border = BorderFactory.createLineBorder(Color.BLACK);
        setBorder(border);

        checkbox = new JCheckBox(model.getName(), null, false);
        checkbox.setBounds(IN.left, IN.top+height+30, 60, 15);
        checkbox.addItemListener(model::enable);
        add(checkbox);

        lossSlider = new JSlider(model);
        lossSlider.setOrientation(JSlider.VERTICAL);
        lossSlider.setBounds(IN.left+width+10, IN.top-7, 20, height+16);
        add(lossSlider);

        lossLabel = new JLabel(getLossLabel());
        lossLabel.setHorizontalAlignment(JLabel.RIGHT);
        lossLabel.setBounds(IN.left+150, IN.top+height+30, 55, 15);
        model.addChangeListener(this::updateLossLabel);
        add(lossLabel);

        freqSlider = new JSlider(model.getFreqModel());
        freqSlider.setBounds(IN.left-7, IN.top+height+8, width+16, 20);
        add(freqSlider);

        freqLabel = new JLabel(getFreqLabel());
        freqLabel.setHorizontalAlignment(JLabel.RIGHT);
        freqLabel.setBounds(IN.left+65, IN.top+height+30, 55, 15);
        model.freqModel.addChangeListener(this::updateFreqLabel);
        add(freqLabel);

        model.gainModel.addChangeListener(ev->repaint());
        model.freqModel.addChangeListener(ev->repaint());
        model.addChangeListener(ev->repaint());

        for (int freq : FREQS) {
            String text = freq<1000 ? String.format("%d", freq) : String.format("%dk", freq/1000);
            JLabel label = new JLabel(text);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            add(label);
            label.setBounds(pix(freq)-12, IN.top-18, 24, 10);
        }

        for(int loss=DbL; loss<=DbH; loss+=10) {
            String text = String.format("%d", loss);
            JLabel label = new JLabel(text);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            add(label);
            int ix = 0;
            int iy = piy(loss)-6;
            label.setBounds(ix, iy, 24, 10);
        }

        setupKeyBindings();
    }

    void setupKeyBindings() {
        setupKeyBindings("UP", ev->model.incrementLoss(-1));
        setupKeyBindings("DOWN", ev->model.incrementLoss(+1));
        setupKeyBindings("LEFT", ev->model.incrementFreq(-1));
        setupKeyBindings("RIGHT", ev->model.incrementFreq(+1));
    }

    void setupKeyBindings(String command, ActionListener listener) {
        checkbox.registerKeyboardAction(listener, command, KeyStroke.getKeyStroke(command), JComponent.WHEN_FOCUSED);
    }

    @Override
    protected void paintChildren(Graphics g) {
        super.paintChildren(g);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

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

        for(int loss=DbL; loss<=DbH; loss+=10) {
            iy = piy(loss);
            gr.drawLine(ix, iy, jx, iy);
        }

        // currently active channel
        int jch = getChannel();

        for(int ich=0; ich<numChannels(); ++ich) {

            ix = chx(ich);
            iy = chy(ich);

            if(ich==jch) {
                gr.setColor(Color.RED);
                gr.drawLine(ix, piy(130), ix, piy(-10));
            } else {
                gr.setColor(Color.BLACK);
            }

            gr.fillArc(ix-2, iy-2, 4, 4, 0, 360);
        }

        ix = pix(125);
        jx = pix(16000);

        gr.setColor(Color.RED);
        iy = piy(model.getLimit());
        gr.drawLine(ix, iy, jx, iy);
    }
}
