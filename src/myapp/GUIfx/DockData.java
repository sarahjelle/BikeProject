package myapp.GUIfx;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import myapp.data.Docking;
import myapp.data.Location;

public class DockData {
    private final SimpleStringProperty dockName = new SimpleStringProperty("");
    private final SimpleObjectProperty dockLocation = new SimpleObjectProperty(null);
    private final SimpleIntegerProperty dockCapacity= new SimpleIntegerProperty(0);

    public DockData(Docking dock){
        this(dock.getName(), dock.getLocation(), dock.getCapacity());
    }

    public DockData(String dockName, Location dockLocation, int dockCapacity){
        setdockName(dockName);
        setdockLocation(dockLocation);
        setdockCapacity(dockCapacity);
    }

    public String getdockName(){
        return dockName.get();
    }
    public void setdockName(String dName){
        dockName.set(dName);
    }

    public Location getdockLocation(){
        return (Location) dockLocation.get();
    }

    public void setdockLocation(Location loc){
        dockLocation.set(loc);
    }

    public int getdockCapacity(){
        return dockCapacity.get();
    }

    public void setdockCapacity(int cap) {
        dockCapacity.set(cap);
    }
}