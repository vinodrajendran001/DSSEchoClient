package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientLogic {
	private String text;
	String line, msg_received, msg_sent;
	public Socket s;
	public DataInputStream in;
	public DataOutputStream out;
	byte[] msg = new byte[128 * 1024];
	int i = 0, val = 0;
	public String[] host = new String[2];

	/**
	 * @param args
	 */
	public void inspectText(String text) throws IOException {
	}

	public byte[] receive() throws IOException {
		in = new DataInputStream(s.getInputStream());
		msg = new byte[128 * 1024];
		if (((in.read(msg)) != 13)) {
			text = new String(msg);
			System.out.println("2 :" + text);
		}
		return msg;
	}

	public void send(byte[] msg) throws IOException {
		out = new DataOutputStream(s.getOutputStream());
		i = 0;
		while (i != msg.length) {
			out.write(msg[i++]);
		}
		System.out.println("4: " + new String(msg));
		out.write(13);
	}

}
