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
import org.martincorp.Model.Employee;
import org.martincorp.Model.Online;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class EmpEditController {
    //@FXML objects section:
    @FXML private TextField nameText;
    @FXML private TextField aliasText;
    @FXML private TextField emailText;
    @FXML private DatePicker sDatePick;
    @FXML private DatePicker eDatePick;
    @FXML private Label dateInfoText;
    @FXML private Button cancelBut;
    @FXML private Button saveBut;

    //Other variables:
    private Stage window;
    private int activeId; 
    private DBActions db;
    private Encrypt enc;

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
    @FXML private void editEnter(KeyEvent e){
        if(e.getCode() == KeyCode.ENTER){
            edit();
        }
    }

    //Equivalent to main method when the controller is started:
    @FXML public void initialize(){
        db = new DBActions();
    }

    //Methods:
    @FXML private void edit(){
        if(nameText.getText().strip() == "" || aliasText.getText().strip() == "" || emailText.getText().strip() == "" || sDatePick.getValue() == null){
            GUI.launchMessage(5, "Advertencia", "Uno de los campos requeridos está vacío");
        }
        else{
            boolean eDate = false;
            if(eDatePick.getValue() != null){
                eDate = true;
            }

            db.editEmp(activeId, new Employee(activeId, Online.OFFLINE, nameText.getText().strip(), sDatePick.getValue().toString(), eDate ? eDatePick.getValue().toString() : null, aliasText.getText().strip(), emailText.getText().strip()));
        }
    }

    public void extSetup(Employee emp){
        if(emp.getID() != 0){
            activeId = emp.getID();
            nameText.setText(emp.getName());
            aliasText.setText(emp.getAlias());
            emailText.setText(emp.getMail());
            sDatePick.setValue(LocalDate.parse(emp.getStartDate()));
            if(emp.getEndDate() != null){
                eDatePick.setValue(LocalDate.parse(emp.getEndDate()));
            }
            else{
                eDatePick.setPromptText("Indefinido");
            }

            if(emp.getEndDate() != null){
                if(LocalDate.now().compareTo(LocalDate.parse(emp.getEndDate())) > 0){
                    dateInfoText.setText("El contrato de este empleado ha vencido.");
                }
                else{
                    dateInfoText.setText("Todavía no ha concluido el contrato de este empleado.");
                }
            }
            else{
                dateInfoText.setText("Todavía no ha concluido el contrato de este empleado.");
            }
        }
    }

    public void setStage(Stage w){
        this.window = w;
    }
}
