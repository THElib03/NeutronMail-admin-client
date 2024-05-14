package org.martincorp.Interface;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.martincorp.Database.DBActions;
import org.martincorp.Model.Group;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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

    //Other variables:
    private Stage window;
    private DBActions db;
    private GroupDeleteController dGroupCont;
    
    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize(){
        db = new DBActions();

        Platform.runLater( () -> loadTable());
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
            TemplateController.loadGroupDel();
            TablePosition selection = selectionList.get(0);
            int selectionRow = selection.getRow();
            Group selected = table.getItems().get(selectionRow);

            //eGroupCont.extSetup(selected.getID(), selected.getName(), selected.getPass());
        }
    }
      //contextDelete
    @FXML private void contextDelete(){
        ObservableList<TablePosition> selectionList = table.getSelectionModel().getSelectedCells();
            
        if(selectionList.size() > 0){
            TemplateController.loadGroupDel();
            TablePosition selection = selectionList.get(0);
            int selectionRow = selection.getRow();
            Group selected = table.getItems().get(selectionRow);

            //dGroupCont.extSetup(selected.getID(), selected.getName(), selected.getPass());
        }
        else{
            System.out.println("nada");
        }
    }

    //Methods:
    private void loadTable(){
        List<Group> groups = db.getGroups();

        if(groups.size() == 0){
            groups.add(new Group(0, "No se ha creado", "ningún grupo"));
        }

        table.setItems(FXCollections.observableArrayList(groups));

        idCol.setCellValueFactory(new PropertyValueFactory<>(groups.get(0).IdProperty().getName()));
        nameCol.setCellValueFactory(new PropertyValueFactory<>(groups.get(0).nameProperty().getName()));
        ownerCol.setCellValueFactory(new PropertyValueFactory<>(groups.get(0).ownerProperty().getName()));
    }

    public void setStage(Stage w){
        this.window = w;
    }
}
