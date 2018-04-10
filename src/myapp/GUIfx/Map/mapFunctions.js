var map;
function initMap() {
    var positions = [{lat: 63.4507053, lng: 10.3826452},{lat: 63.4283511, lng: 10.3879615}];
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
