// 전역 지도 인스턴스
let map;
let markers = [];
let userMarker;

// 지도 초기화 함수
function initializeMap(containerId, centerLat, centerLng) {
    map = new kakao.maps.Map(document.getElementById(containerId), {
        center: new kakao.maps.LatLng(centerLat, centerLng), // 사용자 위치를 기본 위치로 설정
        level: 3 // 확대 레벨
    });
}

// 주소를 좌표로 변환하는 함수
function geocodeAddress(address, callback) {
    var geocoder = new kakao.maps.services.Geocoder();
    geocoder.addressSearch(address, function(result, status) {
        if (status === kakao.maps.services.Status.OK) {
            var lat = result[0].y;
            var lng = result[0].x;
            if (callback) callback(lat, lng);
        } else {
            console.error('주소를 좌표로 변환하는 데 실패했습니다.');
            if (callback) callback(null, null);
        }
    });
}

// 지도로 이동하는 함수
function moveToLocation(lat, lng) {
    if (map) {
        var position = new kakao.maps.LatLng(lat, lng);
        map.setCenter(position); // 지도 중심을 이동
    } else {
        console.error('Map is not initialized.');
    }
}

// 현재 위치 마커 추가
function addUserMarker(lat, lng) {
    if (userMarker) {
        userMarker.setMap(null); // 기존 마커가 있다면 제거
    }
    
    // 커스텀 마커 이미지 설정
    var imageSrc = '/images/here.png'; // 커스텀 마커 이미지 경로
    var imageSize = new kakao.maps.Size(40, 55); // 커스텀 마커 이미지 크기
    var imageOption = {offset: new kakao.maps.Point(20, 55)}; // 커스텀 마커 이미지 옵션

    var markerImage = new kakao.maps.MarkerImage(imageSrc, imageSize, imageOption);
    
    userMarker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(lat, lng),
        map: map,
        image: markerImage,
        title: '현재 위치'
    });
}

// 현재 위치로 이동
function moveToCurrentLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
            function(position) {
                const lat = position.coords.latitude;
                const lng = position.coords.longitude;
                moveToLocation(lat, lng); // 사용자의 현재 위치로 이동
                addUserMarker(lat, lng); // 현재 위치에 커스텀 마커 추가
            },
            function() {
                console.error('Unable to retrieve your location.');
                // 사용자 위치를 가져올 수 없을 때 처리
            }
        );
    } else {
        console.error('Geolocation is not supported by this browser.');
        // 위치 서비스가 지원되지 않을 때 처리
    }
}

// 주소를 좌표로 변환하고 지도를 이동시키는 버튼 클릭 이벤트 핸들러
document.addEventListener('DOMContentLoaded', function() {
    var findLocationBtn = document.getElementById('geocodeButton'); // 버튼 ID 확인
    if (findLocationBtn) {
        findLocationBtn.addEventListener('click', function() {
            var address = document.getElementById('storeAddress').value.trim();
            if (address) {
                geocodeAddress(address, function(lat, lng) {
                    if (lat !== null && lng !== null) {
                        document.getElementById('storeLatitude').value = lat;
                        document.getElementById('storeLongitude').value = lng;
                        moveToLocation(lat, lng); // 지도를 새 위치로 이동
                    } else {
                        document.getElementById('storeLatitude').value = '';
                        document.getElementById('storeLongitude').value = '';
                    }
                });
            }
        });
    } else {
        console.error('Geocode button not found.');
    }
});

// 전역 객체에 함수 추가
window.moveToCurrentLocation = moveToCurrentLocation;
window.initializeMap = initializeMap;
window.moveToLocation = moveToLocation;
window.addUserMarker = addUserMarker;
window.geocodeAddress = geocodeAddress; // 전역 객체에 geocodeAddress 추가
