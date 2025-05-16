// CSRF 전용 util.js

let CSRF_TOKEN = '';
let CSRF_HEADER = '';

/**
 * 1) 서버에서 토큰 + 헤더 이름(headerName)을 가져와 전역 변수에 저장.
 */
async function fetchCsrfToken() {
    try {
        const res = await fetch('/csrf-token', {
            method: 'GET',
            credentials: 'same-origin'
        });
        if (!res.ok) throw new Error('CSRF 토큰 조회 실패');
        const data = await res.json();
        CSRF_TOKEN = data.token;
        CSRF_HEADER = data.headerName;  // ex) "X-XSRF-TOKEN"
    } catch (e) {
        console.error(e);
    }
}

/**
 * 2) 모든 POST/PUT/DELETE 폼 제출 시
 *    숨은 input[name=_csrf] 을 자동 추가하고 값을 세팅.
 */
function setupCsrfForForms() {
    document.addEventListener('submit', e => {
        const form = e.target;
        const method = (form.getAttribute('method') || 'GET').toUpperCase();
        if (method !== 'GET') {
            let hid = form.querySelector('input[name="_csrf"]');
            if (!hid) {
                hid = document.createElement('input');
                hid.type = 'hidden';
                hid.name = '_csrf';
                form.appendChild(hid);
            }
            hid.value = CSRF_TOKEN;
        }
    });
}

/**
 * 3) fetch 래퍼: 모든 AJAX 요청에 CSRF 헤더를 자동 포함.
 */
async function fetchWithCsrf(url, options = {}) {
    options.credentials = 'same-origin';
    options.headers = {
        ...(options.headers || {}),
        [CSRF_HEADER]: CSRF_TOKEN
    };
    return fetch(url, options);
}

/**
 * 4) 초기화: 페이지 로드 시 실행
 */
async function initUtil() {
    await fetchCsrfToken();
    setupCsrfForForms();
}

window.addEventListener('load', initUtil);

// 전역 네임스페이스에 노출
window.util = {
    fetchWithCsrf
};

// UI 초기화: 세션스토리지 user 확인 후 nav toggle
function loadNavbar() {
    fetch('/assets/nav.html')
        .then(res => res.text())
        .then(html => {
            document.getElementById('nav-placeholder').innerHTML = html;

            initUI();

            document.getElementById("linkLogout")
                .addEventListener("click", logout);

            document.getElementById("linkMyPage")
                .addEventListener("click", e => {
                    e.preventDefault();
                    location.href = '/myPage';
                });

        })
        .catch(err => console.error('네비 로드 실패:', err));
}

async function initUI() {
    try {
        // 매번 최신 유저 정보 조회
        const res = await fetch('/api/v1/myPage', {
            method: 'GET',
            credentials: 'include'
        });
        if (!res.ok) throw new Error('로그인이 되지 않았습니다.');

        const user = await res.json();

        // 세션 스토리지에 최신 정보 저장
        sessionStorage.setItem('user', JSON.stringify(user));

        // 네비에 이름 반영
        document.querySelector('#navUser .nav-link').textContent = `${user.name}님`;

        // 로그인 상태 UI 표시
        showLoggedIn();
    } catch (err) {
        // 로그인 안 된 상태라면 세션 지우고 로그인 버튼만 보여주기
        sessionStorage.removeItem('user');
        showLoginOnly();
    }
}

function showLoginOnly() {
    document.getElementById("navLogin").style.display = "";
    document.getElementById("navRegister").style.display = "";
    document.getElementById("navUser").style.display = "none";
    document.getElementById("navMyPage").style.display = "none";
    document.getElementById("navLogout").style.display = "none";

}

function showLoggedIn() {
    document.getElementById("navLogin").style.display = "none";
    document.getElementById("navRegister").style.display = "none";
    document.getElementById("navUser").style.display = "";
    document.getElementById("navMyPage").style.display = "";
    document.getElementById("navLogout").style.display = "";
}

