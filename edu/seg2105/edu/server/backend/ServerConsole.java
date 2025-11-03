package edu.seg2105.edu.server.backend;

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.common.*;

public class ServerConsole implements ChatIF {

	/**
	 * The default port to connect on.
	 */
	final public static int DEFAULT_PORT = 5555;
	
	private EchoServer server;
    private Scanner fromConsole;
    
    public ServerConsole(int port) {
    	
    		try {
    			server = new EchoServer(port);
    			server.listen();
    			System.out.println("The server started and listening on port " + port);
    		} catch (IOException e) {
    			System.out.println("Error: Can't start listening on port " + port);
    			System.exit(1);
    		}
        
    		fromConsole = new Scanner(System.in);
    }
    
   /**
    * This method waits for input from the console.  Once it is 
    * received, it sends it to the client's message handler.
    */
    public void accept() 
    {
      try {
        String message;

        while (true) 
        {
          message = fromConsole.nextLine();
          
          if (message.startsWith("#")) {
        	  	handleCommand(message);
          } else {
        	  	display("SERVER MSG> " + message);
        	  	server.sendToAllClients("SERVER MSG> " + message);
          }
        }
      } 
      catch (Exception ex) 
      {
        System.out.println("Unexpected error while reading from console!");
      }
    }
    
    private void handleCommand(String command) {
        String[] parts = command.split(" ");
        
        try {
            if (command.equals("#quit")) {
            		if (server.isListening()) {
            			server.stopListening();
            		}
            		server.close();
            		display("Shutting down the server...");
                System.exit(0);
                
            } else if (command.equals("#stop")) {
            		if (server.isListening()) {
            			server.stopListening();
            			display("The server stopped listening for new clients.");
                } else {
                		display("The server hass already stopped.");
                }
            		
            	} else if (command.equals("#close")) {
            		server.close();
            		display("Server closed and all clients disconnected.");
            		
            	} else if (parts[0].equals("#setport")) {
            		if (server.isListening()) {
            			display("Cannot set port while server is running. Please close the server first.");
            			} else if (parts.length < 2) {
            				display("Usage: #setport <port>");
            				} else {
            					try {
            						int newPort = Integer.parseInt(parts[1]);
            						server.setPort(newPort);
            						display("Server port set to: " + server.getPort());
            					} catch (NumberFormatException e) {
            						display("Error: the port must be a number.");
            					}
            				}
            		
            } else if (command.equals("#start")) {
            		if (!server.isListening()) {
            			server.listen();
            			display("The server started listening on port " + server.getPort());
            			} else {
            				display("The server is already running.");
            			}
            		
            	} else if (command.equals("#getport")) {
            		display("The current server port: " + server.getPort());
            		
            	} else {
            		display("Unknown command: " + command);
            	}
        } catch (IOException e) {
        		display("Error executing command: " + e.getMessage());
        }
    }
    
   /**
    * This method overrides the method in the ChatIF interface.  It
    * displays a message onto the screen.
    *
    * @param message The string to be displayed.
    */
    public void display(String message) 
    {
      System.out.println("> " + message);
    }
    
    public static void main(String[] args) {

    	    int port;

    	    try {
    	      port = Integer.parseInt(args[1]);
    	    } catch(ArrayIndexOutOfBoundsException e) {
    	    		port = DEFAULT_PORT;
    	    } catch(NumberFormatException ne) {
    	    		port = DEFAULT_PORT;
    	    }
    		ServerConsole serverConsole = new ServerConsole(port);
    	    serverConsole.accept();
    	  }
}