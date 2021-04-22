package ditz.audio;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

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

    final AudioModel left;
    final AudioModel right;

    File file;

    final JPanel audiograms;

    public Audiometer() {

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        generator = new Generator(1024, false) {
            public void handleError(Throwable error) {
                Audiometer.this.handleError(error);
            }
        };

        FrequencyModel freqModel = new FrequencyModel(generator);
        left = new AudioModel(freqModel, generator.left);
        right = new AudioModel(freqModel, generator.right);

        add(audiograms=audiogramms());
    }

    private JPanel audiogramms() {
        JPanel panel = new JPanel(new BorderLayout());

        panel.add(new Audiogramm(left), BorderLayout.LINE_START);

        panel.add(Box.createRigidArea(new Dimension(10, 0)), BorderLayout.CENTER);

        panel.add(new Audiogramm(right), BorderLayout.LINE_END);

        return panel;
    }

    private JMenuItem loadItem() {
        JMenuItem item = new JMenuItem("Load");
        item.addActionListener(this::load);
        return item;
    }

    private JMenuItem saveItem() {
        JMenuItem item = new JMenuItem("Save");
        item.addActionListener(this::save);
        return item;
    }

    private JMenuItem exitItem(Window window) {
        JMenuItem item = new JMenuItem("Exit");
        item.addActionListener(ev-> {
            window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
        });
        return item;
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

    private void load(ActionEvent actionEvent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(file);
        int result = chooser.showOpenDialog(this);
        if(result==JFileChooser.APPROVE_OPTION)
            load(chooser.getSelectedFile());
    }

    private void save(ActionEvent actionEvent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(file);
        int result = chooser.showSaveDialog(this);
        if(result==JFileChooser.APPROVE_OPTION)
            save(chooser.getSelectedFile());
    }


    private void load(File file) {
        try(BufferedReader reader = new BufferedReader(new FileReader(file))) {

            for(int i=0; i<FrequencyModel.NUM_CHANNELS; ++i) {
                String line = reader.readLine();
                if(line==null)
                    break;

                int pos = line.indexOf(',');

                int value = Integer.parseInt(line, 0, pos, 10);
                left.setLoss(i, value);

                 value = Integer.parseInt(line, pos+1, line.length(), 10);
                 right.setLoss(i, value);
            }

            this.file = file;
            audiograms.repaint();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "unable to read input file",
                    "Load error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void save(File file) {
        try(PrintWriter writer = new PrintWriter(file))
        {
            for(int i=0; i<FrequencyModel.NUM_CHANNELS; ++i) {
                writer.print(left.getLoss(i));
                writer.append(',');
                writer.print(right.getLoss(i));
                writer.append('\n');
            }

            this.file = file;
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                "unable to write to file",
                "Save error",
                JOptionPane.ERROR_MESSAGE);
        }
    }


    public void handleError(Throwable error) {
        error.printStackTrace();
    }

    public static void main(String args[]) {

        final Audiometer audiometer = new Audiometer();

        SwingUtilities.invokeLater(audiometer::open);
    }
}
