package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class MainController implements Initializable{
	
	@FXML
	TableView<Contact> tableView;
	@FXML
	TableColumn<Contact,String> nameColunm;	
	@FXML
	TableColumn<String,String> emailColunm;	
	@FXML
	Label hoverDetails;
	@FXML
	Button editButton;
	@FXML
	Button removeButton;

	private DbDataSource dataSource;

	public void initialize(URL location, ResourceBundle resources) {
		dataSource = new DbDataSource();
		List<Contact> list = dataSource.selectAll();
    	ObservableList<Contact> items = FXCollections.observableArrayList(list);
    	
    	nameColunm.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Contact,String>, ObservableValue<String>>() {
			
			public ObservableValue<String> call(CellDataFeatures<Contact, String> param) {
				Contact val = param.getValue();
				return new SimpleStringProperty(val.getFirstName() + " "+ val.getLastName());
			}
		});
    	emailColunm.setCellValueFactory(new PropertyValueFactory<String, String>("email") );    	
    	tableView.setItems(items);

    	tableView.setRowFactory(new Callback<TableView<Contact>, TableRow<Contact>>() {
			public TableRow<Contact> call(TableView<Contact> param) {
				final TableRow<Contact> row = new TableRow<Contact>();
				row.hoverProperty().addListener(new ChangeListener<Boolean>() {
					public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
						final Contact cnt = row.getItem();
						if (row.isHover() && cnt != null) {
							hoverDetails.setText(cnt.toString());
						} else {
							hoverDetails.setText("");
						}
					}
				});
				return row;
			}
		});

    	editButton.disableProperty().bind(Bindings.createBooleanBinding(new Callable<Boolean>() {
			
			public Boolean call() throws Exception {
				return tableView.getSelectionModel().getSelectedItem() == null;
			}
		}, tableView.getSelectionModel().selectedIndexProperty()));
    	removeButton.disableProperty().bind(Bindings.createBooleanBinding(new Callable<Boolean>() {
			
			public Boolean call() throws Exception {
				return tableView.getSelectionModel().getSelectedItem() == null;
			}
		}, tableView.getSelectionModel().selectedIndexProperty()));
	}
	
	public void add(ActionEvent ev){
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("contactInfo.fxml"));
	        Parent root1 = (Parent) fxmlLoader.load();
	        Stage stage = new Stage();
	        stage.initModality(Modality.APPLICATION_MODAL);
	        stage.setTitle("Contact Information");
	        Scene scene = new Scene(root1);
	        scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
	        stage.setScene(scene);  
	        stage.show();
	        stage.setOnHidden(new EventHandler<WindowEvent>() {
				
				public void handle(WindowEvent event) {
					List<Contact> list = dataSource.selectAll();
					tableView.setItems(FXCollections.observableArrayList(list));
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void edit(ActionEvent ev){
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("contactInfo.fxml"));
	        Parent root1 = (Parent) fxmlLoader.load();
	        
	        ContactInfoController controller = fxmlLoader.getController();
	        controller.setContact(tableView.getSelectionModel().getSelectedItem());
	        
	        Stage stage = new Stage();
	        stage.initModality(Modality.APPLICATION_MODAL);
	        stage.setTitle("Contact Information");
	        Scene scene = new Scene(root1);
	        scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
	        stage.setScene(scene);  
	        stage.show();
	        stage.setOnHidden(new EventHandler<WindowEvent>() {
				
				public void handle(WindowEvent event) {
					List<Contact> list = dataSource.selectAll();
					tableView.setItems(FXCollections.observableArrayList(list));
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void delete(ActionEvent ev){
		if(dataSource.delete(tableView.getSelectionModel().getSelectedItem())){
			List<Contact> list = dataSource.selectAll();
			tableView.setItems(FXCollections.observableArrayList(list));
		}
	}
	
	public void importContacts(ActionEvent ev){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load File");
		fileChooser.setInitialFileName("ContactsReport.csv");
		File loadFile = fileChooser.showOpenDialog(null);
		if (loadFile != null) {
			try {
				FileReader reader = new FileReader(loadFile);
				BufferedReader bufferedReader = new BufferedReader(reader);
				
				String line;
				int index = 0;
				while ((line = bufferedReader.readLine()) != null) {
					if(index == 0){
						index++;
						continue;
					}
					String[] props = line.split(";");
					Contact obj = new Contact();
					obj.setId(Integer.parseInt(props[0]));
					obj.setFirstName(props[1]);
					obj.setLastName(props[2]);
					obj.setEmail(props[3]);
					obj.setPhone(props[4]);
					obj.setAddress(props[5]);
					obj.setCompanyName(props[6]);
					obj.setCompanyPhone(props[7]);
					obj.setNotes(props[8]);
					if(!dataSource.get(obj.getId()).isEmpty()){
						dataSource.edit(obj);
					}else{
						dataSource.save(obj);
					}
	          }            
	          reader.close();
	          
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
		List<Contact> list = dataSource.selectAll();
		tableView.setItems(FXCollections.observableArrayList(list));
	}
	
	public void exportContacts(ActionEvent ev){
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save File");
		fileChooser.setInitialFileName("ContactsReport.csv");
		File savedFile = fileChooser.showSaveDialog(null);
		if (savedFile != null) {
			try {
				FileWriter writer = new FileWriter(savedFile, false);
				BufferedWriter bufferedWriter = new BufferedWriter(writer);
				List<Contact> list = dataSource.selectAll();
				bufferedWriter.write("id;firstName;lastName;email;phone;address;companyName;companyPhone;notes; \n");
				for (Contact contact : list) {
					bufferedWriter.write(contact.getId()+";"+contact.getFirstName()+";"+contact.getLastName()+";"+contact.getEmail()+";"+contact.getPhone()+";"+contact.getAddress()+";"+contact.getCompanyName()+";"+contact.getCompanyPhone()+";"+contact.getNotes()+"; \n");
				}
				
				bufferedWriter.close();
				writer.close();
				
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}
}
