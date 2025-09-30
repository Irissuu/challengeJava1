const API = {
    login:  "/auth/login",
    logout: "/auth/logout",
    me:     "/auth/me",
    motos:  "/motos",
    vagas:  "/vagas",
    usuarios: "/usuarios"
};

document.addEventListener("DOMContentLoaded", () => {
    const form = document.getElementById("cadastro-form");
    const msg  = document.getElementById("cadastro-msg");

    async function doRegister(ev){
        ev.preventDefault();
        msg.textContent = "";

        const nome  = document.getElementById("nome").value.trim();
        const email = document.getElementById("email").value.trim();
        const senha = document.getElementById("senha").value;
        const conf  = document.getElementById("confirmar").value;
        const role  = (document.querySelector('input[name="perfil"]:checked') || {}).value || "NONE";

        try {
            if (!nome) throw new Error("Informe seu nome.");
            if (!/\S+@\S+\.\S+/.test(email)) throw new Error("E-mail inválido.");
            if (!senha || senha.length < 8) throw new Error("A senha deve ter pelo menos 8 caracteres.");
            if (senha !== conf) throw new Error("As senhas não conferem.");

            const res = await fetch("/auth/register", {
                method: "POST",
                headers: { "Content-Type": "application/json", "Accept": "application/json" },
                credentials: "same-origin",
                body: JSON.stringify({ nome, email, senha, role }) // role pode ser NONE
            });

            const ct   = (res.headers.get("Content-Type") || "").toLowerCase();
            const data = ct.includes("json") ? await res.json().catch(()=>null) : await res.text().catch(()=>null);

            if (!res.ok) {
                const txt = (res.status === 409) ? "E-mail já cadastrado"
                    : (data && (data.error || data.message)) || `Erro ${res.status}`;
                throw new Error(txt);
            }

            msg.innerHTML = '<span class="badge">Conta criada! Faça login.</span>';
            setTimeout(() => location.href = "/login", 600);
        } catch (err) {
            msg.innerHTML = `<span class="alert">${err.message || err}</span>`;
        }
    }

    if (form) {
        form.addEventListener("submit", doRegister);
    }
});


async function apiFetch(path, opts = {}) {
    const headers = new Headers(opts.headers || {});
    if (!headers.has("Content-Type") && !(opts.body instanceof FormData)) {
        headers.set("Content-Type", "application/json");
    }

    const res = await fetch(path, { credentials: "include", ...opts, headers });

    if (res.status === 401) {
        if (!location.pathname.startsWith("/login")) location.href = "/login";
        throw new Error("Não autenticado");
    }
    if (res.status === 403) {
        if (location.pathname !== "/negado") location.href = "/negado";
        throw new Error("Acesso negado");
    }

    if (!res.ok) {
        const ct  = (res.headers.get("Content-Type") || "").toLowerCase();
        const msg = ct.includes("application/json")
            ? await res.json().then(j => j.message || j.error || JSON.stringify(j))
            : await res.text();
        throw new Error(msg || ("Erro HTTP " + res.status));
    }

    const ctOk = (res.headers.get("Content-Type") || "").toLowerCase();
    return ctOk.includes("application/json") ? res.json() : res.text();
}

async function doLogin(e) {
    e.preventDefault();
    const email = document.querySelector("#email").value.trim();
    const senha = document.querySelector("#senha").value;
    const msg   = document.getElementById("login-msg");
    const btn   = document.getElementById("btn-login");

    try {
        btn && (btn.disabled = true);
        msg && (msg.textContent = "");

        const res = await fetch(API.login, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            credentials: "include",
            body: JSON.stringify({ email, senha })
        });

        if (!res.ok) {
            const err = await (res.headers.get("Content-Type")||"").includes("json")
                ? res.json().then(j => j.message || j.error || JSON.stringify(j))
                : res.text();
            throw new Error(err || ("HTTP " + res.status));
        }

        location.href = "/home";
    } catch (ex) {
        msg && (msg.innerHTML = `<span class="alert">Falha no login: ${ex.message || ex}</span>`);
    } finally {
        btn && (btn.disabled = false);
    }
}

