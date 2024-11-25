package com.uni.sgtp.view;

import com.uni.sgtp.classes.ConectaBanco;
import com.uni.sgtp.classes.Projeto;
import com.uni.sgtp.classes.Tarefa;
import com.uni.sgtp.services.ProjetoService;
import com.uni.sgtp.services.TarefaService;
import java.awt.*;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GerenciadorProjetosGUI extends JFrame {

    private TarefaService tarefaService;
    private ProjetoService projetoService;
    private JTable tabelaProjetos;
    private DefaultTableModel modeloTabela;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(
        "dd/MM/yyyy"
    );

    public GerenciadorProjetosGUI(Connection conn) {
        projetoService = new ProjetoService(conn);
        tarefaService = new TarefaService(conn);
        configurarJanela();
        inicializarComponentes();
        carregarProjetos();
    }

    private void configurarJanela() {
        setTitle("Gerenciador de Projetos");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        // Painel principal
        JPanel painelPrincipal = new JPanel(new BorderLayout());

        // Botões
        JPanel painelBotoes = new JPanel();
        JButton btnNovo = new JButton("Novo Projeto");
        JButton btnEditar = new JButton("Editar Projeto");
        JButton btnDeletar = new JButton("Deletar Projeto");
        JButton btnVerTarefas = new JButton("Ver Tarefas");

        painelBotoes.add(btnNovo);
        painelBotoes.add(btnEditar);
        painelBotoes.add(btnDeletar);
        painelBotoes.add(btnVerTarefas);

        // Tabela
        String[] colunas = {
            "ID",
            "Nome",
            "Data Inicial",
            "Data Final",
            "Status",
        };
        modeloTabela = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaProjetos = new JTable(modeloTabela);
        JScrollPane scrollPane = new JScrollPane(tabelaProjetos);

        // Adicionar componentes ao painel principal
        painelPrincipal.add(painelBotoes, BorderLayout.NORTH);
        painelPrincipal.add(scrollPane, BorderLayout.CENTER);

        // Adicionar ações aos botões
        btnNovo.addActionListener(e -> mostrarDialogNovoProjeto());
        btnEditar.addActionListener(e -> editarProjetoSelecionado());
        btnDeletar.addActionListener(e -> deletarProjetoSelecionado());
        btnVerTarefas.addActionListener(e -> verTarefasDoProjeto());

        add(painelPrincipal);
    }

    private void carregarProjetos() {
        modeloTabela.setRowCount(0);
        List<Projeto> projetos = projetoService.consultar();
        for (Projeto projeto : projetos) {
            Object[] row = {
                projeto.getId(),
                projeto.getNomeProjeto(),
                projeto.getDataInicial().format(dateFormatter),
                projeto.getDataFinal().format(dateFormatter),
                projeto.getStatus(),
            };
            modeloTabela.addRow(row);
        }
    }

    private void mostrarDialogNovoProjeto() {
        JDialog dialog = new JDialog(this, "Novo Projeto", true);
        dialog.setLayout(new GridLayout(5, 2));

        JTextField txtNome = new JTextField();
        JTextField txtDataInicial = new JTextField();
        JTextField txtDataFinal = new JTextField();

        dialog.add(new JLabel("Nome:"));
        dialog.add(txtNome);
        dialog.add(new JLabel("Data Inicial (dd/MM/yyyy):"));
        dialog.add(txtDataInicial);
        dialog.add(new JLabel("Data Final (dd/MM/yyyy):"));
        dialog.add(txtDataFinal);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> {
            try {
                LocalDate dataInicial = LocalDate.parse(
                    txtDataInicial.getText(),
                    dateFormatter
                );
                LocalDate dataFinal = LocalDate.parse(
                    txtDataFinal.getText(),
                    dateFormatter
                );
                projetoService.inserir(
                    txtNome.getText(),
                    dataInicial,
                    dataFinal
                );
                dialog.dispose();
                carregarProjetos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Erro ao criar projeto: " + ex.getMessage()
                );
            }
        });

        dialog.add(btnSalvar);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editarProjetoSelecionado() {
        int selectedRow = tabelaProjetos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Selecione um projeto para editar."
            );
            return;
        }

        JDialog dialog = new JDialog(this, "Editar Projeto", true);
        dialog.setLayout(new GridLayout(6, 2));

        JTextField txtNome = new JTextField(
            modeloTabela.getValueAt(selectedRow, 1).toString()
        );
        JTextField txtDataInicial = new JTextField(
            modeloTabela.getValueAt(selectedRow, 2).toString()
        );
        JTextField txtDataFinal = new JTextField(
            modeloTabela.getValueAt(selectedRow, 3).toString()
        );
        JComboBox<Projeto.Status> cbStatus = new JComboBox<>(
            Projeto.Status.values()
        );

        dialog.add(new JLabel("Nome:"));
        dialog.add(txtNome);
        dialog.add(new JLabel("Data Inicial (dd/MM/yyyy):"));
        dialog.add(txtDataInicial);
        dialog.add(new JLabel("Data Final (dd/MM/yyyy):"));
        dialog.add(txtDataFinal);
        dialog.add(new JLabel("Status:"));
        dialog.add(cbStatus);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> {
            try {
                int id = (int) modeloTabela.getValueAt(selectedRow, 0);
                LocalDate dataInicial = LocalDate.parse(
                    txtDataInicial.getText(),
                    dateFormatter
                );
                LocalDate dataFinal = LocalDate.parse(
                    txtDataFinal.getText(),
                    dateFormatter
                );
                projetoService.alterar(
                    id,
                    txtNome.getText(),
                    dataInicial,
                    dataFinal,
                    (Projeto.Status) cbStatus.getSelectedItem()
                );
                dialog.dispose();
                carregarProjetos();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Erro ao editar projeto: " + ex.getMessage()
                );
            }
        });

        dialog.add(btnSalvar);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deletarProjetoSelecionado() {
        int selectedRow = tabelaProjetos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Selecione um projeto para deletar."
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja deletar este projeto?",
            "Confirmar Deleção",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int id = (int) modeloTabela.getValueAt(selectedRow, 0);
            projetoService.deletar(id);
            carregarProjetos();
        }
    }

    private void verTarefasDoProjeto() {
        int selectedRow = tabelaProjetos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Selecione um projeto para ver as tarefas."
            );
            return;
        }

        int projetoId = (int) modeloTabela.getValueAt(selectedRow, 0);
        String nomeProjeto = (String) modeloTabela.getValueAt(selectedRow, 1);

        JDialog dialogTarefas = new JDialog(
            this,
            "Tarefas do Projeto: " + nomeProjeto,
            true
        );
        dialogTarefas.setSize(800, 500);
        dialogTarefas.setLayout(new BorderLayout());

        // Criar tabela de tarefas
        String[] colunasTarefas = {
            "ID",
            "Nome",
            "Descrição",
            "Prazo",
            "Prioridade",
            "Concluída",
        };
        DefaultTableModel modeloTarefas = new DefaultTableModel(
            colunasTarefas,
            0
        ) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 5 ? Boolean.class : Object.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        JTable tabelaTarefas = new JTable(modeloTarefas);
        JScrollPane scrollTarefas = new JScrollPane(tabelaTarefas);

        // Painel de botões
        JPanel painelBotoesTarefas = new JPanel();
        JButton btnNovaTarefa = new JButton("Nova Tarefa");
        JButton btnEditarTarefa = new JButton("Editar Tarefa");
        JButton btnDeletarTarefa = new JButton("Deletar Tarefa");

        painelBotoesTarefas.add(btnNovaTarefa);
        painelBotoesTarefas.add(btnEditarTarefa);
        painelBotoesTarefas.add(btnDeletarTarefa);

        // Carregar tarefas
        carregarTarefas(projetoId, modeloTarefas);

        // Ações dos botões
        btnNovaTarefa.addActionListener(e ->
            mostrarDialogNovaTarefa(projetoId, modeloTarefas)
        );
        btnEditarTarefa.addActionListener(e ->
            editarTarefaSelecionada(tabelaTarefas, modeloTarefas, projetoId)
        );
        btnDeletarTarefa.addActionListener(e ->
            deletarTarefaSelecionada(tabelaTarefas, modeloTarefas, projetoId)
        );

        // Adicionar listener para mudanças na coluna "Concluída"
        modeloTarefas.addTableModelListener(e -> {
            if (e.getColumn() == 5) {
                int row = e.getFirstRow();
                boolean concluida = (boolean) modeloTarefas.getValueAt(row, 5);
                int tarefaId = (int) modeloTarefas.getValueAt(row, 0);
                alterarStatusTarefa(
                    tarefaId,
                    concluida,
                    projetoId,
                    modeloTarefas
                );
            }
        });

        dialogTarefas.add(painelBotoesTarefas, BorderLayout.NORTH);
        dialogTarefas.add(scrollTarefas, BorderLayout.CENTER);
        dialogTarefas.setLocationRelativeTo(this);
        dialogTarefas.setVisible(true);
    }

    private void carregarTarefas(
        int projetoId,
        DefaultTableModel modeloTarefas
    ) {
        modeloTarefas.setRowCount(0);
        List<Tarefa> tarefas = projetoService.consultarTarefasDoProjeto(
            projetoId
        );
        for (Tarefa tarefa : tarefas) {
            Object[] row = {
                tarefa.getId(),
                tarefa.getNome(),
                tarefa.getDescricao(),
                tarefa.getPrazo() != null
                    ? tarefa.getPrazo().format(dateFormatter)
                    : "",
                tarefa.getPrioridade(),
                tarefa.isConcluida(),
            };
            modeloTarefas.addRow(row);
        }
    }

    private void mostrarDialogNovaTarefa(
        int projetoId,
        DefaultTableModel modeloTarefas
    ) {
        JDialog dialog = new JDialog(this, "Nova Tarefa", true);
        dialog.setLayout(new GridLayout(6, 2));

        JTextField txtNome = new JTextField();
        JTextField txtDescricao = new JTextField();
        JTextField txtPrazo = new JTextField();
        JComboBox<Tarefa.Prioridade> cbPrioridade = new JComboBox<>(
            Tarefa.Prioridade.values()
        );

        dialog.add(new JLabel("Nome:"));
        dialog.add(txtNome);
        dialog.add(new JLabel("Descrição:"));
        dialog.add(txtDescricao);
        dialog.add(new JLabel("Prazo (dd/MM/yyyy):"));
        dialog.add(txtPrazo);
        dialog.add(new JLabel("Prioridade:"));
        dialog.add(cbPrioridade);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> {
            try {
                LocalDate prazo = txtPrazo.getText().isEmpty()
                    ? null
                    : LocalDate.parse(txtPrazo.getText(), dateFormatter);

                tarefaService.inserir(
                    txtNome.getText(),
                    false,
                    prazo,
                    (Tarefa.Prioridade) cbPrioridade.getSelectedItem(),
                    txtDescricao.getText(),
                    projetoId
                );

                dialog.dispose();
                carregarTarefas(projetoId, modeloTarefas);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Erro ao criar tarefa: " + ex.getMessage()
                );
            }
        });

        dialog.add(btnSalvar);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void editarTarefaSelecionada(
        JTable tabelaTarefas,
        DefaultTableModel modeloTarefas,
        int projetoId
    ) {
        int selectedRow = tabelaTarefas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Selecione uma tarefa para editar."
            );
            return;
        }

        JDialog dialog = new JDialog(this, "Editar Tarefa", true);
        dialog.setLayout(new GridLayout(6, 2));

        JTextField txtNome = new JTextField(
            modeloTarefas.getValueAt(selectedRow, 1).toString()
        );
        JTextField txtDescricao = new JTextField(
            modeloTarefas.getValueAt(selectedRow, 2).toString()
        );
        JTextField txtPrazo = new JTextField(
            modeloTarefas.getValueAt(selectedRow, 3).toString()
        );
        JComboBox<Tarefa.Prioridade> cbPrioridade = new JComboBox<>(
            Tarefa.Prioridade.values()
        );
        JCheckBox chkConcluida = new JCheckBox(
            "Concluída",
            (Boolean) modeloTarefas.getValueAt(selectedRow, 5)
        );

        dialog.add(new JLabel("Nome:"));
        dialog.add(txtNome);
        dialog.add(new JLabel("Descrição:"));
        dialog.add(txtDescricao);
        dialog.add(new JLabel("Prazo (dd/MM/yyyy):"));
        dialog.add(txtPrazo);
        dialog.add(new JLabel("Prioridade:"));
        dialog.add(cbPrioridade);
        dialog.add(new JLabel("Status:"));
        dialog.add(chkConcluida);

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> {
            try {
                int tarefaId = (int) modeloTarefas.getValueAt(selectedRow, 0);
                LocalDate prazo = txtPrazo.getText().isEmpty()
                    ? null
                    : LocalDate.parse(txtPrazo.getText(), dateFormatter);

                tarefaService.alterar(
                    tarefaId,
                    txtNome.getText(),
                    chkConcluida.isSelected(),
                    prazo,
                    cbPrioridade.getSelectedItem().toString(),
                    txtDescricao.getText()
                );

                dialog.dispose();
                carregarTarefas(projetoId, modeloTarefas);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                    dialog,
                    "Erro ao editar tarefa: " + ex.getMessage()
                );
            }
        });

        dialog.add(btnSalvar);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void deletarTarefaSelecionada(
        JTable tabelaTarefas,
        DefaultTableModel modeloTarefas,
        int projetoId
    ) {
        int selectedRow = tabelaTarefas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                this,
                "Selecione uma tarefa para deletar."
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Tem certeza que deseja deletar esta tarefa?",
            "Confirmar Deleção",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int tarefaId = (int) modeloTarefas.getValueAt(selectedRow, 0);
            tarefaService.deletar(tarefaId);
            carregarTarefas(projetoId, modeloTarefas);
        }
    }

    private void alterarStatusTarefa(
        int tarefaId,
        boolean concluida,
        int projetoId,
        DefaultTableModel modeloTarefas
    ) {
        try {
            tarefaService.alterar(
                tarefaId,
                "", // nome atual
                concluida,
                null, // prazo atual
                "", // prioridade atual
                "" // descrição atual
            );
            carregarTarefas(projetoId, modeloTarefas);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                this,
                "Erro ao atualizar status da tarefa: " + ex.getMessage()
            );
        }
    }

    public static void main(String[] args) {
        // Exemplo de uso

        SwingUtilities.invokeLater(() -> {
            Connection conn = null; // Inicialize sua conexão aqui
            ConectaBanco cb = new ConectaBanco(
                "jdbc:mysql://localhost:3306/bd_projeto",
                "root",
                "root",
                "com.mysql.cj.jdbc.Driver"
            );
            conn = cb.getConnection();

            new GerenciadorProjetosGUI(conn).setVisible(true);
        });
    }
}
