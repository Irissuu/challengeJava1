window.READ_ONLY_PAGE = true;

(function injectReadonlyCss(){
    const css = `
    #table-motos thead th:nth-child(n+5), #table-motos tbody td:nth-child(n+5),
    #table-vagas thead th:nth-child(n+5), #table-vagas tbody td:nth-child(n+5) { display: none !important; }
    #btn-nova-moto, #btn-nova-vaga { display: none !important; }
  `;
    const style = document.createElement("style");
    style.textContent = css;
    document.head.appendChild(style);
})();

async function getJSON(url) {
    const res = await fetch(url, { credentials: "include", headers: { "Accept": "application/json" } });
    if (res.status === 401) { location.href = "/login";  throw new Error("401"); }
    if (res.status === 403) { location.href = "/negado"; throw new Error("403"); }
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
}
function toArrayMaybePage(data) { return Array.isArray(data) ? data : (data?.content ?? []); }
function clear(el){ while (el && el.firstChild) el.removeChild(el.firstChild); }

function hardenReadonly(tableId) {
    const table = document.getElementById(tableId);
    if (!table) return;

    table.querySelector('thead th[data-col="acoes"]')?.remove();

    table.querySelectorAll("tbody tr").forEach(tr => {
        const last = tr.lastElementChild;
        if (last) last.remove();
    });

    table.addEventListener("click", (ev) => {
        const a = ev.target.closest("a,button");
        if (!a) return;
        const href = a.getAttribute("href") || "";
        const txt  = (a.textContent || "").toLowerCase();
        const danger = href.includes("/form") || href.includes("/delete") || /exclu|edit|editar|delete/.test(txt);
        if (danger) {
            ev.preventDefault();
            ev.stopPropagation();
        }
    }, true);
}

async function viewLoadMotos() {
    const tbody = document.getElementById("tbody-motos");
    if (!tbody) return;
    clear(tbody);
    try {
        const data = await getJSON("/motos");
        const arr = toArrayMaybePage(data);
        if (!arr.length) { tbody.innerHTML = `<tr><td colspan="4">Nenhuma moto.</td></tr>`; return; }
        arr.forEach(m => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
        <td>${m.id ?? ""}</td>
        <td>${m.placa ?? ""}</td>
        <td>${m.modelo ?? ""}</td>
        <td>${m.ano ?? ""}</td>`;
            tbody.appendChild(tr);
        });
    } catch {
        tbody.innerHTML = `<tr><td colspan="4">Falha ao carregar motos</td></tr>`;
    }
    hardenReadonly("table-motos");
}
async function viewBuscarMotoPorId() {
    const id = document.getElementById("moto-id")?.value?.trim();
    const tbody = document.getElementById("tbody-motos");
    if (!tbody || !id) return;
    clear(tbody);
    try {
        const m = await getJSON(`/motos/${id}`);
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${m.id ?? ""}</td>
      <td>${m.placa ?? ""}</td>
      <td>${m.modelo ?? ""}</td>
      <td>${m.ano ?? ""}</td>`;
        tbody.appendChild(tr);
    } catch {
        tbody.innerHTML = `<tr><td colspan="4">Moto ${id} não encontrada</td></tr>`;
    }
    hardenReadonly("table-motos");
}

async function viewLoadVagas() {
    const tbody = document.getElementById("tbody-vagas");
    if (!tbody) return;
    clear(tbody);
    try {
        const data = await getJSON("/vagas");
        const arr = toArrayMaybePage(data);
        if (!arr.length) { tbody.innerHTML = `<tr><td colspan="4">Nenhuma vaga.</td></tr>`; return; }
        arr.forEach(v => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
        <td>${v.id ?? ""}</td>
        <td>${v.status ?? ""}</td>
        <td>${v.numero ?? ""}</td>
        <td>${v.patio ?? ""}</td>`;
            tbody.appendChild(tr);
        });
    } catch {
        tbody.innerHTML = `<tr><td colspan="4">Falha ao carregar vagas</td></tr>`;
    }
    hardenReadonly("table-vagas");
}
async function viewBuscarVagaPorId() {
    const id = document.getElementById("vaga-id")?.value?.trim();
    const tbody = document.getElementById("tbody-vagas");
    if (!tbody || !id) return;
    clear(tbody);
    try {
        const v = await getJSON(`/vagas/${id}`);
        const tr = document.createElement("tr");
        tr.innerHTML = `
      <td>${v.id ?? ""}</td>
      <td>${v.status ?? ""}</td>
      <td>${v.numero ?? ""}</td>
      <td>${v.patio ?? ""}</td>`;
        tbody.appendChild(tr);
    } catch {
        tbody.innerHTML = `<tr><td colspan="4">Vaga ${id} não encontrada</td></tr>`;
    }
    hardenReadonly("table-vagas");
}

window.loadMotos = viewLoadMotos;
window.loadVagas = viewLoadVagas;

document.addEventListener("DOMContentLoaded", () => {
    if (document.getElementById("tbody-motos")) {
        viewLoadMotos();
        document.getElementById("btn-buscar-moto")?.addEventListener("click", viewBuscarMotoPorId);
        document.getElementById("btn-limpar-moto")?.addEventListener("click", () => {
            const i = document.getElementById("moto-id"); if (i) i.value = "";
            viewLoadMotos();
        });
    }
    if (document.getElementById("tbody-vagas")) {
        viewLoadVagas();
        document.getElementById("btn-buscar-vaga")?.addEventListener("click", viewBuscarVagaPorId);
        document.getElementById("btn-limpar-vaga")?.addEventListener("click", () => {
            const i = document.getElementById("vaga-id"); if (i) i.value = "";
            viewLoadVagas();
        });
    }
});
