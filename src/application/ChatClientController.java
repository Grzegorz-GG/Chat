package application;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

public class ChatClientController implements Initializable {

	/*Fields visible in "ChatClient.fxml"*/
	@FXML private Button connectButton, disconnectButton, exitButton, submitButton;
	@FXML private ScrollPane nickNamesPane, chatFieldPane;
	@FXML private TextField inputTextField;
	@FXML private TextArea inputChatTextField;
	@FXML private ListView<String> nickList;
	@FXML private DialogPane connectionDetails;
	
	/*Fields visible in a DialogPane which is opened in a new window after clicking "Connect" button in "ChatClient.fxml" .
	 * Separate fxml file called "ConnectionDetails.fxml" was created for this DialogPane, but it reuses ChatClientController.
	 */
	@FXML private TextField host;
	@FXML private TextField port;
	
	//Enum used in updating a nick list.
	private enum UPDATE {ADD, REMOVE};
	
	protected Boolean connected, stopped;
	protected BufferedReader input;
	protected DataOutputStream output;
	protected Socket socket;
	
	private String hostname, portname;
	private String myNick = null;
	
	@FXML //Send a chat message to server after clicking "Submit" button.
	public void submit() {
		
		if(!connected) {
			insertText("Not Connected - not able to send massage");
			inputTextField.clear();
			return;
		}
		
		String line = inputTextField.getText();
		inputTextField.clear();
		
		try {
			output.writeBytes(line + "\n");
			output.flush();
		} catch(IOException e) {
			insertText("Socket operation error: " + e + "\n");
		}
		
	}
	
	@FXML //Send a chat message to server after pressing "ENTER".
	public void onKeyPressed(KeyEvent event) {
		if(event.getCode() == KeyCode.ENTER) 
			submit();
	}
	
	@FXML
	public void exit() {
		System.exit(0);
	}
	
	@FXML //Open "ConnectionDetails.fxml" after clicking "Connect" button.
	public void connectClicked() {
		
		//Nothing happens if the client is already connected.
		if(connected == true) return;
		
		//Create loader, set location of fxml file and controller.
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(MainChatClient.class.getResource("ConnectionDetails.fxml"));
		loader.setController(this);
		
		//Create Dialog window which will be loaded.
	    Dialog<ButtonType> dialog = new Dialog<>();
	    dialog.setTitle("Connection details");
	    Optional<ButtonType> result = null;
		
	    //Load Dialog and wait for the result.
		try {
		    dialog.setDialogPane(loader.load());
		    result = dialog.showAndWait();
		    
		} catch(Exception e) {
		    e.printStackTrace();
		}
		
		//If "Finish" button clicked than connect to a specified host/port.
		if(result.isPresent() && result.get() == ButtonType.FINISH) {
			hostname = host.getText();
			portname = port.getText();
			connect();
		}
		
	}
	
	//Connect to a server - host and port were previously specified by a user.
	public void connect() {

		insertText("Connecting to the host");

		/*Create socket to connect this client with a server - user and port were 
		* specified by a user in text fields in the "ConnectionDetails.fxml" file.
		*/ 
		try {
			socket = new Socket(hostname, Integer.valueOf(portname));
		} catch (UnknownHostException e) {
			insertText("Connection failed - unknown host");
			return;
		} catch (IOException e) {
			insertText("Connection failed - I/O exception");
			return;
		} catch (Exception e) {
			insertText("Connection failed");
			return;
		}
		
		insertText("Socket initialization completed...\n");
		
		//Create output and input streams for the socket.
		try {
			output = new DataOutputStream(socket.getOutputStream());
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			insertText("Error during creating I/O streams");
		}
		
		//Connection was established.
		connected = true;
		
		/*Create and run thread responsible for reading messages from a server.
		 *Messages received from a server will be processed in processMessage() method.  
		 */
		new Thread(new Runnable() {

			@Override
			public void run() {
				String line = null;
				stopped = false;
				
				while(!stopped) {
					try {
						line = input.readLine();
					} catch (IOException e) {
						stopped = true;
						stopClientThread();
						return;
					}

					processMessage(line);
					
				}
				stopClientThread();
			}
		}).start();
		
		insertText("Connected to server! Please type command \"/nick\" to choose a nickname.");
	}
	
	//"Disconnect" button was clicked.
	@FXML
	public void disconnect() {
		if(!connected) {
			insertText("Not connected");
			return;
		}
		
		//Disconnect from a server
		stopClientThread();	
		
		//Clear the list with all nick names.
		Platform.runLater(() -> {
			nickList.getItems().clear();
		});
	}
	
