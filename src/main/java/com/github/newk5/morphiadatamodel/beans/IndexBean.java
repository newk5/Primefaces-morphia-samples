package com.github.newk5.morphiadatamodel.beans;

import com.github.newk5.morphiadatamodel.MorphiaLazyDataModel;
import com.github.newk5.morphiadatamodel.entities.Car;
import com.github.newk5.morphiadatamodel.entities.Color;
import dev.morphia.query.experimental.filters.Filters;
import static dev.morphia.query.experimental.filters.Filters.eq;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import org.primefaces.PrimeFaces;

@Named("indexBean")
@ViewScoped
public class IndexBean implements Serializable {

    private MorphiaLazyDataModel<Car> dataModel;
    private List<Color> colors = new ArrayList<>();
    private boolean showOnlySoldCars;
    private List<String> owners = new ArrayList<>();  
  
    @PostConstruct   
    public void init() {  
        if (!PrimeFaces.current().isAjaxRequest()) { 
            loadCars();
            colors = AppBean.datastore.find(Color.class).iterator().toList();
            owners.add("John");
            owners.add("Michael");
            owners.add("Jim"); 
            owners.add("Steve"); 
            owners.add("Roger");
            owners.add("Sarah");
            owners.add("Peter");
            owners.add("Emily");
            owners.add("Jake");
        }
    }

    public void toggleShowOnlySoldCars() {
        loadCars().prependQuery(query -> {
            if (showOnlySoldCars) {
                //here we modify the original query by adding an additional filter by the "sold" field
                query.filter(eq("sold", true));
            }
        });
    }

    public MorphiaLazyDataModel<Car> loadCars() {
        dataModel = new MorphiaLazyDataModel<>(AppBean.datastore, Car.class);
        //override the default match mode query for the "sold" field to allow filtering with strings
        dataModel.overrideFieldQuery("sold", (query, filterMeta) -> {
            if (filterMeta.getFilterValue().toString().startsWith("y")) {
                query.filter(Filters.eq("sold", true));
            } else if (filterMeta.getFilterValue().toString().startsWith("n")) {
                query.filter(Filters.eq("sold", false));
            }

        });
        return dataModel;
    }

    public MorphiaLazyDataModel<Car> getDataModel() {
        return dataModel;
    }

    public void setDataModel(MorphiaLazyDataModel<Car> dataModel) {
        this.dataModel = dataModel;
    }

    public List<Color> getColors() {
        return colors;
    }

    public void setColors(List<Color> colors) {
        this.colors = colors;
    }

    /**
     * @return the showOnlySoldCars
     */
    public boolean isShowOnlySoldCars() {
        return showOnlySoldCars;
    }

    /**
     * @param showOnlySoldCars the showOnlySoldCars to set
     */
    public void setShowOnlySoldCars(boolean showOnlySoldCars) {
        this.showOnlySoldCars = showOnlySoldCars;
    }

    /**
     * @return the owners
     */
    public List<String> getOwners() {
        return owners;
    }

    /**
     * @param owners the owners to set
     */
    public void setOwners(List<String> owners) {
        this.owners = owners;
    }

}
