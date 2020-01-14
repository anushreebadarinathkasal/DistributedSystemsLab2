//Name: Anushree Badarinath Kasal
//Student ID: 1001624311
//Net ID: abk4311

//https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
//https://www.geeksforgeeks.org/socket-programming-in-java/
//https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/
//https://www.geeksforgeeks.org/multi-threaded-chat-application-set-2/
//https://www.geeksforgeeks.org/a-group-chat-application-in-java/
//https://www.youtube.com/watch?v=cRfsUrU3RjE&list=PLdmXYkPMWIgCocLY-B4SvpQshQWC7Nc0C&index=1

//In this project we are creating client and server on same system. The Server is set to connection port 8818
//The server can handle upto three clients.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

// https://javarevisited.blogspot.com/2015/06/how-to-create-http-server-in-java-serversocket-example.html
public class Server {

	private int PORT;
	ServerSocket serverSocket;
	private ArrayList<WorkerThread> allClients;  

	public Server(int port) {
		PORT = port;
		allClients = new ArrayList<>();
	}

	//To send 1 to 1 message
	public WorkerThread getClient(String clientName) {
		for (WorkerThread wt : allClients) {
			if (clientName.equals(wt.getClientName())) {

				return wt;
			}
		}
		return null;
	}
	
	public WorkerThread getClient(int index) {
		return allClients.get(index);
	}
	
	public int getConnectedClientCount() {
		return allClients.size();
	}
	
	

	//To send 1 to n message
	public void broadcast(String msg) {
		String srcClient = msg.split(":")[0];

		for (WorkerThread wt : allClients) {
			if (!srcClient.equals(wt.getClientName()))
				wt.messages.add("broadcast from " + msg);
		}
	}

	public void startServer() throws IOException, NumberFormatException, InterruptedException {

		serverSocket = new ServerSocket(PORT);
		ServerWindow.Log("Server Started.");
		ServerWindow.Log("Listening for connection on port " + PORT);
		//Maximum number of connections i.e., maximum number of clients.
		//https://courses.cs.washington.edu/courses/cse341/98au/java/jdk1.2beta4/docs/api/java/net/ServerSocket.html
		int maxConnections = 3;
		Thread[] threadArray = new Thread[4];
		int i = 0;
		while (maxConnections-- > 0) {
			Socket socket = serverSocket.accept();
			Runnable myWorkerThread = new WorkerThread(socket);
			((WorkerThread) myWorkerThread).setServer(this);
			Thread newThread = new Thread(myWorkerThread);
			newThread.start();
			allClients.add((WorkerThread) myWorkerThread);
			threadArray[i++] = newThread;
		}

		for (int j = 0; j < threadArray.length; j++) {
			if (threadArray[i] != null)
				//https://www.geeksforgeeks.org/joining-threads-in-java/
				threadArray[i].join(); // wait for all threads
		}

	}

}
