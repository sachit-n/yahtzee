package game;
import java.awt.BorderLayout;

import java.awt.Dimension;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
 

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class SaveServer extends JFrame {

	private JTextArea wordsBox;
	ServerSocket serverSocet;
	private ObjectInputStream inputFromClient;
	private ObjectInputStream inputFromClient2;
	
	YahtzeeGame yahtzeeGame;
	
	private Connection conn;
	private PreparedStatement insertStmt;
	private PreparedStatement updateStmt1;
	private PreparedStatement updateStmt2;
	private PreparedStatement updateStmt3;
	private PreparedStatement qryGames;
	private PreparedStatement qryGame;
	
	public SaveServer() {
		yahtzeeGame = new YahtzeeGame();
//		wordsBox = new JTextArea();
		createMainPanel();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600,400);
		setVisible(true);
		try {
		     ServerSocket serverSocket = new ServerSocket(8080);
		     wordsBox.append("Server started\n");
		     while (true) {
		       // Listen for a new connection request
		       Socket socket = serverSocket.accept();

		       // Create an input stream from the socket
		       wordsBox.append("Recieving Request from client...\n");
		       inputFromClient =
		         new ObjectInputStream(socket.getInputStream());

		       // Read from input
		       Object object = inputFromClient.readObject();
		       if (object.equals("Load Request")) {
		    	   // ToDo - Load Feature 
					 }
		       else {
		       // Received Save Request. Client sent game object. 
		       yahtzeeGame = (YahtzeeGame)object;
		       wordsBox.append("Recieved game state from client. Now saving to SQL\n");
		       saveToSQL();
				}
		     }
			}
		
	    catch(ClassNotFoundException ex) {
		     ex.printStackTrace();
			}
		catch(IOException ex) {
		     ex.printStackTrace();
		    }
		   finally {
		     try {
		       inputFromClient.close();
		     }
		     catch (Exception ex) {
		       ex.printStackTrace();
		     }
		   }
		}
	
	public void saveToSQL() {
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:yahtzee.db");
			insertStmt = conn.prepareStatement("INSERT INTO saveGames (playerName, turnsRem, rollsRem) VALUES (?, ?, ?)");
			insertStmt.setString(1, yahtzeeGame.playerName);
			insertStmt.setString(2, "" + yahtzeeGame.turnsRem);
			insertStmt.setString(3, "" + yahtzeeGame.rollsRem);
			insertStmt.execute();
			
			for(int i=0; i<5; i++) {
				updateStmt1 = conn.prepareStatement("UPDATE saveGames SET diceArray" + i + "=? WHERE ID=(SELECT max(ID) from saveGames);");
				updateStmt1.setString(1, "" + yahtzeeGame.diceArray.getDieValue(i));
				updateStmt1.execute();
			}
			
			for(int i=0; i<19; i++) {
				updateStmt2 = conn.prepareStatement("UPDATE saveGames SET buttonsSelected" + i + "=? WHERE ID=(SELECT max(ID) from saveGames);");
				updateStmt2.setString(1, "" + yahtzeeGame.buttonsSelected[i]);
				updateStmt2.execute();
			}
			
			for(int i=0; i<19; i++) {
				updateStmt3 = conn.prepareStatement("UPDATE saveGames SET scoresArray" + i + "=? WHERE ID=(SELECT max(ID) from saveGames);");
				updateStmt3.setString(1, "" + yahtzeeGame.scoresArray[i]);
				updateStmt3.execute();
			}
			wordsBox.append("Saved Game to SQL\n");
			
		} catch (SQLException e) {
			System.err.println("Connection error: " + e);
			System.exit(1);
		}
		
	}
			
	public void createMainPanel() {
		wordsBox = new JTextArea(35,10);

		JScrollPane listScroller = new JScrollPane(wordsBox);
		this.add(listScroller, BorderLayout.CENTER);
		listScroller.setPreferredSize(new Dimension(250, 80));
	}
	
	public static void main(String[] main) {
		SaveServer saveServer = new SaveServer();
		
	}
}
