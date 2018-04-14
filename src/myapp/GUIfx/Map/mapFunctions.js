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

document.removeAll = function removeAll() {
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

document.checkID = function checkID(ids){
    for(var i = 0; i < ids.length; i++){
        var present = false;
        var bikeIndex = -1;
        for(var j = 0; j < bikes.length; j++){
            if(ids[i] == bikes[j].id){
                present = true;
                bikeIndex = j;
            }
        }
        if(present){
            if(bikeIndex > -1){
                bikes = bikes.splice(bikeIndex, 1);
            }
            for(var j = 0; j < markers.length; j++){
                if(ids[i] == markers[j].id){
                    markers[j].setMap(null);
                    markers.splice(j, 1);
                }
            }
        }
    }
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

    //document.getElementById("console").innerHTML = "Length: " + markers.length;
}



