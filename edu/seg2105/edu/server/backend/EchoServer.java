package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import ocsf.server.*;
import java.io.*;
import edu.seg2105.client.common.ChatIF;
import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer {
  
	private ChatIF serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   * @param serverUI The interface used to display messages to the server console.
   */
  public EchoServer(int port, ChatIF serverUI) 
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client) {
	  
	  String message = msg.toString();
	  String loginID = (String) client.getInfo("loginID");
	  
	  if (message.startsWith("#login")) {
		  // Check for loginID
		  if (loginID != null) {
			  try {
				  client.sendToClient("Error: #login can only be sent once.");
				  client.close();
			  } catch (IOException e) {}
			  return;
		  }
		  
		  // Extract the loginID from the message
		  String[] parts = message.split(" ", 2);
		  if (parts.length < 2 || parts[1].trim().isEmpty()) {
			  try {
				  client.sendToClient("Error: Invalid login command.");
				  client.close();
			  } catch (IOException e) {}
			  return;
		  }
		  
		  client.setInfo("loginID", parts[1].trim());
		  System.out.println("Client " + client + " logged in as " + parts[1].trim());
		  try {
			  client.sendToClient("Successfully logged in as " + parts[1].trim());
		  } catch (IOException e) {}
		  return;
	  }
	  
	  // If loginID is not set, reject all messages
	  if (loginID == null) {
	        try {
	            client.sendToClient("Error: You must log in first.");
	            client.close();
	        } catch (IOException e) {}
	        return;
	    }

	  String messageToSend = loginID + "> " + message;
	  this.sendToAllClients(messageToSend);
	  System.out.println("Message received from " + loginID + ": " + message);
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  /**
   * Called when a client connects to the server.
   *
   * @param client The connection of the client that just connected.
   */
  @Override
  protected void clientConnected(ConnectionToClient client) {
	  String loginID = (String) client.getInfo("loginID");
	  System.out.println("Client " + loginID + " has connected to the server.");
  }

  /**
   * Called when a client disconnects from the server.
   *
   * @param client The connection of the client that disconnected.
   */
  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  String loginID = (String) client.getInfo("loginID");
	  System.out.println("Client " + loginID + " has disconnected from the server.");
  }

  /**
   * Called when a client connection causes an exception.
   *
   * @param client The connection of the client that caused the exception.
   * @param exception The exception thrown by the client's thread.
   */
  @Override
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
	  String loginID = (String) client.getInfo("loginID");
	  if (loginID != null) {
		  System.out.println("Client " + loginID + " disconnected due to an error: " + exception);
	  } else {
		  System.out.println("A client disconnected due to an error: " + exception);
	  }
  }
  
}
//End of EchoServer class