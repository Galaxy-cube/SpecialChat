package cc.specialchat.specialchatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

import static java.lang.System.exit;

public class ServerMain{
	
	public static void main(String[] args) throws SQLException, ClassNotFoundException{
		
		try{
			ServerSocket serverSocket = new ServerSocket(21027);
			System.out.println("---- Special Chat Server started ----");
			int count = 0;
			
			Socket socket;
			while (true) {
				socket = serverSocket.accept();
				ServerThread serverThread = new ServerThread(socket);
				System.out.println("New connection: " + socket.getInetAddress().getHostAddress() );
				serverThread.start();
				count++;
				System.out.println(count+" connections untill now! ");
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
}
