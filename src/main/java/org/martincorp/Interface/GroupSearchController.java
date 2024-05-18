package org.martincorp.Interface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.martincorp.Database.DBActions;
import org.martincorp.Model.Employee;
import org.martincorp.Model.Online;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class GroupSearchController {
    //@FXML objects section:
    @FXML BorderPane searchRoot;

    //Other Variables:
    private DBActions db;
    private static Stage window;
    private static int parent = 0;

    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize(){
        db = new DBActions();

        //Load EmpTable inside this view:
        List<Employee> emptyEmp = new ArrayList<Employee>();
        emptyEmp.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
        importTable(emptyEmp);
    }
    
        //GUI actions:
    

    //Methods:
    private void importTable(List<Employee> employees){
        //DONE: should i load this in a method, and how do i load the search results? Maybe divide the fetch and population to allow for external population without duplicate code?
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/empTable.fxml"));
            Parent root = loader.load();

            EmpTableController eTableCont = loader.getController();
            eTableCont.setStage(window);
            eTableCont.setParent(parent);
            eTableCont.populateTable(employees);

            Platform.runLater( () -> searchRoot.setCenter(null));
            Platform.runLater( () -> searchRoot.setCenter(root));
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.");

        }
    }

    public void setStage(Stage w){
        this.window = w;
    }
}
