package org.martincorp.Interface;

import java.time.LocalDate;
import java.util.List;

import org.martincorp.Database.DBActions;
import org.martincorp.Model.Group;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class GroupTableController {
    //@FXML objects section:
    @FXML private MenuItem contextAdd;
    @FXML private MenuItem contextDelete;

    @FXML private TableView<Group> table;
    @FXML private TableColumn<Group, Integer> idCol;
    @FXML private TableColumn<Group, String>  nameCol;
    @FXML private TableColumn<Group, String> ownerCol;
    @FXML private TableColumn<Group, LocalDate> creationCol;

    //Other variables:
    private Stage window;
    private DBActions db;
    private int parent = 0;
    private GroupDeleteController dGroupCont;
    
    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize(){
        db = new DBActions();
    }

    //GUI actions:
      //contextAdd
    @FXML private void contextAdd(){
        TemplateController.loadGroupAdd();
    }
      //contextEdit
    @FXML private void contextEdit(){
        ObservableList<TablePosition> selectionList = table.getSelectionModel().getSelectedCells();
            
        if(selectionList.size() > 0){
            TablePosition selection = selectionList.get(0);
            int selectionRow = selection.getRow();
            Group selected = table.getItems().get(selectionRow);

            TemplateController.loadGroupEdit(selected);
        }
    }
      //contextDelete
    @FXML private void contextDelete(){
        ObservableList<TablePosition> selectionList = table.getSelectionModel().getSelectedCells();
            
        if(selectionList.size() > 0){
            TablePosition selection = selectionList.get(0);
            int selectionRow = selection.getRow();
            Group selected = table.getItems().get(selectionRow);

            TemplateController.loadGroupDel(selected);
        }
        else{
            
        }
    }

    //Methods:
    public void loadGroups(){
        List<Group> groups = db.getGroups();

        if(groups.size() == 0){
            groups.add(new Group(0, 0, "No se ha creado", "ningún grupo", LocalDate.now().toString()));
        }

        Platform.runLater( () -> populateTable(groups));
    }

    public void populateTable(List<Group> groups){
        table.setItems(FXCollections.observableArrayList(groups));

        idCol.setCellValueFactory(new PropertyValueFactory<>(groups.get(0).IdProperty().getName()));
        nameCol.setCellValueFactory(new PropertyValueFactory<>(groups.get(0).nameProperty().getName()));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>(groups.get(0).ownerProperty().getName()));
        creationCol.setCellValueFactory(new PropertyValueFactory<>(groups.get(0).creationDateProperty().getName()));
    }

    public void setStage(Stage w){
        this.window = w;
    }

    public void setParent(int p){
        this.parent = p;
    }
}
