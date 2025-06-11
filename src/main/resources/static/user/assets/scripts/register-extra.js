const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

const $registerExtraForm = document.getElementById('registerExtraForm');
const $inputBirth = $registerExtraForm['birth'];
const $verificationList = [
    $registerExtraForm['nickname'],
    $registerExtraForm['birth'],
    $registerExtraForm['termOfService'],
]

$inputBirth.max = new Date().toISOString().split('T')[0];

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
    $registerExtraForm['nickname'].setInvalid(false)
        .nextElementSibling.setVisible(false);
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
                $registerExtraForm['nickname'].setInvalid(true)
                    .nextElementSibling.setVisible(true).innerText = '이미 사용 중인 닉네임입니다.';
                break;
            case 'success':
                // dialog.showSimpleOk('닉네임 중복 체크', '사용할 수 있는 닉네임입니다.');
                dialog.show({
                    title: '닉네임 중복 체크',
                    content: '사용할 수 있는 닉네임입니다.\n해당 닉네임으로 확정 하시겠습니까?',
                    buttons: [
                        {
                            caption: '취소',
                            onClickCallback: ($modal) => {
                                dialog.hide($modal);
                            },
                            color: 'brown'
                        }, {
                            caption: '확정',
                            onClickCallback: ($modal) => {
                                dialog.hide($modal);
                                $registerExtraForm['nickname'].setValid(true);
                                $registerExtraForm['nickname'].readOnly = true;
                                $registerExtraForm['checkNickname'].disabled = true;
                            },
                            color: 'green'
                        }
                    ]
                });
                break;
            default:
                dialog.showSimpleOk('오류', '알 수 없는 이유로 닉네임 중복체크에 실패 하였습니다.\n잠시 후 다시 시도해 주세요.');
        }
    };
    xhr.open('POST', '/user/check-nickname');
    xhr.setRequestHeader(header, token);
    xhr.send(formData);
});

$registerExtraForm.addEventListener('submit', (e) => {
    e.preventDefault();
    $verificationList.forEach(($verificationElement) => {
        if ($verificationElement.isInValid()) {
            $verificationElement.setInvalid(false);
        }
    });
    $registerExtraForm.querySelectorAll('.warning').forEach($warning => {
        $warning.setVisible(false);
    })

    if (!$registerExtraForm['nickname'].isValid()) {
        $registerExtraForm['nickname'].setInvalid(true)
            .nextElementSibling.setVisible(true).innerText = '닉네임 중복 체크를 해주세요.';
    } else if ($registerExtraForm['nickname'].validity.valueMissing) {
        $registerExtraForm['nickname'].setInvalid(true)
            .nextElementSibling.setVisible(true).innerText = '닉네임을 입력해주세요.';
    } else if (!$registerExtraForm['nickname'].validity.valid) {
        $registerExtraForm['nickname'].setInvalid(true)
            .nextElementSibling.setVisible(true).innerText = '올바른 닉네임을 입력해주세요.';
    }

    if ($registerExtraForm['birth'].validity.valueMissing) {
        $registerExtraForm['birth'].setInvalid(true)
            .nextElementSibling.setVisible(true).innerText = '생년월일을 입력해주세요.';
    } else if ($registerExtraForm['birth'].validity.rangeOverflow) {
        $registerExtraForm['birth'].setInvalid(true)
            .nextElementSibling.setVisible(true).innerText = '생년월일로 미래 날짜를 선택할 수 없습니다.';
    } else if (!$registerExtraForm['birth'].validity.valid) {
        $registerExtraForm['birth'].setInvalid(true)
            .nextElementSibling.setVisible(true).innerText = '올바른 생년월일을 입력해주세요.';
    }

    if (!$registerExtraForm['agreeTerm'].checked) {
        $registerExtraForm['agreeTerm'].closest('.input-pair')
            .querySelector('.warning')
            .setVisible(true).innerText = '서비스 이용약관에 동의해주세요.';
        $registerExtraForm['termOfService'].setInvalid(true);
    }

    let isVerificationSuccess = true;
    $verificationList.forEach(($verificationElement) => {
        if ($verificationElement.isInValid()) {
            $verificationElement.focus();
            isVerificationSuccess = false;
        }
    });
    if (!isVerificationSuccess) {
        return;
    }

    const xhr = new XMLHttpRequest();
    const formData = new FormData();
    formData.append('nickname', $registerExtraForm['nickname'].value);
    formData.append('birth', $registerExtraForm['birth'].value);
    formData.append('gender', $registerExtraForm['gender'].value);
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
            case 'failure_oauth_session_expired':
                dialog.showSimpleOk('회원가입',
                    `세션이 만료 되었습니다.\n인증을 재시도 해주세요.`,
                    () => {
                        location.href = "/"
                    });
                break;
            case 'success':
                dialog.showSimpleOk('회원가입',
                    `감사합니다.\n회원가입이 완료 되었습니다.`,
                    () => {
                        location.href = "/quiz/"
                    });
                break;
            default:
                dialog.showSimpleOk('회원가입', '알 수 없는 이유로 회원가입에 실패 하였습니다. 잠시 후 다시 시도해 주세요.');
        }
    };
    xhr.open('POST', '/user/register/extra');
    xhr.setRequestHeader(header, token);
    xhr.send(formData);
});