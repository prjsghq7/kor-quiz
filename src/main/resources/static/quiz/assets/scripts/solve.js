const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

const $quizSolveForm = document.getElementById('quizSolveForm');

const $translationMeanings = $quizSolveForm.querySelector(':scope > .meanings-wrapper.translation');
$quizSolveForm['translateToggle'].addEventListener('change', () => {
    if ($quizSolveForm['translateToggle'].checked) {
        getTranslationMeanings();
    } else {
        $translationMeanings.setVisible(false);
        $translationMeanings.innerHTML = '';
    }
});

$quizSolveForm['languageCode'].addEventListener('change', () => {
    if ($quizSolveForm['translateToggle'].checked) {
        getTranslationMeanings();
    }
});

const getTranslationMeanings = () => {
    const xhr = new XMLHttpRequest();
    const url = new URL(`${origin}/quiz/translate-meanings`);
    url.searchParams.set('targetCode', $quizSolveForm['code'].value);
    url.searchParams.set('languageCode', $quizSolveForm['languageCode'].value);
    xhr.onreadystatechange = () => {
        if (xhr.readyState !== XMLHttpRequest.DONE) {
            return;
        }
        if (xhr.status < 200 || xhr.status >= 300) {
            dialog.showSimpleOk('오류', `[${xhr.status}]요청을 처리하는 도중 오류가 발생하였습니다.\n잠시 후 다시 시도해 주세요.`);
            $translationMeanings.setVisible(false);
            $quizSolveForm['translateToggle'].checked = false;
            return;
        }
        const meanings = JSON.parse(xhr.responseText);
        if (meanings.length === 0) {
            dialog.showSimpleOk('번역 보기', '번역 정보가 제공되지 않거나 문제 출제 당시 오류가 발생하였습니다.\n관리자에게 문의 부탁드립니다.');
            $translationMeanings.setVisible(false);
            $quizSolveForm['translateToggle'].checked = false;
            return;
        }

        $translationMeanings.innerHTML = '';
        let meaningHtml = '';
        for (const meaning of meanings) {
            meaningHtml += `
                <div class="meaning">
                    <span class="lang">${meaning.languageName} [${meaning.orderNo}]</span>
                    <p class="def">${meaning.definition}</p>
                </div>`;
        }
        $translationMeanings.innerHTML = meaningHtml;
        $translationMeanings.setVisible(true);
    };
    xhr.open('GET', url);
    xhr.send();
};

$quizSolveForm.addEventListener('submit', (e) => {
    e.preventDefault();

    if ($quizSolveForm['answer'].validity.valueMissing) {
        dialog.showSimpleOk('경고', '정답을 입력해 주세요.');
        return;
    } else if (!$quizSolveForm['answer'].validity.valid) {
        dialog.showSimpleOk('경고', '올바른 정답을 입력해 주세요.\n[1 ~ 30자리 한글]');
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('answer', $quizSolveForm['answer'].value);
    formData.append('code', $quizSolveForm['code'].value);
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
                dialog.showSimpleOk('경고', '알 수 없는 이유로 제출에 실패 하였습니다.\n잠시 후 다시 시도해 주세요.');
                break;
            case 'failure_session_expired':
                dialog.showSimpleOk('경고',
                    `세션이 만료 되었습니다.\n인증을 재시도 해주세요.`,
                    () => {
                        location.href = "/"
                    });
                break;
            case 'success':
                updateResultArea(response.quizResult);
                break;
            default:
                dialog.showSimpleOk('경고', '알 수 없는 이유로 제출에 실패 하였습니다.\n잠시 후 다시 시도해 주세요.');
        }
    };
    xhr.open('POST', '/quiz/solve');
    xhr.setRequestHeader(header, token);
    xhr.send(formData);
});

const updateResultArea = (quizResult) => {
    const $resultArea = document.getElementById('resultArea');
    const $quizSettingForm = document.getElementById('quizSettingForm');
    const $answerArea = $quizSolveForm.querySelector(':scope > .answer-area');

    const isCorrectClass = quizResult.correct === true ? 'correct' : 'incorrect';
    const resultMsg = quizResult.correct === true ? '정답입니다!' : '오답입니다';
    console.log(quizResult);
    console.log(quizResult.correct);
    console.log(isCorrectClass);
    console.log(resultMsg);
    $resultArea.innerHTML = `
        <span class="result-status ${isCorrectClass}">${resultMsg}</span>
        <div class="result-grid">
            <div class="result-column">
                <span class="result-label">입력값</span>
                <span class="result-value">${quizResult.userAnswer}</span>
            </div>
            <div class="result-column">
                <span class="result-label">정답</span>
                <span class="result-value">${quizResult.correctAnswer}</span>
            </div>
        </div>

        <div class="result-footer">
            <a href="${quizResult.link}" target="_blank" rel="noopener" class="dict-link">정답 단어 상세 보기</a>
        </div>`;

    $resultArea.setVisible(true);
    $quizSettingForm.setVisible(true);
    $answerArea.setVisible(false);
}