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

import org.martincorp.Model.Employee;
import org.martincorp.Model.Online;

import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.shape.Circle;

public class OnlineCell extends TableCell<Employee, Online>{
    //Variables:
    private final Circle c = new Circle(10);

    //Builder:
    public OnlineCell(){}

    //Methods:
    @Override
    protected void updateItem(Online on, boolean empty){
        super.updateItem(on, empty);

        if(on == null || empty){
            setGraphic(null);
            return;
        }
        else if(on == Online.ONLINE || on == Online.OFFLINE){
            setAlignment(Pos.CENTER);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            c.setFill(on.getColor());
            setGraphic(c);
        }
        else{
            GUI.launchMessage(2, "Error gráfico", "No se ha podido representar el estado\nde conexión de los empleados.");
        }
    }
}
