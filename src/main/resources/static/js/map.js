
        console.log("Stores data in JavaScript:", stores);
        console.log("Type of stores:", typeof stores);
        console.log("Stores data in JavaScript:", stores);

        var mapContainer = document.getElementById('map'); 
        var mapOption = { 
            center: new kakao.maps.LatLng(37.5665, 126.978), 
            level: 3 
        };

        var map = new kakao.maps.Map(mapContainer, mapOption);

        stores.forEach(function(store) {
            var markerPosition  = new kakao.maps.LatLng(store.latitude, store.longitude); 
            var marker = new kakao.maps.Marker({
                position: markerPosition
            });
            marker.setMap(map);
        });