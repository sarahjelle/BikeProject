var map;
var bikes = [];
var centerPos = {lat: 63.429148, lng: 10.392461};
let markers = [];

var docks = [];
var dockMarkers = [];


function initMap() {
    var options = {
        zoom: 13,
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

document.addDock = function addDock(dock){
    var marker = new google.maps.Marker({
        position: {lat: dock.lat, lng: dock.lng},
        //icon: "http://google.com/mapfiles/kml/paddle" + dock.id + ".png",
        map: map,
        id: dock.id
    });
    var present = false;
    for(var i = 0; i < docks.length; i++){
        if(docks[i].id == dock.id){
            present = true;
        }
    }

    if(!present){
        docks.push(dock);
        dockMarkers.push(marker);
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
        //document.addMarker(dock);
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



