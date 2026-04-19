// Global Variables
let currentUser = JSON.parse(localStorage.getItem('currentUser'));
let notificationLogs = [];

if(!currentUser || currentUser.role !== 'USER') {
    window.location.href = 'login.html';
}
document.getElementById('sidebar-email').innerText = currentUser.email;
document.getElementById('dashName').innerText = currentUser.name;

// Utilities
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

// Data Fetching
async function refreshData() {
    const dashRes = await fetch(`/api/app/users/${currentUser.userId}/dashboard`);
    const dashData = await dashRes.json();
    
    document.getElementById('dashTotalBalance').innerText = `$${parseFloat(dashData.totalBalance).toFixed(2)}`;
    
    // Check Credit Score Dynamic Offers
    const offersRes = await fetch(`/api/app/loans/offers/${currentUser.userId}`);
    const offersData = await offersRes.json();
    document.getElementById('dashScore').innerText = offersData.creditScore;
    if(offersData.creditScore < 600) document.getElementById('dashRiskAlert').innerText = "High Risk Classification: Expect higher interest rates.";
    else document.getElementById('dashRiskAlert').innerText = "";
    
    let htmlOffers = '';
    offersData.offers.forEach(o => {
        htmlOffers += `<div style="padding:10px; border-bottom:1px solid #d0e3e5;"><strong>${o.type}</strong> - Up to $${o.maxAmount} at ${o.rate}</div>`;
    });
    document.getElementById('preapprovedOffers').innerHTML = htmlOffers;

    // Accounts
    const accTable = document.getElementById('dashAccountsTable');
    const tfFrom = document.getElementById('transferFrom');
    accTable.innerHTML = '';
    tfFrom.innerHTML = '';
    
    dashData.accounts.forEach(acc => {
        accTable.innerHTML += `<tr><td>${acc.accountType}</td><td>${acc.accountNumber}</td><td>${acc.status}</td><td>$${acc.balance}</td></tr>`;
        if(acc.status === 'ACTIVE') {
            tfFrom.innerHTML += `<option value="${acc.accountNumber}">${acc.accountType} - $${acc.balance}</option>`;
        }
    });

    // Global Accounts for Transfer target
    const allRes = await fetch(`/api/app/accounts/all`);
    const allAccs = await allRes.json();
    const tfTo = document.getElementById('transferTo');
    tfTo.innerHTML = '';
    allAccs.filter(a => a.status === 'ACTIVE').forEach(a => {
        tfTo.innerHTML += `<option value="${a.accountNumber}">${a.accountNumber}</option>`;
    });

    // Loans
    const loansRes = await fetch(`/api/app/loans/user/${currentUser.userId}`);
    const loansData = await loansRes.json();
    const lTable = document.getElementById('loansTableBody');
    lTable.innerHTML = '';
    loansData.forEach(l => {
        lTable.innerHTML += `<tr><td>${l.applicationId.substring(0,6)}</td><td>${l.purpose}</td><td>$${l.loanAmount}</td><td>${l.status}</td></tr>`;
    });
}

// Actions
async function openAccount() {
    const type = document.getElementById('newAccType').value;
    const dep = document.getElementById('newAccInitDep').value;
    
    const fileInput = document.getElementById('kycFileInput');
    const documentName = fileInput.files.length > 0 ? fileInput.files[0].name : "No_Document_Attached.pdf";
    
    await fetch(`/api/app/accounts/create?customerId=${currentUser.userId}&type=${type}&initialDeposit=${dep}&kycDoc=${documentName}`, { method: 'POST' });
    showToast(`KYC Required: ${documentName} submitted. Account created in UNVERIFIED state.`);
    refreshData();
}

async function transferFunds() {
    const from = document.getElementById('transferFrom').value;
    const to = document.getElementById('transferTo').value;
    const amt = document.getElementById('transferAmt').value;
    
    try {
        const res = await fetch(`/api/app/transactions/transfer?fromAccount=${from}&toAccount=${to}&amount=${amt}`, { method: 'POST' });
        if(!res.ok) throw new Error("Transaction flagged or insufficient funds");
        showToast("Transfer complete. Observer dispatched.");
    } catch(e) {
        showToast("FRAUD ENGINE ALERT: " + e.message);
    }
    refreshData();
}

async function applyForLoan() {
    const t = document.getElementById('loanType').value;
    const a = document.getElementById('loanAmt').value;
    const ten = document.getElementById('loanTenure').value;
    await fetch(`/api/app/loans/apply?customerId=${currentUser.userId}&amount=${a}&tenure=${ten}&purpose=${t}`, { method: 'POST' });
    showToast("Application submitted. Credit system notified.");
    refreshData();
}

refreshData();
