document.addEventListener('DOMContentLoaded', function() {
    const userPosition = { lat: 37.5665, lng: 126.978 }; // 기본 위치 (서울)
    const markers = []; // 마커를 저장할 배열
    const infoWindows = []; // 정보창을 저장할 배열
    let map; // 지도 객체를 저장할 변수

    // 초기화 함수
    function initialize() {
        getCurrentPosition(); // 현재 위치 가져오기
        fetchTags(); // 태그 필터 가져오기
    }

    // 현재 위치를 가져오는 함수
    function getCurrentPosition() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                function(position) {
                    userPosition.lat = position.coords.latitude;
                    userPosition.lng = position.coords.longitude;
                    initializeMap('map', userPosition.lat, userPosition.lng);
                    fetchStoreList();
                    addUserMarker(userPosition.lat, userPosition.lng);
                },
                function() {
                    console.error('Unable to retrieve your location.');
                    initializeMap('map', userPosition.lat, userPosition.lng);
                    fetchStoreList();
                }
            );
        } else {
            console.error('Geolocation is not supported by this browser.');
            initializeMap('map', userPosition.lat, userPosition.lng);
            fetchStoreList();
        }
    }

    // 태그 필터를 가져오는 함수
    function fetchTags() {
        fetch('/api/stores/tags')
            .then(response => response.json())
            .then(tags => renderTagFilters(tags))
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
            label.appendChild(checkbox);
            label.appendChild(document.createTextNode(tag));
            tagFiltersContainer.appendChild(label);
        });
    }

    // 태그 필터 변경 시 호출되는 함수
    document.getElementById('tag-filters').addEventListener('change', function(event) {
        if (event.target && event.target.classList.contains('tag-checkbox')) {
            fetchStoreList();
        }
    });

    // 상가 리스트를 서버에서 가져오는 함수
    function fetchStoreList() {
        const selectedTags = Array.from(document.querySelectorAll('.tag-checkbox:checked'))
                                .map(checkbox => checkbox.value);

        fetch('/api/stores')
            .then(response => response.json())
            .then(stores => {
                if (userPosition.lat && userPosition.lng) {
                    stores = calculateDistances(stores, userPosition);
                    stores.sort((a, b) => a.distance - b.distance);
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

    // 거리 계산 함수
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
        const dLon = toRadians(lon1 - lon2);
        const a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                  Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2)) *
                  Math.sin(dLon / 2) * Math.sin(dLon / 2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    // 도를 라디안으로 변환
    function toRadians(degrees) {
        return degrees * (Math.PI / 180);
    }

    // 상가 리스트를 렌더링하는 함수
    function renderStoreList(stores) {
	    var storeList = document.getElementById('store-list-items');
	    storeList.innerHTML = '';
	
	    stores.forEach(store => {
	        var li = document.createElement('li');
	        li.className = 'store-item';
	        li.setAttribute('data-lat', store.latitude);
	        li.setAttribute('data-lng', store.longitude);
	
	        var img = document.createElement('img');
	        img.src = store.photoUrl || '/uploads/storeimg/storeimg_placeholder.png';
	        img.alt = store.name;
	
	        var infoDiv = document.createElement('div');
	        infoDiv.className = 'store-info';
	
	        var nameElem = document.createElement('h4');
	        nameElem.textContent = store.name;
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
	
	        var tagsElem = document.createElement('p');
	        tagsElem.textContent = '태그: ' + (store.tags ? store.tags.join(', ') : '없음');
	
	        var reviewsContainer = document.createElement('div');
	        reviewsContainer.className = 'reviews-container';
	        reviewsContainer.innerHTML = `
	            <div class="review-list"></div>
	            <button class="show-more-reviews" style="display:none;">더보기</button>
	            <div class="review-form">
	                <textarea class="review-input" placeholder="리뷰 작성..."></textarea>
	                <button class="submit-review">리뷰 작성</button>
	            </div>
	        `;
	
	        infoDiv.appendChild(nameElem);
	        infoDiv.appendChild(addressElem);
	        infoDiv.appendChild(phoneElem);
	        infoDiv.appendChild(descriptionElem);
	        infoDiv.appendChild(tagsElem);
	        infoDiv.appendChild(reviewsContainer);
	
	        li.appendChild(img);
	        li.appendChild(infoDiv);
	
	        storeList.appendChild(li);
	
	        console.log('Rendering store reviews:', store.reviews); // 리뷰 데이터 로그
	
	        renderReviews(store, reviewsContainer);
	
	        var markerPosition = new kakao.maps.LatLng(store.latitude, store.longitude);
	        var marker = new kakao.maps.Marker({
	            position: markerPosition,
	            map: map
	        });
	
	        var infoWindow = new kakao.maps.InfoWindow({
	            content: `<div style="padding:5px;font-size:12px;">${store.name}</div>`,
	            removable: true
	        });
	
	        kakao.maps.event.addListener(marker, 'mouseover', function() {
	            infoWindow.open(map, marker);
	        });
	
	        kakao.maps.event.addListener(marker, 'mouseout', function() {
	            infoWindow.close();
	        });
	
	        kakao.maps.event.addListener(marker, 'click', function() {
	            moveToLocation(store.latitude, store.longitude);
	        });
	
	        markers.push(marker);
	        infoWindows.push(infoWindow);
	    });
	
	    window.dispatchEvent(new Event('storeListUpdated'));
	}

    // 리뷰를 서버에 저장하는 함수
    function submitReview(storeId, review) {
	    fetch(`/api/stores/${storeId}/reviews`, {
	        method: 'POST',
	        headers: {
	            'Content-Type': 'application/json'
	        },
	        body: JSON.stringify({ review: review })
	    })
	    .then(response => {
	        if (!response.ok) {
	            throw new Error(`HTTP error! status: ${response.status}`);
	        }
	        return response.text();
	    })
	    .then(text => {
	        console.log('Review submission response:', text); // 응답 로그
	        if (text) {
	            fetchStoreList();
	        } else {
	            console.warn('Received empty response body.');
	        }
	    })
	    .catch(error => console.error('Error submitting review:', error));
	}

    // 리뷰를 렌더링하는 함수
    function renderReviews(store, reviewsElem) {
	    const reviewListDiv = reviewsElem.querySelector('.review-list');
	    const showMoreBtn = reviewsElem.querySelector('.show-more-reviews');
	    const reviewForm = reviewsElem.querySelector('.review-form');
	    const reviewInput = reviewsElem.querySelector('.review-input');
	    const submitReviewBtn = reviewsElem.querySelector('.submit-review');
	
	    reviewListDiv.innerHTML = '';
	
	    // JSON 문자열을 객체로 파싱
	    const parsedReviews = store.reviews.map(review => {
	        try {
	            return JSON.parse(review); // JSON 문자열을 객체로 변환
	        } catch (error) {
	            console.error('Error parsing review JSON:', error);
	            return null;
	        }
	    }).filter(review => review); // 변환 실패한 경우 필터링
	
	    // 최근 작성된 2개의 리뷰만 보여줌
	    const reviewsToShow = parsedReviews.slice(0, 2);
	
	    reviewsToShow.forEach((review, index) => {
	        const reviewText = review.review || '리뷰 내용 없음'; // 리뷰 내용이 없을 경우 처리
	        const reviewElem = document.createElement('p');
	        reviewElem.textContent = `최근 리뷰 ${index + 1} : ${reviewText}`;
	        reviewListDiv.appendChild(reviewElem);
	    });
	
	    // 더보기 버튼의 동작 설정
	    if (parsedReviews.length > 2) {
	        showMoreBtn.style.display = 'block'; // 버튼을 보이게 설정
	        showMoreBtn.onclick = function() {
	            reviewListDiv.innerHTML = '';
	            parsedReviews.forEach((review, index) => {
	                const reviewText = review.review || '리뷰 내용 없음';
	                const reviewElem = document.createElement('p');
	                reviewElem.textContent = `최근 리뷰 ${index + 1} : ${reviewText}`;
	                reviewListDiv.appendChild(reviewElem);
	            });
	            showMoreBtn.style.display = 'none'; // 모든 리뷰를 표시한 후 버튼 숨김
	        };
	    } else {
	        showMoreBtn.style.display = 'none'; // 리뷰가 2개 이하일 경우 버튼 숨김
	    }
	
	    // 리뷰 제출 버튼 클릭 이벤트 핸들러
	    submitReviewBtn.onclick = function() {
	        const newReview = reviewInput.value.trim();
	        if (newReview) {
	            submitReview(store.id, newReview);
	            reviewInput.value = '';
	        }
	    };
	}

    // 현재 위치로 이동하는 함수
    function moveToLocation(lat, lng) {
        const position = new kakao.maps.LatLng(lat, lng);
        map.setCenter(position);
    }

    // 지도를 초기화하는 함수
    function initializeMap(containerId, lat, lng) {
        const mapContainer = document.getElementById(containerId);
        const mapOptions = {
            center: new kakao.maps.LatLng(lat, lng),
            level: 3
        };
        map = new kakao.maps.Map(mapContainer, mapOptions);
    }

    // 사용자 위치에 마커를 추가하는 함수
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

    // 모달 닫기 버튼 클릭 이벤트 리스너
    const modalCloseBtn = document.getElementById('modal-close-btn');
    if (modalCloseBtn) {
        modalCloseBtn.addEventListener('click', function() {
            document.getElementById('store-modal').style.display = 'none';
        });
    } else {
        console.error('Modal close button not found.');
    }

    // 상가 추가 폼 제출 이벤트 리스너
    document.getElementById('addStoreForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const tagsInput = document.getElementById('storeTags').value;
    const tagsArray = tagsInput.split(',').map(tag => tag.trim()).filter(tag => tag.length > 0);

    const formData = new FormData(this);
    formData.set('tags', JSON.stringify(tagsArray));

    // 디버깅: FormData 내용 확인
    for (let [key, value] of formData.entries()) {
        console.log(`${key}: ${value}`);
    }

    fetch(this.action, {
        method: 'POST',
        body: formData
    })
    .then(response => {
        if (response.ok) {
            return response.text(); // 서버 응답을 텍스트로 반환
        } else {
            return response.text().then(text => {
                console.error('Failed to save store. Status:', response.status);
                console.error('Response body:', text);
                throw new Error('Failed to save store.');
            });
        }
    })
    .then(text => {
        console.log('Server response:', text);
        window.location.href = '/map'; // 성공 시 페이지 이동
    })
    .catch(error => console.error('Error saving store:', error));
});

    // 현재 위치로 이동 버튼 클릭 이벤트 리스너
    const moveToCurrentLocationBtn = document.getElementById('moveToCurrentLocation');
    if (moveToCurrentLocationBtn) {
        moveToCurrentLocationBtn.addEventListener('click', function() {
            moveToLocation(userPosition.lat, userPosition.lng);
        });
    } else {
        console.error('Move to current location button not found.');
    }

    initialize(); // 초기화 호출
});
