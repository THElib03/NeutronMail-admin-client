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
import org.martincorp.Model.Group;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class GroupDeleteController{
    //@FXML objects section:
    @FXML private TextField nameText;
    @FXML private TextField ownerText;
    @FXML private TextField dateText;
    @FXML private TextField passText;
    @FXML private Button cancelBut;
    @FXML private Button deleteBut;

    //Other variables:
    private Stage window;
    private int activeId; 
    private DBActions db;
    
    //Equivalent to main method when the controller is started:
    @FXML public void initialize(){
        db = new DBActions();
        activeId = 0;
    }

    //GUI actions:
      //cancelBut
    @FXML private void cancelClick(){
        TemplateController.loadMain();
    }
    @FXML private void cancelEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            TemplateController.loadMain();
        }
    }
      //deleteBut
    @FXML private void deleteEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            delete();
        }
    }

    //Methods:
    @FXML private void delete(){
        if(activeId != 0){
            if(db.dropGrp(activeId)){
                cleanText();
            }
        }
    }

    public void extSetup(Group grp){
        activeId = grp.getId();
        nameText.setText(grp.getName());
        ownerText.setText(grp.getOwner());
        dateText.setText(grp.getCreationDate());
    }

    private void cleanText(){
        nameText.setText("");
        ownerText.setText("");
        dateText.setText("");
        passText.setText("");
    }

    public void setStage(Stage w){
        this.window = w;
    }
}
