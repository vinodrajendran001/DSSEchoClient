package userInterface;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class Application extends JFrame implements KeyListener {

    private static final long serialVersionUID = 1L;
    private JPanel topPanel;
    private JTextPane tPane;
    private String text;
    String line;
    public Socket s;
    public BufferedReader r;
    public PrintWriter w;
    private static Logger mylogger = Logger.getLogger(Application.class);

    
    public String[] host = new String[2];

    public Application() throws AWTException {

        topPanel = new JPanel();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);

        // EmptyBorder eb = new EmptyBorder(new Insets(0, 0, 0, 0));
        tPane = new JTextPane();
        // topPanel.setBorder(eb);
        // tPane.setBorder(eb);
        // topPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        // tPane.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));

        // tPane.setMargin(new Insets(5, 5, 5, 5));
        setBackground(Color.WHITE);
        topPanel.setBackground(Color.WHITE);
        tPane.setBackground(Color.WHITE);
        appendToPane(tPane, "EchoClient>", Color.DARK_GRAY);
        appendToPane(tPane, "", Color.BLACK);
        getContentPane().add(topPanel, BorderLayout.WEST);
        // pack();
        tPane.addKeyListener(this);
        topPanel.add(tPane, BorderLayout.WEST);
        setVisible(true);

    }

    private void appendToPane(JTextPane tp, String msg, Color c) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY,
                StyleConstants.Foreground, c);
        aset = sc.addAttribute(aset, StyleConstants.FontFamily,
                "Lucida Console");
        aset = sc.addAttribute(aset, StyleConstants.Alignment,
                StyleConstants.ALIGN_LEFT);

        int len = tp.getDocument().getLength();
        tp.setCaretPosition(len);
        tp.setCharacterAttributes(aset, false);
        tp.replaceSelection(msg);

    }

    public void inspectText(String text) throws IOException {
        if (text.contains("connect") && !text.contains("disconnect")) {
            host[0] = text.split("\\s+")[1];
            host[1] = text.split("\\s+")[2];
            try {
                s = new Socket(host[0], Integer.parseInt(host[1]));
                r = new BufferedReader(
                        new InputStreamReader(s.getInputStream()));
                w = new PrintWriter(s.getOutputStream(), true);
                line = r.readLine();
                if (line != null) {
                    appendToPane(tPane, line + "\n", Color.BLACK);
                    appendToPane(tPane, "EchoClient>", Color.DARK_GRAY);
                }
            } catch (Exception e1) {
                appendToPane(tPane, e1.getMessage() + "\n", Color.RED);
                e1.printStackTrace();
                
                
            }

        } else if (text.contains("send")) {
            try {
                text = text.replace("send", "").trim();
                w.println(text);
                line = r.readLine();
                appendToPane(tPane, line + "\n", Color.BLACK);
                appendToPane(tPane, "EchoClient>", Color.DARK_GRAY);
            } catch (Exception e1) {
                appendToPane(tPane, e1.getMessage() + "\n", Color.RED);
                e1.printStackTrace();
                mylogger.debug("HELLLO");
            }

        }
        
        /*else if (text.contains("log")) {
            try {
                Level lev;
                text = text.replace("log", "").trim();
                lev.toString() = text.;
                w.println(text);
                mylogger.setLevel(lev);
            
                
            }
        }*/    
        else if (text.trim().equals("disconnect")) {
            try {
                s.close();
                appendToPane(tPane, "Connection to Server" + host[0]
                        + " to port " + host[1] + " is now closed! \n",
                        Color.RED);
                appendToPane(tPane, "EchoClient>", Color.DARK_GRAY);
            } catch (IOException e1) {
                appendToPane(tPane, e1.getMessage() + "\n", Color.RED);
                e1.printStackTrace();
            }
        } else if (text.trim().equals("quit")) {

            appendToPane(tPane, "Application to close!", Color.RED);
            appendToPane(tPane, "EchoClient>", Color.DARK_GRAY);
            dispose();
        } else if (text.trim().equals("help")) {

            appendToPane(tPane, "Type the following comments \n "
                    + "connect <Server> <Port> - to connect to the server \n "
                    + "send <message> - to send message to the server \n "
                    + "disconnect - to disconnect from the server \n "
                    + "quit - to quit the application \n "
                    + "log <level> - to set the log level for the logger. \n",
                    Color.RED);
            appendToPane(tPane, "EchoClient>", Color.DARK_GRAY);

        } else {
            appendToPane(tPane, "Please enter a valid command. \n "
                    + "Type the following comments \n "
                    + "connect <Server> <Port> - to connect to the server \n "
                    + "send <message> - to send message to the server \n "
                    + "disconnect - to disconnect from the server \n "
                    + "quit - to quit the application \n "
                    + "log <level> - to set the log level for the logger. \n",
                    Color.RED);
            appendToPane(tPane, "EchoClient>", Color.DARK_GRAY);

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        String text = "";
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            text = tPane.getText().substring(
                    tPane.getText().lastIndexOf(">") + 1,
                    tPane.getText().length());
            System.out.print("tPane : " + tPane.getDocument().getLength()
                    + "  text: " + text);
            appendToPane(tPane, "EchoClient>", Color.DARK_GRAY);
            try {
                inspectText(text);
            } catch (IOException e1) {
                appendToPane(tPane, e1.getMessage() + "\n", Color.RED);
                e1.printStackTrace();
            }
            tPane.setEditable(true);

            appendToPane(tPane, "", Color.BLACK);
        }
        AttributeSet attributeSet = tPane.getInputAttributes();
        Color c = (Color) (attributeSet == null ? null : attributeSet
                .getAttribute(StyleConstants.Foreground));
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && c.getRed() == 255) {
            appendToPane(tPane, "", Color.BLACK);
            e.consume();
        }
        if (c.getRed() == 255) {
            tPane.setEditable(false);
            appendToPane(tPane, "", Color.BLACK);
            tPane.setEditable(true);
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;

    }

    @Override
    public void keyTyped(KeyEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // TODO Auto-generated method stub
    }
    
    public static void main(String[] args) {
        
        ///PropertyConfigurator.configure("log4j.properties");
        PropertyConfigurator.configure("log.config");

        mylogger.info("Log msg");

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new Application();
                } catch (AWTException e) {
                    e.printStackTrace();
                    mylogger.info("This is a message");
                    
                    BasicConfigurator.configure();
                    mylogger.debug("Hello World!");
                    
                }
            }
        });
    }
}