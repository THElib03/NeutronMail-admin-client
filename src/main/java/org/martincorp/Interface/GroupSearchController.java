package org.martincorp.Interface;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.martincorp.Database.DBActions;
import org.martincorp.Model.Employee;
import org.martincorp.Model.Group;
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

public class GroupSearchController {
    //@FXML objects section:
    @FXML private BorderPane searchRoot;
    @FXML private ChoiceBox<String> searchCombo;
    @FXML private TextField searchText;
    @FXML private Button searchBut;
    @FXML private ChoiceBox<String> searchDateCombo;
    @FXML private DatePicker searchDatePick;
    @FXML private TextField searchDateText;

    //Other Variables:
    private DBActions db;
    private static Stage window;
    private static int parent = 0;

    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize(){
        db = new DBActions();

        List<Group> emptyGrp = new ArrayList<Group>();
        emptyGrp.add(new Group(0, 0, "No se ha encontrado", "ningún grupo", LocalDate.now().toString()));
        importTable(emptyGrp);

        searchDateCombo.getItems().addAll("Día-Mes-Año", "Año", "Mes", "Día-Mes", "Mes-Año");
        searchDateCombo.setValue(searchDateCombo.getItems().get(0));

        searchCombo.getItems().addAll("Nombre", "Dueño (Nombre Empleado)", "Fecha de Creación");
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
        if(searchCombo.getValue() == "Fecha de Creación"){
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

    private void importTable(List<Group> groups){
        if(groups.size() == 0){
            groups.add(new Group(0, 0, "No se ha encontrado", "ningún grupo", LocalDate.now().toString()));
        }

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/grpTable.fxml"));
            Parent root = loader.load();

            GroupTableController gTableCont = loader.getController();
            gTableCont.setStage(window);
            gTableCont.setParent(parent);
            gTableCont.populateTable(groups);

            Platform.runLater( () -> searchRoot.setCenter(null));
            Platform.runLater( () -> searchRoot.setCenter(root));
        }
        catch(IOException ioe){
            ioe.printStackTrace();
            GUI.launchMessage(2, "Error de interfaz", "No se ha modido cargar la vista seleccionada del programa.");

        }
    }

    @FXML private void search(){
        List<Group> grps = new ArrayList<Group>();

        switch (searchCombo.getValue()){
            case "Nombre":
                if(!searchText.getText().strip().equals("")){
                    grps = db.searchGrpTerm(1, searchText.getText().strip());
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para el nombre de grupo está vacío.");
                }
                break;
            case "Dueño (Nombre Empleado)":
                if(!searchText.getText().strip().equals("")){
                    grps = db.searchGrpTerm(2, searchText.getText().strip());
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para el nombre del dueño está vacío.");
                }
                break;
            case "Fecha de Creación":
                //TODO: test the last two options. Move the code to EmpSearch then test it too.
                grps = searchDate();
                break;

            default:
                GUI.launchMessage(2, "Error interno", "No se ha podido establecer la categoría de búsqueda.");
                break;
        }

        importTable(grps);
    }

    private List<Group> searchDate(){
        List<Group> grps = new ArrayList<Group>();

        switch(searchDateCombo.getValue()){
            case "Día-Mes-Año":
                if(searchDatePick.getValue() != null){
                    if(searchDatePick.getValue().getYear() < 1 || searchDatePick.getValue().getYear() > 9999){
                        GUI.launchMessage(5, "Advertencia", "Solo se permiten valores entre los años 1 y 9999 después de Cristo.");
                    }
                    else{
                        grps = db.searchGrpDateFull(searchDatePick.getValue());
                    }
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para la fecha de creación está vacío.");
                }
                break;
            case "Año":
                if(!searchDateText.getText().strip().equals("")){
                    try{
                        if(!(Integer.valueOf(searchDateText.getText().strip()) < 1) && !(Integer.valueOf(searchDateText.getText().strip()) > 9999)){
                            grps = db.searchGrpDateSingle(2, Integer.valueOf(searchDateText.getText().strip()));
                        }
                        else{
                            GUI.launchMessage(5, "Advertencia", "Introduzca un valor entre 1 y 9999.");
                        }
                    }
                    catch(NumberFormatException nfe){
                        GUI.launchMessage(5, "Advertencia", "Solo se permiten valores numéricos en este campo.");
                    }
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para la fecha de creación está vacío.");
                }
                break;
            case "Mes":
                if(!searchDateText.getText().strip().equals("")){
                    try{
                        if(Integer.valueOf(searchDateText.getText().strip()) < 1 || Integer.valueOf(searchDateText.getText().strip()) > 12){
                            GUI.launchMessage(5, "Advertencia", "Introduzca un valor entre 1 y 12.");
                        }
                        else{
                            grps = db.searchGrpDateSingle(3, Integer.valueOf(searchDateText.getText().strip()));
                        }
                    }
                    catch(NumberFormatException nfe){
                        GUI.launchMessage(5, "Advertencia", "Solo se permiten valores numéricos en este campo.");
                    }
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para la fecha de creación está vacío.");
                }
                break;
            case "Día-Mes":
                if(searchDatePick.getValue() != null){
                    grps = db.searchGrpDateDouble(4, searchDatePick.getValue().getDayOfMonth(), searchDatePick.getValue().getMonthValue());
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para la fecha de creación está vacío.");
                }
                break;
            case "Mes-Año":
                if(searchDatePick.getValue() != null){
                    if(searchDatePick.getValue().getYear() < 1 || searchDatePick.getValue().getYear() > 9999){
                        GUI.launchMessage(5, "Advertencia", "Solo se permiten valores entre los años 1 y 9999 después de Cristo.");
                    }
                    else{
                        grps = db.searchGrpDateDouble(5, searchDatePick.getValue().getMonthValue(), searchDatePick.getValue().getYear());
                    }
                }
                else{
                    GUI.launchMessage(5, "Advertencia", "El campo requerido para la fecha de creación está vacío.");
                }
                break;
                
            default:
                grps.add(new Group(0, 0, "No se ha encontrado", "ningún grupo", LocalDate.now().toString()));
                GUI.launchMessage(2, "Error interno", "No se ha podido establecer la categoría de búsqueda.");
                break;
        }

        return grps;
    }    

    public void setStage(Stage w){
        this.window = w;
    }

    public void setParent(int p){
        parent = p;
    }
}
