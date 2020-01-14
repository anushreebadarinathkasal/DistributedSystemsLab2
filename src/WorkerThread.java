//Name: Anushree Badarinath Kasal
//Student ID: 1001624311
//Net ID: abk4311

//https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
//https://www.geeksforgeeks.org/socket-programming-in-java/
//https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/
//https://www.geeksforgeeks.org/multi-threaded-chat-application-set-2/
//https://www.geeksforgeeks.org/a-group-chat-application-in-java/
//https://www.youtube.com/watch?v=cRfsUrU3RjE&list=PLdmXYkPMWIgCocLY-B4SvpQshQWC7Nc0C&index=1

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

//This file facilitates the connection between the client and the server

public class WorkerThread implements Runnable {

	private Socket client;
	private String clientName;
	private Server server;

	public ConcurrentLinkedQueue<String> messages;

	String RESPONSE_FORMAT = "HOST: " + "127.0.0.1" + "\r\n" + "HTTP/1.1 200 OK" + "\r\n" + "Content-Type: text/plain"
			+ "\r\n" + "Content-Length: " + 20 + "\r\n" + "User-Agent: Socket Client Application" + "\r\n" + "Date: "
			+ getDate() + "\r\n" + "\r\n";

	// https://stackoverflow.com/questions/5683728/convert-java-util-date-to-string
	// http://www.java2s.com/Tutorial/Java/0120__Development/Formatdateinmmddyyyyhhmmssformat.htm
	// http://tutorials.jenkov.com/java-internationalization/simpledateformat.html

	// get the current date and time to be printed in the header.
	public static String getDate() {
		Date todaysDate = new Date();
		DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String str = dateformat.format(todaysDate);
		return str;
	}

	public WorkerThread(Socket client) {
		this.client = client;
		messages = new ConcurrentLinkedQueue<>();

	}

	public String getClientName() {
		return clientName;
	}

	public void setServer(Server server) {
		this.server = server;
	}

	// This method is used to read the data from the client
	private String readRequestDataFromClient(BufferedReader reader) throws IOException {

		String line;
		String requestData = "";
		final ArrayList<String> lines = new ArrayList<>();
		// To read all the data in the buffer till we completely read the http header.

		while (true) {
			// https://stackoverflow.com/questions/1164301/how-do-i-call-some-blocking-method-with-a-timeout-in-java
			try {
				TimeoutBlock timeoutBlock = new TimeoutBlock(900);
				Runnable block = new Runnable() {

					@Override
					public void run() {
						// TO DO write block of code
						try {
							String l = reader.readLine();
							lines.add(l);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};

				timeoutBlock.addBlock(block);

			} catch (Throwable e) {
				// catch the exception here . Which is block didn't execute within the time
				// limit.

				return null;
			}

			line = lines.get(0);
			lines.remove(0);

			if (line == null)
				break;
			else {
				// if line is empty, it means headers are over and next line is the actual data
				if (line.equals("\r\n") || line.equals("")) {
					requestData = reader.readLine(); // In order to read the data line.
					// System.out.println(requestData);
					ServerWindow.Log(requestData);// print the data in the server window.
					break;
				}
			}
			ServerWindow.Log(line);
			// System.out.println(line);
		}
		if (requestData == null) {
			System.out.println("null data");
		}
		return requestData; // Returning the requested data
	}

	// this method is used to write to the server.
	private static void writeToServer(BufferedWriter wr, String req) throws IOException {
		wr.write(req);
		wr.flush();
	}

	@Override
	public void run() {
		try {
			// TODO Auto-generated method stub
			// https://docs.oracle.com/javase/tutorial/networking/sockets/index.html
			// https://docs.oracle.com/javase/8/docs/api/?java/io/InputStreamReader.html
			// https://stackoverflow.com/questions/7376647/what-is-the-difference-between-javas-bufferedreader-and-inputstreamreader-class

			BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));

			String data = readRequestDataFromClient(reader);
			// System.out.println(data);
			// ServerWindow.Log(data);
			clientName = data.replaceAll("USERNAME:", "");
			Thread.currentThread().setName(clientName);
			ServerWindow.addClient(clientName);

			while (true) {
				// This loop is used to continuously read data from the client.
				data = readRequestDataFromClient(reader);
				
				if (data == null || data.equals("")) {
					System.out.println("Breaking");
					continue;
				}
				data = data.replaceAll("DATA:", "");
				if (data.equals("POLL")) {
					// Basically to poll to server.
					System.out.println("POLL RCVD");
					writeToServer(writer, RESPONSE_FORMAT + "POLLING BACK\r\n");
				} else if (data.contains("ALL:")) {
					// Basically to handle 1 to N messages.
					this.server.broadcast(this.clientName + ":" + data.replaceAll("ALL:", ""));

				}
				//Lab2 
				else if (data.contains("CLK_TIME")) {
					
					System.out.println(data);

					// Randomly select a client based on the active list of clients maintained in the server
					// other than itself
					int index = new Random().nextInt(this.server.getConnectedClientCount());
					WorkerThread destWorkerThread = this.server.getClient(index);
					//destWorkerThread.messages.add("From:" + this.clientName + " # " + data);
					if(!this.clientName.equals(destWorkerThread.getClientName())) {
						destWorkerThread.messages.add("From:" + this.clientName + " # " + data);
					}
					
					//ClientWindow.Log("trying to send message"+ destWorkerThread.getClientName() );
				}
				if (!this.messages.isEmpty()) {
					// To print the message in the server window
					String msg = this.messages.remove();
					ServerWindow.Log("\n***********************************\n" + msg);
					ServerWindow.Log("sending message" + this.clientName);
					writeToServer(writer, RESPONSE_FORMAT + msg + "\r\n");
				}

			}

		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
//			reader.close();
//			writer.close();
//			client.close();
			return;
		} finally {
			// This is used to remove the client from the active list.
			// This is also used to print message on server that the client has stopped.
			ServerWindow.removeClient(clientName);
			ServerWindow.Log(clientName + " has exited.");
		}
	}
}
