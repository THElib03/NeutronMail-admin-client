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

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class AboutController {
    //@FXML objects section:
    @FXML private Label javaVerText;
    @FXML private Label fxVerText;
    @FXML private Button closeBut;

    //Other variables section:
    private Stage window;

    //Equivalent to main method when the controller is started:
    @FXML
    public void initialize() {
        javaVerText.setText(javaVerText.getText() + Runtime.version().toString());
        fxVerText.setText(fxVerText.getText() + FXMLLoader.JAVAFX_VERSION);
    }

    //GUI actions:
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
    public void setStage(Stage w){
        this.window = w;
    }
}
