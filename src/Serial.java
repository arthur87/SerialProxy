import gnu.io.*;
import java.io.*;

public class Serial implements SerialPortEventListener {
	private SerialProxy proxy;
	private SerialPort port;
	
	private final int parity = SerialPort.PARITY_NONE;
	private final int dataBits = 8;
	private final int stopBits = SerialPort.STOPBITS_1;
	
	public InputStream input;
	public OutputStream output;
	
	public Serial(SerialProxy proxy) {
		this.proxy = proxy;
	}
	public boolean openPort(String portName, String portRate) {
		proxy.printLogMessage("\nstarting Serial Proxy.");
		try {
			CommPortIdentifier portId = CommPortIdentifier.getPortIdentifier(portName);
			CommPort commPort = portId.open("IOProxy", 2000);
			port = (SerialPort)commPort;
			input = port.getInputStream();
			output = port.getOutputStream();
			port.setSerialPortParams(Integer.valueOf(portRate), dataBits, stopBits, parity);
			port.addEventListener(this);
			port.notifyOnDataAvailable(true);
			proxy.printLogMessage("Serial Proxy started on port " + portName + "(" +portRate + ")." );
		}catch(Exception e) {
			e.printStackTrace();
			proxy.printLogMessage("Serial Proxy cannot start on port " + portName + "(" +portRate + ").");
			return false;
		}
		return true;
	}
	public void closePort() {
		try {
			if(input != null) input.close();
			if(output != null) output.close();
		}catch(Exception e) {}
		input = null;
		output = null;
		
		try {
			if(port != null) {
				port.removeEventListener();
				port.close();
				proxy.printLogMessage("\nSerial Proxy closed.");
			}
		}catch(Exception e){}
		port = null;
	}
	
	synchronized public void serialEvent(SerialPortEvent event) {
		if(event.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				while(input.available() > 0) {
					proxy.sendToClient(String.valueOf(input.read())+ "\0");
				}
			}catch(IOException e) {
				
			}
		}
	}
	public void write(String s) {
		try {
			output.write(s.getBytes());
			output.flush();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

}

