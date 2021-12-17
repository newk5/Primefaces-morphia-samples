package com.github.newk5.morphiadatamodel.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.io.Serializable;
import java.util.Date;
import org.bson.types.ObjectId;

@Entity("Colors")
public class Color implements Serializable{

    @Id
    private ObjectId id;
    private String name;
    private Date created;

    public Color() {
        this.id = new ObjectId();
    }

    public Color(String name, Date created) {
        this.id = new ObjectId();
        this.name = name;
        this.created = created;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
    
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

}
