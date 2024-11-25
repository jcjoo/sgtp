package com.uni.sgtp.services;

import com.uni.sgtp.classes.Projeto;
import com.uni.sgtp.classes.Tarefa;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class ProjetoService {

    private Connection conn;

    public ProjetoService(Connection conn) {
        this.conn = conn;
    }

    public int inserir(
        String nomeProjeto,
        LocalDate dataInicial,
        LocalDate dataFinal
    ) {
        String query =
            "INSERT INTO tb_projetos (nome_projeto, data_inicial, data_final, status) VALUES (?, ?, ?, ?)";

        try (
            PreparedStatement stmt = conn.prepareStatement(
                query,
                Statement.RETURN_GENERATED_KEYS
            )
        ) {
            stmt.setString(1, nomeProjeto);
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            stmt.setString(4, Projeto.Status.EM_ANDAMENTO.toString());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        JOptionPane.showMessageDialog(
                            null,
                            "Projeto inserido com sucesso!"
                        );
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null,
                "Erro ao inserir projeto: " + e.getMessage()
            );
        }
        return -1;
    }

    public List<Projeto> consultar() {
        String query =
            "SELECT id, nome_projeto, data_inicial, data_final, status FROM tb_projetos";
        List<Projeto> projetos = new ArrayList<>();

        try (
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery()
        ) {
            while (rs.next()) {
                String nomeProjeto = rs.getString("nome_projeto");
                LocalDate dataInicial = rs
                    .getDate("data_inicial")
                    .toLocalDate();
                LocalDate dataFinal = rs.getDate("data_final").toLocalDate();
                int projetoId = rs.getInt("id");

                Projeto projeto = new Projeto(
                    projetoId,
                    nomeProjeto,
                    dataInicial,
                    dataFinal
                );
                projeto.setStatus(
                    Projeto.Status.valueOf(rs.getString("status"))
                );

                List<Tarefa> tarefas = consultarTarefasDoProjeto(projetoId);
                for (Tarefa tarefa : tarefas) {
                    projeto.adicionarTarefa(tarefa);
                }

                projetos.add(projeto);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null,
                "Erro ao consultar projetos: " + e.getMessage()
            );
        }
        return projetos;
    }

    public List<Tarefa> consultarTarefasDoProjeto(int projetoId) {
        String query = "SELECT * FROM tb_tarefas WHERE projeto_id = ?";
        List<Tarefa> tarefas = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, projetoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                boolean concluida = rs.getBoolean("concluida");
                LocalDate prazo = rs.getDate("prazo") != null
                    ? rs.getDate("prazo").toLocalDate()
                    : null;
                Tarefa.Prioridade prioridade = Tarefa.Prioridade.valueOf(
                    rs.getString("prioridade")
                );

                Tarefa tarefa = new Tarefa(
                    id,
                    nome,
                    concluida,
                    prazo,
                    prioridade
                );
                tarefa.setDescricao(rs.getString("descricao"));
                tarefas.add(tarefa);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null,
                "Erro ao consultar tarefas do projeto: " + e.getMessage()
            );
        }
        return tarefas;
    }

    public void alterar(
        int id,
        String nomeProjeto,
        LocalDate dataInicial,
        LocalDate dataFinal,
        Projeto.Status status
    ) {
        String query =
            "UPDATE tb_projetos SET nome_projeto = ?, data_inicial = ?, data_final = ?, status = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nomeProjeto);
            stmt.setObject(2, dataInicial);
            stmt.setObject(3, dataFinal);
            stmt.setString(4, status.toString());
            stmt.setInt(5, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(
                    null,
                    "Projeto alterado com sucesso!"
                );
            } else {
                JOptionPane.showMessageDialog(
                    null,
                    "Nenhum projeto encontrado com o ID fornecido."
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null,
                "Erro ao alterar projeto: " + e.getMessage()
            );
        }
    }

    public void deletar(int id) {

        String updateTarefasQuery =
            "UPDATE tb_tarefas SET projeto_id = NULL WHERE projeto_id = ?";

        try (
            PreparedStatement stmt = conn.prepareStatement(updateTarefasQuery)
        ) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null,
                "Erro ao atualizar tarefas do projeto: " + e.getMessage()
            );
            return;
        }

        String deleteProjectQuery = "DELETE FROM tb_projetos WHERE id = ?";

        try (
            PreparedStatement stmt = conn.prepareStatement(deleteProjectQuery)
        ) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(
                    null,
                    "Projeto deletado com sucesso!"
                );
            } else {
                JOptionPane.showMessageDialog(
                    null,
                    "Nenhum projeto encontrado com o ID fornecido."
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                null,
                "Erro ao deletar projeto: " + e.getMessage()
            );
        }
    }

    public void adicionarTarefaAoProjeto(int projetoId, Tarefa tarefa) {
        TarefaService tarefaService = new TarefaService(conn);
        tarefaService.inserir(
            tarefa.getNome(),
            false,
            tarefa.getPrazo(),
            tarefa.getPrioridade(),
            tarefa.getDescricao(),
            projetoId
        );
    }
}
