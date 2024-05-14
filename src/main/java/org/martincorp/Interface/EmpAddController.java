package org.martincorp.Interface;

import java.time.LocalDate;

import org.martincorp.Codec.Encrypt;
import org.martincorp.Database.DBActions;

import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class EmpAddController {
    //@FXML objects section:
    @FXML private TextField fnameText;
    @FXML private TextField lnameText;
    @FXML private TextField aliasText;
    @FXML private TextField emailText;
    @FXML private RadioButton tempRadio;
    @FXML private RadioButton indefRadio;
    @FXML private DatePicker startDatePick;
    @FXML private DatePicker endDatePick;
    @FXML private TextField pass1Text;
    @FXML private TextField pass2Text;

    @FXML private Button cancelBut;
    @FXML private Button saveBut;

    //Other variables section:
    private Stage window;
    private Scene scene;
    private DBActions db;
    private Encrypt enc;

    private EmpDeleteController dCont;
    private EmpEditController eCont;

    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize(){
        db = new DBActions();
        enc = new Encrypt(4);

        ToggleGroup dateGroup = new ToggleGroup();
        tempRadio.setToggleGroup(dateGroup);
        indefRadio.setToggleGroup(dateGroup);
        indefRadio.setSelected(true);
    }

    //GUI actions:
      //indefRadio
    @FXML private void indefClick(){
        tempRadio.setVisible(true);
        endDatePick.setVisible(false);
    }
      //tempRadio
    @FXML private void tempClick(){
        tempRadio.setVisible(false);
        endDatePick.setVisible(true);
    }
      //cancelBut
    @FXML private void cancelClick(){
        TemplateController.loadMain();
    }
    @FXML private void cancelEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            TemplateController.loadMain();
        }
    }
      //SaveBut
    @FXML private void saveEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            saveEmp();
        }
    }

    //Misc. methods:
    @FXML private void saveEmp(){
        if(!pass1Text.getText().equals(pass2Text.getText())){
            GUI.launchMessage(5, "Advertencia", "Las contraseñas no coinciden.");
            return;
        }
        else if(pass1Text.getText().equals("") || pass2Text.getText().equals("")){
            GUI.launchMessage(5, "Advertencia", "La contraseña no puede estar vacía.");
            return;
        }
        else if(fnameText.getText().equals("") || lnameText.getText().equals("") || aliasText.getText().equals("") || emailText.getText().equals("") || startDatePick.getValue() == null){
            GUI.launchMessage(5, "Advertencia", "Uno de los campos requeridos está vacío");
            return;
        }
            
        LocalDate endDate = null;
        if(tempRadio.isSelected()){
            endDate = endDatePick.getValue();
            System.out.println(endDate.toString());
        }

        byte[] salt = enc.getSalt();
        byte[] hash = enc.saltedHash(pass1Text.getText().toCharArray(), salt);
        byte[] key = new byte[salt.length + hash.length];
        System.arraycopy(salt, 0, key, 0, salt.length);
        System.arraycopy(hash, 0, key, salt.length, hash.length);

        //TODO: trim al strings with .strip()
        if(db.newEmp(fnameText.getText(), lnameText.getText(), startDatePick.getValue(), endDate, key, aliasText.getText(), emailText.getText())){
            if(db.newEmpCert(db.getLastEmp() - 1) && db.newEmpAct(db.getLastEmp() - 1)){
                GUI.launchMessage(3, "Operación completada", "Se ha añadido con éxito un nuevo empleado");
            }
            else{
                db.dropEmp(db.getLastEmp());
            }
        }
    }

    public void setStage(Stage w){
        this.window = w;
        scene = window.getScene();
    }
}
