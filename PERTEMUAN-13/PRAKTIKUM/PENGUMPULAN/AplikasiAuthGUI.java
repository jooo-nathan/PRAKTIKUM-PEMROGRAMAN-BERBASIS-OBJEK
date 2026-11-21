
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/*
 * VERSI STATIS: tanpa database. Data akun disimpan di memori (ArrayList),
 * jadi data kembali ke awal setiap program ditutup. Cukup JDK, tanpa driver/library.
 */
public class AplikasiAuthGUI {

    // ==================== WARNA (diambil dari css/style.css web kayang) ====================
    static final Color DARK = new Color(0x161b20);
    static final Color ORANGE = new Color(0xff6600);
    static final Color PAGE = new Color(0xf5f7f9);
    static final Color BORDER = new Color(0xd8dbdf);
    static final Color MUTED = new Color(0x5d646c);
    static final Color TAB_BG = new Color(0xe8ebef);

    // ==================== MODEL ====================
    static class User {
        String username, namaLengkap, password, noTelepon, alamat, role;

        User(String username, String namaLengkap, String password, String noTelepon, String alamat, String role) {
            this.username = username.toLowerCase();
            this.namaLengkap = namaLengkap;
            this.password = password;
            this.noTelepon = noTelepon;
            this.alamat = alamat;
            this.role = role;
        }
    }

    // ==================== DATA STATIS (di memori, tanpa database) ====================
    private static final List<User> daftarUser = new ArrayList<>();

    // Mengisi akun bawaan (admin + contoh customer)
    public static void initDatabase() {
        daftarUser.add(new User("admin", "Administrator Bengkel", "admin123", "081234567890", "Bengkel Sumber Lancar", "ADMIN"));
        daftarUser.add(new User("budi", "Budi Santoso", "budi123", "081298765432", "Jl. Merdeka No. 10, Malang", "CUSTOMER"));
        daftarUser.add(new User("siti", "Siti Aminah", "siti123", "085612345678", "Jl. Veteran No. 5, Malang", "CUSTOMER"));
    }

    // CREATE
    public static void create(User u) {
        daftarUser.add(u);
    }

    // READ satu user (untuk login), null jika tidak cocok
    public static User get(String username, String password) {
        for (User u : daftarUser) {
            if (u.username.equalsIgnoreCase(username) && u.password.equals(password)) {
                return u;
            }
        }
        return null;
    }

