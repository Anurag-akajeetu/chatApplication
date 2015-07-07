/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.util.*;
import java.net.*;
import java.io.*;
/**
 *
 * @author user
 */
public class Chatserver {

    /**
     * @param args the command line arguments
     */
    private ObjectInputStream input;
    private ObjectOutputStream output;
	private ServerSocket serverSock;
	private Socket fromClient;
	static String validate,matching;
	private static Vector loginNames;
	private static Vector loginSocks;
	//private String userid,password;
        
        public Chatserver()
	{


	    //{
         //String userid;
		 //String password;

	    //}//


	  	loginNames = new Vector();
		loginSocks = new Vector();
		try
		{
		  	serverSock = new ServerSocket(12345);
	    }
		catch (Exception Excep)
		{
			//Excep.printStackTrace();
                     System.out.println(Excep);
                }
 	    System.out.println("Server Started");
	    try
		{
			while (true)
	         {
			    try
				{
					fromClient = serverSock.accept();
					System.out.println("Server:Client connected");
					 try
           {
            input = new ObjectInputStream(fromClient.getInputStream());
	        matching = (String)input.readObject();
	       }catch(Exception e){System.out.println(e);}



					Connect con = new Connect(fromClient);
			    }
				catch(Exception ex)
                                {
                                    //ex.printStackTrace();
                                     System.out.println(ex);
                                }
		}
	    }catch (Exception Excep)

            {
              //  Excep.printStackTrace();
                 System.out.println(Excep);
            }

	}
    public static void main(String[] args) {
        // TODO code application logic here
        Chatserver c=new Chatserver();
    }
class Connect extends Thread
	{
		Socket listClientSocket;
		PrintStream outPrintStream;
		String stringFromClient;
		String currentLogin;
		String delLogin;
		String cmdStr;
		StringTokenizer readTokenizer;
		Iterator myIterator;
		String tmpNameList;
		String tmpName;
		String tmpMesg;
		Socket fromClient;
		boolean threadOk=true;
		private PrintStream out;
		private BufferedReader in;
		public Connect(Socket fromClient)
		{
			super();
			this.fromClient = fromClient;
			try
			{
				out = new PrintStream(fromClient.getOutputStream());
				in = new BufferedReader(new InputStreamReader(fromClient.getInputStream()));
			}
			catch (Exception Excep)
                        {
                          //Excep.printStackTrace();
                            System.out.println(Excep);
                        }
			this.start();
		}
		public void run()
		{

		    System.out.println("Server:thread started for "+fromClient);
		    while (threadOk)
		    {
			try
			{
				stringFromClient = in.readLine();
				System.out.println("Server:" +stringFromClient);
				/* Storing the client input into a variable of type
				StringTokenizer with ":" as a separator */
				readTokenizer = new StringTokenizer(stringFromClient,":");
				// Moving to the next element
				cmdStr = readTokenizer.nextToken();
				// If a new client has logged in
				if (cmdStr.equals("Login"))
				{
	                // Move to the next element and store the name of the person logged in
					currentLogin = readTokenizer.nextToken();
					// Add the loginnames to the vector
					loginNames.add(currentLogin);
					// Add the socket to the vector
					loginSocks.add(fromClient);
					// Moving through the vector i.e. Login names
			  		myIterator = loginNames.iterator();
					tmpName = "NewLogin";
					tmpMesg = "NewLoginMess:"+currentLogin;
					while (myIterator.hasNext())
					{
						tmpName += ":";
						tmpName += (String)myIterator.next();
					}
					myIterator = loginSocks.iterator();
					// Moving through the vector LoginSocks
					while (myIterator.hasNext())
					{
					    listClientSocket = (Socket)myIterator.next();
					    outPrintStream = new PrintStream(listClientSocket.getOutputStream());
                        // Passing the information about the new person who has logged in and the message to the clients 0
						outPrintStream.println(tmpName);
						outPrintStream.println(tmpMesg);
					}
			  	}
				// If any client has logged out
			  	if (cmdStr.equals("Logout"))
				{
					fromClient.close();
					//this.stop();
					in.close();out.close();
					//break;
			  		delLogin = readTokenizer.nextToken();
                    //Storing the element number of the client who has logged out into a variable
					int vecIndex = loginNames.indexOf((Object)delLogin);
					int curIndex = loginSocks.indexOf((Object)fromClient);
					if (vecIndex == curIndex)
					threadOk = false;
					//Remove the logged out client from the vector
					loginNames.removeElementAt(vecIndex);
					loginSocks.removeElementAt(vecIndex);
			  		myIterator = loginNames.iterator();
					tmpName = "DelLogin";
					tmpMesg = "DelLoginMess:"+delLogin;
					while (myIterator.hasNext())
					{
						tmpName += ":";
						tmpName += (String)myIterator.next();
					}
					myIterator = loginSocks.iterator();
					/*Send the information of the logged out client to the 					other clients */
					while (myIterator.hasNext())
					{
						listClientSocket = (Socket)myIterator.next();
						outPrintStream = new PrintStream
						(listClientSocket.getOutputStream());
						outPrintStream.println(tmpName);
						outPrintStream.println(tmpMesg);
					}
					break;
			  	}
				// If any client wants to chat
			  	if (cmdStr.equals("SendMessage"))
				{
					//Store the message of the client
			  		String mesg = readTokenizer.nextToken();
					//Store the message of the sender
					String sender = readTokenizer.nextToken();
					String receiver;
					Socket sendToSocket;
					//Send the message to all the clients
					while (readTokenizer.hasMoreTokens())
					{
						receiver = readTokenizer.nextToken();
						int vecIndex = loginNames.indexOf((Object)receiver);
						sendToSocket = (Socket)loginSocks.elementAt(vecIndex);
						outPrintStream = new PrintStream(sendToSocket.getOutputStream());
						outPrintStream.println("ChatMess:"+mesg+":"+sender);
					}
				}
                                   if (cmdStr.equals("Filesend"))
				{
					//Store the message of the client
			  		String File = readTokenizer.nextToken();
					//Store the message of the sender
					String sender = readTokenizer.nextToken();
					String receiver;
					Socket sendToSocket;
					//Send the message to all the clients
					while (readTokenizer.hasMoreTokens())
					{
						receiver = readTokenizer.nextToken();
						int vecIndex = loginNames.indexOf((Object)receiver);
						sendToSocket = (Socket)loginSocks.elementAt(vecIndex);
						outPrintStream = new PrintStream(sendToSocket.getOutputStream());
						outPrintStream.println("Filesend:"+File+":"+sender);
					}
				} 
			} catch (Exception Excep){System.out.println("Inputstream problem "+ Excep);}
		}
	   }
	}

}
