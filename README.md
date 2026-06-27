# 🍽️ RestoManager

**RestoManager** is a fully offline restaurant management app for Android, built entirely with **Kotlin** and **Jetpack Compose**. It helps café and restaurant owners manage their finances, staff, attendance, menu, and payroll — all from a single, clean, dark-themed interface. Designed and developed end-to-end by a single developer.

---

## ✨ Features

- **🔐 Passcode lock** — Secure the app with a custom 4-digit passcode set on first launch, changeable anytime.
- **👋 Onboarding** — A smooth first-run introduction to the app.
- **💰 Financial management** — Track expenses, debts, withdrawals, and installments. Add multiple business partners, each with their own separate records.
- **👥 Employee management** — Store staff details (position, marital status, guarantees, health cards, salary, and working hours).
- **🕒 Attendance** — Daily check-in/check-out tracking with a Persian (Jalali) calendar and monthly work-hour summaries.
- **🍕 Products & menu** — Build menu items by category, with live ingredient-cost calculation.
- **🧾 Payroll** — Calculate net payable per employee based on attendance, overtime, and custom adjustments.
- **📝 Notes & tasks** — Quick notes and a task list right on the home screen.
- **🏠 Dashboard** — At-a-glance view of total debt, upcoming payments, and daily tasks.

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose (Material 3) |
| Architecture | Feature-based modular structure, MVVM |
| Local storage | Room (per-feature databases) |
| Preferences | DataStore |
| Navigation | Navigation Compose |
| Min SDK | 24 |

---

## 🏗️ Architecture

The project follows a **feature-based modular structure**. Each feature is self-contained with its own UI, ViewModel, and Room database, which keeps modules independent and avoids cross-feature migrations.

```
com.example.resturant
├── core
│   ├── navigation        # Screen routes & NavHost
│   └── settings          # Passcode, café name, onboarding state (DataStore)
└── feature
    ├── onboarding        # First-run intro
    ├── setup             # Café name + passcode creation
    ├── login             # Passcode lock screen
    ├── mainpage          # Dashboard
    ├── finance           # Expenses, debts, partners
    ├── employee          # Staff management
    ├── attendance        # Check-in / check-out
    ├── product           # Menu & ingredients
    ├── payroll           # Salary calculation
    ├── note              # Notes & tasks
    └── account           # Profile & settings
```

---

## 🚀 Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/MobinJamshidi/resto-manager.git
   ```
2. Open the project in **Android Studio**.
3. Let Gradle sync, then run on an emulator or a physical device (Android 7.0+).

On first launch, you'll go through onboarding, create your café name and a 4-digit passcode, and you're in.

---

## 📦 Data & Privacy

All data is stored **locally on the device** using Room and DataStore. No account, no server, no internet required.

> ⚠️ Note: Because data is stored locally, uninstalling the app will erase all stored data.

---

## 📬 Contact

Designed & developed by **Mobin Jamshidi** — from UI/UX to code.

📧 jamshid.mobin567@gmail.com

---

## 📄 License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.
