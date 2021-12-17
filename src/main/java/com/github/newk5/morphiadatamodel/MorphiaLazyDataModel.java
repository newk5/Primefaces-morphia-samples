
package com.github.newk5.morphiadatamodel;


import dev.morphia.Datastore;
import dev.morphia.query.FindOptions;
import dev.morphia.query.Query;
import dev.morphia.query.Sort;
import static dev.morphia.query.experimental.filters.Filters.*;
import dev.morphia.query.experimental.filters.RegexFilter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel; 
import static org.primefaces.model.MatchMode.CONTAINS;
import static org.primefaces.model.MatchMode.ENDS_WITH;
import static org.primefaces.model.MatchMode.EXACT;
import static org.primefaces.model.MatchMode.STARTS_WITH;
import org.primefaces.model.SortMeta;


public class MorphiaLazyDataModel<T> extends LazyDataModel<T> implements Serializable {

    private Datastore ds;
    private Class<T> klass;
    //usually will be getId() but can be a user specified method aswell when using the 2nd constructor
    private Method rowKeyGetter; 
    /*
    if the default match mode queries in applyFilters() dont work for a specific
    field, overridden field queries with the overrideFieldQuery method will add
    BiConsumers to this map, where the key is the field name specified in
    <p:column filterBy="">
    */
    private Map<String, BiConsumer<Query<T>, FilterMeta>> overrides = new HashMap<>();
    //consumer to be executed before the query is built, useful to modify the original query
    private  Consumer<Query<T>> prependConsumer;

    public MorphiaLazyDataModel(Datastore ds, Class cl) {
        this.ds = ds;
        this.klass = cl;
        this.rowKeyGetter = Arrays.stream(klass.getDeclaredMethods())
                .filter(me -> me.getName().equalsIgnoreCase("getId"))
                .findFirst().orElse(null);
        if (rowKeyGetter==null){
            //log it, facesMessage, throw exception?
           // System.out.println("Failed to find "+cl.getSimpleName()+"#getId()");    
        }
    }
    
    public MorphiaLazyDataModel(Datastore ds, Class cl, String idGetter) {
        this.ds = ds;
        this.klass = cl;
        this.rowKeyGetter = Arrays.stream(klass.getDeclaredMethods())
                .filter(me -> me.getName().equalsIgnoreCase(idGetter))
                .findFirst().orElse(null);
        if (rowKeyGetter==null){
            //log it, FacesMessage, throw exception?
           // System.out.println("Failed to find "+cl.getSimpleName()+"#"+idGetter);    
        }
    }

