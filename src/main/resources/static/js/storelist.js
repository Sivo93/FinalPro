document.addEventListener('DOMContentLoaded', function() {
    var userPosition = { lat: 37.5665, lng: 126.978 }; // 기본 위치 (서울)

    // 현재 위치를 가져오는 함수
    function getCurrentPosition() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                function(position) {
                    userPosition.lat = position.coords.latitude;
                    userPosition.lng = position.coords.longitude;
                    fetchStoreList(); // 현재 위치를 가져온 후 상가 리스트를 가져옴
                },
                function() {
                    console.error('Unable to retrieve your location.');
                    fetchStoreList(); // 현재 위치를 가져오지 못해도 상가 리스트를 가져옴
                }
            );
        } else {
            console.error('Geolocation is not supported by this browser.');
            fetchStoreList(); // 위치 서비스가 지원되지 않을 경우에도 상가 리스트를 가져옴
        }
    }

    // 태그 필터를 가져오는 함수
    function fetchTags() {
        fetch('/api/stores/tags')
            .then(response => response.json())
            .then(tags => {
                // 로그를 통해 태그 데이터 확인
                console.log('Fetched tags:', tags);

                // 태그 데이터 파싱
                let parsedTags = [];

                tags.forEach(tag => {
                    try {
                        // 태그가 JSON 배열로 직렬화된 문자열인 경우, 이를 파싱
                        if (tag.startsWith('[') && tag.endsWith(']')) {
                            parsedTags = parsedTags.concat(JSON.parse(tag));
                        } else {
                            parsedTags.push(tag);
                        }
                    } catch (e) {
                        console.error('Error parsing tag:', tag, e);
                    }
                });

                // 중복 제거 및 태그 렌더링
                renderTagFilters([...new Set(parsedTags)]);
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
            checkbox.addEventListener('change', handleTagFilterChange);

            label.appendChild(checkbox);
            label.appendChild(document.createTextNode(tag));
            tagFiltersContainer.appendChild(label);
        });
    }

    // 태그 필터 변경 시 호출되는 함수
    function handleTagFilterChange() {
        fetchStoreList(); // 태그 필터 변경 시 상가 리스트를 다시 가져옴
    }

    // 상가 리스트를 서버에서 가져오는 함수
    function fetchStoreList() {
        var selectedTags = Array.from(document.querySelectorAll('.tag-checkbox:checked'))
                                .map(checkbox => checkbox.value);

        fetch('/api/stores')
            .then(response => response.json())
            .then(stores => {
                if (userPosition.lat && userPosition.lng) {
                    stores = calculateDistances(stores, userPosition);
                    stores.sort((a, b) => a.distance - b.distance); // 거리 기준으로 정렬
                }

                if (selectedTags.length > 0) {
                    stores = stores.filter(store =>
                        store.tags && store.tags.some(tag => selectedTags.includes(tag))
                    );
                }

                renderStoreList(stores);
            })
            .catch(error => console.error('Error fetching store list:', error));
    }

    // 거리 계산 함수 (Haversine Formula)
    function calculateDistances(stores, userPosition) {
        return stores.map(store => {
            const distance = getDistance(userPosition.lat, userPosition.lng, store.latitude, store.longitude);
            return { ...store, distance: distance };
        });
    }

    // 두 지점 간의 거리 계산 (Haversine Formula)
    function getDistance(lat1, lon1, lat2, lon2) {
        const R = 6371; // 지구의 반지름 (킬로미터)
        const dLat = toRadians(lat2 - lat1);
        const dLon = toRadians(lon2 - lon1);
        const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                  Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2)) *
                  Math.sin(dLon / 2) * Math.sin(dLon / 2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // 도(degree)를 라디안(radian)으로 변환
    function toRadians(degrees) {
        return degrees * (Math.PI / 180);
    }

    // 상가 리스트를 렌더링하는 함수
    function renderStoreList(stores) {
        var storeList = document.getElementById('store-list-items');
        storeList.innerHTML = ''; // 기존 리스트 초기화

        stores.forEach(store => {
            var li = document.createElement('li');
            li.className = 'store-item';
            li.setAttribute('data-lat', store.latitude);
            li.setAttribute('data-lng', store.longitude);

            // 이미지 요소 생성
            var img = document.createElement('img');
            img.src = store.photoUrl || '/uploads/storeimg/storeimg_placeholder.png'; // 기본 이미지 설정
            img.alt = store.name;

            // 상가 정보 컨테이너
            var infoDiv = document.createElement('div');
            infoDiv.className = 'store-info';

            var nameElem = document.createElement('h4');
            nameElem.textContent = store.name;
            // 상가 이름 클릭 시 지도 이동
            nameElem.addEventListener('click', function() {
                var lat = parseFloat(li.getAttribute('data-lat'));
                var lng = parseFloat(li.getAttribute('data-lng'));
                moveToLocation(lat, lng);
            });

            var addressElem = document.createElement('p');
            addressElem.textContent = '주소: ' + store.address;

            var phoneElem = document.createElement('p');
            phoneElem.textContent = '전화번호: ' + store.phoneNumber;

            var descriptionElem = document.createElement('p');
            descriptionElem.textContent = '소개: ' + store.description;

            var reviewsElem = document.createElement('p');
            reviewsElem.textContent = '리뷰: ' + store.reviews;

            // 태그 요소 생성
            var tagsElem = document.createElement('p');
            tagsElem.textContent = '태그: ' + (store.tags ? store.tags.join(', ') : '없음');

            // 정보를 infoDiv에 추가
            infoDiv.appendChild(nameElem);
            infoDiv.appendChild(addressElem);
            infoDiv.appendChild(phoneElem);
            infoDiv.appendChild(descriptionElem);
            infoDiv.appendChild(reviewsElem);
            infoDiv.appendChild(tagsElem); // 태그 추가

            // 리스트 항목에 이미지와 정보 추가
            li.appendChild(img);
            li.appendChild(infoDiv);

            // 리스트에 항목 추가
            storeList.appendChild(li);
        });

        // 상가 리스트 업데이트 이벤트 발생
        window.dispatchEvent(new Event('storeListUpdated'));
    }

    // 지도로 이동하는 함수
    function moveToLocation(lat, lng) {
        const map = new kakao.maps.Map(document.getElementById('map'), {
            center: new kakao.maps.LatLng(lat, lng),
            level: 3
        });

        const markerPosition  = new kakao.maps.LatLng(lat, lng);
        const marker = new kakao.maps.Marker({
            position: markerPosition
        });
        marker.setMap(map);
    }

    // 모달 저장 버튼 클릭 이벤트 리스너
    document.getElementById('addStoreForm').addEventListener('submit', function(event) {
        event.preventDefault(); // 폼의 기본 제출 동작을 막음

        // 태그를 쉼표로 구분하여 배열로 변환
        var tagsInput = document.getElementById('storeTags').value;
        var tagsArray = tagsInput.split(',').map(tag => tag.trim()).filter(tag => tag.length > 0);

        // FormData 객체 생성
        var formData = new FormData(this);
        formData.set('tags', JSON.stringify(tagsArray)); // 태그를 JSON 문자열로 설정

        // AJAX 요청으로 폼 데이터 전송
        fetch(this.action, {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.ok) {
                // 저장 성공 후 페이지 리다이렉션
                window.location.href = '/map';
            } else {
                console.error('Failed to save store.');
            }
        })
        .catch(error => console.error('Error saving store:', error));
    });

    // 페이지 로드 시 현재 위치를 가져와서 상가 리스트를 렌더링
    getCurrentPosition();
    fetchTags(); // 페이지 로드 시 태그 필터 가져오기
});
