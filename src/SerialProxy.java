import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import gnu.io.CommPortIdentifier;
import java.util.*;
import java.net.*;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
public class SerialProxy extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private JComboBox serialPorts;
	private JComboBox serialRates;
	private TextArea logArea;
	private JTextField serverPortText;
	private JToggleButton connectToggleButton;
	private Serial serial = null;
	private Server server = null;
	
	public SerialProxy() {
		super();
		Runtime.getRuntime().addShutdownHook(new Shutdown());
		this.setSize(740, 480);
		this.setTitle("Bridge");
		this.setResizable(false);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if(serial != null) {
					serial.closePort();
				}
				System.exit(0);
			}
		});
		/* logArea */
		logArea = new TextArea("", 10, 20, TextArea.SCROLLBARS_VERTICAL_ONLY);
		logArea.setEditable(false);
		String systemInfo = System.getProperty("os.name") + " " + System.getProperty("os.version")
		+ " (JRE:" + System.getProperty("java.version") + ")";
		this.printLogMessage("Bridge Preview (20101219)");
		this.printLogMessage(systemInfo);
		this.printLogMessage("");
		
		/* IPアドレスの取得 */
		String ip = "UnknownHost";
		try {
			ip = InetAddress.getLocalHost().getHostAddress().toString();
		} catch (UnknownHostException e1) {
			// TODO 自動生成された catch ブロック
			e1.printStackTrace();
		}
		/* シリアルポートの検出 */
		serialPorts = new JComboBox();
		this.printLogMessage("get the current serial port(s).");
		try {
			Enumeration<?> portList = CommPortIdentifier.getPortIdentifiers();
			while(portList.hasMoreElements()) {
				CommPortIdentifier portId = (CommPortIdentifier)portList.nextElement();
				if(portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
					String portName = portId.getName();
					if(portName.startsWith("/dev/tty") || portName.startsWith("COM")) {
						serialPorts.addItem(portName);
						this.printLogMessage(" " + portName);
					}
				}
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		serialPorts.setPreferredSize(new Dimension(1000, serialPorts.getPreferredSize().height));
		String[] rates = {"300","1200","2400","4800","9600","14400","19200","28800","38400","57600","115200"};
		serialRates = new JComboBox(rates);

		/* コンポーネントの配置 */
		Container c = this.getContentPane();
		c.setLayout(new GridLayout(1,2));
		JPanel p1 = new JPanel();
		JPanel p2 = new JPanel();
		JPanel p3 = new JPanel();
		JPanel p4 = new JPanel();

		p2.setLayout(new BoxLayout(p2, BoxLayout.PAGE_AXIS));
		
		/*  p3 */
		p3.setBorder(BorderFactory.createTitledBorder("Setting"));
		GroupLayout groupLayout1 = new GroupLayout(p3);
		p3.setLayout(groupLayout1);
		groupLayout1.setAutoCreateGaps(true);
		groupLayout1.setAutoCreateContainerGaps(true);

		JLabel serverPortLabel= new JLabel("Server Port");
		JLabel serverAddressLabel= new JLabel("Server Address");
		JLabel serialRateLabel = new JLabel("Serial Baud Rate");
		JLabel serialPortLabel = new JLabel("Serial Port");
		serverPortText = new JTextField("9000");
		JLabel serverAddressText = new JLabel(ip);
		serverPortText.setInputVerifier(new IntegerInputVerifier());
		
		GroupLayout.SequentialGroup hGroup1 = groupLayout1.createSequentialGroup();
		hGroup1.addGroup(groupLayout1.createParallelGroup()
				.addComponent(serverAddressLabel).addComponent(serverPortLabel)
				.addComponent(serialRateLabel).addComponent(serialPortLabel));
		hGroup1.addGroup(groupLayout1.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(serverAddressText).addComponent(serverPortText)
				.addComponent(serialRates).addComponent(serialPorts));
		groupLayout1.setHorizontalGroup(hGroup1);

		GroupLayout.SequentialGroup vGroup1 = groupLayout1.createSequentialGroup();
		vGroup1.addGroup(groupLayout1.createParallelGroup(Alignment.BASELINE).addComponent(serverAddressLabel).addComponent(serverAddressText));
		vGroup1.addGroup(groupLayout1.createParallelGroup(Alignment.BASELINE).addComponent(serverPortLabel).addComponent(serverPortText));
		vGroup1.addGroup(groupLayout1.createParallelGroup(Alignment.BASELINE).addComponent(serialRateLabel).addComponent(serialRates));
		vGroup1.addGroup(groupLayout1.createParallelGroup(Alignment.BASELINE).addComponent(serialPortLabel).addComponent(serialPorts));
		groupLayout1.setVerticalGroup(vGroup1);

		/* p4 */
		p4.setBorder(BorderFactory.createTitledBorder("Log"));
		GroupLayout groupLayout2 = new GroupLayout(p4);
		p4.setLayout(groupLayout2);
		groupLayout2.setAutoCreateGaps(true);
		groupLayout2.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup hGroup2 = groupLayout2.createSequentialGroup();
		hGroup2.addComponent(logArea);
		groupLayout2.setHorizontalGroup(hGroup2);

		GroupLayout.ParallelGroup vGroup2 = groupLayout2.createParallelGroup();
		vGroup2.addComponent(logArea);
		groupLayout2.setVerticalGroup(vGroup2);

		/* p1 */
		JLabel appImageIcon = new JLabel(new ImageIcon(this.getClass().getResource("application.png")));
		connectToggleButton = new JToggleButton("OFF");
		/*
		connectToggleButton = new JToggleButton("OFF", new ImageIcon(this.getClass().getResource("usbpendrive_unmount.png")));
		connectToggleButton.setSelectedIcon(new ImageIcon(this.getClass().getResource("usbpendrive_mount.png")));
		*/
		appImageIcon.setPreferredSize(new Dimension(320, 380));
		connectToggleButton.setPreferredSize(new Dimension(290, 50));
		
		connectToggleButton.addActionListener(this);

		p1.add(appImageIcon);
		p1.add(connectToggleButton);

		p2.add(p3);
		p2.add(p4);
		c.add(p1);
		c.add(p2);
		p1.setBackground(Color.WHITE);
		p2.setBackground(Color.WHITE);
		p3.setBackground(Color.WHITE);
		p4.setBackground(Color.WHITE);

		this.setVisible(true);		
	}
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		JToggleButton b = (JToggleButton)e.getSource();
		if(b.isSelected()) {
			b.setText("ON");
			String portName = serialPorts.getSelectedItem().toString();
			String portRate = serialRates.getSelectedItem().toString();
			if(serial != null) {
				serial.closePort();
			}
			if(server == null) {
				int serverPort = 9000;
				try {
					serverPort = Integer.valueOf(serverPortText.getText().toString());
				}catch(NumberFormatException e1) {}
				serverPortText.setText(String.valueOf(serverPort));
				server = new Server(this, serverPort);
				server.start();
				serverPortText.setEditable(false);
			}
			serialRates.setEnabled(false);
			serialPorts.setEnabled(false);
			serial = new Serial(this);
			serial.openPort(portName, portRate);
		}else {
			b.setText("OFF");
			if(serial != null) {
				serial.closePort();
			}
			serialRates.setEnabled(true);
			serialPorts.setEnabled(true);
		}
	}
	class Shutdown extends Thread {
		@Override
		public void run() {
			if(serial != null) {
				serial.closePort();
			}
		}
	}

	public void sendToClient(String s) {
		server.sendToAllClient(s);
	}
	public void sendToSerial(String s) {
		try {
			serial.write(s);
		}catch(Exception e) {}
	}
	
	public void printLogMessage(String s) {
		logArea.append(s+"\n");
	}
	
	public static void main(String[] args) {
		new SerialProxy();
	}
	class IntegerInputVerifier extends InputVerifier{
		@Override
		public boolean verify(JComponent c) {
				boolean verified = false;
				JTextField textField = (JTextField)c;
				try{
					Integer.parseInt(textField.getText());
					verified = true;
				}catch(NumberFormatException e) {
					UIManager.getLookAndFeel().provideErrorFeedback(c);
				}
				return verified;
		}
	}
}
