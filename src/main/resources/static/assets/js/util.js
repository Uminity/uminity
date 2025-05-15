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
    document.getElementById("navUser").style.display = "none";
    document.getElementById("navMyPage").style.display = "none";
    document.getElementById("navLogout").style.display = "none";
}

function showLoggedIn() {
    document.getElementById("navLogin").style.display = "none";
    document.getElementById("navUser").style.display = "";
    document.getElementById("navMyPage").style.display = "";
    document.getElementById("navLogout").style.display = "";
}

async function logout(e) {
    e.preventDefault();

    // 1) 서버에 로그아웃 요청 (세션 쿠키 포함)
    try {
        const res = await fetch("/logout", {
            method: "GET",              // 또는 POST로 설정한 경우 POST
            credentials: "include"      // ← 매우 중요: 세션 쿠키(JSESSIONID)를 서버에 전달
        });
        // 로그아웃 리스폰스를 JSON으로 파싱하지 않습니다.
        if (!res.ok) {
            console.warn("서버 로그아웃 실패:", res.status);
        }
    } catch (err) {
        console.error("서버 로그아웃 에러:", err);
    }

    // 2) 클라이언트 세션정보 지우기
    sessionStorage.removeItem("user");

    // 3) UI 초기화 (Login 버튼만 보이도록)
    initUI();
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
    if (endPageIndex > pageCount) {
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

