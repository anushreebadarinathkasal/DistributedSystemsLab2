# DistributedSystemsLab2
Synchronization and Logical Clocks
page 1 of 5
Objectives:
1. Synchronization with Logical Clocks.
2. Further experience with HTTP and sockets.
3. Further experience with multithreading.
Project Specification:
These will be individual projects. You may write the program in any language that is supported under any Integrated Development Environment (IDE). Keep in mind that more help may be available to you in some languages than in others. Furthermore, available controls, objects, libraries etc. may make some of these tasks easier in one language than in another. Finally, because of the lack of restrictions on IDEs, you will have to have that IDE available to demo to the TA. For example, you might bring a laptop to demo the program. Socket programming is so universal that you can probably find major portions of this part of the program with searching on Google. Using code you find on the Internet is fine, but be sure to document the source in the writeup and in the program source code!
You will write a system consisting of a server and three client processes. Each client process will connect to the server over a socket connection and register a user name at the server. The server should be able to handle all three clients concurrently and display the names of the connected clients in real time. The server should be a passive component that acts only to facilitate message delivery.
The server and the client should each be managed with a simple GUI. The GUI should provide a way kill the process without using the ‘exit’ button on the window.
Each client will maintain a logical clock. This clock will be implemented as a counter that will increment once every second. The clock will be initialized with a random integer between 0 and 50.
Every two to ten seconds, each client will randomly choose one other client (e.g., a unicast) and send that client its present local time. The remote clock will be adjusted based on Lamport’s Logical Clocks, described in Tanenbaum and van Steen (2nd Edition), section 6.2.1.
When a client sends a message, it will print the intended recipient of the message, as well as its present local time, to its respective GUI. When a client receives a message, it will print the sender’s ID, the sender’s local time, and whether or not its local clock needs to be adjusted according to Lamport’s Logical Clocks.
If a clock adjustment is necessary, it will print the necessary adjustment, and its clock will continue forward with that value. If an adjustment is not necessary, it should print, “No adjustment necessary.” Clients will continue to operate in such a fashion until manually killed by the user.
Messages exchanged between server and client should use HTTP formats and commands. The HTTP tags must use, at minimum, Host, User-Agent, Content-
Type, Content-Length, and Date. If you are polling the server, use GET. If you are sending data to the server, use POST.
The required actions are summarized as follows:
Client
The client will execute the following sequence of steps:
1. Connect to the server via a socket.
2. Provide the server with a unique user name. The user name may be:
a. A string provided by the user; or,
b. Some value associated with the process.
3. Proceed to send and receive messages until killed by the user.
Sending messages:
1. Randomly pick a client.
2. Print the remote client’s ID and local client’s logical time.
3. Encode the message in HTTP and upload to the server.
4. Indicate that the message has been successfully sent to the server.
Receiving messages:
1. Wait for a message to be received from the server.
2. Parse the HTTP metadata.
3. Print the sender’s ID.
4. Proceed according to Lamport’s Logical Clocks.
5. Print the necessary adjustment, or that no adjustment was necessary.
Server
The server should support three concurrently connected clients and display a list of which clients are connected in real-time. The server will execute the following sequence of steps:
1. Startup and listen for incoming connections.
2. Print that a client has connected and fork a thread to handle that client.
3. Accept and forward messages received from users.
4. Print the unparsed HTTP message received from a client to the screen.
5. Begin at step 3 until connection is closed by the client.
The server must correctly handle an unexpected client disconnection without crashing. When a client disconnects from the server, the server GUI must indicate this to the user in real time. The server should print messages both received from, and sent to, the client in unparsed HTTP so that the grader can verify the format.
Your program must operate independently of a browser. Date/time on the messages should be encoded according to HTTP.
