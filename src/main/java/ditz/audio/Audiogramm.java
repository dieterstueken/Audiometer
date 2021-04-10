package ditz.audio;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: stueken
 * Date: 06.04.21
 * Time: 13:35
 */
class Audiogramm extends JPanel {

    static final int[] FREQS = {125, 250, 500, 1000, 2000, 3000, 4000, 6000, 8000, 11000, 16000};

    final int NX = 7;
    final int NY = 14;

    final int INX = 30;
    final int INY = 20;

    final int width;
    final int height;

    int pix(float freq) {
        double lf = Math.log(freq/1000)/Math.log(2) + 3;
        double x = INX + (width-INX-20)*lf/NX;
        return (int) x;
    }

    int piy(float db) {
        float y = INY + (height-INY)*(db/10+1)/NY;
        return (int) y;
    }

    Audiogramm(int width, int height) {
        super(null);
        this.width = width;
        this.height = height;

        setPreferredSize(new Dimension(width, height));

        Border border = BorderFactory.createLineBorder(Color.BLACK);
         
        setBorder(border);

        for (int freq : FREQS) {
            String text = freq<1000 ? String.format("%d", freq) : String.format("%dk", freq/1000);
            JLabel label = new JLabel(text);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            add(label);
            label.setBounds(pix(freq)-12, 5, 24, 10);
        }

        for(int i=0; i<NY; ++i) {
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

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D gr = (Graphics2D) g;

        gr.setColor(Color.GRAY);

        int iy = piy(-10);
        int jy = piy(120);
        for (int freq : FREQS) {
            int ix = pix(freq);
            gr.drawLine(ix, iy, ix, jy);
            // draw label
        }

        int ix = pix(125);
        int jx = pix(16000);
        for(int i=0; i<NY; ++i) {
            int db = 10*i-10;
            iy = piy(db);
            gr.drawLine(ix, iy, jx, iy);
        }
    }
}
