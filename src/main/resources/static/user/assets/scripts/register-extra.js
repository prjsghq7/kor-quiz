const $registerExtraForm = document.getElementById('registerExtraForm');
const $inputBirth = $registerExtraForm['birth'];

$inputBirth.addEventListener('focus', () => {
    if (!$inputBirth.value) {
        const today = new Date();
        const defaultValue = new Date(today.setFullYear(today.getFullYear() - 10)).toISOString().slice(0, 10);
        $inputBirth.value = defaultValue;
        $inputBirth.dataset.tempValue = defaultValue;
    }
});

$inputBirth.addEventListener('focusout', () => {
    if ($inputBirth.dataset.tempValue === $inputBirth.value) {
        $inputBirth.value = '';
        delete $inputBirth.dataset.tempValue;
    }
});

$registerExtraForm['checkNickname'].addEventListener('click', () => {
    if ($registerExtraForm['nickname'].validity.valueMissing) {
        $registerExtraForm['nickname'].setInvalid(true)
            .nextElementSibling.setVisible(true).innerText = '닉네임을 입력해주세요.';
        return;
    } else if (!$registerExtraForm['nickname'].validity.valid) {
        $registerExtraForm['nickname'].setInvalid(true)
            .nextElementSibling.setVisible(true).innerText = '올바른 닉네임을 입력해주세요.';
        return;
    }
    
    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('nickname', $registerExtraForm['nickname'].value);
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
                $registerExtraForm['nickname'].setInvalid(true).nextElementSibling.innerText = '이미 사용 중인 닉네임입니다.';
                break;
            case 'success':
                $registerExtraForm['nickname'].setValid(true).nextElementSibling.innerText = ' 사용할 수 있는 닉네임입니다.';
                break;
        }
    };
    xhr.open('POST', '/user/check-nickname');
    xhr.send(formData);
});

$registerExtraForm.addEventListener('submit', (e) => {
    e.preventDefault();

    $registerExtraForm['nickname']
});