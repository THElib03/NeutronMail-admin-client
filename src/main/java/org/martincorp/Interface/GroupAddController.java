/**
 *  NeutronMail client for admintration purposes.
 *  Copyright (C) 2024 by Martín Marín.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.martincorp.Interface;

import java.time.LocalDate;

import org.martincorp.Codec.Encrypt;
import org.martincorp.Database.DBActions;
import org.martincorp.Model.Group;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GroupAddController{
    //@FXML objects section:
    @FXML private TextField nameText;
    @FXML public TextField ownerText;
    @FXML private Text searchHint;
    @FXML private DatePicker datePick;
    @FXML private PasswordField pass1Text;
    @FXML private PasswordField pass2Text;
    @FXML private Button cancelBut;
    @FXML private Button deleteBut;
    
    //Other variables: 
    private Stage window;
    private DBActions db;
    private Encrypt enc;
    public int activeOwner = 0;
    private EmpSearchController search;

    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize(){
        db = new DBActions();
        enc = new Encrypt(2);

        datePick.setValue(LocalDate.now());
        searchHint.setText("Busque y seleccione un empleado\ndel menú de la derecha");
    }
    
    //GUI actions:
      //cancelBut
    @FXML private void cancelClick(){
        TemplateController.loadGroupTable();
    }
    @FXML private void cancelEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            TemplateController.loadGroupTable();
        }
    }
      //SaveBut
    @FXML private void saveEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            saveGrp();
        }
    }

    //Misc. methods:
    @FXML private void saveGrp(){
        if(pass1Text.getText().strip().equals("") || pass2Text.getText().strip().equals("")){
            GUI.launchMessage(5, "Advertencia", "Las contraseñas no coinciden.");
            return;
        }
        else if(!pass1Text.getText().equals(pass2Text.getText())){
            GUI.launchMessage(5, "Advertencia", "La contraseña no puede estar vacía.");
            return;
        }
        else if(nameText.getText().strip().equals("") || ownerText.getText().strip().equals("")){
            GUI.launchMessage(5, "Advertencia", "Uno de los campos requeridos está vacío.");
            return;
        }

        byte[] salt = enc.getSalt();
        byte[] hash = enc.saltedHash(pass1Text.getText().strip().toCharArray(), salt);
        byte[] key = new byte[salt.length + hash.length];
        System.arraycopy(salt, 0, key, 0, salt.length);
        System.arraycopy(hash, 0, key, salt.length, hash.length);

        if(db.newGroup(new Group(0, activeOwner, nameText.getText().strip(), "", LocalDate.now().toString()), key)){
            if(db.newGrpCert(activeOwner)){
                cleanText();
            }
            else{
                db.dropGrp(db.getLastGroup());
            }
        }
    }

    public void cleanText(){
        nameText.setText("");
        ownerText.setText("");
        datePick.setValue(LocalDate.now());
        pass1Text.setText("");
        pass2Text.setText("");
    }

    public void setStage(Stage w){
        this.window = w;
    }
}
