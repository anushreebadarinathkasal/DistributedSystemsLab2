//Name: Anushree Badarinath Kasal
//Student ID: 1001624311
//Net ID: abk4311

//This file has the code of Server GUI.
//I have used Swing components to implement the GUI of Server Window. which displays the server side logs as well as the active list of clients.
//It includes start and stop buttons to start and stop server.

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

//To install swing extension into eclipse
//https://www.youtube.com/watch?v=oeswfZz4IW0
//Usage of Swing components
//https://docs.oracle.com/javase/tutorial/uiswing/components/


public class ServerWindow {

	private JFrame frame;
	Server sv;
	static JTextArea textArea;
	static JTextArea clientList;

	private static ArrayList<String> clients = new ArrayList<>();

	/**
	 * Launch the application.
	 */
	//To create the initial Server window
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerWindow window = new ServerWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	

	//To display the logs in server window
	public static void Log(String msg) {
		if (SwingUtilities.isEventDispatchThread()) {
			if(msg != null && textArea != null)
			textArea.setText(textArea.getText() + "\n" + msg);
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stu
					if(msg != null && textArea != null)
					textArea.setText(textArea.getText() + "\n" + msg);
				}
			});
		}
	}
   
	//To display the active list of clients on the server
	public static synchronized void renderClientList() {

		if (SwingUtilities.isEventDispatchThread()) {
			String text = "";
			for (String value : clients) {
				text += value + "\n";
			}
			clientList.setText(text);
			System.out.println(text);
			System.out.println("render called from thread: " + Thread.currentThread().getName());
			System.out.println(clients.size());
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					String text = "";
					for (String value : clients) {
						text += value + "\n";
					}
					clientList.setText(text);
					System.out.println(text);
					System.out.println("render called from thread: " + Thread.currentThread().getName());
					System.out.println(clients.size());
				}
			});
		}
	}
  
	//Client is added into the list once a client window starts.
	public static synchronized void addClient(String name) {
		clients.add(name);
		renderClientList();
	}

	//Client is removed from the list when the client window closes.
	public static synchronized void removeClient(String name) {
		System.out.println("remove called from thread: " + Thread.currentThread().getName());
		int index = -1;
		for (int i = 0; i < clients.size(); i++) {
			String string = (String) clients.get(i);
			if (string.equals(name)) {
				index = i;
				break;
			}
		}
		if (index != -1)
			clients.remove(index);
		renderClientList();
	}

	/**
	 * Create the application.
	 */
	public ServerWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	//https://javatutorial.net/swing-jframe-basics-create-jframe
	//https://docs.oracle.com/javase/tutorial/uiswing/layout/box.html
	//Components of the Server window
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(50, 50, 800, 800);
		frame.setTitle("Server Window");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//frame.getContentPane().setLayout(null);
		
		frame.setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		JPanel panel = new JPanel();// to include start stop buttons
		JPanel panel2 = new JPanel();//contains panel 3
		JPanel panel3 = new JPanel();//to include text area and scroll bar
		JPanel panel4 = new JPanel();//to include client list
		
		panel3.setLayout(new BoxLayout(panel3,BoxLayout.X_AXIS));
		

		JButton btnStart = new JButton("Start");
		btnStart.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnStart.setBounds(294, 715, 97, 25);

		btnStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				Thread svThread = new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						sv = new Server(8818);
						try {
							sv.startServer();
						} catch (NumberFormatException | IOException | InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});
				svThread.start();
			}
		});

		panel.add(btnStart);
		JButton btnStop = new JButton("Stop");
		btnStop.setFont(new Font("Tahoma", Font.PLAIN, 18));
		btnStop.setBounds(416, 715, 97, 25);

		btnStop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
        panel.add(btnStop);
		JLabel lblActiveListOf = new JLabel("Active Clients List:");
		lblActiveListOf.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblActiveListOf.setBounds(83, 500, 182, 16);
		//frame.getContentPane().add(lblActiveListOf);
		panel4.add(lblActiveListOf);
		Border border = BorderFactory.createLineBorder(Color.BLACK);
		
		//https://stackoverflow.com/questions/10177183/java-add-scroll-into-text-area
		//https://docs.oracle.com/javase/7/docs/api/javax/swing/border/CompoundBorder.html
		
		textArea = new JTextArea(30,75);
		textArea.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		textArea.setBounds(37, 60, 705, 427);
		
		//frame.getContentPane().add(textArea);
		
		//https://www.youtube.com/watch?v=GfJokob_eGM
		
		JScrollPane jp = new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		//frame.getContentPane().add(jp);
		//frame.getContentPane().add(jp);
		
		panel.add(jp);
		

		JLabel lblLogs = new JLabel("Logs:");
		lblLogs.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblLogs.setBounds(37, 23, 56, 24);
		//frame.getContentPane().add(lblLogs);
		panel3.add(lblLogs);
		
		clientList = new JTextArea(20,60);
		clientList.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		//clientList
		//		.setBorder(new CompoundBorder(new LineBorder(new Color(0, 0, 0)), new LineBorder(new Color(0, 0, 0))));
		//clientList.setFont(new Font("Tahoma", Font.PLAIN, 15));
		clientList.setBounds(56, 60, 171, 160);

		
		panel3.add(jp);
		panel4.add(clientList);
		panel2.add(panel3);
		
		
		mainPanel.add(panel);
		mainPanel.add(panel2);
		mainPanel.add(panel4);

		frame.getContentPane().add(mainPanel);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	private void setContentPane(JPanel contentPane) {
		// TODO Auto-generated method stub
		
	}


}