	//Disconnect from a server
	public void stopClientThread() {
		
		//Client won't be connected anymore.
		connected = false;
		
		//This one is used in a network thread (loop reading messages from a server).
		stopped = true;
		
		//Close socket.
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			insertText("Socket operation error");
			return;
		}
		
		insertText("Disconnected!!!");
	}
	
	//Add some text to the main text area.
	public synchronized void insertText(String line) {
		Platform.runLater(() -> {
			inputChatTextField.appendText(line + "\n");
		});
	}
	
	//Process a message received from a server
	public void processMessage(String line) {
		
		if(line == null) return;
		
		//It's not a command, so show the message from another user.
		if(line.length() < 5) {
			insertText(line + "\n");
			return;
		}
		
		String command = line.substring(0, 5);
		
		/*Check what type of a command was sent by a server. Available options:
		 * 
		 * "/quit" - disconnect,
		 * "/nick xxx" - nick name chosen by some client was added and accepted by a server,
		 *  "/nkok" - nick name for this particular client (my nick name) was accepted,
		 *  "/nkrm xxx" - some nick name was removed or changed,
		 *  "/nonk" - nick was incorrect or not defined at all,
		 *  "/nkex" - nick already exists,
		 */
		if(command.equals("/quit")) {
			stopped = true;//disconnect
		} else if(command.equals("/nick")) {
			
			//Message: "/nick " which is wrong because there is no nick name.
			if(line.length() < 7) {
				insertText("Bad server response!");
				return;
			}
			
			//Get a nick name.
			String nick = line.substring(6, line.length());
			
			/*If it is not listed than add it to the list of nicks.
			 *The nick name for this particular client is always highlighted. 
			 */
			if(!nickExists(nick)) {
				updateNicks(nick, UPDATE.ADD);
				highlightMyNick(nickList);
			}
			
		} else if(command.equals("/nonk")) {
			//Nick needs to be defined in order to send messages to other clients
			insertText("Please specify your nick. Use \"/nick\" command. \n");	
		} else if(command.equals("/nkex")) {
			//Wrong nick
			insertText("This nick is already used by another user.\n");
		} else if(command.equals("/nkok")) {
			//Nick accepted - it's a nick name chosen for this particular client.
			insertText("Nick OK!\n");
			myNick = line.substring(6, line.length());
		} else if(command.equals("/nkrm") ) {
			
			//Message: "/nkrm " which is wrong because there is no nick name to remove.
			if(line.length() < 7) {
				insertText("Bad server command");
				return;
			}
			
			//Get a nick name to remove.
			String nick = line.substring(6, line.length());
			System.out.println("Remove nick: " + nick);
			
			/*If it exists than remove it and update list of nick names.
			 * My nick name is always highlighted.
			 */
			if(nickExists(nick)) {
				updateNicks(nick, UPDATE.REMOVE);
				highlightMyNick(nickList);
			}
		} else {
			
			//It's not a command, it's a message from another client.
			insertText(line + "\n");
		}
	}
	
	//Check if a nick name exists.
	public boolean nickExists(String nick) {
		for(int i = 0; i < nickList.getItems().size(); i++) {
			if(nickList.getItems().get(i).equals(nick))
				return true;
		}
		
		return false;
	}

	//Update list of nicks - add or remove nick.
	public void updateNicks(String nick, UPDATE type) {
		Platform.runLater(() -> {
			
			//Get all nicks.
			ObservableList<String> nicks = nickList.getItems();
			
			//Check if it is an update or a removal.
			if(type == UPDATE.REMOVE)
				nicks.remove(nick);
			else if(type == UPDATE.ADD)
				nicks.add(nick);
			
			//Update nicks in ListView
			nickList.setItems(nicks);
		}
		);
	}
	
	//Highlight my nick name.
	public void highlightMyNick(ListView<String> nickList) {
		Platform.runLater(() -> {
			
			//CellFactory will always have my nick name highlighted in red.
			nickList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
				@Override
				public ListCell<String> call(ListView<String> list) {
					return new ColouredNicks(myNick);			
				}
			});
		});
	}

	public void initialize(URL arg0, ResourceBundle arg1) {
		
		/*At the beginning the connection with a server is not established.
		*It must be enforced by a client (by simply clicking a button).
		*/
		connected = false;
		stopped = true;	
	}
	
}
