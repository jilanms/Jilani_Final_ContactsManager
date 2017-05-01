package main;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ContactInfoController implements Initializable {

	@FXML
		TextField firstName;
		@FXML
		TextField lastName;
		@FXML
		TextField email;
		@FXML
		TextField phone;
		@FXML
		TextField address;
		@FXML
		TextField companyName;
		@FXML
		TextField companyPhone;
		@FXML
		TextArea notes;
		
		private DbDataSource dataSource;
		private Contact contact;

	public void initialize(URL location, ResourceBundle resources) {
		dataSource = new DbDataSource();
	}
	
	public void save(ActionEvent ev){
		Contact obj = new Contact();
		obj.setFirstName(firstName.getText());
		obj.setLastName(lastName.getText());
		obj.setEmail(email.getText());
		obj.setAddress(address.getText());
		obj.setPhone(phone.getText());
		obj.setCompanyName(companyName.getText());
		obj.setCompanyPhone(companyPhone.getText());
		obj.setNotes(notes.getText());
		boolean saved = false;
		if(this.contact != null){
			obj.setId(contact.getId());
			saved = dataSource.edit(obj);
		}else{
			saved = dataSource.save(obj);
		}
		if(saved){
			Stage stage = (Stage) firstName.getScene().getWindow();
			stage.close();
		}
	}

	public void cancel(ActionEvent ev){
		Stage stage = (Stage) firstName.getScene().getWindow();
		stage.close();
	}

	public void setContact(Contact selectedItem) {
		this.contact = selectedItem;
		firstName.setText(contact.getFirstName());
		lastName.setText(contact.getLastName());
		email.setText(contact.getEmail());
		address.setText(contact.getAddress());
		phone.setText(contact.getPhone());
		companyName.setText(contact.getCompanyName());
		companyPhone.setText(contact.getCompanyPhone());
		notes.setText(contact.getNotes());
	}

}