# MVC Architecture Report
## Online Banking & Loan Management System

---

## 1. Architecture Overview

This project is built on the **Model-View-Controller (MVC)** architectural pattern, implemented using **Spring Boot** as the backend framework. The architecture enforces a strict **separation of concerns** — each layer has one well-defined responsibility and cannot bypass the other layers.

```
┌────────────────────────────────────────────────────────────────────┐
│                          VIEW LAYER                                │
│     login.html  dashboard.html  admin.html                         │
│     dashboard.js  admin.js  style.css                              │
│             (Browser — renders UI, calls REST APIs)                │
└───────────────────────┬────────────────────────────────────────────┘
                        │  HTTP REST (JSON)
┌───────────────────────▼────────────────────────────────────────────┐
│                       CONTROLLER LAYER                             │
│   RealBankController   LoanController   DemoController             │
│          (Receives requests, delegates to Services)                │
└──────────┬─────────────────────────────────────────────────────────┘
           │  calls
┌──────────▼─────────────────────────────────────────────────────────┐
│                        SERVICE LAYER                               │
│   CustomerService  AccountService  FraudDetectionService           │
│   NotificationService  CreditScoreService  AuthService  OCRService │
│       (Business logic, design patterns live here)                  │
└──────────┬──────────────────────┬──────────────────────────────────┘
           │ JPA                  │ Events
┌──────────▼──────────┐  ┌───────▼──────────────────────────────────┐
│   MODEL LAYER       │  │        EVENT BUS (Observer)               │
│   Entities, Enums   │  │   TransactionCompletedEvent               │
│   Repositories      │  │   LoanStatusEvent                         │
└─────────────────────┘  └──────────────────────────────────────────┘
```

---

## 2. THE MODEL LAYER

> The Model is everything that represents **data** and **business state**. It includes entity classes (mapped to DB tables), enumerations, repositories (data access), and domain services.

### 2.1 Entity Models (Domain Objects)

These are the core data objects, mapped to the H2 in-memory database via **JPA/Hibernate**.

#### 📁 `models/users/`

| File | Role |
|------|------|
| `User.java` | Abstract base entity. Fields: `userId`, `name`, `email`, `role`, `passwordHash`, `createdAt`. Uses JPA `@Inheritance(JOINED)` — shared table `app_user`. |
| `Customer.java` | Extends `User`. Adds `creditScore`, `income`, `kycStatus`, `sessionToken`, `failedLoginAttempts`, `isLocked`, and a `@OneToMany` relationship to `BankAccount`. Represents a bank client. |
| `BankStaff.java` | Extends `User`. Represents an admin/manager. `role` is set to `"ADMIN"`. |

#### 📁 `models/accounts/`

| File | Role |
|------|------|
| `BankAccount.java` | Abstract base entity for all account types. Holds `accountNumber`, `balance`, `status`, `accountType`, `kycDocuments[]`, and a `@ManyToOne` link to `Customer`. Contains core banking behaviors: `deposit()`, `withdraw()`, `transfer()`, `freeze()`, `closeAccount()`. |
| `SavingsAccount.java` | Concrete subclass. Has `interestRate`. Implements `generateMonthlyStatement()`. |
| `CurrentAccount.java` | Concrete subclass. Has `overdraftLimit`. Implements `generateMonthlyStatement()`. |
| `CorporateAccount.java` | Concrete subclass. Has `companyRegistrationNumber`. Implements `generateMonthlyStatement()`. |

> **Note:** `BankAccount` uses `@Inheritance(JOINED)` — each subtype gets its own table joined to the base.

#### 📁 `models/loans/`

| File | Role |
|------|------|
| `LoanApplication.java` | Core loan entity. Fields: `applicationId`, `customerId`, `loanAmount`, `tenure`, `purpose`, `status`, `emi`, `submittedAt`. Contains **State Pattern** transition methods: `submitApplication()`, `officerEndorse()`, `managerApprove()`, `managerReject()`. |
| `LoanAccount.java` | Entity representing an active disbursed loan — tracks `principalAmount`, `outstandingBalance`, `interestRate`, `nextDueDate`. |

