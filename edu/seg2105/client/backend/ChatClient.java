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
  
  private String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI, String loginID) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    this.loginID = loginID;
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
  
  private void handleCommand (String command) {
	  if (command.equals("#quit")) {
		  quit();
	  }
	  else if (command.equals("#logoff")) {
		  if (!isConnected()) {
			  clientUI.display("Error: You are not currently connected to the server.");
		  } else {
			  try {
				  closeConnection();
			  } catch (IOException e) {
				  clientUI.display("Error: Could not log off properly.");
			  }
		  }
	  }
	  else if (command.equals("#sethost")) {
		  if (isConnected()) {
			  clientUI.display("Error: You must log off before setting the host.");
		  } else {
			  String[] parts = command.split(" ");
			  if (parts.length < 2) {
				  clientUI.display("Usage: #sethost <host>");
			  } else {
				  setHost(parts[1]);
				  clientUI.display("Host set to " + parts[1]);
			  }
		  }
	  }
	  else if (command.equals("#setport")) {
		  if (isConnected()) {
			  clientUI.display("Error: You must log off before setting the port.");
		  } else {
			  String[] parts = command.split(" ");
			  if (parts.length < 2) {
				  clientUI.display("Usage: #setport <port>");
			  } else {
				  try {
					  int newPort = Integer.parseInt(parts[1]);
					  setPort(newPort);
					  clientUI.display("Port set to " + newPort);
				  } catch (NumberFormatException e) {
					  clientUI.display("Error: Port must be an integer.");
				  }
			  }
		  }
	  }
	  else if (command.equals("#login")) {
		  if (isConnected()) {
			  clientUI.display("Error: Already connected to the server.");
		  } else {
			  try {
				  openConnection();
				  clientUI.display("Successfully logged in to the server.");
			  } catch (IOException e) {
				  clientUI.display("Error: Could not connect to server.");
			  }
		  }
	  }
	  else if (command.equals("#gethost")) {
		  clientUI.display("Current host: " + getHost());
	  }
	  else if (command.equals("#getport")) {
		  clientUI.display("Current port: " + getPort());
	  }
	  else
		  clientUI.display("Command not recognized, please try again.");  
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
   * implementation does nothing. The method may be overridden by subclasses to
   * perform special processing such as cleaning up and terminating, or
   * attempting to reconnect.
   */
  @Override
  protected void connectionClosed() {
	  clientUI.display("Connection closed");
  }
  
  /**
   * Implements the hook method called after a connection has been established. The default
   * implementation does nothing. It may be overridden by subclasses to do
   * anything they wish.
   */
  @Override
  protected void connectionEstablished() {
      try {
          // Send login command immediately after connection
          sendToServer("#login " + loginID);
      } catch (IOException e) {
          clientUI.display("Error: Could not send login ID to server.");
      }
  }

}
//End of ChatClient class