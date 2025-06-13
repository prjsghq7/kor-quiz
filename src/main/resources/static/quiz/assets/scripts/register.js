const $searchForm = document.getElementById('searchForm');
const $quizTable = document.getElementById('quizTable');

const updateQuizTable = (quizDtos) => {
    console.log(quizDtos);
    const $tbody = $quizTable.querySelector(':scope > tbody');
    $tbody.innerHTML = '';
    let tbodyHtml = ``;
    for (const quizDto of quizDtos) {
        let $tr = `
        <tr>
            <td>${quizDto['quiz']['code']}</td>
            <td>${quizDto['quiz']['answer']}</td>
            <td>${quizDto['quiz']['partOfSpeach']}</td>
            <td>${quizDto['quiz']['wordGrade']}</td>
            <td>
                <button class="-object-button --green" data-show-detail>상세정보 보기</button>
            </td>
        </tr>
        <tr class="detail-row" data-target-code="${quizDto['quiz']['code']}">
            <td colspan="4">
                <div class="meanings-wrapper">`;

        const meanings = quizDto['meanings'];
        for (const meaning of meanings) {
            $tr += `
                <span class="lang">${meaning['languageName']}</span>
                <p class="def">${meaning['definition']}</p>
            `;
        }

        $tr += `
                </div>
             </td>
             <td><button class="-object-button --green">추가하기</button></td>
        </tr>`;
        tbodyHtml += $tr;
    }
    $tbody.innerHTML = tbodyHtml;

    $tbody.querySelectorAll(':scope .-object-button[data-show-detail]').forEach(($button) => {
        $button.addEventListener('click', () => {
            const code = $button.closest('tr').children[0].textContent;
            const $detailRow = $tbody.querySelector(
                `tr.detail-row[data-target-code="${code}"]`
            );
            if ($detailRow.hasAttribute('data-state-visible')) {
                $button.innerHTML = '상세정보 보기'
                $detailRow.removeAttribute('data-state-visible');
            } else {
                $button.innerHTML = '상세정보 닫기'
                $detailRow.setAttribute('data-state-visible', '');
            }
        });
    });
};

$searchForm.addEventListener('submit', (e) => {
    e.preventDefault();

    $searchForm['keyword'].setInvalid(false);
    $searchForm.querySelectorAll('.warning').forEach($warning => {
        $warning.setVisible(false);
    })

    if ($searchForm['keyword'].validity.valueMissing) {
        $searchForm['keyword'].setInvalid(true)
            .nextElementSibling.setVisible(true).innerText = '검색할 단어를 입력해주세요.';
        return;
    } else if (!$searchForm['keyword'].validity.valid) {
        $searchForm['keyword'].setInvalid(true)
            .nextElementSibling.setVisible(true).innerText = '올바른 한글 단어를 입력해주세요.';
        return;
    }
    
    const xhr = new XMLHttpRequest();
    const url = new URL(`${origin}/quiz/search-external`);
    url.searchParams.set('keyword', $searchForm['keyword'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            dialog.showSimpleOk('오류', `[${xhr.status}]요청을 처리하는 도중 오류가 발생하였습니다.\n잠시 후 다시 시도해 주세요.`);
            return;
        }
        const response = JSON.parse(xhr.responseText);
        switch (response.result) {
            case 'failure':
                dialog.showSimpleOk('경고', '알 수 없는 이유로 단어 검색에 실패 하였습니다.\n잠시 후 다시 시도해 주세요.');
                break;
            case 'success':
                updateQuizTable(response.quizDtos);
                break;
            default:
                dialog.showSimpleOk('경고', '알 수 없는 이유로 단어 검색에 실패 하였습니다.\n잠시 후 다시 시도해 주세요.');
        }
    };
    xhr.open('GET', url);
    xhr.send();
});