// State
let currentUser = null;

// Views
const authView = document.getElementById('auth-view');
const dashView = document.getElementById('dashboard-view');

function showView(view) {
    document.querySelectorAll('.view').forEach(v => v.classList.remove('active'));
    view.classList.add('active');
}

// 1. Authenticate / Register
async function registerUser() {
    const name = document.getElementById('regName').value;
    const email = document.getElementById('regEmail').value;
    const income = document.getElementById('regIncome').value;
    const msg = document.getElementById('authMsg');

    msg.innerText = "Registering user in Database...";
    
    try {
        const res = await fetch(`/api/app/users/register?name=${name}&email=${email}&income=${income}`, { method: 'POST' });
        const user = await res.json();
        currentUser = user;
        await refreshDashboard();
        showView(dashView);
    } catch (e) {
        msg.innerText = "Error completing registration.";
    }
}

// 2. Fetch Real Dashboard
async function refreshDashboard() {
    if (!currentUser) return;
    try {
        const res = await fetch(`/api/app/users/${currentUser.userId}/dashboard`);
        const data = await res.json();
        
        // Update DOM
        document.getElementById('dashName').innerText = data.user.name;
        document.getElementById('dashScore').innerText = data.user.creditScore;
        document.getElementById('dashTotalBalance').innerText = `$${parseFloat(data.totalBalance).toFixed(2)}`;
        
        // Populate Accounts grid
        const accContainer = document.getElementById('accountsContainer');
        const transferFrom = document.getElementById('transferFrom');
        const targetDrop = document.getElementById('transferTo');
        accContainer.innerHTML = '';
        transferFrom.innerHTML = '';

        if(data.accounts.length === 0) {
            accContainer.innerHTML = `<p style="color: #64748b; font-style: italic;">No accounts opened yet. Open one above!</p>`;
        }
        
        // Fetch global system accounts to make transferring easy!
        const allRes = await fetch(`/api/app/accounts/all`);
        const allAccs = await allRes.json();
        targetDrop.innerHTML = '<option value="">Select Destination Account</option>';
        allAccs.forEach(a => {
            if (a.customer && a.customer.userId !== currentUser.userId) {
                targetDrop.innerHTML += `<option value="${a.accountNumber}">External: ${a.customer.name} - ${a.accountNumber}</option>`;
            }
        });

        data.accounts.forEach(acc => {
            // Render Card
            accContainer.innerHTML += `
                <div class="acc-card">
                    <div style="font-size: 0.9rem; font-weight: 600;">${acc.accountType}</div>
                    <div class="acc-num">${acc.accountNumber}</div>
                    <div class="acc-bal">$${acc.balance.toFixed(2)}</div>
                </div>
            `;
            // Render Dropdown Option
            transferFrom.innerHTML += `<option value="${acc.accountNumber}">My ${acc.accountType} - Bal: $${acc.balance.toFixed(2)}</option>`;
            targetDrop.innerHTML += `<option value="${acc.accountNumber}">My ${acc.accountType} - ${acc.accountNumber}</option>`;
        });

    } catch (e) {
        console.error("Dashboard refresh error: ", e);
    }
}

// 3. Open New Account
async function openAccount() {
    const type = document.getElementById('newAccType').value;
    const initialDep = document.getElementById('newAccInitDep').value;
    
    try {
        await fetch(`/api/app/accounts/create?customerId=${currentUser.userId}&type=${type}&initialDeposit=${initialDep}`, { method: 'POST' });
        document.getElementById('newAccInitDep').value = '';
        await refreshDashboard();
    } catch (e) {
        alert("Failed to open account");
    }
}

// 4. Transfer Funds
async function transferFunds() {
    const fromAcc = document.getElementById('transferFrom').value;
    const toAcc = document.getElementById('transferTo').value;
    const amt = document.getElementById('transferAmt').value;
    const msg = document.getElementById('transferMsg');

    if(!fromAcc || !toAcc || !amt) {
        msg.innerText = "Please fill all transfer fields."; return;
    }

    msg.innerText = "Processing transfer...";
    
    try {
        const res = await fetch(`/api/app/transactions/transfer?fromAccount=${fromAcc}&toAccount=${toAcc}&amount=${amt}`, { method: 'POST' });
        if(!res.ok) throw new Error("Insufficient funds or invalid target account");
        
        const data = await res.json();
        if(data.success) {
            msg.style.color = "#4ade80";
            msg.innerText = `Success! Transaction ID: ${data.transaction.transactionId.substring(0,8)}...`;
            document.getElementById('transferTo').value = '';
            document.getElementById('transferAmt').value = '';
            await refreshDashboard(); // Reflect new balances immediately
        }
    } catch (e) {
        msg.style.color = "#ef4444";
        msg.innerText = e.message;
    }
}

function logout() {
    currentUser = null;
    showView(authView);
    document.getElementById('authMsg').innerText = "Logged out successfully.";
}
