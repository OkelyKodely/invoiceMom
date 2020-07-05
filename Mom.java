package mom;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class Mom {
    public JPanel panel2 = new MyBackground();

    private void createConnectionToDataBase() {
        try {

            String hostName = "localhost";
            String dbName = "mom";
            String userName = "root";
            String passWord = "";
            String url = "jdbc:mysql://" + hostName + ":3320/" + dbName + "?user=" + userName + "&password=" + passWord;
            connection = DriverManager.getConnection(url);
            stmt = connection.createStatement();

        } catch(SQLException sqle) {
            sqle.printStackTrace();
        }
    }
 
     private void connectToDataBase() {
        createConnectionToDataBase();
 
    }
    
    private void setTree() {
        ArrayList list = new ArrayList();
        try {
            String sql = "select id, dat from topinvoices order by inputdate desc;";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next()) {
                list.add(rs.getInt("id") + " " + rs.getString("dat"));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        tree = new JTree(list.toArray());
        setTreeSelectionListenerForTree();
        tree.setBounds(-30, 10, 150, 500);
        tree.setFont(new Font("arial",Font.BOLD,10));
        panel.remove(jp);
        jp = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(jp);
        jp.setBounds(-30, 10, 150, 500);
    }
private JTree tree2 = null;    
    private void setSaveButton() {
        JButton bu = new JButton("Save");
        panel.add(bu);
        bu.setBounds(130, 300, 90, 30);
        bu.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setItemsSaveOntoDb();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }
    
    private void setTreeSelectionListenerForTree() {
        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                int did = -1;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                Object nodeInfo = node.getUserObject();
                String v = nodeInfo.toString();
                if(v != null)
                    if(v.length() > 0) {
                        StringTokenizer stz = new StringTokenizer(v, " ");
                        String s = stz.nextToken();
                        did = Integer.parseInt(s);
                    }
                setActionListenerForDb(did);
                setJTableSwithDb();
                try {
                    String sql = "select amount from topinvoices where id = " + did;
                    ResultSet r = stmt.executeQuery(sql);
                    if(r.next()) {
                        jlabel.setText("Total: $" + r.getString("amount"));
                        jlabel.setForeground(Color.green);
                        jlabel.setBounds(120, 234, 200, 30);
                        panel.updateUI();
                    }
                } catch(SQLException sqlE) {
                    sqlE.printStackTrace();
                }
            }
        });
    }

    JLabel jlabel = new JLabel();

    private void setTreeSelectionListenerForTree2() {
        tree2.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                int did = -1;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree2.getLastSelectedPathComponent();
                Object nodeInfo = node.getUserObject();
                String v = nodeInfo.toString();
                if(v != null)
                    if(v.length() > 0) {
                        StringTokenizer stz = new StringTokenizer(v, " ");
                        String s = stz.nextToken();
                        did = Integer.parseInt(s);
                    }
                setJTableSwithDb2();
                setActionListenerForDb2(did);
            }
        });
    }

    private void refreshUI() {
        panel.remove(jlabel);
        panel.add(jlabel);
        panel.updateUI();
    }
    boolean toggle = true;
    JButton TOGGLE = new JButton("<DOUBLE-CLICK>");
    JButton T1000 = new JButton("<");
    private void setJPanel() {
        panel.remove(T1000);
        panel.add(T1000);
        panel.remove(TOGGLE);
        panel.add(TOGGLE);
        TOGGLE.setBackground(Color.PINK);
        TOGGLE.setForeground(Color.BLUE);
        TOGGLE.setFont(new Font("arial", Font.BOLD, 7));
        TOGGLE.setBounds(125, 333, 100, 33);
        T1000.setBounds(80, 10, 45, 500);
        T1000.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggle = ! toggle;
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Thread trd = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Mom.this.frame.dispose();
                                setUI();
                                setBackgroundImages();
                                try {
                                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                                    Object nodeInfo = node.getUserObject();
                                    String v = nodeInfo.toString();
                                    if(v != null)
                                        if(v.length() > 0) {
                                            StringTokenizer stz = new StringTokenizer(v, " ");
                                            String s = stz.nextToken();
                                            did = Integer.parseInt(s);
                                        }
                                    try {
                                        String sql = "select amount from topinvoices where id = " + did;
                                        ResultSet r = stmt.executeQuery(sql);
                                        if(r.next()) {
                                            JLabel label = new JLabel("Total:$" + r.getString("amount"));
                                            panel.add(label);
                                            label.setForeground(Color.green);
                                            label.setBounds(10, 500, 200, 30);
                                        }
                                    } catch(SQLException sqlE) {
                                        sqlE.printStackTrace();
                                    }
                                } catch(Exception eeee) {
                                    eeee.printStackTrace();
                                }
                            }
                        });
                        trd.start();
                    }
                });
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        TOGGLE.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggle = ! toggle;
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Thread trd = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Mom.this.frame.dispose();
                                setUI();
                                setBackgroundImages();
                                try {
                                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                                    Object nodeInfo = node.getUserObject();
                                    String v = nodeInfo.toString();
                                    if(v != null)
                                        if(v.length() > 0) {
                                            StringTokenizer stz = new StringTokenizer(v, " ");
                                            String s = stz.nextToken();
                                            did = Integer.parseInt(s);
                                        }
                                    try {
                                        String sql = "select amount from topinvoices where id = " + did;
                                        ResultSet r = stmt.executeQuery(sql);
                                        if(r.next()) {
                                            JLabel label = new JLabel("Total:$" + r.getString("amount"));
                                            panel.add(label);
                                            label.setForeground(Color.green);
                                            label.setBounds(10, 500, 200, 30);
                                        }
                                    } catch(SQLException sqlE) {
                                        sqlE.printStackTrace();
                                    }
                                } catch(Exception eeee) {
                                    eeee.printStackTrace();
                                }
                            }
                        });
                        trd.start();
                    }
                });
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }
    
    private void setBackgroundImages() {
        setJPanel();
        setTree();
        setNewButton();
        setTransferButten();
        setInvoiceLabel();
        setFromLabel();
        setFromYourName();
        setFromYourAddress();
        setToYourName();
        setToYourAddress();
        setToLabel();
        setDaItemsHeader();
        setJTableSwithDb();
        setSaveButton();
        setViewAll();
        setTreeSelectionListenerForTree();
        setDaPrintInvoiceButton();
        refreshUI();
    }

    JButton bu = new JButton("View All");
                JFrame j1 = new JFrame("View All");
    
    private void setViewAll() {
        panel.add(bu);
        bu.setBounds(130, 400, 90, 30);
        bu.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                j1.setBounds(0, 0, 500, 350);
                panel2.setBounds(j1.getBounds());
                j1.add(panel2);
                j1.setLayout(null);
                panel2.setLayout(null);
                j1.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ArrayList list = new ArrayList();
                try {
                    String sql = "select id, dat from invoices order by inputdate desc;";
                    ResultSet rs = stmt.executeQuery(sql);
                    while(rs.next()) {
                        list.add(rs.getInt("id") + " " + rs.getString("dat"));
                    }
                } catch(Exception e3) {
                    e3.printStackTrace();
                }
                tree2 = new JTree(list.toArray());
                setTreeSelectionListenerForTree2();
                tree2.setBounds(-30, 10, 150, 250);
                tree2.setFont(new Font("arial",Font.BOLD,10));
                panel2.remove(jp2);
                jp2 = new JScrollPane(tree2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                panel2.add(jp2);
                jp2.setBounds(-30, 10, 150, 250);
                j1.setVisible(true);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }
    
    private void setDaPrintInvoiceButton() {

        JButton b = new JButton("Print Invoice..");
        b.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                PrintRequestAttributeSet pras
                        = new HashPrintRequestAttributeSet();
                pras.add(new Copies(1));

                try {

                    boolean printAccepted = lbl.print(JTable.PrintMode.FIT_WIDTH,
                            new MessageFormat("Author Table"),
                            new MessageFormat("Page - {0}"),
                            false, pras, false);

                    //..closeFrame();
                    if (!printAccepted) {
                        throw new RuntimeException("User cancels the printer job!");
                    }

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        b.setBounds(150, 500, 110, 20);
        panel.add(b);
    }
    
    private void setItemsSaveOntoDb() {
        try {
            int did = -1;
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            Object nodeInfo = node.getUserObject();
            String v = nodeInfo.toString();
            if(v != null)
                if(v.length() > 0) {
                    StringTokenizer stz = new StringTokenizer(v, " ");
                    String s = stz.nextToken();
                    did = Integer.parseInt(s);
                }
            if(did == -1)
                return;
            String sql = "delete from items where invoiceid = "+did+";";
            stmt.execute(sql);
            sql = "select max(id) as id from items;";
            ResultSet rs = stmt.executeQuery(sql);
            int theid = 0;
            try {
                if(rs.next())
                    theid = rs.getInt("id") + 1;
                else
                    theid = 1;
            } catch(SQLException sql1) {
                sql1.printStackTrace();
            }
            DefaultTableModel model = (DefaultTableModel) lbl.getModel();
            int ii = 0;
            while(ii < model.getRowCount()) {
                String zero = (String) model.getValueAt(ii, 0);
                String one = (String) model.getValueAt(ii, 1);
                String two = (String) model.getValueAt(ii, 2);
                String three = (String) model.getValueAt(ii, 3);
                String four = (String) model.getValueAt(ii, 4);
                sql = "insert into items (id, invoiceid, item, descr, price, qty, amount) values ("+theid+", "+did+",'"+zero+"','"+one+"','"+two+"','"+three+"','"+four+"');";
                stmt.execute(sql);
                ii++;
                theid++;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void setActionListenerForDb(int did) {
        try {
            DefaultTableModel model = (DefaultTableModel) lbl.getModel();
            for(int i = 0; i < model.getRowCount(); i++) {
                model.removeRow(i);
            }

            String idid = did + "";
            String item = "", descr = "", price = "", qty = "", amount = "";
            String sql = "select * from items where invoiceid = "+idid+";";

            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()) {
                rs.absolute(0);
                while(rs.next()) {
                    item = rs.getString("item");
                    descr = rs.getString("descr");
                    price = rs.getString("price");
                    qty = rs.getString("qty");
                    amount = rs.getString("amount");
                    model = (DefaultTableModel) lbl.getModel();
                    if(start) {
                        start = false;
                        model.addColumn("Item");
                        model.addColumn("Description");
                        model.addColumn("Price");
                        model.addColumn("Qty");
                        model.addColumn("Amount");
                    }
                    model.addRow(new Object[]{item, descr, price, qty, amount});
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void setActionListenerForDb2(int did) {
        try {
            DefaultTableModel model = (DefaultTableModel) lbl2.getModel();
            for(int i = 0; i < model.getRowCount(); i++) {
                model.removeRow(i);
            }

            String idid = did + "";
            String item = "", qty = "", price = "", id = "";
            String sql = "select * from items where invoiceid = "+idid+";";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()) {
                rs.absolute(0);
                while(rs.next()) {
                    id = rs.getString("id");
                    item = rs.getString("item");
                    qty = rs.getString("qty");
                    price = rs.getString("price");
                    model = (DefaultTableModel) lbl2.getModel();
                    if(start2) {
                        start2 = false;
                        model.addColumn("Id");
                        model.addColumn("Item");
                        model.addColumn("Qty");
                        model.addColumn("Price");
                    }
                    model.addRow(new Object[]{id, item, qty, price});
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    
       
    private void setJTableSwithDb2() {
        JTable label = new JTable(df2);
        label.setFont(new Font("arial",Font.BOLD,11));
        label.setForeground(Color.gray);
        label.setBackground(Color.WHITE);
        label.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent eee) {
                try {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree2.getLastSelectedPathComponent();
                    Object nodeInfo = node.getUserObject();
                    String v = nodeInfo.toString();
                    if(v != null)
                        if(v.length() > 0) {
                            StringTokenizer stz = new StringTokenizer(v, " ");
                            String s = stz.nextToken();
                            did = Integer.parseInt(s);
                        }
                } catch(Exception eeee) {
                    eeee.printStackTrace();
                }
                int column = 0;
                int row = label.getSelectedRow();
                String value0 = label.getModel().getValueAt(row, 0).toString();
                String value1 = label.getModel().getValueAt(row, column).toString();
                String value2 = label.getModel().getValueAt(row, 1).toString();
                String value3 = label.getModel().getValueAt(row, 2).toString();
                JFrame j = new JFrame("Item");
                JPanel p = new JPanel();
                j.setBounds(0, 0, 800, 934);
                p.setBounds(j.getBounds());
                j.add(p);
                j.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                j.setVisible(true);
                JButton bb = new JButton("Delete Selected Invoice");
                bb.setBounds(90, 800, 900, 80);
                Font fff = new Font("arial", Font.PLAIN,  40);
                bb.setFont(fff);
                p.add(bb);
                bb.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        /**-1*/try {
                            String sqls = "delete from invoices where id = "+did+";";
                            stmt.execute(sqls);
                            String sqls2 = "delete from topinvoices where id = "+did+";";
                            stmt .execute(  sqls2);
                            j.dispose();
                            j1.dispose();
                            bu.doClick();
                        } catch (SQLException sqlE) {
                            sqlE.printStackTrace();
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
                JLabel lblItem = new JLabel("Item Name:");
                JLabel lblDesci = new JLabel("Descr:");
                JLabel lblPri = new JLabel("Price:");
                JLabel lblQty = new JLabel("Qty:");
                JLabel lblAtm = new JLabel("Amt.:");
                lblItem.setBounds(10, 120, 140, 20);
                lblDesci.setBounds(10, 160, 140, 20);
                lblPri.setBounds(10, 200, 140, 20);
                lblQty.setBounds(10, 240, 140, 20);
                lblAtm.setBounds(10, 280, 140, 20);
                p.add(lblItem);
                p.add(lblDesci);
                p.add(lblPri);
                p.add(lblQty);
                p.add(lblAtm);
                JLabel fromName = new JLabel("From Name:");
                JLabel toName = new JLabel("To Name:");
                JLabel fromAddress = new JLabel("From Address:");
                JLabel toAddress = new JLabel("To Address:");
                fromName.setBounds(10, 10, 140, 20);
                fromAddress.setBounds(10, 40, 140, 20);
                toName.setBounds(10, 70, 140, 20);
                toAddress.setBounds(10, 100, 140, 20);
                p.add(fromName);
                p.add(toName);
                j.setLayout(null);
                p.setLayout(null);
                p.add(fromAddress);
                p.add(toAddress);
                JTextField fName = new JTextField();
                JTextField tName = new JTextField();
                JTextField fAddress = new JTextField();
                JTextField tAddress = new JTextField();
                fName.setBounds(160, 10, 140, 20);
                tAddress.setBounds(160, 40, 140, 20);
                fAddress.setBounds(160, 70, 140, 20);
                tName.setBounds(160, 100, 140, 20);
                p.add(fName);
                p.add(tName);
                p.add(fAddress);
                p.add(tAddress);
                JTextField ite = new JTextField();
                JTextField desci = new JTextField();
                JTextField pri = new JTextField();
                JTextField qt = new JTextField();
                JTextField at = new JTextField();
                ite.setBounds(160, 120, 140, 20);
                desci.setBounds(160, 160, 140, 20);
                pri.setBounds(160, 200, 140, 20);
                qt.setBounds(160, 240, 140, 20);
                at.setBounds(160, 280, 140, 20);
                p.add(ite);
                p.add(desci);
                p.add(pri);
                p.add(qt);
                p.add(at);
                JButton b = new JButton("Update.");
                b.setBounds(10, 450, 100, 20);
                p.add(b);
                b.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        /**1*/try {
                            String sqls = "update invoices set fromName = '"+fName.getText()+"', fromAddress = '"+fAddress.getText()+"', toName = '"+tName.getText()+"', toAddress = '"+tAddress.getText()+"' where id = "+did+";";
                            stmt.execute(sqls);
                        } catch (SQLException sqlE) {
                            sqlE.printStackTrace();
                        }
                        /**2*/try {
                int row = label.getSelectedRow();
                String value0 = label.getModel().getValueAt(row, 0).toString();

                            String sqls = "update items set item = '"+ite.getText()+"', descr = '"+desci.getText()+"', price = "+pri.getText()+", qty = "+qt.getText()+", amount = "+at.getText()+" where id="+value0+";";
             System.out.println(sqls);
                            stmt.execute(sqls);

                            DefaultTableModel model = (DefaultTableModel) label.getModel();
                            setRefreshTableOfAll(model, ite, qt, pri, label.getSelectedRow()+"");

                        } catch (SQLException sqlE) {
                            sqlE.printStackTrace();
                        }
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
                try {
                    String sqls = "select * from items where invoiceid = "+did+";";
                    ResultSet rs = stmt.executeQuery(sqls);
                    if(rs.next()) {
                        ite.setText(rs.getString("item"));
                        desci.setText(rs.getString("descr"));
                        pri.setText(rs.getString("price"));
                        qt.setText(rs.getString("qty"));
                        at.setText(rs.getString("amount"));
                    }
                } catch (SQLException sqlE) {
                    sqlE.printStackTrace();
                }
                try {
                    String sqls = "select * from invoices where id = "+did+";";
                    ResultSet rs = stmt.executeQuery(sqls);
                    if(rs.next()) {
                        fName.setText(rs.getString("fromName"));
                        fAddress.setText(rs.getString("fromAddress"));
                        tName.setText(rs.getString("toName"));
                        tAddress.setText(rs.getString("toAddress"));
                    }
                } catch (SQLException sqlE) {
                    sqlE.printStackTrace();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                gr = panel.getGraphics();
                gr.setColor(Color.WHITE);
                gr.fillRect(220, 230, 130, 30);
            }
        });
        t.start();
        if(lbl2 != null)
            panel2.remove(lbl2);

        JLabel labl = new JLabel("Item");
        labl.setFont(new Font("arial",Font.BOLD,20));
        labl.setForeground(Color.black);
        labl.setBackground(Color.WHITE);
        panel2.add(labl);
        labl.setBounds(170, 10, 100, 20);

        JLabel labl2 = new JLabel("Qty");
        labl2.setFont(new Font("arial",Font.BOLD,20));
        labl2.setForeground(Color.black);
        labl2.setBackground(Color.WHITE);
        panel2.add(labl2);
        labl2.setBounds(270, 10, 100, 20);

        JLabel labl3 = new JLabel("Price");
        labl3.setFont(new Font("arial",Font.BOLD,20));
        labl3.setForeground(Color.black);
        labl3.setBackground(Color.WHITE);
        panel2.add(labl3);
        labl3.setBounds(370, 10, 100, 20);

        label.setBounds(690, 110, 130, 30);
        panel2.add(label);
        lbl2 = label;
        label.setBounds(170, 40, 250, 250);
    }

      private void setRefreshTableOfAll(DefaultTableModel mel, JTextField item, JTextField quantity, JTextField price, String value0) {
         String va = value0;
         int rowID = 0;
        
        
        rowID = Integer.parseInt(va);
        mel.setValueAt(item.getText(), rowID, 1);
        mel.setValueAt(quantity.getText(), rowID, 2);
        mel.setValueAt(price.getText(), rowID, 3);
    }
    
    private void setJTableSwithDb() {
        int did = -1;
        try {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
            Object nodeInfo = node.getUserObject();
            String v = nodeInfo.toString();
            if(v != null)
                if(v.length() > 0) {
                    StringTokenizer stz = new StringTokenizer(v, " ");
                    String s = stz.nextToken();
                    did = Integer.parseInt(s);
                }
        } catch(Exception e) {
            e.printStackTrace();
        }
        JTable label = new JTable(df);
        label.setFont(new Font("arial",Font.BOLD,11));
        label.setForeground(Color.gray);
        label.setBackground(Color.WHITE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {            }
        });
        t.start();
        panel.add(label);
        label.setBounds(590, 150, 580, 300);
        String ynf = "", yaf = "", ynt = "", yat = "";
        try {
            String sql = "select * from topinvoices where id = " + did + ";";
            ResultSet rs = stmt.executeQuery(sql);
            if(rs.next()) {
                ynf = rs.getString("fromName");
                yaf = rs.getString("fromAddress");
                ynt = rs.getString("toName");
                yat = rs.getString("toAddress");
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        yourNameFrom.setText(ynf);
        yourAddressFrom.setText(yaf);
        yourNameTo.setText(ynt);
        yourAddressTo.setText(yat);
        lbl = label;
        
    }
    
    private void setDaItemsHeader() {
        JLabel label = new JLabel("Item");
        label.setFont(new Font("arial",Font.BOLD,20));
        label.setForeground(Color.black);
        label.setBackground(Color.WHITE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                gr = panel.getGraphics();
                gr.setColor(Color.WHITE);
                gr.fillRect(220, 230, 130, 30);
            }
        });
        t.start();
        panel.add(label);
        label.setBounds(590, 110, 130, 30);

        label = new JLabel("Description");
        label.setFont(new Font("arial",Font.BOLD,20));
        label.setForeground(Color.black);
        label.setBackground(Color.WHITE);
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                gr = panel.getGraphics();
                gr.setColor(Color.WHITE);
                gr.fillRect(220, 230, 130, 30);
            }
        });
        t.start();
        panel.add(label);
        label.setBounds(690, 110, 130, 30);

        label = new JLabel("Unit Price");
        label.setFont(new Font("arial",Font.BOLD,20));
        label.setForeground(Color.black);
        label.setBackground(Color.WHITE);
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                gr = panel.getGraphics();
                gr.setColor(Color.WHITE);
                gr.fillRect(220, 230, 130, 30);
            }
        });
        t.start();
        panel.add(label);
        label.setBounds(930, 110, 120, 30);

        label = new JLabel("Qty");
        label.setFont(new Font("arial",Font.BOLD,20));
        label.setForeground(Color.black);
        label.setBackground(Color.WHITE);
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                gr = panel.getGraphics();
                gr.setColor(Color.WHITE);
                gr.fillRect(220, 230, 130, 30);
            }
        });
        t.start();
        panel.add(label);
        label.setBounds(1050, 110, 120, 30);

        label = new JLabel("Amount");
        label.setFont(new Font("arial",Font.BOLD,20));
        label.setForeground(Color.black);
        label.setBackground(Color.WHITE);
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                gr = panel.getGraphics();
                gr.setColor(Color.WHITE);
              
                gr.fillRect(220, 230, 130, 30);
            }
        });
        t.start();
        panel.add(label);
        label.setBounds(1100, 110, 120, 30);
    }
    
    private void setToYourName() {
        JTextField label = new JTextField();
        label.setFont(new Font("arial",Font.BOLD,20));
        label.setForeground(Color.GRAY);
        label.setBackground(Color.WHITE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                gr = panel.getGraphics();
                gr.setColor(Color.WHITE);
                gr.fillRect(220, 230+30+15, 330, 30);
            }
        });
        t.start();
        yourNameTo = label;
        panel.add(label);
        label.setBounds(250, 230+30+15, 330, 30);
    }
    
    private void setToYourAddress() {
        JTextArea label = new JTextArea();
        label.setFont(new Font("arial",Font.BOLD,20));
        label.setForeground(Color.GRAY);
        label.setBackground(Color.WHITE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                gr = panel.getGraphics();
                gr.setColor(Color.WHITE);
                gr.fillRect(220, 230+70+15, 330, 130);
            }
        });
        t.start();
        yourAddressTo = label;
        panel.add(label);
        label.setBounds(250, 230+70+15, 330, 130);
    }

    private void setFromYourName() {
        JTextField label = new JTextField();
        label.setFont(new Font("arial",Font.BOLD,20));
        label.setForeground(Color.GRAY);
        label.setBackground(Color.WHITE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                gr = panel.getGraphics();
                gr.setColor(Color.WHITE);
                gr.fillRect(220, 30+15, 330, 30);
            }
        });
        t.start();
        yourNameFrom = label;
        panel.add(label);
        label.setBounds(250, 30+15, 330, 30);
    }
    
    private void setFromYourAddress() {
        JTextArea label = new JTextArea();
        label.setFont(new Font("arial",Font.BOLD,20));
        label.setForeground(Color.GRAY);
        label.setBackground(Color.WHITE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                gr = panel.getGraphics();
                gr.setColor(Color.WHITE);
                gr.fillRect(220, 70+15, 330, 130);
            }
        });
        t.start();
        yourAddressFrom = label;
        panel.add(label);
        label.setBounds(250, 70+15, 330, 130);
    }

    private void setFromLabel() {
        JLabel label = new JLabel("FROM");
        label.setFont(new Font("arial",Font.BOLD,20));
        label.setForeground(Color.WHITE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                gr = panel.getGraphics();
                gr.setColor(Color.WHITE);
                gr.fillRect(220, 10, 130, 30);
            }
        });
        t.start();
        panel.add(label);
        label.setBounds(230, 10, 130, 30);
    }
    
    private void setInvoiceLabel() {
        JLabel label = new JLabel("INVOICE");
        label.setFont(new Font("arial",Font.BOLD,30));
        
        panel.add(label);
        label.setBounds(930, 10, 130, 30);
    }

    private void setToLabel() {
        JLabel label = new JLabel("TO");
        label.setFont(new Font("arial",Font.BOLD,20));
        label.setForeground(Color.WHITE);
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                gr = panel.getGraphics();
                gr.setColor(Color.WHITE);
                gr.fillRect(220, 230, 130, 30);
            }
        });
        t.start();
        panel.add(label);
        label.setBounds(230, 230, 130, 30);
    }

    private void setTransferButten() {
        JButton button = new JButton("Transfer");
        button.setBounds(130, 150, 90, 40);
        panel.remove(button);
        panel.add(button);
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    String sql = "delete from topinvoices;";
                    stmt.execute(sql);
                    sql = "insert into topinvoices select * from invoices order by id desc limit 10;";
                    stmt.execute(sql);
                    setTree();
                    panel.updateUI();
                    tree.updateUI();
                } catch(SQLException sqle) {
                    sqle.printStackTrace();
                }
            }        
            @Override
            public void mousePressed(MouseEvent e) {
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }
    
    private void setNewButton() {
        JButton button0 = new JButton("New Items");
        button0.setBounds(130, 60, 110, 40);
        panel.remove(button0);
        panel.add(button0);
        button0.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
                JFrame jj = new JFrame("New Items");
                JPanel pp = new MyBackground();
                jj.setLayout(null);
                jj.setBounds(0, 0, 500, 800);
                pp.setBounds(jj.getBounds());
                jj.add(pp);
                pp.setLayout(null);
                jj.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                jj.setVisible(true);
                int did = -1;
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
                Object nodeInfo = node.getUserObject();
                String v = nodeInfo.toString();
                if(v != null)
                    if(v.length() > 0) {
                        StringTokenizer stz = new StringTokenizer(v, " ");
                        String s = stz.nextToken();
                        did = Integer.parseInt(s);
                    }
                JLabel label1 = new JLabel("Item");
                JLabel label2 = new JLabel("Descr");
                JLabel label3 = new JLabel("Unit Price");
                JLabel label4 = new JLabel("Qty");
                JLabel label5 = new JLabel("Amount");
                pp.add(label1);
                pp.add(label2);
                pp.add(label3);
                pp.add(label4);
                pp.add(label5);
                label1.setForeground(Color.WHITE);
                label2.setForeground(Color.WHITE);
                label3.setForeground(Color.WHITE);
                label4.setForeground(Color.WHITE);
                label5.setForeground(Color.WHITE);
                label1.setBounds(10, 10, 100, 20);
                label2.setBounds(10, 30, 100, 20);
                label3.setBounds(10, 50, 100, 20);
                label4.setBounds(10, 90, 100, 20);
                label5.setBounds(10, 110, 100, 20);
                JTextField f1 = new JTextField();
                JTextField f2 = new JTextField();
                JTextField f3 = new JTextField();
                JTextField f4 = new JTextField();
                JTextField f5 = new JTextField();
                pp.add(f1);
                pp.add(f2);
                pp.add(f3);
                pp.add(f4);
                pp.add(f5);
                f1.setBounds(150, 10, 100, 20);
                f2.setBounds(150, 30, 100, 20);
                f3.setBounds(150, 50, 100, 20);
                f4.setBounds(150, 90, 100, 20);
                f5.setBounds(150, 110, 100, 20);
                JButton b = new JButton("Insert");
                b.setBounds(10, 200, 200, 20);
                pp.add(b);
                b.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        DefaultTableModel model = (DefaultTableModel) lbl.getModel();
                        if(start) {
                            start = false;
                            model.addColumn("Item");
                            model.addColumn("Description");
                            model.addColumn("Price");
                            model.addColumn("Qty");
                            model.addColumn("Amount");
                        }
                        model.addRow(new Object[]{f1.getText(), f2.getText(), f3.getText(), f4.getText(), f5.getText()});
                        jj.dispose();
                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        JButton button = new JButton("New");
        button.setBounds(130, 10, 90, 40);
        panel.remove(button);
        panel.add(button);
        button.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    JFrame jj = new JFrame("New");
                    JPanel pp = new MyBackground();
                    jj.setLayout(null);
                    jj.setBounds(0, 0, 500, 800);
                    pp.setBounds(jj.getBounds());
                    jj.add(pp);
                    pp.setLayout(null);
                    jj.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    jj.setVisible(true);
                    JLabel label = new JLabel("Date - " + (new Date().getMonth()+1) + new Date().getDate() + (new Date().getYear()+1900));
                    JLabel label1 = new JLabel("For Date:");
                    JLabel label2 = new JLabel("Note");
                    JLabel label3 = new JLabel("From Name");
                    JLabel label4 = new JLabel("From Address");
                    JLabel label5 = new JLabel("To Name");
                    JLabel label6 = new JLabel("To Address");
                    JLabel label7 = new JLabel("Amt.");
                    JLabel label8 = new JLabel("");
                    label.setForeground(Color.WHITE);
                    label1.setForeground(Color.WHITE);
                    label2.setForeground(Color.WHITE);
                    label3.setForeground(Color.WHITE);
                    label4.setForeground(Color.WHITE);
                    label5.setForeground(Color.WHITE);
                    label6.setForeground(Color.WHITE);
                    label7.setForeground(Color.WHITE);
                    label8.setForeground(Color.WHITE);
                    pp.add(label1);
                    pp.add(label2);
                    pp.add(label3);
                    pp.add(label4);
                    pp.add(label5);
                    pp.add(label6);
                    pp.add(label7);
                    JTextField jt = new JTextField("");
                    JTextField note = new JTextField();
                    JTextField fromName = new JTextField();
                    jt.setBounds(100, 30, 100, 20);
                    label1.setBounds(0, 30, 100, 20);
                    note.setBounds(100, 90, 100, 20);
                    label2.setBounds(0, 90, 100, 20);
                    pp.add(note);
                    fromName.setBounds(100, 110, 100, 20);
                    label3.setBounds(0, 110, 100, 20);
                    pp.add(fromName);
                    JTextField fromAddress = new JTextField();
                    label4.setBounds(0, 150, 100, 20);
                    fromAddress.setBounds(100, 150, 100, 20);
                    JTextField toName = new JTextField();
                    toName.setBounds(100, 190, 100, 20);
                    label5.setBounds(0, 190, 100, 20);
                    pp.add(toName);
                    JTextField toAddress = new JTextField();
                    label6.setBounds(0, 230, 100, 20);
                    toAddress.setBounds(100, 230, 100, 20);
                    pp.add(toAddress);
                    pp.add(fromAddress);
                    pp.add(label);
                    pp.add(jt);
                    JTextField amt = new JTextField();
                    label7.setBounds(0, 290, 100, 20);
                    amt.setBounds(100, 290, 100, 20);
                    pp.add(amt);
                    label.setBounds(10, 10, 100, 20);
                    JButton b = new JButton("Add to invoices");
                    b.setBounds(10, 400, 190, 30);
                    pp.add(b);
                    b.addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            try {
                                String sql = "select id from invoices order by id desc;";
                                ResultSet rs = stmt.executeQuery(sql);
                                if(rs.next()) {
                                    int id = rs.getInt("id") + 1;
                                    sql = "insert into invoices (id, dat, inputdate, note, amount, fromName, fromAddress, toName, toAddress) values ("+id+",'"+jt.getText()+"',now(),'"+note.getText()+"',"+amt.getText()+",'"+fromName.getText()+"','"+fromAddress.getText()+"','"+toName.getText()+"','"+toAddress.getText()+"');";
                                }
                                else {
                                    int id = 1;
                                    sql = "insert into invoices (id, dat, inputdate, note, amount, fromName, fromAddress, toName, toAddress) values ("+id+",'"+jt.getText()+"',now(),'"+note.getText()+"',"+amt.getText()+",'"+fromName.getText()+"','"+fromAddress.getText()+"','"+toName.getText()+"','"+toAddress.getText()+"');";
                                }
                                stmt.execute(sql);
                            } catch(SQLException sqle) {
                                sqle.printStackTrace();
                            }
                        }

                        @Override
                        public void mousePressed(MouseEvent e) {
                        }

                        @Override
                        public void mouseReleased(MouseEvent e) {
                        }

                        @Override
                        public void mouseEntered(MouseEvent e) {
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                        }
                    });
                } catch(Exception e1) {
                    e1.printStackTrace();
                }
            }

        
            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }
    
    public class MyBackground extends JPanel {

        public MyBackground() {
            setBackground(new Color(0, true));
        }

        @Override
        public void paintComponent(Graphics g) {
            //Paint background first
            Image image = new ImageIcon("bg.jpg").getImage();
            g.drawImage (image, 0, 0, getWidth (), getHeight (), this);

            //Paint the rest of the component. Children and self etc.
            super.paintComponent(g);
        }
    }

    private void setUI(){
        frame = new JFrame();
        frame.setLayout(null);
        panel = new MyBackground();
        panel.setLayout(null);
        frame.setTitle("Mom");
        if(toggle) {
            frame.setBounds(0, 0, 1200, 600);
            T1000.setText("<");
        } else {
            frame.setBounds(0, 0, 250, 600);
            T1000.setText(">");
        }
       panel.setBounds(frame.getBounds());
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Thread trd = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mom YoungSookCho = new  Mom();
                    }
                });
                trd.start();
            }
        });
    }

    private JFrame frame = null;
    
    private JPanel panel = null;
    
    private Graphics gr = null;
    
    private JTree tree = null;
    
    private Connection connection;

    private Statement stmt;

    public Statement getStmt() {return this. stmt;}

    public Connection getConnection() {return this.connection;}

    private JTextField yourNameTo;
    private JTextArea yourAddressTo;
    private JTextField yourNameFrom;
    private JTextArea yourAddressFrom;
    
    private JTable lbl,lbl2;

    private DefaultTableModel df = new DefaultTableModel();
    private DefaultTableModel df2 = new DefaultTableModel();
    
    private boolean start = true;
 
    private JScrollPane jp = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private JScrollPane jp2 = new JScrollPane(tree2, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
    private int did = -1;
    private boolean start2 = true;

    public Mom()    {
        connectToDataBase();
        setUI();
        setBackgroundImages();
    }
}