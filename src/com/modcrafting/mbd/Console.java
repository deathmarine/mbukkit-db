package com.modcrafting.mbd;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javax.swing.JTextArea;

public class Console implements Runnable{
	private final PipedInputStream pin = new PipedInputStream();
	private final PipedInputStream pin2 = new PipedInputStream();
	private final PipedOutputStream pout3 = new PipedOutputStream();
	private Thread reader;
	private Thread reader2;
	private JTextArea text;
	private boolean quit;
	public Console(JTextArea text){
		this.text = text;
		this.quit = false;
		try {
			PipedOutputStream pout = new PipedOutputStream(this.pin);
			System.setOut(new PrintStream(pout, true));
			PipedOutputStream pout2 = new PipedOutputStream(this.pin2);
			System.setErr(new PrintStream(pout2, true));
			System.setIn(new PipedInputStream(this.pout3));
		} catch (Exception io) {
			this.text.setCaretPosition(this.text.getDocument().getLength());
		}
		this.reader = new Thread(this);
		this.reader.setDaemon(true);
		this.reader.start();
		this.reader2 = new Thread(this);
		this.reader2.setDaemon(true);
		this.reader2.start();
	}
	@Override
	public void run() {
		try {
			while (Thread.currentThread() == this.reader) {
				if (this.pin.available() != 0) {
					String input = readLine(this.pin);
					this.text.append(input);
					this.text.setCaretPosition(this.text.getDocument()
							.getLength());
				}
				if (this.quit)
					return;
			}
			while (Thread.currentThread() == this.reader2) {
				if (this.pin2.available() != 0) {
					String input = readLine(this.pin2);
					this.text.append(input);
					this.text.setCaretPosition(this.text.getDocument()
							.getLength());
				}
				if (this.quit)
					return;
			}
		} catch (Exception e) {
			this.text.append("\nConsole reports an Internal error.");
			this.text.append("The error is: " + e);
			this.text.setCaretPosition(this.text.getDocument().getLength());
		}
		
	}

	private String readLine(PipedInputStream in) throws IOException {
		String input = "";
		do {
			int available = in.available();
			if (available == 0)
				break;
			byte[] b = new byte[available];
			in.read(b);
			input = input + new String(b, 0, b.length);
		} while ((!input.endsWith("\n")) && (!input.endsWith("\r\n"))
				&& (!this.quit));
		return input;
	}
	
	public JTextArea getText(){
		return text;
	}
}
