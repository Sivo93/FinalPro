document.addEventListener('DOMContentLoaded', function() {
    var map;
    var userMarker;
    var stores = []; // 상가 리스트 저장용 배열
    var markers = []; // 지도에 표시된 상가 마커 저장용 배열

    // 현재 위치를 가져오는 함수
    function getCurrentPosition() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                function(position) {
                    var lat = position.coords.latitude;
                    var lng = position.coords.longitude;
                    initMap(lat, lng); // 현재 위치를 인자로 지도 초기화
                },
                function() {
                    console.error('Unable to retrieve your location.');
                    // 기본 위치로 지도 초기화
                    initMap(37.5665, 126.978); // 서울의 위도, 경도
                }
            );
        } else {
            console.error('Geolocation is not supported by this browser.');
            // 기본 위치로 지도 초기화
            initMap(37.5665, 126.978); // 서울의 위도, 경도
        }
    }

    // 지도 객체를 설정하고 초기화하는 함수
    function initMap(lat, lng) {
        var container = document.getElementById('map');
        var options = {
            center: new kakao.maps.LatLng(lat, lng), // 사용자 위치를 중심으로 설정
            level: 3
        };
        map = new kakao.maps.Map(container, options);

        // 사용자 위치 마커 설정
        if (userMarker) {
            userMarker.setMap(null); // 기존 마커 제거
        }
        userMarker = new kakao.maps.Marker({
            position: new kakao.maps.LatLng(lat, lng),
            map: map,
            image: new kakao.maps.MarkerImage('/images/here.png', new kakao.maps.Size(32, 32)) // 사용자 위치 마커 이미지
        });

        // 상가 마커 추가
        addStoreMarkers();
        fetchTags(); // 태그를 가져와 필터링 기능 설정
    }

    // 상가 마커를 추가하는 함수
    function addStoreMarkers() {
        fetch('/api/stores')
            .then(response => response.json())
            .then(data => {
                stores = data;
                // 필터링 후 마커 추가
                updateMarkers();
            })
            .catch(error => console.error('Error fetching stores:', error));
    }

    // 태그 필터를 가져오는 함수
    function fetchTags() {
        fetch('/api/stores/tags')
            .then(response => response.json())
            .then(tags => {
                console.log('Fetched tags:', tags); // 응답 확인
                if (Array.isArray(tags)) {
                    renderTagFilters(tags);
                } else {
                    console.error('Tags data is not an array:', tags);
                }
            })
            .catch(error => console.error('Error fetching tags:', error));
    }

    // 태그 필터 렌더링 함수
    function renderTagFilters(tags) {
        const tagFiltersContainer = document.getElementById('tag-filters');
        tagFiltersContainer.innerHTML = ''; // 기존 태그 필터 초기화

        tags.forEach(tag => {
            const label = document.createElement('label');
            const checkbox = document.createElement('input');
            checkbox.type = 'checkbox';
            checkbox.value = tag;
            checkbox.className = 'tag-checkbox';
            checkbox.addEventListener('change', updateMarkers); // 필터가 변경될 때마다 상가 마커 업데이트

            label.appendChild(checkbox);
            label.appendChild(document.createTextNode(tag));
            tagFiltersContainer.appendChild(label);
        });
    }

    // 상가 마커를 업데이트하는 함수
    function updateMarkers() {
        // 기존 마커 제거
        markers.forEach(marker => marker.setMap(null));
        markers = [];

        var selectedTags = Array.from(document.querySelectorAll('.tag-checkbox:checked'))
                                .map(checkbox => checkbox.value);

        var filteredStores = selectedTags.length > 0 ? 
            stores.filter(store => store.tags && store.tags.some(tag => selectedTags.includes(tag))) : 
            stores;

        filteredStores.forEach(store => {
            var position = new kakao.maps.LatLng(store.latitude, store.longitude);
            var marker = new kakao.maps.Marker({
                position: position,
                map: map,
                title: store.name
            });

            // 상가 마커 클릭 이벤트 설정
            kakao.maps.event.addListener(marker, 'click', function() {
                var infoWindow = new kakao.maps.InfoWindow({
                    content: `<div style="padding:5px;">${store.name}<br>${store.address}<br>${store.phoneNumber}</div>`
                });
                infoWindow.open(map, marker);
            });

            markers.push(marker);
        });
    }

    // 주소를 좌표로 변환하는 버튼 클릭 이벤트 리스너
    document.getElementById('geocodeButton').addEventListener('click', function() {
        var address = document.getElementById('storeAddress').value;

        // 카카오 API를 사용하여 주소를 좌표로 변환
        var geocoder = new kakao.maps.services.Geocoder();
        geocoder.addressSearch(address, function(result, status) {
            if (status === kakao.maps.services.Status.OK) {
                var coords = new kakao.maps.LatLng(result[0].y, result[0].x);
                document.getElementById('storeLatitude').value = result[0].y;
                document.getElementById('storeLongitude').value = result[0].x;
                // 주소를 좌표로 변환한 후 지도 이동
                map.setCenter(coords);
            } else {
                alert('주소를 찾을 수 없습니다.');
            }
        });
    });

    // 현재 위치로 이동하는 함수
    function moveToCurrentLocation() {
        if (map) {
            // 현재 위치를 가져와서 지도 중심으로 이동
            navigator.geolocation.getCurrentPosition(function(position) {
                var lat = position.coords.latitude;
                var lng = position.coords.longitude;
                var position = new kakao.maps.LatLng(lat, lng);
                map.setCenter(position);
                if (userMarker) {
                    userMarker.setPosition(position);
                }
            }, function() {
                alert('현재 위치를 가져올 수 없습니다.');
            });
        } else {
            console.error('Map is not initialized.');
        }
    }

    // 현재 위치로 이동 버튼 클릭 이벤트 리스너
    document.getElementById('moveToCurrentLocation').addEventListener('click', moveToCurrentLocation);

    // 상가 위치로 이동하는 함수
    window.moveToLocation = function(latitude, longitude) {
        if (map) {
            var position = new kakao.maps.LatLng(latitude, longitude);
            map.setCenter(position);
        } else {
            console.error('Map is not initialized.');
        }
    }

    // 페이지 로드 시 현재 위치를 가져와서 지도 초기화
    getCurrentPosition();

    // 상가 리스트가 업데이트되었을 때 호출되는 이벤트 리스너
    window.addEventListener('storeListUpdated', function() {
        // 상가 리스트가 업데이트되었을 때 추가 로직이 필요한 경우 여기에 작성
    });
});
