package org.martincorp.Model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Group{
    //Variables:
    private IntegerProperty Id, ownerId;
    private StringProperty name, owner, creationDate;
    // private BooleanProperty deleted;

    //Builder:
    public Group(int i, int oi, String n, String o, String da/* , boolean de */){
        setId(i);
        setOwnerId(oi);
        setName(n);
        setOwner(o);
        setCreationDate(da);
        // setDeleted(de);
    }

    //Methods:
    public IntegerProperty IdProperty(){
        if(Id == null){
            Id = new SimpleIntegerProperty(this, "Id");
        }

        return Id;
    }

    public void setId(int i){
        IdProperty().set(i);
    }

    public Integer getId(){
        return IdProperty().getValue();
    }

    public IntegerProperty ownerIdProperty(){
        if(ownerId == null){
            ownerId = new SimpleIntegerProperty(this, "ownerId");
        }

        return ownerId;
    }

    public void setOwnerId(int ownerId){
        ownerIdProperty().set(ownerId);
    }

    public Integer getOwnerId(){
        return ownerIdProperty().getValue();
    }

    public StringProperty nameProperty(){
        if(name == null){
            name = new SimpleStringProperty(this, "name");
        }

        return name;
    }

    public void setName(String n){
        nameProperty().set(n);
    }

    public String getName(){
        return nameProperty().getValue();
    }

    public StringProperty ownerProperty(){
        if(owner == null){
            owner = new SimpleStringProperty(this, "owner");
        }

        return owner;
    }

    public void setOwner(String o){
        ownerProperty().set(o);
    }

    public String getOwner(){
        return ownerProperty().getValue();
    }

    public StringProperty creationDateProperty(){
        if(creationDate == null){
            creationDate = new SimpleStringProperty(this, "creationDate");
        }

        return creationDate;
    }

    public void setCreationDate(String d){
        creationDateProperty().set(d);
    }

    public String getCreationDate(){
        return creationDateProperty().getValue();
    }

    /* public BooleanProperty deletedProperty(){
        if(deleted == null){
            deleted = new SimpleBooleanProperty(this, "deleted");
        }

        return deleted;
    }

    public void setDeleted(boolean d){
        deletedProperty().set(d);
    }

    public boolean getDeleted(){
        return deletedProperty().getValue();
    } */
}
