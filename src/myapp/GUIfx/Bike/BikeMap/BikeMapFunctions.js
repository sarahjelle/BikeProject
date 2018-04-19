var center_bike = null;
var center_marker = null;
var docking_stations = [];
var docking_markers = [];

var all_bikes = [];
var uncentered_bike_markers = [];

var map;

function initMap(){
    center_bike = null;

    var options = {
        zoom: 14,
        center: {lat: 63.429148, lng: 10.392461},
        disableDefaultUI: true
    };

    map = new google.maps.Map(document.getElementById('map'), options);

    center_marker = null;
}

document.addDocks = function addDocks(docks){
    // First remove any markers that are currently drawn, that are not present in docks
    for(var i = 0; i < docking_stations.length; i++){
        var present = false;
        var presentAtIndex = -1;
        for(var j = 0; j < docks.length; j++){
            if(docking_stations[i].id == docks[j].id){
                present = true;
                presentAtIndex = j;
            }
        }
        if(present){
            // Update the docking_station position and marker
            docking_stations[i].lat = docks[presentAtIndex].lat;
            docking_stations[i].lng = docks[presentAtIndex].lng;
            for(var j = 0; j < docking_markers.length; j++){
                if(docking_markers[j].id == docking_stations[i].id){
                    docking_markers[j].setPosition(
                        new google.maps.LatLng(docking_stations[i].lat, docking_stations[i].lng)
                    );
                    break;
                }
            }
        } else{
            // Remove the docking_station and marker
            for(var j = 0; j < docking_markers.length; j++){
                if(docking_markers[j].id == docking_stations[i].id){
                    docking_markers[j].setMap(null);
                    docking_markers[j] = null;
                    docking_markers.splice(j, 1);
                    break;
                }
            }

            docking_stations[i] = null;
            docking_stations.splice(i, 1);
            i--;
        }
    }
    // Then update/add all the docks markers

}

document.centerMap = function centerMap(bike){
    var present = false;
    for(var i = 0; i < uncentered_bike_markers.length; i++){
        if(uncentered_bike_markers[i].id == bike.id){
            uncentered_bike_markers[i].setPosition(
                new google.maps.LatLng(bike.lat, bike.lng)
            );
            //map.setCenter(uncentered_bike_markers[i].getPosition());
            map.panTo(uncentered_bike_markers[i].getPosition());
            center_bike = bike;
            if(center_marker == null){
                center_marker = uncentered_bike_markers[i]
                center_marker.setIcon("green_bike.png");
            } else{
                center_marker.setIcon("bike.png");
                center_marker = uncentered_bike_markers[i];
                center_marker.setIcon("green_bike.png");
            }
            present = true;
            break;
        }
    }

    if(!present){
        all_bikes.push(bike);
        var marker = addBikeMarker(bike);
        center_marker.setIcon("bike.png");
        center_marker = marker;
        marker.setIcon("green_bike.png");
        center_bike = bike;
        map.setCenter(marker.getPosition());
    }
}

function manageDockMarkers(dock){
    var markerPresent = false;
    for(var i = 0; i < docking_markers.length; i++){
        if(dock.id == docking_markers[i].id){
            markerPresent = true;
        }
    }
    if(markerPresent){
        updateDockMarker(dock);
    } else {
        addDockMarker(dock);
    }
}

function manageDockMarkers(docks) {
    for(var i = 0; i < docks.length; i++){
        manageDockMarkers(docks[i]);
    }
}

