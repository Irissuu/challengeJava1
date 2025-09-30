async function loginSubmit(event) {
    event.preventDefault();

    const email = document.querySelector('#email').value.trim();
    const senha = document.querySelector('#senha').value;

    const res = await fetch('/auth/login', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, senha })
    });

    if (!res.ok) {
        const msg = await safeErrorMessage(res);
        showToast(`Falha no login: ${msg || res.status}`);
        return;
    }

    let token = null;
    const ct = (res.headers.get('content-type') || '').toLowerCase();

    if (ct.includes('application/json')) {
        try {
            const data = await res.json();
            token = data.token || data.access_token || data.jwt || null;
        } catch (_) {  }
    }

    if (!token) {
        const auth = res.headers.get('Authorization') || res.headers.get('authorization');
        if (auth && auth.toLowerCase().startsWith('bearer ')) {
            token = auth.slice(7);
        }
    }

    if (!token && !ct.includes('application/json')) {
        try {
            const text = (await res.text()).trim();
            if (/^[\w-]+\.[\w-]+\.[\w-]+$/.test(text)) token = text;
        } catch (_) {}
    }

    if (!token) {
        showToast('Falha no login: Resposta sem token');
        return;
    }

    localStorage.setItem('jwt', token);

    const meOk = await quickValidateToken(token);
    if (!meOk) {
        showToast('Token inválido');
        return;
    }

    window.location.href = '/home';
}

async function quickValidateToken(token) {
    try {
        const r = await fetch('/auth/me', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        return r.ok;
    } catch (e) {
        return false;
    }
}

async function safeErrorMessage(res) {
    try {
        const ct = res.headers.get('content-type') || '';
        if (ct.includes('application/json')) {
            const j = await res.json();
            return (j.message || j.error || JSON.stringify(j));
        }
        return (await res.text()).slice(0, 300);
    } catch { return ''; }
}

function showToast(msg) {
    const el = document.querySelector('#toast');
    if (el) { el.textContent = msg; el.classList.remove('hidden'); }
    else alert(msg);
}

document.addEventListener('DOMContentLoaded', () => {
    const form = document.querySelector('#form-login');
    if (form) form.addEventListener('submit', loginSubmit);
});