    @Override
    public String getRowKey(T object) {
        try {

            return rowKeyGetter.invoke(object) + "";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int count(Map<String, FilterMeta> map) {

        Query<T> q =this.buildQuery();
        
        Long v = applyFilters(q, map).count();
        return v.intValue();

    }

    public MorphiaLazyDataModel overrideFieldQuery(String field, BiConsumer<Query<T>, FilterMeta> consumer) {
        this.overrides.put(field, consumer);
        return this;
    }

    @Override
    public T getRowData(String rowKey) {
        for (T o : (List<T>) getWrappedData()) {
            try {
                String rk = rowKeyGetter.invoke(o) + "";
                if (rk.equals(rowKey)) {
                    return o;
                }
            } catch (Exception ex) {
                Logger.getLogger(MorphiaLazyDataModel.class.getName()).log(Level.SEVERE, null, ex);
            } 
        }
        return null;
    }

   

    public MorphiaLazyDataModel prependQuery(Consumer<Query<T>> consumer) {
        prependConsumer = consumer;
        return this;
    }

    public Query<T> applyFilters(Query<T> q, Map<String, FilterMeta> filters) {
        filters.forEach((field, metadata) -> {
   
            if (metadata.getFilterValue() != null) {
                BiConsumer<Query<T>, FilterMeta> override = overrides.get(field);
                if (override != null) {
                    override.accept(q, metadata); 
                } else {
                    Object val = metadata.getFilterValue();
                    if (metadata.getMatchMode() != null){ 
                         
                        switch (metadata.getMatchMode()) { 
                            case STARTS_WITH:
                                RegexFilter regStartsWith = regex(field);
                                regStartsWith.pattern("^"+val).caseInsensitive();
                                q.filter(regStartsWith);
                                break;
                            case ENDS_WITH: 
                                RegexFilter regEndsWith = regex(field);
                                regEndsWith.pattern(val+"$").caseInsensitive();
                                q.filter(regEndsWith);
                                break;   
                            case CONTAINS:
                                 q.filter(regex(field).pattern(val+"").caseInsensitive());
                                break; 
                            case EXACT:  
                                Object castedValueEx = castedValue(field, val);
                                if (castedValueEx != null){
                                     q.filter(eq(field, castedValueEx));
                                }  else{
                                    q.filter(eq(field, val));
                                }

                                break;
                            case LESS_THAN:  
                                Object castedValueLt = castedValue(field, val);
                                if (castedValueLt != null){
                                    q.filter(lt(field, castedValueLt));
                                }else{
                                    q.filter(lt(field, val));
                                }

                                break;
                            case LESS_THAN_EQUALS:
                                Object castedValueLte = castedValue(field, val);
                                if (castedValueLte != null){
                                    q.filter(lte(field, castedValueLte));
                                }else{
                                    q.filter(lte(field, val));
                                }
                                break;
                            case GREATER_THAN:
                                Object castedValueGt = castedValue(field, val);
                                if (castedValueGt != null){
                                    q.filter(gt(field, castedValueGt));
                                }else{
                                    q.filter(gt(field, val));
                                }
                                break;
                            case GREATER_THAN_EQUALS:

                                Object castedValueGte = castedValue(field, val);
                                if (castedValueGte != null){
                                    q.filter(gte(field, castedValueGte));
                                }else{ 
                                    q.filter(gte(field, val));
                                }
                                break;

                            case EQUALS:
                                 q.filter(eq(field, val));
                                break;

                            case IN:
                                if (metadata.getFilterValue().getClass() == Object[].class){
                                    Object [] parts =( Object[]) metadata.getFilterValue();
                                    q.filter(in(field, Arrays.asList(parts)));
                                }

                                break;


                            default:
                                throw new UnsupportedOperationException("MatchMode "+metadata.getMatchMode()+" not suppported");

                        }
                    }
                }

            }

        });
        return q;
    }
     
    
    /*
    checks the data type of the field on the corresponding class
    and tries to convert the string value to its data type
    (only handles basic primitive types, for more complex data types
    the field query should be overridden with the overrideFieldQuery method)
    
    for example this can be useful when filtering for fields which are numbers like
    ints,floats,doubles and longs where the filterValue will be a string and thus the
    query wont find any matches since it's comparing a string with a number
    */
    private Object castedValue(String field, Object filterValue){
        try {
            Field f = klass.getDeclaredField(field);
            if (f== null) return null;
            if (f.getType().isAssignableFrom(Integer.class) || f.getType().isAssignableFrom(int.class)){
                return Integer.valueOf(filterValue+"");
            } else  if (f.getType().isAssignableFrom(Float.class) || f.getType().isAssignableFrom(float.class)){
                return Float.valueOf(filterValue+"");
            } else  if (f.getType().isAssignableFrom(Double.class) || f.getType().isAssignableFrom(double.class)){
                return Double.valueOf(filterValue+"");
            }else  if (f.getType().isAssignableFrom(Long.class) || f.getType().isAssignableFrom(long.class)){
                return Long.valueOf(filterValue+"");
            }else  if (f.getType().isAssignableFrom(Boolean.class) || f.getType().isAssignableFrom(boolean.class)){
                return Boolean.valueOf(filterValue+"");
            } else  if (f.getType().isAssignableFrom(String.class)){
                return filterValue+"";
            }
        } catch (Exception e) {
            //should we print this? log it? Send a FacesMessage?
           // System.out.println("Failed to convert "+field+" to its corresponding data type");
        }
        return null;
    }

    private Query<T> buildQuery(){
        Query<T> q = ds.find(klass).disableValidation();
        if (prependConsumer != null) {
            prependConsumer.accept(q);
        }
        return q;
    }
    
    
    @Override
    public List<T> load(int first, int pageSize, Map<String, SortMeta> sort, Map<String, FilterMeta> filters) {
        Query<T> q = this.buildQuery();
        FindOptions opt = new FindOptions();
        sort.forEach((field, sortData)->{
            opt.sort(sortData.getOrder().name().equalsIgnoreCase("DESCENDING") ? Sort.descending(field): Sort.ascending(field));

        });
     
        this.applyFilters(q, filters);
        opt.skip(first).limit(pageSize);
        List<T> list =q.iterator(opt).toList();

        return list;
    }

}