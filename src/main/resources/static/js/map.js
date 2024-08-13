function initializeMap(stores) {
    var mapContainer = document.getElementById('map');
    var mapOption = {
        center: new kakao.maps.LatLng(37.5665, 126.9780),
        level: 3
    };

    var map = new kakao.maps.Map(mapContainer, mapOption);

    for (var i = 0; i < stores.length; i++) {
        var position = new kakao.maps.LatLng(stores[i].latitude, stores[i].longitude);

        var marker = new kakao.maps.Marker({
            position: position,
            map: map
        });

        var infowindow = new kakao.maps.InfoWindow({
            content: '<div style="padding:5px;">' + stores[i].name + '</div>'
        });

        infowindow.open(map, marker);
    }
}