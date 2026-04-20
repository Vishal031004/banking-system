let currentUser = JSON.parse(localStorage.getItem('currentUser'));
let notificationLogs = [];

if(!currentUser || currentUser.role !== 'ADMIN') {
    // If not admin, or missing, force logic. For demo purposes we can just mock admin if missing
    if(!currentUser) {
        // Fallback for presentation
        currentUser = { email: 'admin@finance.com', role: 'ADMIN' };
    } else {
        window.location.href = 'login.html';
    }
}
document.getElementById('sidebar-email').innerText = currentUser.email;

function navigateTo(pageId, navElement) {
    document.querySelectorAll('.page').forEach(el => el.classList.remove('active'));
    document.getElementById(pageId).classList.add('active');
    document.querySelectorAll('.nav-links li').forEach(el => el.classList.remove('active'));
    navElement.classList.add('active');
}

function logout() {
    localStorage.removeItem('currentUser');
    window.location.href = 'login.html';
}

function showToast(message) {
    const wrapper = document.getElementById('toast-wrapper');
    const toast = document.createElement('div');
    toast.className = 'toast';
    toast.innerText = message;
    wrapper.appendChild(toast);
    setTimeout(() => toast.remove(), 3500);

    notificationLogs.unshift({ time: new Date().toLocaleTimeString(), msg: message });
    document.getElementById('notif-badge').innerText = notificationLogs.length;
    let html = '';
    notificationLogs.forEach(log => {
        html += `<div class="event-log"><span class="event-time">${log.time}</span><span>${log.msg}</span></div>`;
    });
    document.getElementById('notif-list').innerHTML = html;
}

function toggleNotifications() {
    document.getElementById('notif-dropdown').classList.toggle('hidden');
}

async function refreshData() {
    try {
        // Fetch all accounts to find UNVERIFIED
        const allRes = await fetch(`/api/app/accounts/all`);
        if (!allRes.ok) throw new Error('Server error or session lost. Please reload.');
        const allAccs = await allRes.json();
        const kycT = document.getElementById('kycTableBody');
        kycT.innerHTML = '';
        allAccs.filter(a => a.status === 'UNVERIFIED').forEach(a => {
            let docStr = a.kycDocuments && a.kycDocuments.length > 0 ? a.kycDocuments[a.kycDocuments.length - 1] : "No Docs";
            kycT.innerHTML += `<tr>
                <td>SYSTEM_USER</td>
                <td>${a.accountNumber}</td>
                <td>${a.accountType}</td>
                <td><code>${docStr}</code></td>
                <td><button onclick="verifyAccount('${a.accountNumber}')" style="padding:5px 10px; background:#16a34a; color:white; border:none; border-radius:4px; cursor:pointer;">VERIFY KYC DATA</button></td>
            </tr>`;
        });

        // Fetch all loans
        const loanRes = await fetch(`/api/app/loans/all`);
        const allLoans = await loanRes.json();
        const loanT = document.getElementById('loansTableBody');
        loanT.innerHTML = '';
        allLoans.filter(l => l.status === 'SUBMITTED' || l.status === 'PENDING_MANAGER').forEach(l => {
            loanT.innerHTML += `<tr>
                <td>${l.applicationId.substring(0,8)}</td>
                <td>${l.purpose}</td>
                <td>$${l.loanAmount}</td>
                <td>
                    <button onclick="reviewLoan('${l.applicationId}', true)" style="padding:5px 10px; background:#16a34a; color:white; border:none; border-radius:4px; cursor:pointer; margin-right:5px;">APPROVE</button>
                    <button onclick="reviewLoan('${l.applicationId}', false)" style="padding:5px 10px; background:#dc2626; color:white; border:none; border-radius:4px; cursor:pointer;">REJECT</button>
                </td>
            </tr>`;
        });
    } catch (err) {
        showToast('⚠️ ' + err.message);
    }
}

async function verifyAccount(id) {
    await fetch(`/api/app/admin/accounts/${id}/verify`, { method: 'POST' });
    showToast(`Account ${id} verified. Customer can now transact.`);
    refreshData();
}

async function reviewLoan(id, approve) {
    await fetch(`/api/app/loans/manager-review/${id}?approve=${approve}`, { method: 'POST' });
    showToast(`Loan ${id} state mutated to ${approve ? "APPROVED" : "REJECTED"}`);
    refreshData();
}

refreshData();

// Live updates: poll every 10 seconds
setInterval(refreshData, 10000);
