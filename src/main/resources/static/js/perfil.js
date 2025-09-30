(function () {
    function getToken() {
        return localStorage.getItem('token');
    }

    function jsonHeaders(opts) {
        const h = new Headers(opts?.headers || {});
        const t = getToken();
        if (t && !h.has('Authorization')) h.set('Authorization', 'Bearer ' + t);
        if (opts?.body && !(opts.body instanceof FormData) && !h.has('Content-Type')) {
            h.set('Content-Type', 'application/json');
        }
        return h;
    }

    async function perFetch(path, opts = {}) {
        const res = await fetch(path, { credentials: 'same-origin', ...opts, headers: jsonHeaders(opts) });

        const ct = (res.headers.get('Content-Type') || '').toLowerCase();
        const parse = async () => (ct.includes('application/json') ? res.json() : res.text());
        const data = await parse().catch(() => null);

        if (!res.ok) {

            const msg =
                (data && (data.error || data.message)) ||
                (res.status === 401 ? 'Faça login novamente' :
                    res.status === 403 ? 'Sem permissão' :
                        `Erro ${res.status}`);
            throw new Error(msg);
        }
        return typeof data === 'string' && ct.includes('text/') ? data : data;
    }

    const el = (id) => document.getElementById(id);
    const ui = {
        panel:   () => el('pf-panel'),
        msg:     () => el('pf-msg'),
        nome:    () => el('pf-nome'),
        email:   () => el('pf-email'),
        pass1:   () => el('pf-pass'),
        pass2:   () => el('pf-pass2'),
        display: () => el('pf-display'),
        emailSm: () => el('pf-email-small'),
    };

    function perSetReadOnly(ro) {
        const panel = ui.panel(); if (!panel) return;
        panel.classList.toggle('readonly', ro);
        panel.classList.toggle('editing', !ro);
        [ui.nome(), ui.email(), ui.pass1(), ui.pass2()].forEach(i => {
            if (!i) return;
            const isPass = i === ui.pass1() || i === ui.pass2();
            i.readOnly = ro;
            i.disabled = isPass ? ro : false;
            if (ro && isPass) i.value = '';
        });
    }

    function perFill(u) {
        if (!u) return;
        if (ui.nome())    ui.nome().value = u.nome || '';
        if (ui.email())   ui.email().value = u.email || '';
        if (ui.display()) ui.display().textContent = u.nome ? `Olá, ${u.nome}` : 'Meu perfil';
        if (ui.emailSm()) ui.emailSm().textContent = u.email || '';
    }

    async function perLoad() {
        const msg = ui.msg();
        try {
            msg && (msg.textContent = '');
            const me = await perFetch('/perfil/me');
            perFill(me);
            perSetReadOnly(true);
        } catch (err) {
            console.error('GET /perfil/me falhou:', err);
            msg && (msg.textContent = err.message || String(err));
            if ((err.message || '').includes('Faça login')) {
            }
        }
    }

    window.perToggleEdit = function (edit) { perSetReadOnly(!edit); };
    window.perCancel     = function () { perLoad(); };

    window.perSave = async function (ev) {
        ev.preventDefault();
        const msg = ui.msg();
        const nome  = (ui.nome()?.value || '').trim();
        const email = (ui.email()?.value || '').trim();
        const p1    = ui.pass1()?.value || '';
        const p2    = ui.pass2()?.value || '';

        try {
            msg && (msg.textContent = '');

            if (!nome) throw new Error('Informe seu nome.');
            if (!email || !/^\S+@\S+\.\S+$/.test(email)) throw new Error('E-mail inválido.');
            if (p1 || p2) {
                if (p1.length < 8) throw new Error('A nova senha deve ter pelo menos 8 caracteres.');
                if (p1 !== p2)     throw new Error('As senhas não conferem.');
            }
            const body = { nome, email, senha: p1 || null, confirmaSenha: p2 || null };

            const resp = await perFetch('/perfil/me', { method: 'PUT', body: JSON.stringify(body) });

            await perLoad();

            msg && (msg.textContent = (resp && (resp.message || resp.msg)) || 'Perfil atualizado!');
        } catch (err) {
            console.error('PUT /perfil/me falhou:', err);
            msg && (msg.textContent = err.message || String(err));
        }
    };

    document.addEventListener('DOMContentLoaded', () => {
        if (document.getElementById('profile-page')) {
            perLoad();
        }
    });

    window.perDeleteAccount = async function () {
        if (!confirm("Tem certeza que deseja excluir permanentemente sua conta?")) return;
        const msg = document.getElementById("pf-msg");
        try {
            await perFetch("/perfil/me", { method: "DELETE" });

            localStorage.removeItem('token');
            location.href = "/login";
        } catch (err) {
            console.error("DELETE /usuarios/me falhou:", err);
            if (msg) msg.innerHTML = `<span class="alert">Erro ao excluir conta: ${err.message || err}</span>`;
        }
    };

})();