#### 📁 `models/transactions/`

| File | Role |
|------|------|
| `Transaction.java` | Entity for each fund transfer. Fields: `transactionId`, `senderAccount`, `receiverAccount`, `amount`, `type`, `status`, `timestamp`. |
| `Statement.java` | Represents a monthly account statement — list of transactions with summary totals. |
| `Receipt.java` | Represents a receipt issued after a transaction completes. |

#### 📁 `models/system/`

| File | Role |
|------|------|
| `NotificationMessage.java` | Data object passed between the Observer and Strategy layers. Holds `payload`, `type`, `status`, `generatedAt`. Has `generateMessagePayload()` method. |
| `AuditTrail.java` | Records who did what and when — `action`, `performedBy`, `timestamp`, `details`. Used for compliance tracking. |
| `Document.java` | Represents a KYC document — `documentId`, `documentType`, `filePath`, `uploadedAt`, `verifiedBy`. |

#### 📁 `models/enums/`

| File | Values | Purpose |
|------|--------|---------|
| `AccountStatus.java` | `ACTIVE`, `UNVERIFIED`, `SUSPENDED`, `CLOSED` | Lifecycle of a bank account |
| `AccountType.java` | `SAVINGS`, `CURRENT`, `CORPORATE` | Type of account |
| `LoanStatus.java` | `DRAFT`, `SUBMITTED`, `PENDING_MANAGER`, `APPROVED`, `REJECTED` | Loan application state machine |
| `TransactionStatus.java` | `PENDING`, `COMPLETED`, `FAILED`, `REVERSED` | Fund transfer state |
| `NotificationType.java` | `EMAIL`, `SMS` | Delivery channel for notifications |
| `NotificationStatus.java` | `PENDING`, `SENT`, `FAILED` | Delivery result |
| `RiskLevel.java` | `LOW`, `MEDIUM`, `HIGH` | Output from the Fraud Engine |
| `StaffRole.java` | `LOAN_OFFICER`, `BRANCH_MANAGER`, `ADMIN` | Admin role classification |

### 2.2 Repositories (Data Access Layer)

These are Spring Data JPA interfaces — they auto-implement all standard DB queries (find, save, delete).

| File | Manages | Key Methods |
|------|---------|-------------|
| `UserRepository.java` | `User` (Customer + BankStaff) | `findById()`, `findAll()` |
| `BankAccountRepository.java` | `BankAccount` | `findById()`, `findAll()`, `save()` |
| `LoanApplicationRepository.java` | `LoanApplication` | `findByCustomerId()`, `findAll()`, `findById()` |
| `TransactionRepository.java` | `Transaction` | `save()`, inherited JPA queries |

---

## 3. THE VIEW LAYER

> The View is responsible for **presenting data to the user** and **capturing user input**. In this project it is a set of plain HTML + JavaScript SPA pages served as static resources by Spring Boot.

All files live in: `src/main/resources/static/`

### 3.1 HTML Pages (Structure)

| File | Serves | Purpose |
|------|--------|---------|
| `login.html` | `http://localhost:8080/` | Entry point. Presents two forms — Register (new user) and Sign In (existing user). On success stores user object in `localStorage` and redirects. |
| `dashboard.html` | `http://localhost:8080/dashboard.html` | Customer-facing interface. Contains 4 pages rendered as tabs: **Overview** (balance + credit score), **My Accounts** (KYC + account list), **Transfer Funds**, and **Loan Offers + Applications**. |
| `admin.html` | `http://localhost:8080/admin.html` | Manager-facing interface. Contains 2 pages: **KYC Verifications** (UNVERIFIED accounts queued for approval) and **Loan Exceptions** (pending loans with Approve/Reject). |

### 3.2 JavaScript Files (Behaviour / View Logic)

