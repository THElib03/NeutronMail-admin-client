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

public class GroupEditController{
    //@FXML objects section:
    @FXML private TextField nameText;
    @FXML public TextField ownerText;
    @FXML private DatePicker datePick;
    @FXML private PasswordField pass1Text;
    @FXML private PasswordField pass2Text;
    @FXML private Text passHint;
    @FXML private Button cancelBut;
    @FXML private Button editBut;
    
    //Other variables:
    private Stage window;
    private DBActions db;
    private Encrypt enc;
    public int activeId = 0; 

    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize(){
        db = new DBActions();
        enc = new Encrypt(2);
        //DONE: implement edition, db interaction, check if the context menu works, gn.
        //TODO: if you fail again, add a button for searching and changing the owner.
        passHint.setText("Deje los campos en blanco si no\ndesea cambiar la contraseña.");;
    }

    //GUI actions:
      //nameText, pass1Text & pass2Text
    @FXML private void editEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            editGrp();
        }
    }
      //cancelBut
    @FXML private void cancelClick(){
        TemplateController.loadGroupTable();
    }
    @FXML private void cancelEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            TemplateController.loadGroupTable();
        }
    }
    
    //Methods:
    @FXML private void editGrp(){
        if(nameText.getText().strip().equals("") || ownerText.getText().strip().equals("")){
            GUI.launchMessage(5, "Advertencia", "Uno de los campos requeridos está vacío.");
            return;
        }
        else if(pass1Text.getText().strip().equals("") && pass2Text.getText().strip().equals("")){
            if(db.editGrp(new Group(activeId, 0, nameText.getText().strip(), ownerText.getText().strip(), null), pass2Text.getText().strip().getBytes())){
                cleanText();
            }
            return;
        }
        else if(pass2Text.getText().strip().equals("")){
            GUI.launchMessage(5, "Advertencia", "La nueva contraseña debe contener al menos un (1) carácter.");
            return;
        }
        else if(db.checkGrp(activeId, pass1Text.getText().strip())){
            GUI.launchMessage(5, "Advertencia", "La contraseña actual no coincide.");
            return;
        }
        else{
            if(pass2Text.getText().strip().equals("")){
                db.editGrp(new Group(activeId, 0, nameText.getText().strip(), null, null), null);
            }
            else{
                byte[] salt = enc.getSalt();
                byte[] hash = enc.saltedHash(pass1Text.getText().strip().toCharArray(), salt);
                byte[] key = new byte[salt.length + hash.length];
                System.arraycopy(salt, 0, key, 0, salt.length);
                System.arraycopy(hash, 0, key, salt.length, hash.length);

                db.editGrp(new Group(activeId, 0, nameText.getText().strip(), null, null), key);
                //TODO: debug
                //TODO: I  FORGOT WHERE THE FUCK TO PUT THE MESSAGES, CHECK AND ADD ONE TO THIS AND MAYBE GRP DELETE.
            }
        }
    }

    public void extSetup(Group grp){
        activeId = grp.getId();
        nameText.setText(grp.getName());
        ownerText.setText(grp.getOwner());
        datePick.setValue(LocalDate.parse(grp.getCreationDate()));
    }

    public void cleanText(){
        nameText.setText("");
        ownerText.setText("");
        datePick.setValue(null);
        pass1Text.setText("");
        pass2Text.setText("");
    }

    public void setStage(Stage w){
        this.window = w;
    }
}
