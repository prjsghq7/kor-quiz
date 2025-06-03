const toggle = document.createElement('button');
toggle.className = 'night-toggle -object-button --light';
toggle.innerText = '야간 모드';
document.body.appendChild(toggle);

toggle.addEventListener('click', () => {
    document.documentElement.classList.toggle('night-mode');
    toggle.innerText = document.documentElement.classList.contains('night-mode') ? '주간 모드' : '야간 모드';
});