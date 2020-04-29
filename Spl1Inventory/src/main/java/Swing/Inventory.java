package Swing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public class Inventory {
    private JFrame frame;
    private JPanel panelInventory,panel;
    private Font f1, f2;
    private JButton inventoryAddButton, inventoryDeleteButton;
    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;

    private JTable inventoryTable;
    private DefaultTableModel inventoryModel;
    private JScrollPane inventoryScrollPane;

    private String[] inventoryColumns = {"Name", "Id", "MRP", "Quantity"};
    private String[] inventoryRows = new String[4];

    OracleConnect oc = new OracleConnect();
    PreparedStatement ps;
    ResultSet rs;


    public JPanel initComponents(JPanel mainPanel){

        this.panel=mainPanel;

        panelInventory = new JPanel();
        panelInventory.setLayout(null);
        panelInventory.setBackground(Color.lightGray);


        f1 = new Font("Arial", Font.BOLD, 15);
        f2 = new Font("Arial", Font.BOLD, 11);


        inventoryDeleteButton = new JButton("Delete");
        inventoryDeleteButton.setBounds(600, 240, 90, 25);
        inventoryDeleteButton.setBackground(Color.cyan);
        inventoryDeleteButton.setFont(f2);
        panelInventory.add(inventoryDeleteButton);
        inventoryDeleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DefaultTableModel d = (DefaultTableModel) inventoryTable.getModel();
                int selectRow = inventoryTable.getSelectedRow();
                String name = d.getValueAt(selectRow, 1).toString();
                int warningMsg = JOptionPane.showConfirmDialog(frame, "Do you want to delete the product?", "DELETE", JOptionPane.YES_NO_OPTION);

                if (warningMsg == JOptionPane.YES_OPTION) {
                    try {
                        String sql1 = "delete from PRODUCT where NAME=?";
                        OracleConnect oc1 = new OracleConnect();
                        PreparedStatement ps1 = oc.conn.prepareStatement(sql1);

                        ps1.setString(1, name);
                        ps1.executeUpdate();

                        String sql2 = "delete from SUPPLY_ORDER where S_NAME=?";
                        OracleConnect oc2 = new OracleConnect();
                        PreparedStatement ps2 = oc.conn.prepareStatement(sql2);

                        ps2.setString(1, name);
                        ps2.executeUpdate();

                        table_update_inventory();

                    } catch (Exception ex) {
                        System.out.println(ex + " inventory delete");
                    }
                }

            }
        });


        inventoryTable = new JTable();
        inventoryModel = new DefaultTableModel();
        inventoryScrollPane = new JScrollPane(inventoryTable);
        inventoryModel.setColumnIdentifiers(inventoryColumns);
        inventoryTable.setModel(inventoryModel);
        inventoryTable.setFont(f1);
        inventoryTable.setBackground(Color.WHITE);
        inventoryTable.setSelectionBackground(Color.GRAY);
        inventoryTable.setRowHeight(30);

        inventoryScrollPane.setBounds(150,350,1000,300);
        panelInventory.add(inventoryScrollPane);


        table_update_inventory();

        return panelInventory;
    }

    public void table_update_inventory() {
        int n;
        try {
            String sql = "select P_ID,NAME,MRP,S_QUANTITY from PRODUCT , SUPPLY_ORDER where PRODUCT.S_NAME=SUPPLY_ORDER.S_NAME ";
            ps = oc.conn.prepareStatement(sql);
            rs = ps.executeQuery();
            ResultSetMetaData rsd = rs.getMetaData();
            n = rsd.getColumnCount();

            DefaultTableModel d = (DefaultTableModel) inventoryTable.getModel();
            d.setRowCount(0);

            while (rs.next()) {
                Vector v = new Vector();

                for (int i = 1; i <= n; i++) {

                    v.add(rs.getInt("P_ID"));
                    v.add(rs.getString("NAME"));
                    v.add(rs.getInt("MRP"));
                    v.add(rs.getInt("S_QUANTITY"));


                }
                d.addRow(v);
            }


        } catch (Exception e) {
            System.out.println("table_update_inventory");
        }
        finally {
            try {
                rs.close();
                ps.close();

            } catch (SQLException e) {
                System.out.println("table_update_inventory");
            }
        }
    }
}
