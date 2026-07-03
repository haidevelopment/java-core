package com.ticket.view;

import com.ticket.model.Coupon;
import com.ticket.repository.CouponRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CouponManagementPanel extends JPanel {

    private CouponRepository repo = new CouponRepository();
    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField txtCode, txtPercent, txtAmount, txtDate, txtKeyword;
    private JCheckBox chkActive;
    private JComboBox<String> cbActiveFilter;
    private JLabel lblStats;
    private List<Coupon> allCouponsCache;

    public CouponManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(28, 32, 28, 32));

        initComponents();
        loadData();
    }

    private void initComponents() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(new Color(245, 247, 250));

        // ── Header ──
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 247, 250));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JLabel title = new JLabel("Coupon Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(15, 23, 42));
        headerPanel.add(title, BorderLayout.WEST);
        mainContent.add(headerPanel);
        mainContent.add(Box.createRigidArea(new Dimension(0, 20)));

        // ── Filter Bar ──
        mainContent.add(createFilterBar());
        mainContent.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── Table ──
        String[] columns = {"ID", "Code", "Discount %", "Discount Amount", "Expiry Date", "Active"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> fillForm());

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setBackground(Color.WHITE);

        JPanel tableContainer = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
                g2.dispose();
            }
        };
        tableContainer.setOpaque(false);
        tableContainer.setBorder(new EmptyBorder(15, 15, 15, 15));
        tableContainer.add(tableScroll, BorderLayout.CENTER);
        mainContent.add(tableContainer);
        mainContent.add(Box.createRigidArea(new Dimension(0, 16)));

        // ── Form ──
        mainContent.add(createFormPanel());

        JScrollPane outerScroll = new JScrollPane(mainContent);
        outerScroll.setBorder(null);
        outerScroll.getVerticalScrollBar().setUnitIncrement(16);
        outerScroll.setBackground(new Color(245, 247, 250));
        outerScroll.getViewport().setBackground(new Color(245, 247, 250));
        add(outerScroll, BorderLayout.CENTER);
    }

    private JPanel createFilterBar() {
        JPanel bar = new JPanel(new BorderLayout(16, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 14, 14));
                g2.setColor(new Color(0, 0, 0, 8));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 14, 14));
                g2.dispose();
            }
        };
        bar.setOpaque(false);
        bar.setBorder(new EmptyBorder(16, 20, 16, 20));
        bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JPanel filterGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterGroup.setOpaque(false);

        JLabel lblKw = new JLabel("🔍");
        lblKw.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        filterGroup.add(lblKw);

        txtKeyword = new JTextField(14);
        txtKeyword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtKeyword.setPreferredSize(new Dimension(160, 36));
        txtKeyword.putClientProperty("JTextField.placeholderText", "Mã coupon...");
        filterGroup.add(txtKeyword);

        filterGroup.add(new JLabel("TT:"));
        cbActiveFilter = new JComboBox<>(new String[]{"[Tất cả]", "active", "inactive"});
        cbActiveFilter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cbActiveFilter.setPreferredSize(new Dimension(120, 36));
        cbActiveFilter.setBackground(Color.WHITE);
        filterGroup.add(cbActiveFilter);

        bar.add(filterGroup, BorderLayout.WEST);

        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionGroup.setOpaque(false);

        lblStats = new JLabel("");
        lblStats.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblStats.setForeground(new Color(100, 116, 139));
        actionGroup.add(lblStats);

        JButton btnSearch = createSmallButton("Lọc", new Color(59, 130, 246));
        btnSearch.addActionListener(e -> applyFilter());
        JButton btnClear = createSmallButton("Xóa lọc", new Color(148, 163, 184));
        btnClear.addActionListener(e -> clearFilter());

        actionGroup.add(btnSearch);
        actionGroup.add(btnClear);
        bar.add(actionGroup, BorderLayout.EAST);

        txtKeyword.addActionListener(e -> applyFilter());
        return bar;
    }

    private JButton createSmallButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = color;
                if (getModel().isPressed()) bg = color.darker();
                else if (getModel().isRollover()) bg = color.brighter();
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(90, 36));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void applyFilter() {
        String kw = txtKeyword.getText().trim();
        String activeFilter = (String) cbActiveFilter.getSelectedItem();
        List<Coupon> filtered = repo.searchCoupons(kw, activeFilter);
        renderTable(filtered);
        lblStats.setText(filtered.size() + " / " + (allCouponsCache != null ? allCouponsCache.size() : 0) + " coupons");
    }

    private void clearFilter() {
        txtKeyword.setText("");
        cbActiveFilter.setSelectedItem("[Tất cả]");
        applyFilter();
    }

    // ═══════════════════════════════════════════
    //  FORM
    // ═══════════════════════════════════════════
    private JPanel createFormPanel() {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 18, 18));
                g2.setColor(new Color(0, 0, 0, 8));
                g2.draw(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, 18, 18));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCode = new JTextField(15);
        txtPercent = new JTextField(15);
        txtAmount = new JTextField(15);
        txtDate = new JTextField(15);
        txtDate.setToolTipText("dd/MM/yyyy");
        chkActive = new JCheckBox("Active", true);
        chkActive.setOpaque(false);

        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        txtCode.setFont(fieldFont);
        txtPercent.setFont(fieldFont);
        txtAmount.setFont(fieldFont);
        txtDate.setFont(fieldFont);

        String[] labels = {"Coupon Code:", "Discount %:", "Discount Amount:", "Expiry Date:"};
        JComponent[] fields = {txtCode, txtPercent, txtAmount, txtDate};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            form.add(lbl, gbc);
            gbc.gridx = 1;
            form.add(fields[i], gbc);
        }
        gbc.gridx = 1; gbc.gridy = labels.length;
        form.add(chkActive, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setOpaque(false);

        JButton btnAdd = createActionButton("➕ Add", new Color(34, 197, 94));
        JButton btnUpdate = createActionButton("✏️ Edit", new Color(59, 130, 246));
        JButton btnDelete = createActionButton("🗑️ Delete", new Color(239, 68, 68));
        JButton btnClear = createActionButton("🔄 Clear", new Color(148, 163, 184));

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        JPanel south = new JPanel(new BorderLayout());
        south.setOpaque(false);
        south.add(form, BorderLayout.CENTER);
        south.add(btnPanel, BorderLayout.SOUTH);

        card.add(south, BorderLayout.CENTER);

        btnAdd.addActionListener(e -> addCoupon());
        btnUpdate.addActionListener(e -> updateCoupon());
        btnDelete.addActionListener(e -> deleteCoupon());
        btnClear.addActionListener(e -> clearForm());

        return card;
    }

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = color;
                if (getModel().isPressed()) bg = color.darker();
                else if (getModel().isRollover()) bg = color.brighter();
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 10, 10));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(120, 36));
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ═══════════════════════════════════════════
    //  DATA
    // ═══════════════════════════════════════════
    private void loadData() {
        allCouponsCache = repo.getAllCoupons();
        applyFilter();
    }

    private void renderTable(List<Coupon> list) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Coupon c : list) {
            tableModel.addRow(new Object[]{
                    c.getId(), c.getCode(),
                    c.getDiscountPercent() != null ? c.getDiscountPercent().toString() : "",
                    c.getDiscountAmount() != null ? c.getDiscountAmount().toString() : "",
                    c.getExpiredDate() != null ? sdf.format(c.getExpiredDate()) : "",
                    c.isActive() ? "✅" : "❌"
            });
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtCode.setText(tableModel.getValueAt(row, 1).toString());
        txtPercent.setText(tableModel.getValueAt(row, 2) != null ? tableModel.getValueAt(row, 2).toString() : "");
        txtAmount.setText(tableModel.getValueAt(row, 3) != null ? tableModel.getValueAt(row, 3).toString() : "");
        txtDate.setText(tableModel.getValueAt(row, 4) != null ? tableModel.getValueAt(row, 4).toString() : "");
        chkActive.setSelected(tableModel.getValueAt(row, 5).equals("✅"));
    }

    private Coupon buildCoupon() throws Exception {
        String code = txtCode.getText().trim();
        if (code.isEmpty()) throw new Exception("Please enter coupon code!");

        String pctText = txtPercent.getText().trim();
        String amtText = txtAmount.getText().trim();

        if (pctText.isEmpty() && amtText.isEmpty())
            throw new Exception("Phai nhap Discount % hoac Discount Amount!");
        if (!pctText.isEmpty() && !amtText.isEmpty())
            throw new Exception("Chi duoc nhap 1 trong 2: Discount % hoac Discount Amount!");

        Double percent = null;
        Double amount = null;

        if (!pctText.isEmpty()) {
            percent = Double.parseDouble(pctText);
            if (percent < 1 || percent > 100)
                throw new Exception("Discount % phai trong khoang 1 den 100!");
        }
        if (!amtText.isEmpty()) {
            amount = Double.parseDouble(amtText);
            if (amount <= 0)
                throw new Exception("Discount Amount phai lon hon 0!");
        }

        Date date = new SimpleDateFormat("dd/MM/yyyy").parse(txtDate.getText().trim());
        Coupon c = new Coupon(code, percent, amount, date);
        c.setActive(chkActive.isSelected());
        return c;
    }

    private void addCoupon() {
        try {
            repo.addCoupon(buildCoupon());
            JOptionPane.showMessageDialog(this, "Coupon added successfully!");
            clearForm(); loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void updateCoupon() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a coupon to edit!"); return; }
        try {
            Coupon c = buildCoupon();
            c.setId((int) tableModel.getValueAt(row, 0));
            repo.updateCoupon(c);
            JOptionPane.showMessageDialog(this, "Updated successfully!");
            clearForm(); loadData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    private void deleteCoupon() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Please select a coupon to delete!"); return; }
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            repo.deleteCoupon((int) tableModel.getValueAt(row, 0));
            JOptionPane.showMessageDialog(this, "Deleted successfully!");
            clearForm(); loadData();
        }
    }

    private void clearForm() {
        txtCode.setText(""); txtPercent.setText("");
        txtAmount.setText(""); txtDate.setText("");
        chkActive.setSelected(true);
        table.clearSelection();
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(235, 245, 251));
    }
}