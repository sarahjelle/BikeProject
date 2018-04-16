var map;
var bikes = [{id: 1, lat: 43.1293213, lng: 34.9541231},{id: 2, lat: 43.1293213, lng: 34.9541231}];
var centerPos = {lat: 63.429148, lng: 10.392461};
var markers = [];


function initMap() {
    var options = {
        zoom: 13,
        center: centerPos
    };

    map = new google.maps.Map(document.getElementById('map'), options);

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
        for (var i = 0; i < marker.length; i++) {
            if (markers[i].id == bike.id) {
                markers[i].setPosition(new google.maps.LatLng(bike.lat,bike.lng));
                break;
            }
        }
    }

    document.removeBike = function(){

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
    }

    for (var i = 0; i < bikes.length; i++) {
        document.addMarker(bikes[i])
    };

}




