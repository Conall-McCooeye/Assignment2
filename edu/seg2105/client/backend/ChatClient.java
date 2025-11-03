// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      if (message.startsWith("#")) {
    	  	handleCommand(message);
      }
      else
    	  	sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  private void handleCommand (String command){
	  String[] parts = command.split(" ");
	  
	  try {
		  if(command.equals("#quit") ) {
			  quit();
		  }
		  
		  else if (command.equals("#logoff")) {
			  if (isConnected()) {
			        sendToServer("#logoff");
			        closeConnection();
			        clientUI.display("You have logged off of the server.");
			  } else {
				  clientUI.display("You have already logged off");
			  }
		  }
		  
		  else if (command.equals("#sethost")) {
			  if (isConnected()) {
				  clientUI.display("Cannot set host while connected to the server. Please log off of the server.");
			  } else if (parts.length < 2) {
				  clientUI.display("Usage: #sethost <host>");
			  } else {
				  setHost(parts[1]);
				  clientUI.display("Host set to: " + getHost());
			  }
		  }
		  
		  else if (parts[0].equals("#setport")) {
			  if (isConnected()) {
				  clientUI.display("Cannot set port while connected to the server. Please log off first.");
			  } else if (parts.length < 2) {
				  clientUI.display("Usage: #setport <port>");
			  } else {
				  try {
					  int newPort = Integer.parseInt(parts[1]);
					  setPort(newPort);
					  clientUI.display("Port set to: " + getPort());
				  } catch (NumberFormatException e) {
					  clientUI.display("Error: port must be a number.");
				  }
			  }
		  }
		  
		  else if (command.equals("#login")) {
			  if (isConnected()) {
				  clientUI.display("You are already connected to the server.");
			  } else {
				  openConnection();
				  clientUI.display("Logged in to server " + getHost() + " on port " + getPort());
			  }
		  }
		  
		  else if (command.equals("#gethost")) {
			  clientUI.display("Current host: " + getHost());
		  }
		  
		  else if (command.equals("#getport")) {
			  clientUI.display("Current port: " + getPort());
		  }
		  
		  else {
			  clientUI.display("Unknown command: " + command);
		  }
	  }
	  catch (IOException e) {
	        clientUI.display("Error processing command: " + e.getMessage());
	    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  /**
   * Implements the hook method called each time an exception is thrown by the client's
   * thread that is waiting for messages from the server. The method may be
   * overridden by subclasses.
   * 
   * @param exception
   *            the exception raised.
   */
  @Override
  protected void connectionException(Exception exception) {
	  clientUI.display("The server has shut down");
	  quit();
  }
  
  /**
   * Implements the hook method called after the connection has been closed. The default
   * implementation does nothing. The method may be overriden by subclasses to
   * perform special processing such as cleaning up and terminating, or
   * attempting to reconnect.
   */
  @Override
  protected void connectionClosed() {
	  clientUI.display("Connection Closed");
  }
}
//End of ChatClient class
