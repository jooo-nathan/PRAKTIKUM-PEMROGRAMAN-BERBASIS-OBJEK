APLIKASI LOGIN & REGISTER - SUMBER LANCAR (Java Swing, VERSI STATIS)

Tanpa database dan tanpa library tambahan. Data akun disimpan di memori,
jadi data kembali ke awal setiap program ditutup.

SYARAT
JDK (Java) sudah terpasang.  Ubuntu: sudo apt install default-jdk

CARA MENJALANKAN
   javac AplikasiAuthGUI.java
   java AplikasiAuthGUI

AKUN UJI
Admin    : admin / admin123   -> tabel semua akun (Create, Read, Update, Delete)
Customer : budi / budi123     -> halaman "Profil Saya" (hanya data sendiri)
           siti / siti123
Akun baru bisa dibuat lewat tab Register (otomatis berole CUSTOMER).

JIKA ADA ERROR
- "javac: command not found" : pasang JDK (sudo apt install default-jdk).