async function doLogout() {
    try { await fetch(API.logout, { method:"POST", credentials:"include" }); }
    finally { location.href = "/login"; }
}

async function loadUserBadge() {
    const badge = document.querySelector("#user-badge");
    if (!badge) return;
    try {
        const me = await apiFetch(API.me);
        badge.innerHTML = `<span class="badge">${me.email || "usuário"}</span>`;
    } catch {}
}

async function loadUsuarios(){
    const data = await apiFetch(API.usuarios);
    const tbody = document.querySelector("#tbody-usuarios");
    if (!tbody) return;
    tbody.innerHTML = "";
    (data.content || data).forEach(u => {
        const roles = Array.isArray(u.roles) ? u.roles.join(", ") : (u.roles ?? "");
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${u.id ?? ""}</td>
      <td>${u.nome ?? ""}</td>
      <td>${u.email ?? ""}</td>
      <td>${roles}</td>`;
        tbody.appendChild(tr);
    });
}

const pf = {
    panel:   () => document.getElementById("pf-panel"),
    form:    () => document.getElementById("pf-form"),
    msg:     () => document.getElementById("pf-msg"),
    nome:    () => document.getElementById("pf-nome"),
    email:   () => document.getElementById("pf-email"),
    pass1:   () => document.getElementById("pf-pass"),
    pass2:   () => document.getElementById("pf-pass2"),
    avatar:  () => document.getElementById("pf-avatar"),
    file:    () => document.getElementById("pf-file"),
    display: () => document.getElementById("pf-display"),
    emailSm: () => document.getElementById("pf-email-small"),
};

function pfSetReadOnly(ro){
    const panel = pf.panel();
    if (!panel) return;
    panel.classList.toggle("readonly", ro);
    panel.classList.toggle("editing", !ro);

    [pf.nome(), pf.email(), pf.pass1(), pf.pass2()].forEach(i=>{
        if (!i) return;
        if (i.id.startsWith("pf-pass")) {
            i.readOnly = ro; i.disabled = ro;
            if (ro) i.value = "";
        } else {
            i.readOnly = ro; i.disabled = false;
        }
    });
}

function pfFill(u){
    pf.nome().value  = u.nome  || "";
    pf.email().value = u.email || "";
    pf.display().textContent = u.nome  || "Meu perfil";
    pf.emailSm().textContent  = u.email || "";
    if (u.avatarUrl) pf.avatar().src = u.avatarUrl;

    const roleEl = document.getElementById("pf-role-line");
    if (roleEl) {
        let raw = u.role ?? u.perfil ?? u.papel ?? u.funcao ??
            (Array.isArray(u.authorities) ? u.authorities[0]?.authority : null);

        raw = String(raw || "NONE").toUpperCase();
        if (raw.startsWith("ROLE_")) raw = raw.slice(5);

        roleEl.textContent = `Função: ${raw}`;
    }
}

async function pfLoad(){
    try{
        try{
            const me = await apiFetch("/perfil/me");
            pfFill(me);
        }catch(_){
            const me = await apiFetch(API.me); // /auth/me
            pfFill({ nome:"", email: me.email, avatarUrl:"" });
        }
        pfSetReadOnly(true);
        pf.msg().innerHTML = "";
    }catch(err){
        pf.msg().innerHTML = `<span class="alert">${err.message||err}</span>`;
    }
}

function pfToggleEdit(edit){
    pfSetReadOnly(!edit);
}

function pfCancel(){
    pfLoad();
}

async function pfSave(ev){
    ev.preventDefault();
    const msg = pf.msg();
    const nome  = pf.nome().value.trim();
    const email = pf.email().value.trim();
    const p1    = pf.pass1().value;
    const p2    = pf.pass2().value;

    try{
        msg.innerHTML = "";
        if (!nome)  throw new Error("Informe seu nome.");
        if (!email || !/^\S+@\S+\.\S+$/.test(email)) throw new Error("E-mail inválido.");

        if (p1 || p2){
            if (p1.length < 8) throw new Error("A nova senha deve ter pelo menos 8 caracteres.");
            if (p1 !== p2)     throw new Error("As senhas não conferem.");
        }

        const body = { nome, email };
        if (p1) body.novaSenha = p1;

        const updated = await apiFetch("/perfil/me", { method:"PUT", body: JSON.stringify(body) });

        pfSetReadOnly(true);
        msg.innerHTML = `<span class="badge">Perfil atualizado!</span>`;
    }catch(err){
        msg.innerHTML = `<span class="alert">${err.message||err}</span>`;
    }
}

async function pfDeleteAccount(){
    if (!confirm("Tem certeza que deseja excluir PERMANENTEMENTE sua conta?")) return;
    const msg = pf.msg();
    try{
        await apiFetch("/perfil/me", { method:"DELETE" });
        await doLogout();
    }catch(err){
        msg.innerHTML = `<span class="alert">Erro ao excluir conta: ${err.message||err}</span>`;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    loadUserBadge();

    const fLogin = document.getElementById("login-form");
    if (fLogin) {
        fLogin.removeEventListener("submit", doLogin);
        fLogin.addEventListener("submit", doLogin);
    }

    if (document.getElementById("tbody-motos"))    loadMotos();
    if (document.getElementById("tbody-vagas"))    loadVagas();
    if (document.getElementById("tbody-usuarios")) loadUsuarios();

    if (document.getElementById("profile-page"))   pfLoad();

});

function getVagaQueryId() {
    const m = location.search.match(/[?&]id=(\d+)/);
    return m ? Number(m[1]) : null;
}

function presetVagaIdHidden() {
    const id = getVagaQueryId();
    const hidden = document.getElementById("vaga-id");
    if (id && hidden && !hidden.value) hidden.value = String(id);
}

async function loadVagas() {
    const data  = await apiFetch(API.vagas);
    const tbody = document.querySelector("#tbody-vagas");
    if (!tbody) return;

    const list = data.content || data;
    tbody.innerHTML = "";

    if (!Array.isArray(list) || !list.length) {
        tbody.innerHTML = `<tr><td colspan="5">Nenhuma vaga cadastrada.</td></tr>`;
        return;
    }

    list.forEach(v => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${v.id ?? ""}</td>
      <td>${v.status ?? ""}</td>
      <td>${v.numero ?? ""}</td>
      <td>${v.patio ?? ""}</td>
      <td style="white-space:nowrap;display:flex;gap:8px;">
        <a class="btn btn-small" href="/vagas/form?id=${encodeURIComponent(v.id)}" title="Editar">✏️</a>
        <button class="btn btn-small btn-danger" onclick="deleteVaga(${v.id})" title="Excluir">🗑️</button>
      </td>`;
        tbody.appendChild(tr);
    });
}

