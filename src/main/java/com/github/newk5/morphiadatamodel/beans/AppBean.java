package com.github.newk5.morphiadatamodel.beans;

import com.github.newk5.morphiadatamodel.entities.Car;
import com.github.newk5.morphiadatamodel.entities.Color;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import io.quarkus.runtime.StartupEvent;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

@ApplicationScoped
public class AppBean {

    public static Datastore datastore;
    private final static String[] brands;
    private final static List<Color> colors = Arrays.asList(new Color[]{
        new Color("Black", new Date()),
        new Color("White", new Date()),
        new Color("Green", new Date()),
        new Color("Red", new Date()),
        new Color("Blue", new Date()),
        new Color("Orange", new Date()),
        new Color("Silver", new Date()),
        new Color("Yellow", new Date()),
        new Color("Brown", new Date()),
        new Color("Maroon", new Date())

    });

    static {

        brands = new String[10];
        brands[0] = "BMW";
        brands[1] = "Mercedes";
        brands[2] = "Volvo";
        brands[3] = "Audi";
        brands[4] = "Renault";
        brands[5] = "Fiat";
        brands[6] = "Volkswagen";
        brands[7] = "Honda";
        brands[8] = "Jaguar";
        brands[9] = "Ford";
    }

    void onStart(@Observes StartupEvent ev) {
        //open connection to "cars" db on localhost:27017
        datastore = Morphia.createDatastore(MongoClients.create(), "cars");

        datastore.getMapper().mapPackage("com.github.newk5.morphiadatamodel.entities");

        //insert default colors and cars in the db
        if (datastore.find(Color.class).count() == 0) {
            colors.forEach(color -> datastore.save(color));
        }

        if (datastore.find(Car.class).count() == 0) {
            createCars(50);
        } 
    }

    public void createCars(int size) {
        for (int i = 0; i < size; i++) {
            Random rand = new Random();

            Car c = new Car(getRandomBrand(), getRandomYear(), getRandomPrice(), getRandomSoldState(), colors.get(rand.nextInt(colors.size())));
            if (i % 3 == 0) {
                c.getPreviousOwners().add("John");
                c.getPreviousOwners().add("Michael");
                c.getPreviousOwners().add("Jim");
            } else if (i % 5 == 0) {
                c.getPreviousOwners().add("Steve");
                c.getPreviousOwners().add("Roger");
            } else if (i % 10 == 0) {
                c.getPreviousOwners().add("Sarah");
                c.getPreviousOwners().add("Peter");
            } else if (i % 15 == 0) {
                c.getPreviousOwners().add("Emily");
            }else if (i % 20 == 0) {
                c.getPreviousOwners().add("Jake");
            }
            datastore.save(c);
        }

    }

    private int getRandomYear() {
        return (int) (Math.random() * 50 + 1960);
    }

    private String getRandomBrand() {
        return brands[(int) (Math.random() * 10)];
    }

    private int getRandomPrice() {
        return (int) (Math.random() * 100000);
    }

    private boolean getRandomSoldState() {
        return (Math.random() > 0.5) ? true : false;
    }

}
