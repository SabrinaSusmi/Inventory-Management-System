package Swing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.*;

public class Sell {
    private JFrame frame;
    private Font f1, f2;
    private JComboBox sellComboBox;//sell
    private JLabel sellNameLabel;
    private JLabel sellIdLabel;
    private JTextField sellIdTextField;
    private JLabel sellMRPLabel;
    private JTextField sellMRPTextField;
    private JLabel sellQuantityLabel;
    private JLabel sellDateLabel;
    private JTextField sellQuantityTextField, sellDateTextField;//sell
    private JButton invoiceButton, sellSaveButton,sellAddButton,sellUpdateButton;//sell
    private JTable sellTable;
    private JPanel panelSell,Panel;
    private DefaultTableModel  sellModel;
    private JScrollPane  sellScrollPane;

    private String[] sellColumns = {"Name", "Id", "MRP","Quantity","Date"};
    private String[] sellRows = new String[5];

    OracleConnect oc = new OracleConnect();
    PreparedStatement ps;
    ResultSet rs;

    public class productName {
        int id;
        String name;

        public productName(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String toString() {
            return name;
        }
    }


    Sell(JFrame frame){
        this.frame=frame;
    }

    public JPanel initComponents(final JPanel mainPanel){

        this.Panel=mainPanel;

        panelSell = new JPanel();
        panelSell.setLayout(null);
        panelSell.setBackground(new Color(0xD9B9F2));

        f1 = new Font("Arial", Font.BOLD, 15);
        f2 = new Font("Arial", Font.BOLD, 11);

        {
            ///Sell Tab

            sellComboBox = new JComboBox();
            sellComboBox.setBounds(600, 160, 200, 30);
            panelSell.add(sellComboBox);
            sellComboBox.setEditable(false);

            sellNameLabel = new JLabel("Product Name : ");
            sellNameLabel.setBounds(450, 150, 150, 50);
            sellNameLabel.setFont(f1);
            panelSell.add(sellNameLabel);

            sellIdLabel = new JLabel("Product Id : ");
            sellIdLabel.setBounds(450, 200, 150, 50);
            sellIdLabel.setFont(f1);
            panelSell.add(sellIdLabel);


            sellMRPLabel = new JLabel("MRP : ");
            sellMRPLabel.setBounds(450, 250, 150, 50);
            sellMRPLabel.setFont(f1);
            panelSell.add(sellMRPLabel);

            sellQuantityLabel = new JLabel("Quantity : ");
            sellQuantityLabel.setBounds(450, 300, 150, 50);
            sellQuantityLabel.setFont(f1);
            panelSell.add(sellQuantityLabel);

            sellDateLabel = new JLabel("Date : ");
            sellDateLabel.setBounds(450, 350, 150, 50);
            sellDateLabel.setFont(f1);
            panelSell.add(sellDateLabel);

            sellIdTextField = new JTextField();
            sellIdTextField.setBounds(600, 210, 200, 30);
            sellIdTextField.setFont(f1);
            panelSell.add(sellIdTextField);
            sellIdTextField.setEditable(false);

            sellComboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    try {
                        Statement st = oc.conn.createStatement();
                        String sql = "select P_ID,NAME,MRP from PRODUCT , SUPPLY_ORDER where PRODUCT.S_NAME=SUPPLY_ORDER.S_NAME and NAME='" + sellComboBox.getSelectedItem() + "'";
                        ResultSet rs = st.executeQuery(sql);

                        while (rs.next()) {
                            sellIdTextField.setText(String.valueOf(rs.getInt("P_ID")));
                            //  sellManufacturerTextField.setText(rs.getString("MANUFACTURER"));
                            sellMRPTextField.setText(String.valueOf(rs.getInt("MRP")));
                        }


                    } catch (Exception ex) {
                        System.out.println(ex);
                    }
                }
            });


            sellMRPTextField = new JTextField();
            sellMRPTextField.setBounds(600, 260, 200, 30);
            sellMRPTextField.setFont(f1);
            panelSell.add(sellMRPTextField);
            sellMRPTextField.setEditable(false);

