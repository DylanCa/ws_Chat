package Server_Side;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Runnable {
	private int port;
	private ServerSocket socket;
	private Thread acceptThread;
	private List<Client> connectedClients;

	public Server(int port) {
		if (port < 1 || port > 65535) {
			throw new IllegalArgumentException("Invalid port");
		}

		this.port = port;
	}

	public void start() throws IOException {
		this.socket = new ServerSocket(this.port);

		this.acceptThread = new Thread(this);
		this.acceptThread.start();

		this.connectedClients = new ArrayList<>();

	}

	public void onClientMessageReceived(Client c, String message) {
		System.out.println("[Server][" + c.getSocket().getInetAddress() + "] Received message: " + message);
		c.write("ECHO -> " + message);

	}

	public void onClientDisconnected(Client c) {
		System.out.println("[Server][" + c.getSocket().getInetAddress() + "] is now disconnected.");
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket s = socket.accept();

				System.out.println("Connection received from " + s.getInetAddress());

				Client c = new Client(s, this);

				c.startPollingThread();

				this.connectedClients.add(c);

			} catch (IOException e) {
				System.out.println("Connection error");
				e.printStackTrace();
			}
		}
	}
}
