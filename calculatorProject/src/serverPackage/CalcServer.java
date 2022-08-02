package serverPackage;

import java.io.*;
import java.net.*;

import expressionPackage.*;

public class CalcServer {

	public static void main(String... strings) {
		int p = 0;
		try {
			strings[0] = strings[0].strip();
			p = Integer.parseInt(strings[0]);
		} catch (Exception e) {
		}

		new CalcServer(p);
	}

	private int port;

	public CalcServer(int port) {
		this.port = port;
		launch();
	}



	@SuppressWarnings("resource")
	private void launch() {
		ServerSocket servSock = null;
		try {
			servSock = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		port = servSock.getLocalPort();
		System.out.println("awaiting clients at tcp port " + port);
		while (true) {
			try (Socket sock = servSock.accept();
					BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
					BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()))) {
				String data = null;
				while ((data = br.readLine()) == null)
					Thread.yield();

				System.out.println("received:  " + data);
				String output = null;
				try {
					ComposedExpression ce = new ComposedExpression(data);
					output = ce.getStringResult();

				} catch (Exception e) {
					output = "error!";
				}
				bw.write(output + "\n");
				bw.flush();
				System.out.println("responded: " + output);
				System.out.println("***********************");
			} catch (Exception e) {
			}

		}

	}

}
