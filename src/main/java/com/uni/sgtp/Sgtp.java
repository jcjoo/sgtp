package com.uni.sgtp;

import javax.swing.*;
import com.uni.sgtp.classes.ConectaBanco;
import com.uni.sgtp.view.GerenciadorProjetosGUI;
import java.sql.Connection;

public class Sgtp {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            Connection conn = null;
            ConectaBanco cb = new ConectaBanco(
                    "jdbc:mysql://localhost:3306/bd_projeto",
                    "root",
                    "root",
                    "com.mysql.cj.jdbc.Driver");
            conn = cb.getConnection();

            new GerenciadorProjetosGUI(conn).setVisible(true);
        });
    }
}