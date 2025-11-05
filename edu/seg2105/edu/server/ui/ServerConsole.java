package edu.seg2105.edu.server.ui;

import java.io.IOException;
import java.util.Scanner;
import edu.seg2105.edu.server.backend.EchoServer;
import edu.seg2105.client.common.ChatIF;

public class ServerConsole implements ChatIF {
	
	/**
	 * The default port to connect on.
	 */
	final public static int DEFAULT_PORT = 5555;
	
	
	/**
	   * The instance of the server that created this ConsoleChat.
	   */
	private EchoServer server;
	
	/**
	* Scanner to read from the console
	*/
    private Scanner fromConsole;
    
 // Constructor
    public ServerConsole(int port) {
        server = new EchoServer(port, this);
        
        // Create scanner object to read from console
        fromConsole = new Scanner(System.in);
    }
    
    /**
     * This method is responsible for the creation of 
     * the server instance
     *
     * @param args[0] The port number to listen on.  Defaults to 5555 
     *          if no argument is entered.
     */
    public static void main(String[] args) {
      int port = 0; //Port to listen on

      try
      {
        port = Integer.parseInt(args[0]); //Get port from command line
      }
      catch(Throwable t)
      {
        port = DEFAULT_PORT; //Set port to 5555
      }
  	
      ServerConsole console = new ServerConsole(port);

      try {
          console.server.listen();
      } catch (Exception ex) {
          console.display("ERROR - Could not listen for clients!");
      }

      console.accept();
    }
    
    public void accept() 
    {
    		try 
        {
            
        		String message;

        		while (true) 
            {
            	message = fromConsole.nextLine();

                if (message.startsWith("#")) {
                		handleCommand(message);
                } else {
                	String serverMessage = "SERVER MSG> " + message;
                    display(serverMessage);
                    server.sendToAllClients(serverMessage);
                }
            }
        } catch (Exception ex) 
        {
            System.out.println
              ("Unexpected error while reading from console!");
          }
    }
    
    private void handleCommand(String command) {
    		if (command.equals("#quit")) {
    			try {
    				server.close();
    			} catch (IOException e) {
    				display("Error closing the server.");
    			}
    			display("Server shutting down.");
    			System.exit(0);
    		}
    		else if (command.equals("#stop")) {
    			server.stopListening();
    			display("Server stopped listening for new clients.");
    		}
    		else if (command.equals("#close")) {
    			try {
    				server.close();
    				display("Server closed. All clients disconnected.");
    			} catch (IOException e) {
    				display("Error: Could not close server properly.");
    			}
    		}
    		else if (command.startsWith("#setport")) {
    			if (server.isListening()) {
    				display("Error: Cannot change port while server is listening.");
    			} else {
    				String[] parts = command.split(" ");
    				if (parts.length < 2) {
    					display("Usage: #setport <port>");
    				} else {
    					try {
    						int newPort = Integer.parseInt(parts[1]);
    						server.setPort(newPort);
    						display("Port set to " + newPort);
    					} catch (NumberFormatException e) {
    						display("Error: Port must be an integer.");
    					}
    				}
    			}
    		}
    		else if (command.equals("#start")) {
    			if (server.isListening()) {
    				display("Server is already listening for new clients.");
    			} else {
    				try {
    					server.listen();
    					display("Server started listening for new clients.");
    				} catch (Exception e) {
    					display("Error: Could not start listening.");
    				}
    			}
    		}
    		else if (command.equals("#getport")) {
    			display("Current port: " + server.getPort());
    		}

    		else {
    			display("Command not recognized, please try again.");  
    		}
	}

    /**
     * This method overrides the method in the ChatIF interface.  It
     * displays a message onto the screen.
     *
     * @param message The string to be displayed.
     */
    @Override
    public void display(String message) {
        System.out.println(message);
    }
}
//End of ServerConsole class