    public static boolean usernameAda(String username) {
        for (User u : daftarUser) {
            if (u.username.equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    // READ semua user (untuk tabel)
    public static List<User> getAll() {
        return daftarUser;
    }

    // UPDATE (password kosong = password lama tidak diubah)
    public static void update(User baru) {
        for (User u : daftarUser) {
            if (u.username.equalsIgnoreCase(baru.username)) {
                u.namaLengkap = baru.namaLengkap;
                u.noTelepon = baru.noTelepon;
                u.alamat = baru.alamat;
                u.role = baru.role;
                if (!baru.password.isEmpty()) {
                    u.password = baru.password;
                }
                return;
            }
        }
    }

    // DELETE
    public static void delete(String username) {
        daftarUser.removeIf(u -> u.username.equalsIgnoreCase(username));
    }


    // ==================== VALIDATOR ====================
    // Mengembalikan pesan error, atau null jika valid
    private static String validasi(String username, String nama, String pass, String pass2,
                                   String noTelp, String alamat, boolean passWajib) {
        if (username.length() < 3 || username.length() > 50 || !username.matches("^[A-Za-z0-9_.]+$")) {
            return "Username harus 3-50 karakter (huruf, angka, titik, atau garis bawah)!";
        }
        if (nama.length() < 2 || nama.length() > 100) {
            return "Nama lengkap wajib diisi (2-100 karakter)!";
        }
        if (passWajib || !pass.isEmpty()) {
            if (pass.length() < 6 || pass.length() > 72) {
                return "Password minimal 6 karakter!";
            }
            if (!pass.equals(pass2)) {
                return "Konfirmasi password tidak cocok!";
            }
        }
        if (!noTelp.replaceAll("[\\s\\-().+]", "").matches("^(08|62)\\d{8,13}$")) {
            return "Masukkan nomor WhatsApp yang valid, contoh: 081234567890!";
        }
        if (alamat.length() > 500) {
            return "Alamat maksimal 500 karakter!";
        }
        return null;
    }

    // ==================== HELPER TAMPILAN ====================
    // Tombol oranye (utama) atau abu-abu (sekunder)
    private static void gayaTombol(JButton b, boolean utama) {
        b.setBackground(utama ? ORANGE : TAB_BG);
        b.setForeground(utama ? Color.WHITE : DARK);
        b.setFont(new Font("SansSerif", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    // Label kecil di atas + kolom input di bawahnya (seperti .auth-field di web)
    private static void tambahField(JPanel p, String teks, int y, JComponent input) {
        JLabel l = new JLabel(teks);
        l.setFont(new Font("SansSerif", Font.BOLD, 12));
        l.setForeground(DARK);
        l.setBounds(40, y, 380, 18);
        p.add(l);
        input.setBounds(40, y + 20, 380, 28);
        input.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER), BorderFactory.createEmptyBorder(0, 8, 0, 8)));
        p.add(input);
    }

    // ==================== DASHBOARD (TABEL DATA AKUN) ====================
    private static int selectedRow = -1; // atribut kelas agar bisa dipakai di dalam lambda

    private static void tampilkanDashboard(User login, JFrame loginFrame) {
        boolean isAdmin = login.role.equals("ADMIN");

        JFrame dash = new JFrame();
        dash.setSize(780, 620);
        dash.setTitle("Data Akun - Sumber Lancar (" + login.namaLengkap + " | " + login.role + ")");
        dash.setLocationRelativeTo(null);
        dash.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dash.setLayout(null);
        dash.getContentPane().setBackground(PAGE);

        String[] label = {"Username", "Nama Lengkap", "Password", "No. WhatsApp", "Alamat", "Role"};
        for (int i = 0; i < label.length; i++) {
            JLabel l = new JLabel(label[i]);
            l.setBounds(30, 20 + i * 35, 120, 25);
            dash.add(l);
        }

        JTextField txtUser = new JTextField();
        JTextField txtNama = new JTextField();
        JPasswordField txtPass = new JPasswordField();
        JTextField txtTelp = new JTextField();
        JTextField txtAlamat = new JTextField();
        JComboBox<String> cmbRole = new JComboBox<>(new String[] {"CUSTOMER", "ADMIN"});
        JComponent[] input = {txtUser, txtNama, txtPass, txtTelp, txtAlamat, cmbRole};
        for (int i = 0; i < input.length; i++) {
            input[i].setBounds(160, 20 + i * 35, 240, 25);
            input[i].setEnabled(isAdmin);
            dash.add(input[i]);
        }

        JButton btnCreate = new JButton("Create");
        btnCreate.setBounds(30, 245, 100, 30);
        JButton btnUpdate = new JButton("Update");
        btnUpdate.setBounds(140, 245, 100, 30);
        JButton btnDelete = new JButton("Delete");
        btnDelete.setBounds(250, 245, 100, 30);
        JButton btnReset = new JButton("Reset");
        btnReset.setBounds(360, 245, 100, 30);
        JButton btnEdit = new JButton("Edit");
        btnEdit.setBounds(570, 300, 100, 30);
        JButton btnView = new JButton("View");
        btnView.setBounds(570, 340, 100, 30);
        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(570, 520, 100, 30);
        for (JButton b : new JButton[] {btnCreate, btnUpdate, btnDelete, btnReset, btnEdit, btnView, btnLogout}) {
            gayaTombol(b, b == btnCreate || b == btnUpdate);
            dash.add(b);
        }

        btnCreate.setEnabled(isAdmin);
        btnEdit.setEnabled(isAdmin);
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        // Tabel tidak bisa diedit langsung
        String[] kolom = {"Username", "Nama Lengkap", "No. WhatsApp", "Alamat", "Role"};
        DefaultTableModel model = new DefaultTableModel(kolom, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable tabel = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tabel);
        scrollPane.setBounds(30, 300, 520, 250);
        dash.add(scrollPane);

        // Mengisi ulang tabel dari database
        Runnable muatTabel = () -> {
            try {
                model.setRowCount(0);
                for (User u : getAll()) {
                    model.addRow(new Object[] {u.username, u.namaLengkap, u.noTelepon, u.alamat, u.role});
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dash, "Gagal memuat data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        };

        Runnable resetForm = () -> {
            txtUser.setText("");
            txtNama.setText("");
            txtPass.setText("");
            txtTelp.setText("");
            txtAlamat.setText("");
            cmbRole.setSelectedIndex(0);
            txtUser.setEditable(true);
            selectedRow = -1;
            tabel.clearSelection();
            btnCreate.setEnabled(isAdmin);
            btnUpdate.setEnabled(false);
            btnDelete.setEnabled(false);
        };

        muatTabel.run();

        // ---------- CREATE ----------
        btnCreate.addActionListener(e -> {
            String username = txtUser.getText().trim();
            String nama = txtNama.getText().trim();
            String pass = new String(txtPass.getPassword());
            String telp = txtTelp.getText().trim();
            String alamat = txtAlamat.getText().trim();

            String error = validasi(username, nama, pass, pass, telp, alamat, true);
            if (error != null) {
                JOptionPane.showMessageDialog(dash, error);
                return;
            }
            try {
                if (usernameAda(username)) {
                    JOptionPane.showMessageDialog(dash, "Username sudah dipakai!");
                    return;
                }
                create(new User(username, nama, pass, telp, alamat.isEmpty() ? "-" : alamat,
                    cmbRole.getSelectedItem().toString()));
                muatTabel.run();
                resetForm.run();
                JOptionPane.showMessageDialog(dash, "Data berhasil ditambahkan!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dash, "Gagal menyimpan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ---------- EDIT (binding tabel -> form) ----------
        btnEdit.addActionListener(e -> {
            selectedRow = tabel.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dash, "Pilih baris di tabel terlebih dahulu!");
                return;
            }
            txtUser.setText(model.getValueAt(selectedRow, 0).toString());
            txtNama.setText(model.getValueAt(selectedRow, 1).toString());
            txtTelp.setText(model.getValueAt(selectedRow, 2).toString());
            txtAlamat.setText(model.getValueAt(selectedRow, 3).toString());
            cmbRole.setSelectedItem(model.getValueAt(selectedRow, 4).toString());
            txtPass.setText(""); // kosong = password tidak diubah
            txtUser.setEditable(false);
            btnCreate.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setEnabled(true);
        });

        // ---------- VIEW ----------
        btnView.addActionListener(e -> {
            int row = tabel.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(dash, "Pilih baris di tabel terlebih dahulu!");
                return;
            }
            JOptionPane.showMessageDialog(dash,
                "Username : " + model.getValueAt(row, 0) + "\n" +
                "Nama     : " + model.getValueAt(row, 1) + "\n" +
                "WhatsApp : " + model.getValueAt(row, 2) + "\n" +
                "Alamat   : " + model.getValueAt(row, 3) + "\n" +
                "Role     : " + model.getValueAt(row, 4),
                "Detail Data", JOptionPane.INFORMATION_MESSAGE);
        });

        // ---------- UPDATE ----------
        btnUpdate.addActionListener(e -> {
            String username = txtUser.getText().trim();
            String nama = txtNama.getText().trim();
            String pass = new String(txtPass.getPassword());
            String telp = txtTelp.getText().trim();
            String alamat = txtAlamat.getText().trim();

            String error = validasi(username, nama, pass, pass, telp, alamat, false);
            if (error != null) {
                JOptionPane.showMessageDialog(dash, error);
                return;
            }
            try {
                update(new User(username, nama, pass, telp, alamat.isEmpty() ? "-" : alamat,
                    cmbRole.getSelectedItem().toString()));
                muatTabel.run();
                resetForm.run();
                JOptionPane.showMessageDialog(dash, "Data berhasil diperbarui!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dash, "Gagal memperbarui: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // ---------- DELETE ----------
        btnDelete.addActionListener(e -> {
            String username = model.getValueAt(selectedRow, 0).toString();
            if (username.equalsIgnoreCase(login.username)) {
                JOptionPane.showMessageDialog(dash, "Akun yang sedang login tidak bisa dihapus!");
                return;
            }
            if (JOptionPane.showConfirmDialog(dash, "Hapus akun '" + username + "'?",
                    "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
                return;
            }
            try {
                delete(username);
                muatTabel.run();
                resetForm.run();
                JOptionPane.showMessageDialog(dash, "Data berhasil dihapus!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dash, "Gagal menghapus: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReset.addActionListener(e -> resetForm.run());

        btnLogout.addActionListener(e -> {
            dash.dispose();
            loginFrame.setVisible(true);
        });

        dash.setVisible(true);
    }

    // ==================== PROFIL SAYA (TAMPILAN CUSTOMER) ====================
    private static void tampilkanProfil(User login, JFrame loginFrame) {
        JFrame prof = new JFrame("Profil Saya - Sumber Lancar");
        prof.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel(null);
        p.setBackground(PAGE);
        p.setPreferredSize(new Dimension(480, 440));
        prof.add(p);

        JLabel judul = new JLabel("Halo, " + login.namaLengkap + "!");
        judul.setFont(new Font("SansSerif", Font.BOLD, 20));
        judul.setBounds(40, 20, 380, 30);
        p.add(judul);

        JTextField txtUser = new JTextField(login.username);
        txtUser.setEditable(false); // username tidak boleh diubah
        JTextField txtNama = new JTextField(login.namaLengkap);
        JTextField txtTelp = new JTextField(login.noTelepon);
        JTextField txtAlamat = new JTextField(login.alamat);
        JPasswordField txtPass = new JPasswordField();
        tambahField(p, "Username", 65, txtUser);
        tambahField(p, "Nama Lengkap", 120, txtNama);
        tambahField(p, "No. WhatsApp", 175, txtTelp);
        tambahField(p, "Alamat", 230, txtAlamat);
        tambahField(p, "Password Baru (kosongkan jika tidak diubah)", 285, txtPass);

        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setBounds(40, 365, 185, 40);
        gayaTombol(btnSimpan, true);
        p.add(btnSimpan);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setBounds(235, 365, 185, 40);
        gayaTombol(btnLogout, false);
        p.add(btnLogout);

        btnSimpan.addActionListener(e -> {
            String nama = txtNama.getText().trim();
            String telp = txtTelp.getText().trim();
            String alamat = txtAlamat.getText().trim();
            String pass = new String(txtPass.getPassword());

            String error = validasi(login.username, nama, pass, pass, telp, alamat, false);
            if (error != null) {
                JOptionPane.showMessageDialog(prof, error);
                return;
            }
            try {
                update(new User(login.username, nama, pass, telp, alamat.isEmpty() ? "-" : alamat, login.role));
                login.namaLengkap = nama; // samakan data di memori
                judul.setText("Halo, " + nama + "!");
                txtPass.setText("");
                JOptionPane.showMessageDialog(prof, "Profil berhasil diperbarui!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(prof, "Gagal menyimpan: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnLogout.addActionListener(e -> {
            prof.dispose();
            loginFrame.setVisible(true);
        });

        prof.pack();
        prof.setLocationRelativeTo(null);
        prof.setVisible(true);
    }

    // ==================== MAIN: FORM LOGIN & REGISTER ====================
    public static void main(String[] args) {
        initDatabase(); // isi data akun bawaan

        // Jendela 820 x 540: kiri panel gelap (340), kanan panel form (480)
        JFrame frame = new JFrame("Login & Register - Sumber Lancar");
        frame.getContentPane().setPreferredSize(new Dimension(820, 540));
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ---------- PANEL KIRI (brand) ----------
        JPanel kiri = new JPanel(null);
        kiri.setBackground(DARK);
        kiri.setBounds(0, 0, 340, 540);

        JLabel lblBrand = new JLabel("Sumber Lancar");
        lblBrand.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblBrand.setForeground(ORANGE);
        lblBrand.setBounds(40, 40, 260, 30);
        kiri.add(lblBrand);

        JLabel lblHeadline = new JLabel("<html>Kelola stok bengkel<br><font color='#ff6600'>tanpa ribet.</font></html>");
        lblHeadline.setFont(new Font("SansSerif", Font.BOLD, 30));
        lblHeadline.setForeground(new Color(0xf1f2f4));
        lblHeadline.setBounds(40, 200, 290, 90);
        kiri.add(lblHeadline);

        JPanel garis = new JPanel(); // garis oranye penghias
        garis.setBackground(ORANGE);
        garis.setBounds(0, 440, 340, 14);
        kiri.add(garis);
        frame.add(kiri);

        // ---------- PANEL KANAN (form) ----------
        JPanel kanan = new JPanel(null);
        kanan.setBackground(PAGE);
        kanan.setBounds(340, 0, 480, 540);

        JButton tabLogin = new JButton("Login");
        tabLogin.setBounds(40, 20, 190, 40);
        JButton tabRegister = new JButton("Register");
        tabRegister.setBounds(230, 20, 190, 40);
        kanan.add(tabLogin);
        kanan.add(tabRegister);

        // ---------- FORM LOGIN ----------
        JPanel panelLogin = new JPanel(null);
        panelLogin.setBackground(PAGE);
        panelLogin.setBounds(0, 70, 480, 470);

        JLabel judulLogin = new JLabel("Masuk ke Sumber Lancar");
        judulLogin.setFont(new Font("SansSerif", Font.BOLD, 20));
        judulLogin.setBounds(40, 10, 380, 30);
        panelLogin.add(judulLogin);

        JTextField txtLoginUser = new JTextField();
        JPasswordField txtLoginPass = new JPasswordField();
        tambahField(panelLogin, "Username", 60, txtLoginUser);
        tambahField(panelLogin, "Password", 120, txtLoginPass);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(40, 190, 380, 40);
        gayaTombol(btnLogin, true);
        panelLogin.add(btnLogin);

        // ---------- FORM REGISTER ----------
        JPanel panelRegister = new JPanel(null);
        panelRegister.setBackground(PAGE);
        panelRegister.setBounds(0, 70, 480, 470);

        JLabel judulReg = new JLabel("Buat Akun Baru");
        judulReg.setFont(new Font("SansSerif", Font.BOLD, 20));
        judulReg.setBounds(40, 10, 380, 30);
        panelRegister.add(judulReg);

        JTextField txtRegUser = new JTextField();
        JTextField txtRegNama = new JTextField();
        JPasswordField txtRegPass = new JPasswordField();
        JPasswordField txtRegPass2 = new JPasswordField();
        JTextField txtRegTelp = new JTextField();
        JTextField txtRegAlamat = new JTextField();
        tambahField(panelRegister, "Username", 50, txtRegUser);
        tambahField(panelRegister, "Nama Lengkap", 105, txtRegNama);
        tambahField(panelRegister, "Password", 160, txtRegPass);
        tambahField(panelRegister, "Ulangi Password", 215, txtRegPass2);
        tambahField(panelRegister, "No. WhatsApp", 270, txtRegTelp);
        tambahField(panelRegister, "Alamat (opsional)", 325, txtRegAlamat);

        JButton btnRegister = new JButton("Register");
        btnRegister.setBounds(40, 392, 380, 40);
        gayaTombol(btnRegister, true);
        panelRegister.add(btnRegister);

        kanan.add(panelLogin);
        kanan.add(panelRegister);
        frame.add(kanan);

        // ---------- GANTI TAB ----------
        Runnable[] pilihTab = new Runnable[2]; // [0] = tab login, [1] = tab register
        pilihTab[0] = () -> {
            panelLogin.setVisible(true);
            panelRegister.setVisible(false);
            tabLogin.setBackground(Color.WHITE);
            tabLogin.setForeground(DARK);
            tabRegister.setBackground(TAB_BG);
            tabRegister.setForeground(MUTED);
        };
        pilihTab[1] = () -> {
            panelLogin.setVisible(false);
            panelRegister.setVisible(true);
            tabRegister.setBackground(Color.WHITE);
            tabRegister.setForeground(DARK);
            tabLogin.setBackground(TAB_BG);
            tabLogin.setForeground(MUTED);
        };
        for (JButton t : new JButton[] {tabLogin, tabRegister}) {
            t.setFont(new Font("SansSerif", Font.BOLD, 14));
            t.setFocusPainted(false);
            t.setBorderPainted(false);
            t.setOpaque(true);
            t.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
        tabLogin.addActionListener(e -> pilihTab[0].run());
        tabRegister.addActionListener(e -> pilihTab[1].run());
        pilihTab[0].run();

        // ==================== EVENT LOGIN ====================
        btnLogin.addActionListener(e -> {
            String username = txtLoginUser.getText().trim().toLowerCase();
            String password = new String(txtLoginPass.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username dan password wajib diisi!");
                return;
            }

            User ditemukan;
            try {
                ditemukan = get(username, password);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Gagal mengakses database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (ditemukan == null) {
                JOptionPane.showMessageDialog(frame, "Username atau password salah!", "Login Gagal", JOptionPane.ERROR_MESSAGE);
                txtLoginPass.setText("");
                return;
            }

            txtLoginUser.setText("");
            txtLoginPass.setText("");
            frame.setVisible(false);
            if (ditemukan.role.equals("ADMIN")) {
                tampilkanDashboard(ditemukan, frame);
            } else {
                tampilkanProfil(ditemukan, frame);
            }
        });

        // ==================== EVENT REGISTER ====================
        btnRegister.addActionListener(e -> {
            String username = txtRegUser.getText().trim();
            String nama = txtRegNama.getText().trim();
            String pass = new String(txtRegPass.getPassword());
            String pass2 = new String(txtRegPass2.getPassword());
            String telp = txtRegTelp.getText().trim();
            String alamat = txtRegAlamat.getText().trim();

            String error = validasi(username, nama, pass, pass2, telp, alamat, true);
            if (error != null) {
                JOptionPane.showMessageDialog(frame, error);
                return;
            }

            try {
                if (usernameAda(username)) {
                    JOptionPane.showMessageDialog(frame, "Username sudah dipakai!");
                    return;
                }
                // Pendaftaran selalu membuat role CUSTOMER (sesuai AuthController.php)
                create(new User(username, nama, pass, telp, alamat.isEmpty() ? "-" : alamat, "CUSTOMER"));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Gagal menyimpan ke database: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(frame, "Akun berhasil dibuat. Silakan masuk!", "Register Berhasil", JOptionPane.INFORMATION_MESSAGE);
            for (JTextField t : new JTextField[] {txtRegUser, txtRegNama, txtRegPass, txtRegPass2, txtRegTelp, txtRegAlamat}) {
                t.setText("");
            }
            txtLoginUser.setText(username.toLowerCase());
            pilihTab[0].run(); // pindah otomatis ke tab Login
        });

        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
