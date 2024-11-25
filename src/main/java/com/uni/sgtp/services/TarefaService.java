package com.uni.sgtp.services;

import com.uni.sgtp.classes.Tarefa;
import com.uni.sgtp.classes.Tarefa.Prioridade;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class TarefaService {

    private Connection conn;

    public TarefaService(Connection conn) {
        this.conn = conn;
    }

    public void inserir(
        String nome,
        boolean concluida,
        LocalDate prazo,
        Prioridade prioridade,
        String descricao,
        Integer projetoId
    ) {
        String query =
            "INSERT INTO tb_tarefas (nome, concluida, prazo, prioridade, descricao, projeto_id) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nome);
            stmt.setBoolean(2, concluida);
            stmt.setObject(3, prazo);
            stmt.setString(4, prioridade.toString());
            stmt.setString(5, descricao);
            if (projetoId != null) {
                stmt.setInt(6, projetoId);
            } else {
                stmt.setNull(6, Types.INTEGER);
            }

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Tarefa inserida com sucesso!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null,
                "Erro ao inserir: " + e.getMessage()
            );
        }
    }

    public List<Tarefa> consultar() {
        String query =
            "SELECT nome, concluida, prazo, prioridade, descricao FROM tb_tarefas";
        List<Tarefa> tarefas = new ArrayList<>();

        try (
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                boolean concluida = rs.getBoolean("concluida");
                LocalDate prazo = rs.getDate("prazo") != null
                    ? rs.getDate("prazo").toLocalDate()
                    : null;
                String prioridade = rs.getString("prioridade");
                String descricao = rs.getString("descricao");

                Tarefa tarefa = new Tarefa(
                    id,
                    nome,
                    concluida,
                    prazo,
                    Tarefa.Prioridade.valueOf(prioridade)
                );
                tarefa.setDescricao(descricao);

                tarefas.add(tarefa);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null,
                "Erro ao consultar: " + e.getMessage()
            );
        }
        return tarefas;
    }

    public void alterar(
        int id,
        String nome,
        boolean concluida,
        LocalDate prazo,
        String prioridade,
        String descricao
    ) {
        String query =
            "UPDATE tb_tarefas SET nome = ?, concluida = ?, prazo = ?, prioridade = ?, descricao = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nome);
            stmt.setBoolean(2, concluida);
            stmt.setObject(3, prazo);
            stmt.setString(4, prioridade);
            stmt.setString(5, descricao);
            stmt.setInt(6, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(
                    null,
                    "Tarefa alterada com sucesso!"
                );
            } else {
                JOptionPane.showMessageDialog(
                    null,
                    "Nenhuma tarefa encontrada com o ID fornecido."
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null,
                "Erro ao alterar: " + e.getMessage()
            );
        }
    }

    public void deletar(int id) {
        String query = "DELETE FROM tb_tarefas WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(
                    null,
                    "Tarefa deletada com sucesso!"
                );
            } else {
                JOptionPane.showMessageDialog(
                    null,
                    "Nenhuma tarefa encontrada com o ID fornecido."
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null,
                "Erro ao deletar: " + e.getMessage()
            );
        }
    }
}