| File | Linked To | Responsibilities |
|------|-----------|-----------------|
| `dashboard.js` | `dashboard.html` | Reads session from `localStorage`. Guards route (redirects to login if no valid user). Calls all backend REST APIs via `fetch()`. Renders account tables, loan tables, transfer dropdowns. Handles: `openAccount()`, `transferFunds()`, `applyForLoan()`. Polls `refreshData()` every **10 seconds** for live updates. |
| `admin.js` | `admin.html` | Mocks an admin session if none exists (presentation backdoor). Calls APIs to fetch UNVERIFIED accounts and pending loans. Renders them into tables with action buttons. Handles: `verifyAccount()`, `reviewLoan()`. Polls `refreshData()` every **10 seconds**. |

### 3.3 Stylesheet

| File | Purpose |
|------|---------|
| `style.css` | Shared CSS for all pages. Defines the sidebar layout, card styles, toast notification styles, table styles, and responsive design. |

### How the View communicates with the Controller

The View does **not** call Java methods directly. It communicates exclusively via **HTTP REST API calls**:

```
View (dashboard.js)
  └─► fetch('/api/app/users/{id}/dashboard')   → GET  → RealBankController
  └─► fetch('/api/app/accounts/create')         → POST → RealBankController
  └─► fetch('/api/app/transactions/transfer')   → POST → RealBankController
  └─► fetch('/api/app/loans/apply')             → POST → LoanController
  └─► fetch('/api/app/loans/offers/{id}')       → GET  → LoanController
  └─► fetch('/api/app/admin/accounts/verify')   → POST → RealBankController
  └─► fetch('/api/app/loans/manager-review')    → POST → LoanController
```

---

## 4. THE CONTROLLER LAYER

> The Controller is the **entry point for all HTTP requests**. It receives input from the View, validates/delegates to the Service/Model layers, and returns a JSON response. Controllers contain **no business logic**.

### 4.1 `RealBankController.java`
**Base URL:** `/api/app`

This is the primary controller handling the core banking operations for both the Customer and Admin portals.

| Endpoint | Method | What it does |
|----------|--------|-------------|
| `/users/register` | `POST` | Delegates to `CustomerService.registerCustomer()` → returns new Customer JSON |
| `/users/login` | `POST` | Looks up user by email. If `admin@finance.com`, mocks an Admin session. Otherwise fetches from DB. |
| `/users/{id}/dashboard` | `GET` | Fetches Customer + their ACTIVE accounts + total balance. Returns as a composite JSON map. |
| `/accounts/create` | `POST` | Calls `AccountService.createAccountForCustomer()` which invokes the **Factory Pattern**. |
| `/accounts/all` | `GET` | Returns all accounts (used by both dashboards for transfer targets and KYC queue). |
| `/admin/accounts/{id}/verify` | `POST` | Sets account status to `ACTIVE`. Admin-only action. |
| `/transactions/transfer` | `POST` | Validates funds → calls **FraudDetectionService** → debits/credits accounts → fires **Observer event**. |

### 4.2 `LoanController.java`
**Base URL:** `/api/app/loans`

Handles all loan lifecycle operations.

| Endpoint | Method | What it does |
|----------|--------|-------------|
| `/offers/{customerId}` | `GET` | Calls `CreditScoreService.fetchScore()` and returns tiered pre-approved loan offers based on score bracket. |
| `/apply` | `POST` | Creates `LoanApplication`, calls `submitApplication()` (State Pattern), calculates EMI, fires **LoanStatusEvent** (Observer). |
| `/user/{customerId}` | `GET` | Returns all loan applications for a specific customer. |
| `/all` | `GET` | Returns all loans (Admin view). |
| `/manager-review/{id}` | `POST` | Calls `managerApprove()` or `managerReject()` (State Pattern), saves, fires **LoanStatusEvent** (Observer). |

### 4.3 `DemoController.java`
**Base URL:** `/api/demo`

A testing/demo controller that validates design patterns in isolation. Not used by the frontend.

| Endpoint | Method | What it does |
|----------|--------|-------------|
| `/factory` | `POST` | Directly instantiates an account via `BankAccountFactory` and returns the class name. |
| `/observer` | `POST` | Publishes a `TransactionCompletedEvent` directly to prove both observers fire. |
| `/state` | `POST` | Walks a `LoanApplication` through all 3 state transitions and returns each status name. |

