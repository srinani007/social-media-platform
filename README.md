# 🚀 Social Media Platform

A full-stack Java Spring Boot web app with authentication, role-based access control, payment integration, and real-time posting.

<img alt="![img.png](img.png)" src="https://your-screenshot-url.com"/>

---

## 🧰 Tech Stack

- **Java 17** + Spring Boot 3
- **Spring Security** for auth
- **Thymeleaf** for templating
- **PostgreSQL (Render)** + H2 for dev
- **PayPal** Sandbox for Pro upgrades
- **Gmail SMTP** for contact support
- **Bootstrap 5** for styling
- **Render** deployment (free-tier friendly)

---

## 💡 Features

- ✅ User Registration / Login
- ✅ Profile page with Pro badge
- ✅ Create / View Posts
- ✅ Admin Panel: delete/upgrade users & posts
- ✅ Email contact form
- ✅ PayPal integration
- ✅ Multi-profile environment config (`dev`/`prod`)
- ✅ H2 Console for dev inspection

---

## 🖥️ Local Development

```bash
# Run locally with H2 + dev profile
mvn spring-boot:run
