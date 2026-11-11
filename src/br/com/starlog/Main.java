package br.com.starlog;

import br.com.starlog.dao.ProdutoDAO;
import br.com.starlog.dao.ProdutoDAOSqlite;
import br.com.starlog.model.Produto;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class Main {

    private static final String CABECALHO = String.format("%-5s | %-26s | %12s | %8s", "ID", "NOME", "PRECO (R$)", "ESTOQUE");
    private static final String LINHA = "-".repeat(CABECALHO.length());

    public static void main(String[] args) {
        // Banco limpo a cada execução para a simulação ser reprodutível
        File arquivo = new File("starlog.db");
        if (arquivo.exists()) {
            arquivo.delete();
        }

        ProdutoDAO dao = new ProdutoDAOSqlite("starlog.db");

        // ===== 1. CREATE =====
        titulo("1. [CREATE] Cadastro de produtos");
        Produto teclado = new Produto("Teclado Mecânico RGB", 349.90, 40);
        Produto mousepad = new Produto("Mousepad Speed XL", 59.90, 120);
        dao.salvar(teclado);
        dao.salvar(mousepad);
        System.out.println("Teclado salvo com ID autoincrement: " + teclado.getId());
        System.out.println("Mousepad salvo com ID autoincrement: " + mousepad.getId());

        // ===== 2. READ =====
        titulo("2. [READ] Listagem de todos os produtos (ORDER BY nome ASC)");
        imprimirTabela(dao.listarTodos());

        // ===== 3. UPDATE =====
        titulo("3. [UPDATE] Teclado: estoque = 15 e preço = 289.90");
        teclado.setEstoque(15);
        teclado.setPreco(289.90);
        boolean atualizou = dao.atualizar(teclado);
        System.out.println("atualizar() retornou: " + atualizou);
        Optional<Produto> atualizado = dao.buscarPorId(teclado.getId());
        System.out.println("Registro atualizado: " + atualizado.map(Produto::toString).orElse("não encontrado"));

        // ===== 4. DELETE =====
        titulo("4. [DELETE] Exclusão por ID");
        boolean removeuMousepad = dao.excluir(mousepad.getId());
        System.out.println("excluir(" + mousepad.getId() + ") retornou: " + removeuMousepad);

        boolean removeuInexistente = dao.excluir(888L);
        System.out.println("excluir(888) retornou: " + removeuInexistente);
        if (!removeuInexistente) {
            System.out.println("Aviso: nenhum produto com ID 888 foi encontrado. Nada foi removido.");
        }

        // ===== 5. FINAL STATE =====
        titulo("5. [FINAL STATE] Estado final do banco");
        List<Produto> finais = dao.listarTodos();
        imprimirTabela(finais);
        System.out.println("Total de produtos restantes: " + finais.size());
    }

    private static void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }

    private static void imprimirTabela(List<Produto> produtos) {
        System.out.println(CABECALHO);
        System.out.println(LINHA);
        for (Produto p : produtos) {
            System.out.println(String.format("%-5d | %-26s | %12.2f | %8d",
                    p.getId(), p.getNome(), p.getPreco(), p.getEstoque()));
        }
        System.out.println(LINHA);
    }
}
