package com.github.newk5.morphiadatamodel.entities;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.bson.types.ObjectId;

@Entity("Cars")
public class Car implements Serializable {

    @Id
    private ObjectId id;
    private String brand;
    private Integer year;
    private int price;
    private boolean sold;
    private Color color;
    private List<String> previousOwners = new ArrayList<>();

    public Car() {
    }

    public Car(String brand, Integer year, int price, boolean sold, Color color) {
        this.id = new ObjectId();
        this.brand = brand;
        this.year = year;
        this.price = price;
        this.sold = sold;
        this.color = color;
    }

    public String getPreviousOwnersAsString() {
        return previousOwners.stream().reduce("", (o1, o2) -> {
            if (!"".equals(o1)) {
                return o1 + ", " + o2;
            } else {
                return o2;
            }
        });

    }

    public List<String> getPreviousOwners() {
        return previousOwners;
    }

    public void setPreviousOwners(List<String> previousOwners) {
        this.previousOwners = previousOwners;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isSold() {
        return sold;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Car{" + "id=" + id + ", brand=" + brand + ", year=" + year + ", price=" + price + ", sold=" + sold + ", color=" + color + '}';
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.id);
        hash = 47 * hash + Objects.hashCode(this.brand);
        hash = 47 * hash + Objects.hashCode(this.year);
        hash = 47 * hash + this.price;
        hash = 47 * hash + (this.sold ? 1 : 0);
        hash = 47 * hash + Objects.hashCode(this.color);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Car other = (Car) obj;
        if (this.price != other.price) {
            return false;
        }
        if (this.sold != other.sold) {
            return false;
        }
        if (!Objects.equals(this.brand, other.brand)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.year, other.year)) {
            return false;
        }
        if (!Objects.equals(this.color, other.color)) {
            return false;
        }
        return true;
    }

}
