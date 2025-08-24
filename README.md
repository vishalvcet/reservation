# 📚 Library Book Reservation System

A simple Android app (or Java/Kotlin project) for reserving library books.  
**Admin uploads book details (title, available copies, due date). Users can reserve only one book per day. If the due date is within 3 days, an urgency fee of ₹10 is applied automatically.**

---

## ✨ Features
- **Admin Panel**
  - Add new books with title, author, copies available, and due date  
  - Update/remove books  
- **User Panel**
  - Browse/search available books  
  - Reserve only **1 book per calendar day**  
  - Automatic urgency fee (₹10) if the book’s due date is within 3 days  
  - Reservation summary with total price (base fee + urgency fee)  
- **Validation**
  - No reservation if copies = 0  
  - Prevent duplicate reservation on the same day  

---

## 🧰 Tech Stack
- Language: Kotlin/Java  
- UI: XML Layout + RecyclerView  
- DB: SQLite / Room Database  
- Architecture: Activity → Adapter → Model (Simple MVC/MVVM)  

---

## ⚖️ Business Rules

1. **One book per user per day**  
   ```pseudo
   if (userAlreadyReservedToday) {
       showError("You can reserve only 1 book per day");
   }
   ```

2. **Urgency Fee**  
   ```java
   long daysLeft = DAYS.between(today, book.dueDate);
   int urgencyFee = (daysLeft <= 3) ? 10 : 0;
   total = baseFee + urgencyFee;
   ```

---

## 🧪 Example Test Cases

| Book | Due Date | Today | Days Left | Urgency Fee | Total Fee |
|------|----------|-------|-----------|-------------|-----------|
| DBMS | 2025-08-27 | 2025-08-24 | 3 | 10 | Base+10 |
| Java | 2025-08-28 | 2025-08-24 | 4 | 0 | Base    |
| C++  | 2025-08-24 | 2025-08-24 | 0 | 10 | Base+10 |

---

## 🚀 Getting Started
1. Clone repo:  
   ```bash
   git clone https://github.com/<your-username>/<your-repo>.git
   cd <your-repo>
   ```
2. Open in Android Studio  
3. Run on emulator/device  

---

## 🗺️ Roadmap
- User login with student ID  
- Notifications for due date reminders  
- Multiple branch libraries  
- Fine calculation after due date  

---

## 📜 License
MIT License — free for educational use.
