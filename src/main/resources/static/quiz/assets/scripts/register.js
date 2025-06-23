const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

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
            <td colspan="5">
                <form class="register-form">
                    <input type="hidden" name="code" value="${quizDto['quiz']['code']}">
                    <input type="hidden" name="answer" value="${quizDto['quiz']['answer']}">
                    <input type="hidden" name="partOfSpeach" value="${quizDto['quiz']['partOfSpeach']}">
                    <input type="hidden" name="wordGrade" value="${quizDto['quiz']['wordGrade']}">
                    <input type="hidden" name="link" value="${quizDto['quiz']['link']}">`;

        const meanings = quizDto['meanings'];
        let index = 0;
        for (const meaning of meanings) {
            $tr += `
                    <span class="lang">${meaning['languageName']}</span>
                    <p class="def">${meaning['definition']}</p>
                    <input type="hidden" name="meanings[${index}].targetCode" value="${quizDto['quiz']['code']}">
                    <input type="hidden" name="meanings[${index}].languageCode" value="${meaning['languageCode']}">
                    <input type="hidden" name="meanings[${index}].languageName" value="${meaning['languageName']}">
                    <input type="hidden" name="meanings[${index}].definition" value="${meaning['definition']}">
                    <input type="hidden" name="meanings[${index}].orderNo" value="${meaning['orderNo']}">
            `;
            index++;
        }

        $tr += `
                    <button class="-object-button --green" type="submit">추가하기</button>
                </form>
             </td>
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

    $tbody.querySelectorAll(':scope .register-form').forEach(($registerForm) => {
        $registerForm.addEventListener('submit', (e) => {
            e.preventDefault();

            const xhr = new XMLHttpRequest();
            const formData = new FormData();
            // Quiz Entity
            formData.append('quiz.code', $registerForm['code'].value);
            formData.append('quiz.answer', $registerForm['answer'].value);
            formData.append('quiz.partOfSpeach', $registerForm['partOfSpeach'].value);
            formData.append('quiz.wordGrade', $registerForm['wordGrade'].value);
            formData.append('quiz.link', $registerForm['link'].value);

            // Meaning Entities
            const $targetCodes = $registerForm.querySelectorAll('input[name$=".targetCode"]');
            const $languageCodes = $registerForm.querySelectorAll('input[name$=".languageCode"]');
            const $languageNames = $registerForm.querySelectorAll('input[name$=".languageName"]');
            const $definitions = $registerForm.querySelectorAll('input[name$=".definition"]');
            const $orderNos = $registerForm.querySelectorAll('input[name$=".orderNo"]');
            for (let i = 0; i < $targetCodes.length; i++) {
                formData.append(`meanings[${i}].targetCode`, $targetCodes[i].value);
                formData.append(`meanings[${i}].languageCode`, $languageCodes[i].value);
                formData.append(`meanings[${i}].languageName`, $languageNames[i].value);
                formData.append(`meanings[${i}].definition`, $definitions[i].value);
                formData.append(`meanings[${i}].orderNo`, $orderNos[i].value);
            }
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
                    case 'failure_duplicate_quiz':
                        dialog.showSimpleOk(`문제 추가`, '해당 단어는 이미 추가된 문제 입니다.');
                        break;
                    case 'failure':
                        dialog.showSimpleOk('오류', '알 수 없는 이유로 문제 추가에 실패 하였습니다.\n잠시 후 다시 시도해 주세요.');
                        break;
                    case 'success':
                        dialog.showSimpleOk('문제 추가', '문제 추가에 성공 하였습니다');
                        break;
                    default:
                        dialog.showSimpleOk('오류', '알 수 없는 이유로 문제 추가에 실패 하였습니다.\n잠시 후 다시 시도해 주세요.');
                }
            };
            xhr.open('POST', '/quiz/register');
            xhr.setRequestHeader(header, token);
            xhr.send(formData);
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