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
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class Mom {

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
        tree.setBounds(-30, 10, 150, 500);
        tree.setFont(new Font("arial",Font.BOLD,10));
        panel.remove(jp);
        jp = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panel.add(jp);
        jp.setBounds(-30, 10, 150, 500);
    }
    
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
            }
        });
    }
    
    private void refreshUI() {
        panel.updateUI();
    }
    
    private void setBackgroundImages() {
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
        setTreeSelectionListenerForTree();
        setDaPrintInvoiceButton();
        refreshUI();
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
        b.setBounds(150, 500, 90, 20);
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
                    if(1==1||start) {
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
            public void run() {
                gr = panel.getGraphics();
                gr.setColor(Color.WHITE);
                gr.fillRect(220, 230, 130, 30);
            }
        });
        t.start();
        panel.add(label);
        label.setBounds(590, 150, 630, 300);
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
        label.setForeground(Color.BLACK);
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
        label.setBounds(220, 230+30+15, 330, 30);
    }
    
    private void setToYourAddress() {
        JTextArea label = new JTextArea();
        label.setFont(new Font("arial",Font.BOLD,20));
        label.setForeground(Color.BLACK);
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
        label.setBounds(220, 230+70+15, 330, 130);
    }

    private void setFromYourName() {
        JTextField label = new JTextField();
        label.setFont(new Font("arial",Font.BOLD,20));
        label.setForeground(Color.BLACK);
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
        label.setBounds(220, 30+15, 330, 30);
    }
    
    private void setFromYourAddress() {
        JTextArea label = new JTextArea();
        label.setFont(new Font("arial",Font.BOLD,20));
        label.setForeground(Color.BLACK);
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
        label.setBounds(220, 70+15, 330, 130);
    }

    private void setFromLabel() {
        JLabel label = new JLabel("FROM");
        label.setFont(new Font("arial",Font.BOLD,20));
        label.setForeground(Color.WHITE);
        label.setBackground(Color.WHITE);
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
        button0.setBounds(130, 60, 90, 40);
        panel.remove(button0);
        panel.add(button0);
        button0.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
                JFrame jj = new JFrame("New Items");
                JPanel pp = new JPanel();
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
                        if(1==1||start) {
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
                    JPanel pp = new JPanel();
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
        frame.setBounds(0, 0, 1200, 600);
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
    
    private JTable lbl;

    private DefaultTableModel df = new DefaultTableModel();
    
    private boolean start = true;
 
    private JScrollPane jp = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
    public Mom()
    {
        connectToDataBase();
        setUI();
        setBackgroundImages();
    }
}