function addDockMarker(dock){
    var marker = new google.maps.Marker({
        position: {lat: dock.lat, lng: dock.lng}, //"http://labs.google.com/ridefinder/images/mm_20_blue.png",
        map: map,
        icon: "bike.png",
        id: dock.id
    });
    docking_markers.push(marker);

    let infoWindow = new google.maps.InfoWindow({
        content: "Dock id: " + dock.id
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

function updateDockMarker(dock){
    for(var i = 0; i < docking_markers.length; i++){
        if(docking_markers[i].id == dock.id){
            docking_markers[i].lat = dock.lat;
            docking_markers[i].lng = dock.lng;
            docking_markers[i].setPosition(
                new google.maps.LatLng(dock.lat, dock.lng)
            );
            break;
        }
    }
}

document.addBikes = function addBikes(bikes){
    for(var i = 0; i < all_bikes.length; i++){
        var present = false;
        var presentAtIndex = -1;
        for(var j = 0; j < bikes.length; j++){
            if(all_bikes[i].id == bikes[j].id){
                present = true;
                presentAtIndex = j;
                break;
            }
        }
        if(present){
            // Should not be added, only updated
            if(bikes[presentAtIndex].id == center_bike.id){ // If bikes[presentAtIndex] = bike to center map to
                //should recenter map around new centerbike position
                center_bike.lat = bikes[presentAtIndex].lat;
                center_bike.lng = bikes[presentAtIndex].lng;
                center_marker.setPosition( new google.maps.LatLng(center_bike.lat, center_bike.lng));
                map.setCenter(center_marker.getPosition);
            } else{
                // Bike is in the all_bikes array
                // Its position and marker should be updated
                all_bikes[i].lat = bikes[presentAtIndex].lat;
                all_bikes[i].lng = bikes[presentAtIndex].lng;
                //addBikeMarker(all_bikes[i]);
                //updateBikeMarker(all_bikes[i]);
                manageMarkers(all_bikes[i]);
            }
        } else{
            //Remove bike and coresponding marker from all_bikes
            for(var j = 0; j < uncentered_bike_markers.length; j++){
                if(uncentered_bike_markers[j].id == all_bikes[i].id){
                    uncentered_bike_markers[j].setMap(null);
                    uncentered_bike_markers[j] = null;
                    uncentered_bike_markers.splice(j, 1);
                    break;
                }
            }
            all_bikes[i] = null;
            all_bikes.splice(i, 1);
            i--;
        }
    }
    // all_bikes should now not contain any bikes that are not in bikes
    for(var i = 0; i < bikes.length; i++){
        var present = false;
        for(var j = 0; j < all_bikes.length; j++){
            if(bikes[i].id == all_bikes[j].id){
                all_bikes[j] = bikes[i];
                updateBikeMarker(all_bikes[j]);
                present = true;
            }
        }
        if(!present){
            all_bikes.push(bikes[i]);
            addBikeMarker(bikes[i]);
        }
    }
    manageMarkers(bikes);
}

function addBikeMarker(bike){
    var marker = new google.maps.Marker({
        position: {lat: bike.lat, lng: bike.lng},
        icon: "bike.png",//"http://labs.google.com/ridefinder/images/mm_20_blue.png",
        map: map,
        id: bike.id
    });
    uncentered_bike_markers.push(marker);

    let infoWindow = new google.maps.InfoWindow({
        content: "Bike id: " + bike.id
    });
    var counter = 0;
    google.maps.event.addListener(marker,'click',function(){
        document.centerMap(bike);
        if(counter == 0){
            infoWindow.open(map,marker);
            counter = 1;
        } else if(counter == 1){
            infoWindow.close(map,marker);
            counter = 0;
        }
    });
    return marker;
}

function updateBikeMarker(bike){
    for(var i = 0; i < uncentered_bike_markers.length; i++){
        if(bike.id == uncentered_bike_markers[i].id){
            uncentered_bike_markers[i].setPosition(
                new google.maps.LatLng(bike.lat, bike.lng)
            );
            break;
        }
    }
}

function manageMarkers(bike){
    var markerPresent = false;
    for(var i = 0; i < uncentered_bike_markers.length; i++){
        if(bikes.id == uncentered_bike_markers[i].id){
            markerPresent = true;
        }
    }
    if(markerPresent){
        updateBikeMarker(bike);
    } else {
        addBikeMarker(bike);
    }
}

function manageMarkers(bikes){
    for(var i = 0; i < bikes.length; i++){
        manageMarkers(bikes[i]);
    }
}