### 4.4 `SystemController.java`
**Base URL:** (Spring-managed)

A lightweight utility controller for system-level health or config endpoints.

---

## 5. THE SERVICE LAYER (Bridge between Controller and Model)

> Services are **not strictly part of MVC** but are an essential addition in Spring Boot to keep Controllers thin. They contain all business logic.

| File | Role |
|------|------|
| `CustomerService.java` | Registers and fetches Customer objects. Sets initial credit score (650), KYC status, and role. |
| `AccountService.java` | Creates accounts via **Factory Pattern**. Associates accounts to customers. Invokes OCR and fraud checks on KYC. |
| `FraudDetectionService.java` | **Observer** of `TransactionCompletedEvent`. Also called synchronously during transfers. Blocks transactions over $10,000 or with high anomaly scores. |
| `NotificationService.java` | **Observer** of both `TransactionCompletedEvent` and `LoanStatusEvent`. Uses **Strategy Pattern** (`INotificationGateway`) to dispatch via Email or SMS. |
| `CreditScoreService.java` | Fetches and adjusts a customer's credit score. Called by `LoanController` to determine loan offer tiers. |
| `AuthService.java` | Handles authentication logic (token generation, lockout tracking). |
| `OCRService.java` | Simulates document OCR verification for KYC. |

---

## 6. DESIGN PATTERNS WITHIN THE MVC STRUCTURE

| Pattern | GoF Category | Where it lives | How it integrates with MVC |
|---------|-------------|----------------|---------------------------|
| **Factory** | Creational | `patterns/factory/BankAccountFactory.java` | Controller calls Service → Service calls Factory → Factory creates Model object |
| **Observer** | Behavioural | `patterns/observer/` events + `@EventListener` in Services | Controller fires event after Model mutation → Services (observers) react asynchronously |
| **Strategy** | Behavioural | `patterns/strategy/INotificationGateway` + `EmailGateway` + `SMSGateway` | NotificationService (observer) delegates to a swappable Strategy for delivery channel |
| **State** | Behavioural | `LoanApplication.java` methods | Model manages its own state transitions — Controller just calls `submitApplication()`, `managerApprove()` etc. |
| **Singleton** | Creational | Every `@Service`, `@Component` | Spring IoC container ensures one instance of each service — used across all Controller injections |

---

## 7. ARCHITECTURE APPROACH & JUSTIFICATION

### Why MVC?

**MVC was chosen because banking applications have a clear three-way separation of concerns:**

1. **Data integrity is critical (Model)** — All account balances, loan statuses, and transaction records are encapsulated in JPA entities. Business rules like `withdraw()` validating balance live directly on the model. This prevents corruption regardless of how the data is accessed.

2. **Multiple user roles need different views (View)** — The same backend serves two completely different interfaces: `dashboard.html` (customer) and `admin.html` (manager). MVC makes this natural — just two separate Views pointing to the same Controller/Model stack.

3. **REST API-first keeps the View decoupled (Controller)** — By making the Controller a REST API layer (not a template-rendering controller), the frontend becomes a pure SPA that can be replaced, redesigned, or tested independently. This also supports future mobile app development using the same APIs.

### Why Spring Boot's Layered MVC over a traditional Servlet MVC?

- **Dependency Injection** — Controllers, Services, and Repositories are wired automatically. No manual `new` instantiation required, making it trivially easy to swap implementations (e.g., replace H2 with MySQL).
- **Repository Pattern** — Spring Data JPA eliminates all boilerplate SQL/DAO code. `BankAccountRepository` gets `findAll()`, `save()`, `findById()` for free.
- **Event-Driven Extension** — Spring's `ApplicationEventPublisher` adds an Observer backbone on top of MVC without modifying the core controller flow. Fraud detection and notifications are completely decoupled from transfer logic.

### Why an In-Memory H2 Database?

- **Zero setup for demo/viva** — No MySQL/PostgreSQL installation required. Application is fully self-contained.
- **Trade-off acknowledged** — All data is lost on server restart. This is intentional for a demo system but would be replaced by a persistent store in production.

