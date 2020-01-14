//Name: Anushree Badarinath Kasal
//Student ID: 1001624311
//Net ID: abk4311

//https://www.geeksforgeeks.org/introducing-threads-socket-programming-java/
//https://www.geeksforgeeks.org/socket-programming-in-java/
//https://www.geeksforgeeks.org/multi-threaded-chat-application-set-1/
//https://www.geeksforgeeks.org/multi-threaded-chat-application-set-2/
//https://www.geeksforgeeks.org/a-group-chat-application-in-java/
//https://www.youtube.com/watch?v=cRfsUrU3RjE&list=PLdmXYkPMWIgCocLY-B4SvpQshQWC7Nc0C&index=1

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.io.IOException;
import java.net.UnknownHostException;
import java.security.Timestamp;

//This is a client class that sends the user name to server as well as messages to destination client.
//It also handles methods to send to 1 to 1 and 1 to N between the clients.
public class Client {

	private Socket socket;
	private int port;
	private String IP;
	private String userName;
	private String p = "";

	private LogicalClock clock;

	private ArrayList<String> oneToOne, oneToAll;

	private String USERNAME_REQUEST = "HOST: " + "127.0.0.1" + "\r\n" + "POST /" + " HTTP/1.1" + "\r\n"
			+ "Content-Type: text/plain" + "\r\n" + "Content-Length: " + 20 + "\r\n"
			+ "User-Agent: Socket Client Application" + "\r\n" + "Date: " + getDate() + "\r\n" + "\r\n" + "USERNAME:";

	private String MESSAGE_REQUEST = "HOST: " + "127.0.0.1" + "\r\n" + "POST /" + " HTTP/1.1" + "\r\n"
			+ "Content-Type: text/plain" + "\r\n" + "Content-Length: " + 20 + "\r\n"
			+ "User-Agent: Socket Client Application" + "\r\n" + "Date: " + getDate() + "\r\n" + "\r\n" + "DATA:";

	public Client(String IP, int port) {
		this.IP = IP;
		this.port = port;
	}
	// Client constructor class takes IP which is IPaddress (127.0.0.1)and
	// portno(8818)and the userName of client.

	public Client(String IP, int port, String userName) {
		this.IP = IP;
		this.port = port;
		this.userName = userName;
		clock = new LogicalClock();
	}


	// SendToOne is used by client to handle 1 to 1 message request.
	public void SendToOne(String msg) {
		oneToOne.add(msg);
	}

	// SendToAll is used by client to handle 1 to N message request.
	public void SendToAll(String msg) {
		oneToAll.add(msg);
	}

