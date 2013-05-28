import java.io.*;
import java.net.*;

public class Client extends Thread{
	private Server server;
	private Socket socket;
	private String ip;
	
	protected BufferedReader in;
	//protected PrintWriter out;
	protected OutputStream out;
	
	private boolean runFlag = true;
	
	public Client(Server server, Socket socket) {
		this.server = server;
		this.socket = socket;
		this.ip = socket.getInetAddress().getHostAddress();
		try {
			in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF8"));
			out = socket.getOutputStream();
			//out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"), true);
		}catch(IOException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			char c[] = new char[1];
			while(in.read(c, 0, 1) != -1 && runFlag) {
				StringBuffer sb = new StringBuffer();
				while(c[0] != '\0') {
					sb.append(c[0]);
					in.read(c, 0, 1);
				}
				server.reserveMessage(sb.toString());
			}
		}catch(IOException e) {
			
		}finally {
			stopClient();
		}
	}
	public String getIp() {
		return this.ip;
	}
	public void sendToClient(String s) {
		try {
			byte[] b = s.getBytes("UTF8");
			out.write(b);
			out.flush();
			//System.out.println(s+" "+b);
		} catch (Exception e) {
			// TODO é©ìÆê∂ê¨Ç≥ÇÍÇΩ catch ÉuÉçÉbÉN
			e.printStackTrace();
		}
	}
	private void stopClient() {
		server.deleteClient(this);
		try {
			in.close();
			out.close();
			socket.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	public void dispose() {
		runFlag = false;
	}
}
