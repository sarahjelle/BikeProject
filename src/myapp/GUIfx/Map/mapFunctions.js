var map;
var bikes = [];
var centerPos = {lat: 63.429148, lng: 10.392461};
let markers = [];


function initMap() {
    var options = {
        zoom: 13,
        center: centerPos
    };

    map = new google.maps.Map(document.getElementById('map'), options);



    for (var i = 0; i < bikes.length; i++) {
        document.addMarker(bikes[i])
    };

}
document.addBike = function addBike(bike) {
    var present = false;
    for (var i = 0; i < bikes.length; i++) {
        if (bike == bikes[i]) {
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

document.removeAll = function removeAll(){
    for(let i = 0; i < markers.length; i++){
        markers[i].setMap(null);
        markers.splice(i,1);
        /*try{
            markers[i].f.setMap(null);
        } catch{
            markers[i].setMap(null);
        }*/

    }

    markers = [];
    bikes = [];
    //redraw()
}

document.addMarker = function addMarker(bike) {
    var marker = new google.maps.Marker({
        position: {lat: bike.lat, lng: bike.lng},
        map: map,
        id: bike.id
    });
    markers.push(marker);

    let infoWindow = new google.maps.InfoWindow({
        content: "Bike Id: " + bike.id
    });

    google.maps.event.addListener(marker,'click',function(){
        infoWindow.open(map,marker);
    });

    //document.getElementById("console").innerHTML = "Length: " + markers.length;
}
/*
function redraw() {
    map.setZoom(map.getZoom() - 1);
    map.setZoom(map.getZoom() + 1);
}
*/