	public void startClient() throws UnknownHostException, IOException, InterruptedException {

		socket = new Socket(IP, port);
		oneToAll = new ArrayList<>();
		oneToOne = new ArrayList<>();
		Random ran = new Random();
		int send_count = ran.nextInt(5)+2;

		// https://www.baeldung.com/a-guide-to-java-sockets
		// https://www.codejava.net/java-se/networking/java-socket-client-examples-tcp-ip

		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		// https://www.geeksforgeeks.org/naming-thread-fetching-name-current-thread-java/

		String threadName = userName;
		// username given to server
		String req = USERNAME_REQUEST + threadName;

		writeToServer(wr, req);

		int count = 0;
		

		while (true) {

			count++;
			if (p == "vbfgh")
				break;

			//ClientWindow.Log("client:" + threadName + ":localcounter"+ Integer.toString(this.clock.getClock()));
			if (!oneToAll.isEmpty()) {
				String msg = oneToAll.get(0);
				oneToAll.remove(0);
				req = MESSAGE_REQUEST + "ALL:" + msg;
			} else if (!oneToOne.isEmpty()) {
				String msg = oneToOne.get(0);
				oneToOne.remove(0);
				req = MESSAGE_REQUEST + msg;
			} else
				req = MESSAGE_REQUEST + "POLL";
			
			// Lab2
            // At this point if the counter value is greater than send count client is sending messing to server saying its current clk time.
			if (count > send_count) {
				count = 0;
				req = MESSAGE_REQUEST + "CLK_TIME:" + clock.getClock();
				System.out.println(clock.getClock());
				ClientWindow.Log("Sending request: with clock time"+ Integer.toString(clock.getClock()));
			}

			// req = MESSAGE_REQUEST + time_counter;

			writeToServer(wr, "\n*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
			// message to be printed in between *-*-* in order to differentiate one from
			// another
			writeToServer(wr, req);

			writeToServer(wr, "\n*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*");

			// wait for response from server
			// parse response and print msg
			String responseFromServer = readResponseDataFromServer(rd);
			// client waits for the response from server
			if (responseFromServer == null || responseFromServer.contains("POLLING")) {
				System.out.println("POLL GOT FROM SERVER");
				Thread.sleep(500);
				continue;
			}

			else {
				// lab2
				// Check for clock adjustments based on the received clk time
				ClientWindow.Log("Msg from server: " + responseFromServer);
				System.out.println(responseFromServer);
				String[] splits = responseFromServer.split("CLK_TIME:");
				String clockVal = splits[splits.length - 1].replaceAll("\r\n", "");
				int oldVal = clock.getClock();
				// if current clk value is less than the received one clks are adjusted accordingly.
				//https://www.mkyong.com/java/java-convert-string-to-int/
				int val = clock.checkAdjustment(Integer.parseInt(clockVal));
				if (val == -99) {
					ClientWindow.Log("Current clock is at " + clock.getClock() + ". No adjustment needed.");
				} else {
					ClientWindow.Log("Old clock was at " + oldVal + ". Current clock is at " + clock.getClock()
							+ ". Adjustments were made.");
				}
			}

			Thread.sleep(500);

			// ClientWindow.Log("time_counter");
		}

		wr.close();
		rd.close();
		socket.close();
	}

	private void writeToServer(BufferedWriter wr, int rand) throws IOException {
		// TODO Auto-generated method stub
		wr.write(rand);
		wr.flush();

	}

	// This method is used to write to the server
	private void writeToServer(BufferedWriter wr, String req) throws IOException {
		wr.write(req);
		wr.flush();
	}

	// This method is used to handle the response from the server.
	private String readResponseDataFromServer(BufferedReader reader) throws IOException {

		String line;
		String requestData = "";
		final ArrayList<String> lines = new ArrayList<>();
		// To read complete data from the buffer until the http header is complete.

		while (true) {
			try {
				TimeoutBlock timeoutBlock = new TimeoutBlock(900);// set timeout in milliseconds
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

				timeoutBlock.addBlock(block);// execute the runnable block

			} catch (Throwable e) {
				// catch the exception here . Which is block didn't execute within the time
				// limit
				return null;
			}

			line = lines.get(0);
			lines.remove(0);

			if (line == null)
				break;
			else {
				// If the line is \r\n or the line is empty the header data is completed the
				// next data is the data to be read.
				if (line.equals("\r\n") || line.equals("")) {
					// To read the data line
					requestData = reader.readLine();
					// To send the request data to server window
					ServerWindow.Log(requestData);
					break;
				}
			}
			ServerWindow.Log(line);
			// System.out.println(line);
		}
		if (requestData == null) {
			// To handle when the data is null
			System.out.println("NULL Data");
		}
		return requestData;
	}

	// https://stackoverflow.com/questions/5683728/convert-java-util-date-to-string
	// http://www.java2s.com/Tutorial/Java/0120__Development/Formatdateinmmddyyyyhhmmssformat.htm
	// http://tutorials.jenkov.com/java-internationalization/simpledateformat.html
	// Java method to convert date of dd-MM-yyyy HH:mm:ss into a string
	public String getDate() {
		Date todaysDate = new Date();
		DateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String str = dateformat.format(todaysDate);
		return str;
	}
}
