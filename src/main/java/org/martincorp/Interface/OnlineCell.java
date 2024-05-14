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
