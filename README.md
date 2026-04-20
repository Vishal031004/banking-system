# Online Banking & Loan Management System

Welcome to the production-grade Online Banking repository! This guide provides a quick setup walkthrough so you and your teammates can run the server locally, explore the different user roles, and test out the complex backend architectures (KYC verification, Event-Driven Fraud Engines, and Dynamic Loan scoring).

## 🚀 1. How to Run the Application

This is a Spring Boot application. You do not need to install Maven, as the repository includes a Maven Wrapper (`mvnw`).

1. Open your terminal to the root directory (`banking-system`).
2. Run the Spring Boot Server:
   - **Windows:** `.\mvnw.cmd spring-boot:run`
   - **Mac/Linux:** `./mvnw spring-boot:run`
3. Wait for the console to display `Started OoadBankApplication` (usually takes ~5-10 seconds).

The backend and frontend are now running jointly on port **8080**.

---

## 🧭 2. Portal URLs & Workflow Testing

There are two completely isolated user flows with distinct HTML interfaces. 

### Customer Portal (The User View)
- **URL:** `http://localhost:8080/`
- **Testing Flow:**
  1. This URL redirects you to the authentication page.
  2. Fill in the "Identify & Register" form to create a fresh Customer.
  3. Once registered, you will be routed to the **Customer Dashboard**.
  4. Navigate to **My Accounts** in the sidebar. Select any mock PDF file for your KYC Document and hit **Submit to Factory & KYC**. *Note: Your account is now stored in an UNVERIFIED state.*
  5. Go to **Transfer Funds** and attempt a transfer exceeding $10,000 to trigger the Transaction Risk Engine (Fraud Alert).
  6. Go to **Loan Offers** to see your Pre-Approved rates based dynamically on the user's initial credit score (650).

### Manager Hub (The Admin View)
- **URL:** `http://localhost:8080/admin.html`
- **Testing Flow:**
  1. The system has a built-in backdoor for easy presentations! If you navigate directly to this URL, it securely bypasses standard login and mocks an active Admin session (`admin@finance.com`) , password ('admin123').
  2. On the **KYC Verifications** page, you will see the exact PDF file uploaded by your test Customer. 
  3. Click **VERIFY KYC DATA** to unlock the account. 
  4. Switch to the **Loan Exceptions** tab to Approve or Reject any loans formally submitted by users.

---

## 🗄️ 3. Live Database Viewer (H2 Console)

This project uses an embedded H2 In-Memory database. You can directly view the tables mutating during your presentation.

1. In a new tab, navigate to: `http://localhost:8080/h2-console`
2. **Crucial Login Config:** You must enter the exact credentials below:
   - **JDBC URL:** `jdbc:h2:mem:bankdb`
   - **User Name:** `sa`
   - **Password:** `password` *(Do not leave this blank!)*
3. Click **Connect**. 

You can now click on tables like `BANK_ACCOUNT`, `CUSTOMER`, and `LOAN_APPLICATION` and run `SELECT * FROM...` to visually prove that the Database state changes locally when an Admin clicks 'Approve' or 'Verify'.

## 🛠️ Architecture Highlights for the Viva
- **Observer Pattern:** Used extensively for dispatching UI notifications and adjusting credit scores without tight coupling.
- **Factory Pattern:** Account creation flows entirely via Factory instantiation mapped to KYC verification engines.
- **Role-Based Access Control:** Physical file separation (`dashboard.js` vs `admin.js`) ensuring users are restricted from privileged methods.
