//importClass(myapp.map.DummyLocations);
//var bike1 = new DummyLocations(123, 63.429, 10.3969);
var map;
function initMap() {
    var positions = [{lat: 63.429148, lng: 10.392461},{lat: 63.427189, lng: 10.396924},{lat: 63.429319, lng: 10.386667},{lat: 63.431948, lng: 10.397567}];
    var cetnterPos = {lat: 63.429148, lng: 10.392461};

    var options = {
        zoom: 13,
        center: cetnterPos
    };

    map = new google.maps.Map(document.getElementById('map'), options);
    for (var i=0; i<positions.length;i++)
    {addMarker(positions[i])};



    // Add marker function
    function addMarker(coords){
        var marker = new google.maps.Marker({
            position: coords,
            map: map
            //icon: "Bike.png",
        });
    }
}