async function submitVaga(ev) {
    ev.preventDefault();

    const idHidden = (document.getElementById("vaga-id")?.value || "").trim();
    const idQuery  = getVagaQueryId();
    const id       = idHidden || (idQuery ? String(idQuery) : "");

    const status = document.getElementById("vaga-status").value;
    const numero = Number(document.getElementById("vaga-numero").value);
    const patio  = document.getElementById("vaga-patio").value;

    const selMotos = document.getElementById("vaga-motos");
    const motos = selMotos
        ? Array.from(selMotos.selectedOptions).map(opt => ({ id: opt.value }))
        : undefined;

    if (!numero || Number.isNaN(numero) || numero < 1) {
        alert("Informe um número de vaga válido.");
        return;
    }
    if (!patio || !patio.trim()) {
        alert("Informe o pátio da vaga.");
        return;
    }

    const body = { status, numero, patio };
    if (motos) body.motos = motos;

    try {
        if (id) {
            await apiFetch(`${API.vagas}/${id}`, {
                method: "PUT",
                body: JSON.stringify(body)
            });
        } else {
            await apiFetch(API.vagas, {
                method: "POST",
                body: JSON.stringify(body)
            });
        }
        location.href = "/vagas/list";
    } catch (err) {
        alert("Erro ao salvar vaga: " + err.message);
    }
}