async function logout(e) {
    e.preventDefault();
    // util.fetchWithCsrf를 사용해 POST /logout 요청
    try {
        const res = await util.fetchWithCsrf('/logout', {method: 'POST'});
        if (!res.ok) console.warn('로그아웃 실패:', res.status);
    } catch (err) {
        console.error('로그아웃 에러:', err);
    }
    sessionStorage.removeItem('user');
    initUI();
    window.location.href = "/";
}

// 페이지네이션
function makePaginationHTML(listRowCount, pageLinkCount, currentPageIndex, totalListCount, htmlTargetId) {
    let targetUI = document.querySelector("#" + htmlTargetId);

    let pageCount = Math.ceil(totalListCount / listRowCount);

    let startPageIndex = 0;
    if ((currentPageIndex % pageLinkCount) == 0) { //10, 20...맨마지막
        startPageIndex = ((currentPageIndex / pageLinkCount) - 1) * pageLinkCount + 1
    } else {
        startPageIndex = Math.floor(currentPageIndex / pageLinkCount) * pageLinkCount + 1
    }

    let endPageIndex = 0;
    if ((currentPageIndex % pageLinkCount) == 0) { //10, 20...맨마지막
        endPageIndex = ((currentPageIndex / pageLinkCount) - 1) * pageLinkCount + pageLinkCount
    } else {
        endPageIndex = Math.floor(currentPageIndex / pageLinkCount) * pageLinkCount + pageLinkCount;
    }

    let prev;
    if (currentPageIndex <= pageLinkCount) {
        prev = false;
    } else {
        prev = true;
    }

    let next;
    if (endPageIndex > pageLinkCount) {
        endPageIndex = pageCount
        next = false;
    } else {
        next = true;
    }


    let paginationHTML = `<ul class="pagination justify-content-center">`;

    if (prev) {
        paginationHTML +=
            `<li class="page-item">
			     <a class="page-link" href="javascript:movePage(${startPageIndex - 1});" aria-label="Previous">
			     <span aria-hidden="true">&laquo;</span>
			     </a>
			 </li>`;
    }

    for (let i = startPageIndex; i <= endPageIndex; i++) {
        if (i == currentPageIndex) {
            paginationHTML += `<li class="page-item active"><a class="page-link" href="javascript:movePage(${i});">${i}</a></li>`;
        } else {
            paginationHTML += `<li class="page-item"><a class="page-link" href="javascript:movePage(${i});">${i}</a></li>`;
        }

    }

    if (next) {
        paginationHTML +=
            `<li class="page-item">
			     <a class="page-link" href="javascript:movePage(${endPageIndex + 1});" aria-label="Previous">
			     <span aria-hidden="true">&raquo;</span>
			     </a>
			 </li>`;
    }

    paginationHTML += `</ul>`;

    targetUI.innerHTML = paginationHTML;
}

// YYYY-MM-DD HH:mm 형식으로 날짜 출력
function formatDate(isoString) {
    const date = new Date(isoString);
    const now = new Date();
    const diff = now - date;  // 밀리초 단위

    const minutes = Math.floor(diff / (1000 * 60));
    const hours = Math.floor(diff / (1000 * 60 * 60));
    const days = Math.floor(diff / (1000 * 60 * 60 * 24));

    // 1시간 이내 -> 몇 분 전
    if (minutes < 60) {
        return `${minutes}분 전`;
    }

    // 24시간 이내 -> 몇 시간 전
    if (hours < 24) {
        return `${hours}시간 전`;
    }

    // 24시간 이상 -> YYYY-MM-DD HH:mm
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hour = String(date.getHours()).padStart(2, '0');
    const minute = String(date.getMinutes()).padStart(2, '0');

    return `${year}-${month}-${day} ${hour}:${minute}`;
}

function movePage(pageIndex) {
    PAGE = pageIndex - 1;
    CURRENT_PAGE_INDEX = pageIndex;
    postList();
}

