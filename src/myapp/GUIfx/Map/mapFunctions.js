var map;
var bikes = [];
var centerPos = {lat: 63.427800, lng: 10.421447};
let markers = [];

var docks = [];
var dockMarkers = [];

var infoWindows = [];

var markerInfWList = [];


function initMap() {
    var options = {
        zoom: 12,
        center: centerPos,
        disableDefaultUI: true
    };

    map = new google.maps.Map(document.getElementById('map'), options);

    for (var i = 0; i < bikes.length; i++) {
        document.addMarker(bikes[i])
    };

}

document.addBikes = function addBikes(bikesArr){
    for(var i = 0; i < bikesArr.length; i++){
           document.addBike(bikesArr[i]);
    }
}

document.allBikes = function allBikes(bikesArr){
    for(var i = 0; i < bikes.length; i++){
        var present = false;
        for(var j = 0; j < bikesArr.length; j++){
            if(bikesArr[j].id == bikes[i].id){
                present = true;
            }
        }
        if(!present){
            //Remove bike from bikes
            for(var j = 0; j < markers.length; j++){
                if(markers[j].id == bikes[i].id){
                    markers[j].setMap(null);
                    markers[j] = null;
                    markers.splice(j, 1);
                    break;
                }
            }
            bikes[i] = null;
            bikes.splice(i, 1);
            i--;
        } else{
            //Update loc
        }
    }
    for(var i = 0; i < bikesArr.length; i++){
        document.addBike(bikesArr[i]);
    }
}

document.addBike = function addBike(bike) {
    var present = false;
    for (var i = 0; i < bikes.length; i++) {
        if (bike.id == bikes[i].id) {
            present = true;
        }
    }
    if (!present) {
        bikes.push(bike);
        document.addMarker(bike);
    }
    else{
        document.updateBike(bike);
    }
}

document.updateBike = function updateBike(bike) {
    for (var i = 0; i < markers.length; i++) {
        if (markers[i].id == bike.id) {
            markers[i].setPosition(new google.maps.LatLng(bike.lat,bike.lng));
            break;
        }
    }
}

document.removeAll = function removeAll() {
    var ids = "";

    for(let i = 0; i < markers.length; i++){
        ids += markers[i].id + ", ";
        markers[i].setMap(null);
        markers[i] = null;
    }

    markers = [];
    bikes = [];
}

document.removeBike = function removeBike(id){
    for(var i = 0; i < markers.length; i++){
        if(markers[i].id == id){
            markers[i].setMap(null);
            markers[i] = null;
            markers.splice(i,1);
        }
    }

    for(var i = 0; i < bikes.length; i++){
        if(bikes[i].id == id){
            bikes[i] = null;
            bikes.splice(i,1);
        }
    }
}

document.addDocks = function addDocks(docking_stations){
    // Remove any docks that is not in docking_stations
    for(var i = 0; i < docks.length; i++){
        var present = false;
        for(var j = 0; j < docking_stations.length; j++){
            if(docks[i].id == docking_stations[j].id){
                present = true;
                break;
            }
        }
        if(!present){
            // Remove docks[i] from docks
            // Find docks[i] marker and remove it from map
            for(var j = 0; j < dockMarkers.length; j++){
                if(dockMarkers[j].id == docks[i].id){
                    dockMarkers[i].setMap(null);
                    dockMarkers[i] = null;
                    dockMarkers.splice(j, 1);
                    break;
                }
            }
            docks[i] = null;
            docks.splice(i, 1);
            i--;
        }
    }
    for(var i = 0; i < docking_stations.length; i++){
        document.addDock(docking_stations[i]);
    }
}

document.addDock = function addDock(dock){

    var marker = new google.maps.Marker({
        position: {lat: dock.lat, lng: dock.lng},
        //icon: "http://google.com/mapfiles/kml/paddle" + dock.id + ".png",
        map: map,
        id: dock.id
    });
    var present = false;
    var presentAtIndex;
    for(var i = 0; i < docks.length; i++){
        if(docks[i].id == dock.id){
            present = true;
            presentAtIndex = i;
            break;
        }
    }

    if(!present){
        docks.push(dock);
        dockMarkers.push(marker);
        let infoWindow = new google.maps.InfoWindow({
            content: "Dock id: " + dock.id + "<br/>"
            + "Address: " + dock.address + "<br/>"
            + "Bikes docked: " + dock.docked + " / " + dock.capasity
        });
        infoWindows.push({id: dock.id, inf: infoWindow});
        var counter = 0;
        var isOpen = false;
        var lockOpen = false;
        google.maps.event.addListener(marker,'click',function(){
            if(!isOpen){
                infoWindow.open(map,marker);
                isOpen = true;
                lockOpen = true;
            } else if(isOpen && lockOpen){
                infoWindow.close(map,marker);
                isOpen = false;
                lockOpen = false;
            } else if(isOpen){
                //infoWindow.close(map,marker);
                isOpen = true;
                lockOpen = true;
            }


        });

        google.maps.event.addListener(marker, 'mouseover', function(){
            if(!isOpen){
                infoWindow.open(map, marker);
                isOpen = true;
            }
        });
        google.maps.event.addListener(marker, 'mouseout', function(){
            if(isOpen && !lockOpen){
                // Close
                infoWindow.close(map, marker);
                isOpen = false;
            }
        });
        var element = {id: dock.id, marker: marker, infoW: infoWindow};
        markerInfWList.push(element);
        //document.addMarker(dock);
    } else{
        docks[presentAtIndex] = dock;
        var dockMarkerPresentAtIndex;
        for(var i = 0; i < dockMarkers.length; i++){
            if(dockMarkers[i].id == dock.id){
                // Update marker position
                dockMarkerPresentAtIndex = i;
                dockMarkers[i].setPosition(new google.maps.LatLng(dock.lat,dock.lng))
                break;
            }
        }
        for(var i = 0; i < infoWindows.length; i++){
            if(infoWindows[i].id == dock.id){
                infoWindows[i].inf.setContent(
                    "Dock id: " + dock.id + "<br/>"
                    + "Address: " + dock.address + "<br/>"
                    + "Bikes docked: " + dock.docked + " / " + dock.capasity
                );
                var counter = 0;
                google.maps.event.addListener(dockMarkers[dockMarkerPresentAtIndex],'click',function(){
                    if(counter == 0){
                        infoWindows[i].inf.open(map,dockMarkers[dockMarkerPresentAtIndex]);
                        counter = 1;
                    } else if(counter == 1){
                        infoWindows[i].inf.close(map,dockMarkers[dockMarkerPresentAtIndex]);
                        counter = 0;
                    }
                });
                marker.setMap(null);
                marker = null;
                break;
            }
        }
    }
}

document.addMarker = function addMarker(bike) {
    var marker = new google.maps.Marker({
        position: {lat: bike.lat, lng: bike.lng},
        icon: "bike.png",//"http://labs.google.com/ridefinder/images/mm_20_blue.png",
        map: map,
        id: bike.id
    });
    markers.push(marker);

    let infoWindow = new google.maps.InfoWindow({
        content: "Bike id: " + bike.id
    });
    var counter = 0;
    google.maps.event.addListener(marker,'click',function(){
        if(counter == 0){
            infoWindow.open(map,marker);
            counter = 1;
        } else if(counter == 1){
            infoWindow.close(map,marker);
            counter = 0;
        }
    });
}