async function deleteVaga(id) {
    if (!confirm("Deseja realmente excluir esta vaga?")) return;
    try {
        await apiFetch(`${API.vagas}/${id}`, { method: "DELETE" });

        if (typeof loadVagas === "function") loadVagas();
    } catch (err) {
        alert("Erro ao excluir vaga: " + err.message);
    }
}

function getMotoQueryId() {
    const m = location.search.match(/[?&]id=(\d+)/);
    return m ? Number(m[1]) : null;
}

function presetMotoIdHidden() {
    const id = getMotoQueryId();
    const hidden = document.getElementById("moto-id");
    if (id && hidden && !hidden.value) hidden.value = String(id);
}

async function submitMoto(ev) {
    ev.preventDefault();

    const idHidden = (document.getElementById("moto-id")?.value || "").trim();
    const idQuery  = getMotoQueryId();
    const id       = idHidden || (idQuery ? String(idQuery) : "");

    const placa  = (document.getElementById("moto-placa").value || "").trim().toUpperCase();
    const marca  = (document.getElementById("moto-marca").value || "").trim();
    const modelo = (document.getElementById("moto-modelo").value || "").trim();
    const anoStr = (document.getElementById("moto-ano").value || "").trim();
    const ano    = Number(anoStr);

    if (!placa || placa.length !== 7) {
        alert("A placa deve ter exatamente 7 caracteres."); return;
    }
    if (!marca)  { alert("Informe a marca.");  return; }
    if (!modelo) { alert("Informe o modelo."); return; }
    if (!ano || Number.isNaN(ano) || ano < 1990 || ano > 2100) {
        alert("Informe um ano válido (1990–2100)."); return;
    }

    const body = { placa, marca, modelo, ano };

    try {
        if (id) {
            await apiFetch(`${API.motos}/${id}`, { method: "PUT", body: JSON.stringify(body) });
        } else {
            await apiFetch(API.motos, { method: "POST", body: JSON.stringify(body) });
        }
        location.href = "/motos/list";
    } catch (err) {
        alert("Erro ao salvar moto: " + (err.message || err));
    }
}

async function deleteMoto(id) {
    if (!confirm("Deseja realmente excluir esta moto?")) return;
    try {
        await apiFetch(`${API.motos}/${id}`, { method: "DELETE" });
        if (typeof loadMotos === "function") loadMotos();
    } catch (err) {
        alert("Erro ao excluir moto: " + (err.message || err));
    }
}

async function loadMotos() {
    const data  = await apiFetch(API.motos);
    const tbody = document.querySelector("#tbody-motos");
    if (!tbody) return;

    const list = data?.content || data;
    tbody.innerHTML = "";

    if (!Array.isArray(list) || !list.length) {
        tbody.innerHTML = `<tr><td colspan="5">Nenhuma moto cadastrada.</td></tr>`;
        return;
    }

    list.forEach(m => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${m.id ?? ""}</td>
      <td>${m.placa ?? ""}</td>
      <td>${m.modelo ?? ""}</td>
      <td>${m.ano ?? ""}</td>   <!-- novo -->
      <td style="white-space:nowrap;display:flex;gap:8px;">
        <a class="btn btn-small" href="/motos/form?id=${encodeURIComponent(m.id)}" title="Editar">✏️</a>
        <button class="btn btn-small btn-danger" onclick="deleteMoto(${m.id})" title="Excluir">🗑️</button>
      </td>`;
        tbody.appendChild(tr);
    });
}

document.addEventListener("DOMContentLoaded", () => {
    if (document.getElementById("moto-form")) presetMotoIdHidden();
});
