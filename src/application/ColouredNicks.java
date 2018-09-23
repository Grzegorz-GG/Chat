package application;

import javafx.scene.control.ListCell;

/* Customize the cell containing my nick.
 * It must be highlighted in red in order to be easily distinguishable from other nicks.
 */
public class ColouredNicks extends ListCell<String> {
	
	private String myNick;
	
	//Construct this class with my nickname.
	public ColouredNicks (String myNick) {
		this.myNick = myNick;
	}
	
	@Override
	public void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		
		//Null condition is required to overwrite properly updateItem() method.
		if(item == null || empty) {
			setText(null);
		}
		else if (item.toString().equals(myNick)) {
			this.setText(item);
			setStyle("-fx-text-fill: red");
		} 
		else {
			this.setText(item);
		}
	}
}