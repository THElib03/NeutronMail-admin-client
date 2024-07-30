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

import org.martincorp.Database.DBActions;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

//DONE: When password is incorrect, it doesn't even mention it or launch an exception.

public class LoginController {
    //@FXML objects section:
    @FXML private TextField nameText;
    @FXML private PasswordField passText;
    @FXML private Button cancelBut;
    @FXML private Button approveBut;
    
    //Other variables section:
    private static Stage window;
    private static DBActions db;

    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize() {
        db = new DBActions();
    }

    //GUI actions:
      //nameText
    @FXML private void sendEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            sendPassword();
        }
    }
      //closeBut
    @FXML private void closeClick(){
        window.close();
    }
    @FXML private void closeEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            window.close();
        }
    }

    //Misc. methods:
    public static void setStage(Stage w){
        window = w;
    }

    @FXML private void sendPassword(){
        if(db.checkUser(nameText.getText(), passText.getText())){
            window.close();
            TemplateController.launchStart(window);
        }
    }
}
