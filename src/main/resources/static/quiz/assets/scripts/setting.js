const $quizSettingForm = document.getElementById('quizSettingForm');

$quizSettingForm.addEventListener('submit', (e) => {
    e.preventDefault();

    // const xhr = new XMLHttpRequest();
    const url = new URL(`${origin}/quiz/solve`);
    url.searchParams.set('wordGrade', $quizSettingForm['wordGrade'].value);
    url.searchParams.set('partOfSpeach', $quizSettingForm['partOfSpeach'].value);
    location.href = url.toString();
    // xhr.onreadystatechange = () => {
    //     if (xhr.readyState !== XMLHttpRequest.DONE) {
    //         return;
    //     }
    //     if (xhr.status < 200 || xhr.status >= 300) {
    //         dialog.showSimpleOk('오류', `[${xhr.status}]요청을 처리하는 도중 오류가 발생하였습니다.\n잠시 후 다시 시도해 주세요.`);
    //         return;
    //     }
    //
    // };
    // xhr.open('GET', url);
    // xhr.send();
});