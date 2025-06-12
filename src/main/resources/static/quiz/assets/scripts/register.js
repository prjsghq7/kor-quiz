const $searchForm = document.getElementById('searchForm');

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
                alert("GOOD");
                break;
            default:
                dialog.showSimpleOk('경고', '알 수 없는 이유로 단어 검색에 실패 하였습니다.\n잠시 후 다시 시도해 주세요.');
        }
    };
    xhr.open('GET', url);
    xhr.send();
});