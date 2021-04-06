package com.company;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Main {
    private JTextField textField1;
    private JButton buttonSelect;
    private JLabel labelGroup;
    private JPanel panelMain;
    private JTable table1;
    private JButton buttonSaveTenFL;
    private JButton buttonSaveAll;

    public static ArrayList<Student> Students = new ArrayList<Student>();


    public Main() {

        buttonSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                String col[] = {"Stid", "Disciplin", "Semister", "Mark"};

                DefaultTableModel tablemodel = new DefaultTableModel(col, 0);

                table1.setModel(new DefaultTableModel(col, 0));

                TestStudent(10);

                for (Student student : Students) {

                    Object[] st = {student.stid, student.discid, student.semnum, student.mark};
                    tablemodel.addRow(st);
                }
                table1.setModel(tablemodel);
            }

        });

        buttonSaveAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                for (Student student : Students) {
                    try {
                        int size = Students.size();
                        FileWriter writer = new FileWriter("C:/Users/Nik1/IdeaProjects/Java/PrjPostgres_SAS/file_All.txt", false);
                        //--------------- PRINT ALL ------------------
                        for (int i = 0; i < size; i++) {
                            String str = Students.get(i).stid + " " + Students.get(i).discid + " " + Students.get(i).semnum + " " + Students.get(i).mark;
                            writer.write(str + "\r\n");
                        }
                        writer.close();

                    } catch (Exception e1) {
                        e1.printStackTrace();
                        System.err.println(e1.getClass().getName() + ": " + e1.getMessage());
                        System.exit(0);
                    }

                }

            }
        });
        buttonSaveTenFL.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //--------------- PRINT 10 FIRST AND 10 LAST ------------------
                for (Student student : Students) {
                    try {
                        int size = Students.size();
                        FileWriter writer = new FileWriter("C:/Users/Nik1/IdeaProjects/Java/PrjPostgres_SAS/file_tf_tl.txt", false);

                        writer.write("Первые 10 элементов" + "\r\n");

                        for (int i = 0; i < 10; i++) {
                            String str = Students.get(i).stid + " " + Students.get(i).discid + " " + Students.get(i).semnum + " " + Students.get(i).mark;
                            writer.write(str + "\r\n");

                        }
                        writer.write("Последние 10 элементов" + "\r\n");
                        for (int i = 10; i > 0; i--) {
                            String str1 = Students.get(size - i).stid + " " + Students.get(size - i).discid + " " + Students.get(size - i).semnum + " " + Students.get(size - i).mark;
                            writer.write(str1 + "\r\n");

                        }
                        writer.close();

                    } catch (Exception e1) {
                        e1.printStackTrace();
                        System.err.println(e1.getClass().getName() + ": " + e1.getMessage());
                        System.exit(0);
                    }

                }

            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Main");
        frame.setPreferredSize(new Dimension(600, 400));
        frame.setContentPane(new Main().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    public static void TestStudent(int kol) {
        Connection c;
        Statement stmt;
        Integer ch = 0;

        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://localhost:5432/iate", "postgres", "postgres");
            c.setAutoCommit(false);
            System.out.println("-- Connected successfully --");


            //--------------- SELECT DATA ------------------
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT stid,discid,semnum,sum(ktmark) FROM studmark " +
                    "WHERE studmark.stid IN (SELECT stid FROM student WHERE student.gid IN " +
                    "(SELECT gid FROM sgroup WHERE sgroup.shname ILIKE 'ЭКЛ%' AND sgroup.enty IN (2007,2008,2009,2010))) " +
                    "AND studmark.semnum IN (4,5,6) GROUP BY studmark.stid, studmark.discid, studmark.semnum ORDER BY studmark.stid ASC , studmark.semnum ASC;");

            while (rs.next()) {
                Student student = new Student();
                String id = rs.getString(1);
                String gid = rs.getString(2);
                String semnum = rs.getString(3);
                String mark = rs.getString(4);

                student.stid = id;
                student.discid = gid;
                student.semnum = semnum;
                student.mark = mark;

                ch += 1;
                Students.add(student);
            }
            rs.close();
            stmt.close();
            c.commit();
            c.close();


            //--------------- PRINT 10 FIRST AND 10 LAST ------------------
            for (int i = 0; i < kol; i++) {
                String str = Students.get(i).stid + " " + Students.get(i).discid + " " + Students.get(i).semnum + " " + Students.get(i).mark;
                System.out.println(str);
            }
            int size = Students.size();
            for (int i = kol; i > 0; i--) {
                String str1 = Students.get(size - i).stid + " " + Students.get(size - i).discid + " " + Students.get(size - i).semnum + " " + Students.get(size - i).mark;
                System.out.println(str1);
            }


            System.out.println("-- Operation SELECT done successfully --");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        System.out.println("Количество записей: " + ch);

    }

    //end_Main
}

class Student {
    String stid;
    String discid;
    String semnum;
    String mark;

}