### Justification for Role-Based View Separation

The physical separation of `dashboard.js` vs `admin.js` enforces **authorization at the View layer**. A regular user, even if they manually navigate to `admin.html`, would get a mocked admin session (for demo ease) — but in a hardened deployment, this would be a server-side session check. This mirrors the **Role-Based Access Control (RBAC)** principle from the MVC architecture — the Controller is the authoritative gatekeeper, while the View is simply the rendering surface.

---

## 8. COMPLETE FILE SUMMARY TABLE

| Layer | File | Package/Path |
|-------|------|-------------|
| **MODEL** | `User.java` | `models/users/` |
| **MODEL** | `Customer.java` | `models/users/` |
| **MODEL** | `BankStaff.java` | `models/users/` |
| **MODEL** | `BankAccount.java` | `models/accounts/` |
| **MODEL** | `SavingsAccount.java` | `models/accounts/` |
| **MODEL** | `CurrentAccount.java` | `models/accounts/` |
| **MODEL** | `CorporateAccount.java` | `models/accounts/` |
| **MODEL** | `LoanApplication.java` | `models/loans/` |
| **MODEL** | `LoanAccount.java` | `models/loans/` |
| **MODEL** | `Transaction.java` | `models/transactions/` |
| **MODEL** | `Statement.java` | `models/transactions/` |
| **MODEL** | `Receipt.java` | `models/transactions/` |
| **MODEL** | `NotificationMessage.java` | `models/system/` |
| **MODEL** | `AuditTrail.java` | `models/system/` |
| **MODEL** | `Document.java` | `models/system/` |
| **MODEL** | `AccountStatus.java` | `models/enums/` |
| **MODEL** | `AccountType.java` | `models/enums/` |
| **MODEL** | `LoanStatus.java` | `models/enums/` |
| **MODEL** | `TransactionStatus.java` | `models/enums/` |
| **MODEL** | `NotificationType.java` | `models/enums/` |
| **MODEL** | `NotificationStatus.java` | `models/enums/` |
| **MODEL** | `RiskLevel.java` | `models/enums/` |
| **MODEL** | `StaffRole.java` | `models/enums/` |
| **MODEL** | `UserRepository.java` | `repositories/` |
| **MODEL** | `BankAccountRepository.java` | `repositories/` |
| **MODEL** | `LoanApplicationRepository.java` | `repositories/` |
| **MODEL** | `TransactionRepository.java` | `repositories/` |
| **VIEW** | `login.html` | `static/` |
| **VIEW** | `dashboard.html` | `static/` |
| **VIEW** | `admin.html` | `static/` |
| **VIEW** | `dashboard.js` | `static/` |
| **VIEW** | `admin.js` | `static/` |
| **VIEW** | `style.css` | `static/` |
| **CONTROLLER** | `RealBankController.java` | `controllers/` |
| **CONTROLLER** | `LoanController.java` | `controllers/` |
| **CONTROLLER** | `DemoController.java` | `controllers/` |
| **CONTROLLER** | `SystemController.java` | `controllers/` |
| **SERVICE** | `CustomerService.java` | `services/` |
| **SERVICE** | `AccountService.java` | `services/` |
| **SERVICE** | `FraudDetectionService.java` | `services/` |
| **SERVICE** | `NotificationService.java` | `services/` |
| **SERVICE** | `CreditScoreService.java` | `services/` |
| **SERVICE** | `AuthService.java` | `services/` |
| **SERVICE** | `OCRService.java` | `services/` |
| **PATTERN** | `BankAccountFactory.java` | `patterns/factory/` |
| **PATTERN** | `TransactionCompletedEvent.java` | `patterns/observer/` |
| **PATTERN** | `LoanStatusEvent.java` | `patterns/observer/` |
| **PATTERN** | `INotificationGateway.java` | `patterns/strategy/` |
| **PATTERN** | `EmailGateway.java` | `patterns/strategy/` |
| **PATTERN** | `SMSGateway.java` | `patterns/strategy/` |
