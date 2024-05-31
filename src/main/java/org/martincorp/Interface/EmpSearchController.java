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
        if(employees.size() == 0){
            employees.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
        }
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

        switch (searchCombo.getValue()){
            case "Nombre":
                if(!searchText.getText().strip().equals("")){
                    emps = db.searchEmpTerm(1, searchText.getText().strip());
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para el nombre del empleado está vacío.");
                }
                break;
            case "Apellidos":
                if(!searchText.getText().strip().equals("")){
                    emps = db.searchEmpTerm(2, searchText.getText().strip());
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para los apellidos del empleado está vacío.");
                }
                break;
            case "Usuario":
                if(!searchText.getText().strip().equals("")){
                    emps = db.searchEmpTerm(3, searchText.getText().strip());
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para el nombre de usuario está vacío.");
                }
                break;
            case "Correo electrónico":
                if(!searchText.getText().strip().equals("")){
                    emps = db.searchEmpTerm(4, searchText.getText().strip());
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para el correo electrónico está vacío.");
                }
                break;
            case "Inicio del contrato":
                emps = searchDate();
                break;
            case "Fin del contrato":
                emps = searchDate();
                break;

            default:
                GUI.launchMessage(2, "Error interno", "No se ha podido establecer la categoría de búsqueda.");
                break;
        }

        importTable(emps);
    }

    private List<Employee> searchDate(){
        //TODO: add 10 character limit to searchDateText (with it's own message?) here and in GroupSearchController. (There's a stackoverflow link in bookmarks)
        List<Employee> emps = new ArrayList<Employee>();
        int i = searchCombo.getValue() == "Fin del contrato" ? 5 : 0;

        switch (searchDateCombo.getValue()) {
            case "Día-Mes-Año":
                if(searchDatePick.getValue() != null){
                    if(searchDatePick.getValue().getYear() < 1 || searchDatePick.getValue().getYear() > 9999){
                        GUI.launchMessage(5, "Advertencia", "Solo se permiten valores entre los años 1 y 9999 después de Cristo.");
                    }
                    else{
                        emps = db.searchEmpDateFull(i + 1, searchDatePick.getValue());
                    }
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para la fecha está vacío.");
                }
                break;
            case "Año":
                if(!searchDateText.getText().strip().equals("")){
                    try{
                        if(Integer.valueOf(searchDateText.getText().strip()) < 1 || Integer.valueOf(searchDateText.getText().strip()) > 9999){
                            GUI.launchMessage(5, "Advertencia", "Introduzca un valor entre 1 y 9999.");
                        }
                        else{
                            emps = db.searchEmpDateSingle(i + 2, Integer.valueOf(searchDateText.getText().strip()));
                        }
                    }
                    catch(NumberFormatException nfe){
                        GUI.launchMessage(5, "Advertencia", "Solo se permiten valores numéricos en este campo.");
                    }
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para la fecha está vacío.");
                }
                break;
            case "Mes":
                if(!searchDateText.getText().strip().equals("")){
                    try{
                        if(Integer.valueOf(searchDateText.getText().strip()) < 1 || Integer.valueOf(searchDateText.getText().strip()) > 12){
                            GUI.launchMessage(5, "Advertencia", "Introduzca un valor entre 1 y 12.");
                        }
                        else{
                            emps = db.searchEmpDateSingle(i + 3, Integer.valueOf(searchDateText.getText().strip()));
                        }
                    }
                    catch(NumberFormatException nfe){
                        GUI.launchMessage(5, "Advertencia", "Solo se permiten valores numéricos en este campo.");
                    }
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para la fecha está vacío.");
                }
                break;
            case "Día-Mes":
                if(searchDatePick.getValue() != null){
                    emps = db.searchEmpDateDouble(i + 4, searchDatePick.getValue().getDayOfMonth(), searchDatePick.getValue().getMonthValue());
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para la fecha está vacío.");
                }
                break;
            case "Mes-Año":
                if(searchDatePick.getValue() != null){
                    if(searchDatePick.getValue().getYear() < 1 || searchDatePick.getValue().getYear() > 9999){
                        GUI.launchMessage(5, "Advertencia", "Solo se permiten valores entre los años 1 y 9999 después de Cristo.");
                    }
                    else{
                        emps = db.searchEmpDateDouble(i + 5, searchDatePick.getValue().getMonthValue(), searchDatePick.getValue().getYear());
                    }
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para la fecha está vacío.");
                }
                break;

            default:
                emps.add(new Employee(0, Online.OFFLINE, "No se ha encontrado", "", "", "ningún empleado", ""));
                GUI.launchMessage(2, "Error interno", "No se ha podido establecer la categoría de búsqueda.");
                break;
        }

        return emps;
    }

    public void setStage(Stage w){
        window = w;
    }

    public void setParent(int p){
        parent = p;
    }
}
