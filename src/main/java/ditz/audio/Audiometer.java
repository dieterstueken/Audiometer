package ditz.audio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

        generator = new Generator(1024, false) {
            public void handleError(Throwable error) {
                Audiometer.this.handleError(error);
            }
        };

        FrequencyModel freqModel = new FrequencyModel(generator);

        add(audiogramms(freqModel));
    }

    private Component audiogramms(FrequencyModel freqModel) {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(audiogramm(freqModel, generator.left), BorderLayout.LINE_START);

        panel.add(Box.createRigidArea(new Dimension(10, 0)), BorderLayout.CENTER);

        panel.add(audiogramm(freqModel, generator.right), BorderLayout.LINE_END);

        return panel;
    }

    private JMenuItem loadItem() {
        JMenuItem item = new JMenuItem("Load");
        return item;
    }

    private JMenuItem saveItem() {
        JMenuItem item = new JMenuItem("Save");
        return item;
    }

    private JMenuItem exitItem(Window window) {
        JMenuItem item = new JMenuItem("Exit");
        item.addActionListener(ev-> {
            window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
        });
        return item;
    }

    private Audiogramm audiogramm(FrequencyModel freqModel, AudioChannel channel) {
        AudioModel model = new AudioModel(freqModel, channel);
        return new Audiogramm(model);
    }

    void open() {
        JFrame frame = new JFrame("Audiometer");

        JMenu menu = new JMenu("File");

        menu.add(loadItem());
        menu.add(saveItem());
        menu.add(exitItem(frame));

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menu);

        frame.setJMenuBar(menuBar);
        frame.setContentPane(this);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                generator.stop();
            }
        });

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
