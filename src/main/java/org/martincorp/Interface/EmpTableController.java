package org.martincorp.Interface;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.martincorp.Database.DBActions;
import org.martincorp.Model.Employee;
import org.martincorp.Model.Online;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class EmpTableController {
    //@FXML objects section:
    @FXML private MenuItem contextAdd;
    @FXML private MenuItem contextEdit;
    @FXML private MenuItem contextDelete;

    @FXML private TableView<Employee> table;
    @FXML private TableColumn<Employee, Integer> idCol;
    @FXML private TableColumn<Employee, Online>  onlineCol;
    @FXML private TableColumn<Employee, String> nameCol;
    @FXML private TableColumn<Employee, String> aliasCol;
    @FXML private TableColumn<Employee, String> mailCol;
    @FXML private TableColumn<Employee, String> startDateCol;
    @FXML private TableColumn<Employee, String> endDateCol;
    
    //Other variables:
    private Stage window;
    private DBActions db;
    private int parent = 0;

    private EmpEditController eEmpCont;
    private EmpAddController dEmpCont;

    //Equivalent to main method when the controller is started:
    @FXML public void initialize(){
        db = new DBActions();
    }

    //GUI actions:
      //contextAdd
    @FXML private void contextAdd(){
        TemplateController.loadEmpAdd();
    }
      //contextEdit
    @FXML private void contextEdit(){
        ObservableList<TablePosition> selectionList = table.getSelectionModel().getSelectedCells();
            
        if(selectionList.size() > 0){
            TemplateController.loadEmpEdit();
            TablePosition selection = selectionList.get(0);
            int selectionRow = selection.getRow();
            Employee selected = table.getItems().get(selectionRow);

            //eEmpCont.extSetup(selected.getID(), selected.getName(), selected.getPass());
        }
    }
      //contextDelete
    @FXML private void contextDelete(){
        ObservableList<TablePosition> selectionList = table.getSelectionModel().getSelectedCells();
            
        if(selectionList.size() > 0){
            TemplateController.loadEmpDel();
            TablePosition selection = selectionList.get(0);
            int selectionRow = selection.getRow();
            Employee selected = table.getItems().get(selectionRow);

            //dEmpCont.extSetup(selected.getID(), selected.getName(), selected.getPass());
        }
    }

    //Misc. Methods:
    public void loadEmployees(){
        List<Employee> employees = db.getEmployees();

        if(employees.size() == 0){
            employees.add(new Employee(0, Online.OFFLINE, "No hay empleados", "", "", "registrados", ""));
        }

        Platform.runLater( () -> populateTable(employees));
    }

    public void populateTable(List<Employee> employees){
        table.setItems(FXCollections.observableArrayList(employees));

        idCol.setCellValueFactory(new PropertyValueFactory<>(employees.get(0).IDProperty().getName()));
        nameCol.setCellValueFactory(new PropertyValueFactory<>(employees.get(0).nameProperty().getName()));
        aliasCol.setCellValueFactory(new PropertyValueFactory<>(employees.get(0).aliasProperty().getName()));
        mailCol.setCellValueFactory(new PropertyValueFactory<>(employees.get(0).mailProperty().getName()));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>(employees.get(0).startDateProperty().getName()));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>(employees.get(0).endDateProperty().getName()));

        onlineCol.setCellValueFactory(new PropertyValueFactory<>(employees.get(0).onlineProperty().getName()));
        onlineCol.setCellFactory((col) -> new OnlineCell());
    }

    @FXML private void updateParent(){
        try{
            switch (parent) {
                case 0:
                    return;
                case 1:
                    ObservableList<TablePosition> selectionList = table.getSelectionModel().getSelectedCells();
                
                    if(selectionList.size() > 0){
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/empDelete.fxml"));
                        Parent root = loader.load();

                        EmpDeleteController dEmpCont = loader.getController();
                        dEmpCont.setStage(window);

                        Platform.runLater( () -> TemplateController.cleanLeft());
                        Platform.runLater( () -> TemplateController.setLeft(root));

                        TablePosition selection = selectionList.get(0);
                        int selectionRow = selection.getRow();
                        dEmpCont.extSetup(table.getItems().get(selectionRow));
                    }
                    break;
                case 2:
                    contextEdit();
                    break;

                default:
                    GUI.launchMessage(2, "Error de interfaz", "La tabla de búsqueda que ha clicado no se encuentra activada.t");
                    break;
            }
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.\n\n" + ioe.getMessage());
        }
    }
    
    public void setStage(Stage w){
        this.window = w;
    }

    public void setParent(int p){
        this.parent = p;
    }
}