            sellQuantityTextField = new JTextField();
            sellQuantityTextField.setBounds(600, 310, 200, 30);
            sellQuantityTextField.setFont(f1);
            panelSell.add(sellQuantityTextField);

            sellDateTextField = new JTextField();
            sellDateTextField.setBounds(600, 360, 200, 30);
            sellDateTextField.setFont(f1);
            panelSell.add(sellDateTextField);

            sellUpdateButton = new JButton("Update");
            sellUpdateButton.setFont(f2);
            sellUpdateButton.setBounds(350, 450, 100, 30);
            sellUpdateButton.setBackground(new Color(0x7E0AB5));
            sellUpdateButton.setForeground(new Color(0xFEFEFE));
            panelSell.add(sellUpdateButton);
            sellUpdateButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    sellTableQtyUpdate(e);
                }
            });

            sellAddButton = new JButton("Add");
            sellAddButton.setFont(f2);
            sellAddButton.setBounds(500, 450, 100, 30);
            sellAddButton.setBackground(new Color(0x7E0AB5));
            sellAddButton.setForeground(new Color(0xFEFEFE));
            sellAddButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        String sql1 = "insert into SALES_DETAILS (P_QUANTITY,P_ID) values(?,?)";
                        PreparedStatement ps1 = oc.conn.prepareStatement(sql1);
                        ps1.setInt(1, Integer.parseInt(sellQuantityTextField.getText()));
                        ps1.setInt(2, Integer.parseInt(sellIdTextField.getText()));
                        ps1.executeUpdate();

                        String sellDate = sellDateTextField.getText();
                        Date date = Date.valueOf(sellDate);
                        OracleConnect oc1 = new OracleConnect();
                        String sql2 = "insert into SALES (SALE_DATE) values(?)";
                        PreparedStatement ps2 = oc1.conn.prepareStatement(sql2);
                        ps2.setDate(1, date);
                        ps2.executeUpdate();


                        DefaultTableModel d = (DefaultTableModel) sellTable.getModel();
                        d.addRow(new Object[]{sellComboBox.getSelectedItem().toString(), Integer.parseInt(sellIdTextField.getText()),
                                Integer.parseInt(sellMRPTextField.getText()), Integer.parseInt(sellQuantityTextField.getText()), date});
                        sellQuantityTextField.setText("");

                        sellTable.addMouseListener(new java.awt.event.MouseAdapter() {
                            public void mouseClicked(java.awt.event.MouseEvent evt) {
                                sellTableMouseClicked(evt);
                            }
                        });
                        sellScrollPane.setViewportView(sellTable);




                    } catch (Exception c) {
                        System.out.println(c + "sell qty ");
                    }
                }
            });
            sellAddButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    sellRows [0] = sellComboBox.getSelectedItem().toString();
                    sellRows [1] = sellIdTextField.getText();
                    sellRows [2] = sellMRPTextField.getText();
                    sellRows [3] = sellQuantityTextField.getText();
                    sellRows [4] = sellDateTextField.getText();
                    sellModel.addRow(sellRows);
                }
            });

            panelSell.add(sellAddButton);

            sellSaveButton = new JButton("Save");
            sellSaveButton.setBounds(650, 450, 100, 30);
            sellSaveButton.setBackground(new Color(0x7E0AB5));
            sellSaveButton.setForeground(new Color(0xFEFEFE));
            sellSaveButton.setFont(f2);
            panelSell.add(sellSaveButton);
            sellSaveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {


                    } catch (Exception ex) {
                        System.out.println(ex + " sell save");
                    }
                }
            });

            invoiceButton = new JButton("Invoice");
            invoiceButton.setBounds(800, 450, 100, 30);
            invoiceButton.setBackground(new Color(0x7E0AB5));
            invoiceButton.setForeground(new Color(0xFEFEFE));

            invoiceButton.setFont(f2);
            panelSell.add(invoiceButton);

            invoiceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    //    new CreateInvoice(frame);
                    TableModel tm = sellTable.getModel();
                    int rowNum = sellTable.getRowCount();
                    Object[] ob = new Object[4];
                    CreateInvoice createInvoice = new CreateInvoice(frame);
                    DefaultTableModel d = (DefaultTableModel) createInvoice.table.getModel();
                    for (int i = 0; i < rowNum; i++) {
                        ob[0] = i;
                        ob[1] = tm.getValueAt(i, 0);
                        ob[2] = tm.getValueAt(i, 2);
                        ob[3] = tm.getValueAt(i, 3);
                        // ob[4] = tm.getValueAt(i, 4);
                        d.addRow(ob);
                    }
                    mainPanel.setVisible(false);
                }
            });

            sellTable = new JTable();
            sellModel = new DefaultTableModel();
            sellScrollPane = new JScrollPane(sellTable);
            sellModel.setColumnIdentifiers(sellColumns);
            sellTable.setModel(sellModel);
            sellTable.setFont(f1);
            sellTable.setBackground(Color.WHITE);
            sellTable.setSelectionBackground(Color.GRAY);
            sellTable.setRowHeight(30);

            sellScrollPane.setBounds(150, 610, 1000, 300);
            panelSell.add(sellScrollPane);
        }



        prodName();
        return panelSell;
    }

    private void prodName() {
        try {
            String sql = "select * from PRODUCT";
            ps = oc.conn.prepareStatement(sql);
            rs = ps.executeQuery();
            //buyComboBox.removeAllItems();
            sellComboBox.removeAllItems();
            while (rs.next()) {
                //buyComboBox.addItem(new Dashboard.productName(rs.getInt(1), rs.getString(2)));
                sellComboBox.addItem(new Sell.productName(rs.getInt(1), rs.getString(2)));
            }


        } catch (Exception c) {
            System.out.println(c);
        }
        finally {
            try {
                rs.close();
                ps.close();

            } catch (SQLException e) {
                System.out.println("prodName");
            }
        }
    }

    private void sellTableMouseClicked(java.awt.event.MouseEvent evt) {
        int row = sellTable.getSelectedRow();
        DefaultTableModel d = (DefaultTableModel) sellTable.getModel();
        sellComboBox.setSelectedItem(d.getValueAt(row, 0).toString());
        sellIdTextField.setText(d.getValueAt(row, 1).toString());
        sellMRPTextField.setText(d.getValueAt(row, 2).toString());
        sellQuantityTextField.setText(d.getValueAt(row, 3).toString());
        sellDateTextField.setText(d.getValueAt(row, 4).toString());
    }

    private void sellTableQtyUpdate(java.awt.event.ActionEvent evt) {
        try {

            OracleConnect oc1 = new OracleConnect();
            String sql = "update SALES_DETAILS set P_QUANTITY=? where P_ID=?";
            PreparedStatement p = oc1.conn.prepareStatement(sql);
            p.setInt(1, Integer.parseInt(sellQuantityTextField.getText()));
            p.setString(2, sellIdTextField.getText());
            p.executeUpdate();

            int i = sellTable.getSelectedRow();
            DefaultTableModel d = (DefaultTableModel) sellTable.getModel();
            String sellDate = sellDateTextField.getText();
            Date date = Date.valueOf(sellDate);
            if (i >= 0) {
                d.setValueAt(sellComboBox.getSelectedItem().toString(), i, 0);
                d.setValueAt(Integer.parseInt(sellIdTextField.getText()), i, 1);
                d.setValueAt(Integer.parseInt(sellMRPTextField.getText()), i, 2);
                d.setValueAt(Integer.parseInt(sellQuantityTextField.getText()), i, 3);
                d.setValueAt(date, i, 4);

            } else {
                JOptionPane.showMessageDialog(frame, "update unsuccessful");
            }

        } catch (Exception w) {
            System.out.println(w + "sellTableQtyUpdate ");
        }

    }


}
