# **Izracunava Prihodkov / Odhodkov**

A web application for managing personal finances by tracking income and expenses.

## **E-R Model**
The application is based on a PostgreSQL database with the following key entities:

- **uporabniki**: Contains user information (`id`, `ime`, `email`, `geslo`, `kraj_id`).
- **kraji**: Contains locations with an `ID` and postal code.
- **kategorije**: Transaction categories linked to users.
- **transakcije**: Records users' financial transactions.
- **log_transakcij**: Logs operations performed on transactions.
- **transakcije_audit**: Tracks changes to transactions.

### **Entity Relationships:**
- **uporabniki → kraji** (Each user belongs to a specific location).
- **kategorije → uporabniki** (Each category belongs to a specific user).
- **transakcije → uporabniki** (Each transaction belongs to a specific user).
- **transakcije → kategorije** (Each transaction is assigned to a specific category).
- **log_transakcij → uporabniki** (Logs operations on transactions).
- **transakcije_audit → transakcije** (Tracks changes to transactions).

![PostgreSQL 10](https://github.com/user-attachments/assets/52f25538-bd8f-46f4-8caf-ba15bf58fc6b)
---


## **Database Functions & Procedures (PostgreSQL)**

### **sp_registriraj_uporabnika(p_ime, p_email, p_geslo_hash, p_kraj_id)**
- Checks if the email exists in `uporabniki`.
- Returns `1` if the email is already registered.
- Registers a new user if the email is not found.
- Returns `0` for success, `-1` for errors.

### **sp_prijava(p_email, p_geslo_hash)**
- Verifies email and password hash.
- Returns `id` if successful, `-1` otherwise.

### **sp_dodaj_transakcijo(p_uporabnik_id, p_kategorija_id, p_znesek, p_tip, p_opis, p_datum_transakcije)**
- Inserts a new transaction into `transakcije`.
- Returns `TRUE` if successful, `FALSE` if not.

### **sp_izbrisi_transakcijo(p_id)**
- Deletes a transaction by `id`.
- Returns `TRUE` if deleted, `FALSE` otherwise.

### **sp_dobi_transakcije_30dni(p_uporabnik_id)**
- Retrieves all transactions from the last 30 days.

---

## **Triggers**
- **trg_check_positive_znesek**: Prevents inserting negative or zero amounts in transactions.
- **trg_prepreciBrisanjeStarihTransakcij**: Prevents deletion of transactions older than 30 days.
- **trg_log_transakcije_changes**: Logs all transaction modifications.

---

## **Application Flow**
### **User Registration**
- The user accesses `Registracija.html` and fills in their details.
- Location options are dynamically fetched using `DobiKrajeServlet`.
- Data is sent to `RegistracijaServlet`, which calls `sp_registriraj_uporabnika`.
- If registration is successful, the user is redirected to the login page.

### **User Login**
- On `Prijava.html`, the user enters credentials.
- `PrijavaServlet` calls `sp_prijava` to authenticate.
- If successful, the user is redirected to `index.html`.

### **Transaction Management**
- The main page displays past transactions and a form to add new ones.
- `DobiTransakcijeServlet` retrieves transactions via `sp_dobi_transakcije_30dni`.
- Categories are fetched using `DobiKategorijeServlet`.
- A new transaction is added via `DodajTransakcijoServlet`, which calls `sp_dodaj_transakcijo`.
- Transactions are updated in real-time.

### **Deleting Transactions**
- The user can delete a transaction by clicking "Delete".
- `IzbrisiTransakcijoServlet` calls `sp_izbrisi_transakcijo`.
- Transactions older than 30 days cannot be deleted.

### **User Logout**
- The user clicks logout, triggering `LogoutServlet`.
- The session is terminated, redirecting to the login page.

---

## **Security & Integrity**
- Secure storage of user data.
- Validation during registration and login.
- Audit logging ensures data integrity.

---

## **Installation**
### **Requirements**
- Java (Spring Boot or Servlet-based backend)
- PostgreSQL
- Tomcat

### **Setup Instructions**
1. Clone the repository:
   ```sh
   git clone https://github.com/yourusername/izracunava-prihodkov-odhodkov.git
   ```
2. Set up PostgreSQL and create the necessary tables.
3. Configure the database connection in `application.properties`.
4. Deploy the application using Tomcat or a Java application server.
5. Access the web interface via `localhost:8080`.

---
