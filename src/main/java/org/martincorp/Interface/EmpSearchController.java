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
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class EmpSearchController {
    //@FXML objects section:
    @FXML BorderPane searchRoot;
    @FXML ChoiceBox<String> searchCombo;
    @FXML TextField searchText;
    @FXML ChoiceBox<String> searchDateCombo;
    @FXML DatePicker searchDatePick;
    @FXML TextField searchDateText;
    @FXML Button searchBut;

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
        
        //Set searchDateCombo options:
        searchDateCombo.getItems().addAll("Día-Mes-Año", "Año", "Mes", "Día-Mes", "Mes-Año");
        searchDateCombo.setValue(searchDateCombo.getItems().get(0));

        //Set searchCombo options:
        searchCombo.getItems().addAll("Nombre", "Apellidos", "Usuario", "Correo electrónico", "Inicio del contrato", "Fin del contrato");
        searchCombo.setValue(searchCombo.getItems().get(0));
    }

    //GUI actions:
      //searchText
    @FXML private void searchTextEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            search();
        }
    }
      //searchDateText
    @FXML private void searchDateTextEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            search();
        }
    }
      //searchBut
    @FXML private void searchButEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            search();
        }
    }

    //Methods:
    @FXML private void searchComboOnChange(){
        if(searchCombo.getValue() == "Inicio del contrato" || searchCombo.getValue() == "Fin del contrato"){
            searchText.setVisible(false);
            searchDateCombo.setVisible(true);
            searchDatePick.setVisible(true);
            searchDateCombo.setValue(searchDateCombo.getItems().get(0));
        }
        else{
            searchText.setVisible(true);
            searchDateCombo.setVisible(false);
            searchDatePick.setVisible(false);
            searchDateText.setVisible(false);
        }
    }

    @FXML private void dateComboOnChange(){
        if(searchDateCombo.getValue() == "Año" || searchDateCombo.getValue() == "Mes"){
            searchDatePick.setVisible(false);
            searchDateText.setVisible(true);
        }
        else{
            searchDateText.setVisible(false);
            searchDatePick.setVisible(true);
        }
    }

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

    @FXML private void search(){
        List<Employee> emps = new ArrayList<Employee>();

        switch (searchCombo.getValue()) {
            case "Nombre":
                emps = db.searchEmpTerm(1, searchText.getText());
                break;
            case "Apellidos":
                emps = db.searchEmpTerm(2, searchText.getText());
                break;
            case "Usuario":
                emps = db.searchEmpTerm(3, searchText.getText());
                break;
            case "Correo electrónico":
                emps = db.searchEmpTerm(4, searchText.getText());
                break;
            case "Inicio del contrato":
                searchDate();
                break;
            case "Fin del contrato":
                searchDate();
                break;

            default:
                GUI.launchMessage(2, "Error interno", "");
                break;
        }

        importTable(emps);
    }

    private void searchDate() {
        List<Employee> emps = new ArrayList<Employee>();
        int i = searchCombo.getValue() == "Fin del contrato" ? 5 : 0;

        switch (searchDateCombo.getValue()) {
            case "Día-Mes-Año":
                emps = db.searchEmpDateFull(i + 1, searchDatePick.getValue());
                break;
            case "Año":
                if(Integer.valueOf(searchDateText.getText()) < 1000 || Integer.valueOf(searchDateText.getText()) > 9999){
                    emps = db.searchEmpDateSingle(i + 2, Integer.valueOf(searchDateText.getText()));
                }
                break;
            case "Mes":
                if(Integer.valueOf(searchDateText.getText()) < 1 || Integer.valueOf(searchDateText.getText()) > 12){
                    emps = db.searchEmpDateSingle(i + 2, Integer.valueOf(searchDateText.getText()));
                }
                break;
            case "Día-Mes":
                db.searchEmpDateDouble(i + 4, searchDatePick.getValue().getDayOfMonth(), searchDatePick.getValue().getMonthValue());
                break;
            case "Mes-Año":
                db.searchEmpDateDouble(i + 5, searchDatePick.getValue().getMonthValue(), searchDatePick.getValue().getYear());
                break;

            default:
                emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
                GUI.launchMessage(2, "Error interno", "");
                break;
        }

        importTable(emps);
    }

    public static void setStage(Stage w){
        window = w;
    }

    public static void setParent(int p){
        parent = p;
    }
}
