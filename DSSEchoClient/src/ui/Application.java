package ui;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;

public class Application extends JFrame implements KeyListener {

	private static final long serialVersionUID = 1L;
	private JPanel topPanel;
	private JTextPane tPane;
	private String msg_received, msg_sent;
	private Socket s;
	private DataInputStream in;
	private DataOutputStream out;
	private byte[] msg = new byte[128 * 1024];
	private int i = 0;
	public static Logger mylogger = Logger.getLogger(Application.class);

	public String[] host = new String[2];
	


	/**
	 * Creates an Application GUI with EchoClient> prompt.
	 * 
	 * @throws AWTException
	 */
	public Application() throws AWTException {
		topPanel = new JPanel();
		tPane = new JTextPane();

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800, 500);
		setBackground(Color.WHITE);

		topPanel.setBackground(Color.WHITE);
		tPane.setBackground(Color.WHITE);

		appendToPane(tPane, "EchoClient>", Color.DARK_GRAY);
		appendToPane(tPane, "", Color.BLACK);

		tPane.addKeyListener(this);
		topPanel.add(tPane, BorderLayout.WEST);

		getContentPane().add(topPanel, BorderLayout.WEST);
		setVisible(true);
	}

	/**
	 * Adds style to the text input for the Application GUI.
	 * 
	 * @param tp
	 * @param msg
	 * @param c
	 */
	public void appendToPane(JTextPane tp, String msg, Color c) {
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

	/**
	 * Add text to the textpane.
	 * 
	 * @param text
	 */
	public void appendText(String text) {
		appendToPane(tPane, text + "\n", Color.BLACK);
		appendToPane(tPane, "EchoClient>", Color.DARK_GRAY);
	}

	/**
	 * Add exceptions to the textpane.
	 * 
	 * @param text
	 */
	public void appendException(String text) {
		appendToPane(tPane, text + "\n", Color.RED);
		appendToPane(tPane, "EchoClient>", Color.DARK_GRAY);
	}

	/**
	 * Receive data as byte array from the Server.
	 * 
	 * @return msg
	 * @throws IOException
	 */
	public byte[] receive() throws IOException {
		in = new DataInputStream(s.getInputStream());
		msg = new byte[128 * 1024];
		if (((in.read(msg)) != 13)) {
		}

		return msg;
	}

	/**
	 * Send data as byte array to the Server.
	 * 
	 * @param msg
	 * @throws IOException
	 */
	private void send(byte[] msg) throws IOException {
		out = new DataOutputStream(s.getOutputStream());
		i = 0;
		while (i != msg.length) {
			out.write(msg[i++]);
		}
		out.write(13);
	}

	/**
	 * Identify the commands and perform the corresponding operation.
	 * 
	 * @param text
	 * @throws IOException
	 */
	public void communication(String text) throws IOException {
		if (text.contains("connect") && !text.contains("disconnect")) {
			host[0] = text.split("\\s+")[1];
			host[1] = text.split("\\s+")[2];
			try {
				s = new Socket(host[0], Integer.parseInt(host[1]));
				in = new DataInputStream(s.getInputStream());
				out = new DataOutputStream(s.getOutputStream());

				msg_received = new String(receive());
				mylogger.info("Welcome Message from Server:" + msg_received);
				if (msg != null) {
					appendText(msg_received);
				}
			} catch (Exception e1) {
				appendException(e1.getMessage());
				e1.printStackTrace();

				mylogger.debug("Exception in Connect :" + e1.getMessage());
				mylogger.debug("Trace Exception in Connect"
						+ e1.getStackTrace());

			}

		} else if (text.contains("send")) {
			try {
				msg_sent = text.replace("send", "").trim();
				msg = msg_sent.getBytes();
				mylogger.info("Message Sent to Server :" + msg.toString());
				send(msg);
				mylogger.info("Message Received from Server :" + msg_sent);
				msg_received = new String(receive());
				mylogger.info("Message Received from Server :" + msg_received);
				if (msg != null) {
					appendText(msg_received);
				}
			} catch (Exception e1) {
				appendException(e1.getMessage());
				e1.printStackTrace();

				mylogger.debug("Exception in Send :" + e1.getMessage());
				mylogger.debug("Trace Exception in Send" + e1.getStackTrace());
			}

		}

		else if (text.contains("logLevel")) {
			try {
				text = text.replace("logLevel", "").trim();
				String s = text.toUpperCase();
				if (s.equals("ALL"))
					mylogger.setLevel(Level.ALL);
				if (s.equals("DEBUG"))
					mylogger.setLevel(Level.DEBUG);
				if (s.equals("INFO"))
					mylogger.setLevel(Level.INFO);
				if (s.equals("WARN"))
					mylogger.setLevel(Level.WARN);
				if (s.equals("ERROR"))
					mylogger.setLevel(Level.ERROR);
				if (s.equals("FATAL"))
					mylogger.setLevel(Level.FATAL);
				if (s.equals("OFF"))
					mylogger.setLevel(Level.OFF);
				if (s.equals("TRACE"))
					mylogger.setLevel(Level.TRACE);
				else {
					mylogger.setLevel(Level.INFO);
				}

			} catch (Exception e1) {
				appendException(e1.getMessage());
				e1.printStackTrace();
				mylogger.debug("Exception in LogLevel: " + e1.getMessage());
				mylogger.debug("Trace Exception in LogLevel: "
						+ e1.getStackTrace());
			}
		} else if (text.trim().equals("disconnect")) {
			try {
				s.close();
				text = "Connection to Server" + host[0] + " to port " + host[1]
						+ " is now closed!";
				appendException(text);
			} catch (IOException e1) {
				appendException(e1.getMessage());
				e1.printStackTrace();
				mylogger.debug("Exception in Disconnect:" + e1.getMessage());
				mylogger.debug("Trace Exception in Disconnect: "
						+ e1.getStackTrace());

			}
		} else if (s.isClosed() && text.trim().contains("send")) {
			text = "Socket Closed.";
			appendException(text);
		} else if (text.trim().equals("quit")) {
			text = "Application to close!";
			appendException(text);
			dispose();
		} 
		
		
		else if (text.trim().equals("help")) {
			System.out.println("help inside");
			text = "Type the following comments \n "
					+ "connect <Server> <Port> - to connect to the server \n "
					+ "send <message> - to send message to the server \n "
					+ "disconnect - to disconnect from the server \n "
					+ "quit - to quit the application \n "
					+ "logLevel <level> - to set the log level for the logger. \n "
					+ "Level INFO - to log messages. \n "
					+ "Level DEBUG - to log the Exception + Messages. \n";
			appendException(text);

		} 
				else {
			text = "Please enter a valid command. \n "
					+ "Type the following comments \n "
					+ "connect <Server> <Port> - to connect to the server \n "
					+ "send <message> - to send message to the server \n "
					+ "disconnect - to disconnect from the server \n "
					+ "quit - to quit the application \n "
					+ "logLevel <level> - to set the log level for the logger. \n +"
					+ "Level INFO - to log messages. \n "
					+ "Level DEBUG - to log the Exception + Messages. \n";
			appendException(text);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		String text = "";
		/**
		 * Add EchoClient> prompt whenever ENTER Key is pressed.
		 */
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			text = tPane.getText().substring(
					tPane.getText().lastIndexOf(">") + 1,
					tPane.getText().length());
			appendToPane(tPane, "EchoClient>", Color.DARK_GRAY);
			try {
				communication(text);
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
		/**
		 * BACKSPACE should not delete EchoClient> Prompt.
		 */

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

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		org.apache.log4j.BasicConfigurator.configure();
		
		PropertyConfigurator.configure("log.config");

	 
	  
	  

		mylogger.info("Log msg");

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new Application();
				} catch (AWTException e) {
					e.printStackTrace();
					mylogger.info("This is info level message" + e.getMessage());

					BasicConfigurator.configure();
					mylogger.debug("This is debug level message"
							+ e.getMessage());

				}
			}
		});
	}
}