package com.ticket.view;

import com.ticket.model.Coupon;
import com.ticket.repository.CouponRepository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CouponManagementPanel extends JPanel {

    private CouponRepository repo = new CouponRepository();
    private DefaultTableModel tableModel;
    private JTable table;

    private JTextField txtCode, txtPercent, txtAmount, txtDate;
    private JCheckBox chkActive;

    public CouponManagementPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Tiêu đề
        JLabel title = new JLabel("Coupon Management", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(title, BorderLayout.NORTH);

        // Bảng dữ liệu
        String[] columns = {"ID", "Code", "Discount %", "Discount Amount", "Expiry Date", "Active"};
        tableModel = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(30);
        table.getSelectionModel().addListSelectionListener(e -> fillForm());
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Form nhập
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Coupon Information"));
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCode    = new JTextField(15);
        txtPercent = new JTextField(15);
        txtAmount  = new JTextField(15);
        txtDate    = new JTextField(15);
        txtDate.setToolTipText("Format: dd/MM/yyyy");
        chkActive  = new JCheckBox("Active", true);

        String[] labels = {"Coupon Code:", "Discount %:", "Discount Amount:", "Expiry Date (dd/MM/yyyy):"};
        JComponent[] fields = {txtCode, txtPercent, txtAmount, txtDate};

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            form.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            form.add(fields[i], gbc);
        }
        gbc.gridx = 1; gbc.gridy = labels.length;
        form.add(chkActive, gbc);

        // Nút bấm
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton btnAdd    = new JButton("➕ Add");
        JButton btnUpdate = new JButton("✏️ Edit");
        JButton btnDelete = new JButton("🗑️ Delete");
        JButton btnClear  = new JButton("🔄 Refresh");

        btnAdd.setBackground(new Color(40, 167, 69));
        btnAdd.setForeground(Color.WHITE);
        btnUpdate.setBackground(new Color(0, 123, 255));
        btnUpdate.setForeground(Color.WHITE);
        btnDelete.setBackground(new Color(220, 53, 69));
        btnDelete.setForeground(Color.WHITE);

        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(btnPanel, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        // Sự kiện
        btnAdd.addActionListener(e -> addCoupon());
        btnUpdate.addActionListener(e -> updateCoupon());
        btnDelete.addActionListener(e -> deleteCoupon());
        btnClear.addActionListener(e -> clearForm());

        loadData();
    }

    private void loadData() {
        tableModel.setRowCount(0);
        List<Coupon> list = repo.getAllCoupons();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        for (Coupon c : list) {
            tableModel.addRow(new Object[]{
                    c.getId(), c.getCode(),
                    c.getDiscountPercent(), c.getDiscountAmount(),
                    c.getExpiredDate() != null ? sdf.format(c.getExpiredDate()) : "",
                    c.isActive() ? "✅" : "❌"
            });
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        txtCode.setText(tableModel.getValueAt(row, 1).toString());
        txtPercent.setText(tableModel.getValueAt(row, 2).toString());
        txtAmount.setText(tableModel.getValueAt(row, 3).toString());
        txtDate.setText(tableModel.getValueAt(row, 4).toString());
        chkActive.setSelected(tableModel.getValueAt(row, 5).equals("✅"));
    }

    private Coupon buildCoupon() throws Exception {
        String code = txtCode.getText().trim();
        if (code.isEmpty()) throw new Exception("Please enter coupon code!");
        double percent = txtPercent.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtPercent.getText().trim());
        double amount  = txtAmount.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtAmount.getText().trim());
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
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete?", "Confirm", JOptionPane.YES_NO_OPTION);
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
}