
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;


public class AppBusca {

    static final int MAX_PEDIDOS = 100;
    static Produto[] produtos;
    static int quantProdutos = 0;
    static String nomeArquivoDados = "produtos.txt";
    static IOrdenador<Produto> ordenador;

    // #region utilidades
    static Scanner teclado;

    

    static <T extends Number> T lerNumero(String mensagem, Class<T> classe) {
        System.out.print(mensagem + ": ");
        T valor;
        try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }

    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    static void pausa() {
        System.out.println("Tecle Enter para continuar.");
        teclado.nextLine();
    }

    static void cabecalho() {
        limparTela();
        System.out.println("================================");
        System.out.println("XULAMBS COMÉRCIO DE COISINHAS v0.2\n================================");
    }
    

    static int exibirMenuPrincipal() {
        cabecalho();
        System.out.println("1 - Ordenar produtos carregados");
        System.out.println("2 - Embaralhar produtos carregados");
        System.out.println("3 - Busca sequencial por produtos");
        System.out.println("4 - Busca binária por produtos");
        System.out.println("5 - Listar todos os produtos");
        
        System.out.println("0 - Finalizar");
       
        return lerNumero("Digite sua opção", Integer.class);
    }

    // #endregion
    static Produto[] carregarProdutos(String nomeArquivo){
        Scanner dados;
        Produto[] dadosCarregados;
        try{
            dados = new Scanner(new File(nomeArquivo));
            int tamanho = Integer.parseInt(dados.nextLine());
            
            dadosCarregados = new Produto[tamanho];
            while (dados.hasNextLine()) {
                Produto novoProduto = Produto.criarDoTexto(dados.nextLine());
                dadosCarregados[quantProdutos] = novoProduto;
                quantProdutos++;
            }
            dados.close();
        }catch (FileNotFoundException fex){
            System.out.println("Arquivo não encontrado. Produtos não carregados");
            dadosCarregados = null;
        }
        return dadosCarregados;
    }

    static Produto localizarProduto(IBuscador<Produto> buscador) {
        cabecalho();
        System.out.println("Localizando um produto");
        System.out.print("Digite a descrição do produto: ");
        String desc = teclado.nextLine();
        Produto busca = new ProdutoNaoPerecivel(desc, 0.1);

        Produto encontrado = buscador.buscar(busca);
        
        return encontrado;
    }

    private static void resultadoBusca(Produto produto, IBuscador<Produto> buscador) {
        cabecalho();
        StringBuilder  mensagem = new StringBuilder("Produto não encontrado.\n");
        
        if(produto!=null){
            mensagem = new StringBuilder(String.format("%s\n", produto));            
        }

        mensagem.append(String.format("\nTotal de comparações: %d\n", buscador.getComparacoes()));
        mensagem.append(String.format("Tempo gasto: %,.0f nanossegundos\n", buscador.getTempo()));
        
        System.out.println(mensagem.toString());
    }

    static void ordenarProdutos(){
        cabecalho();
        
        Mergesort<Produto> mergesort = new Mergesort<>();
        produtos = mergesort.ordenar(produtos, Produto::compareTo);

        ordenador = null;
    }

    static void embaralharProdutos(){
        Collections.shuffle(Arrays.asList(produtos));
    }

    static void listarProdutos(){
        cabecalho();
        for (int i = 0; i < quantProdutos; i++) {
            System.out.println(produtos[i]);
        }
    }
    
    static void fazerBuscaSequencial(){
        IBuscador<Produto> buscador = new BuscaSequencial<>(produtos);
        Produto prod = localizarProduto(buscador);
        resultadoBusca(prod, buscador);
    }

    static void fazerBuscaBinaria(){
        IBuscador<Produto> buscador = new BuscaBinaria<>(produtos, Produto::compareTo);
        Produto prod = localizarProduto(buscador);
        resultadoBusca(prod, buscador);
    }

    public static void main(String[] args) {
        teclado = new Scanner(System.in);
        
        produtos = carregarProdutos(nomeArquivoDados);
        embaralharProdutos();

        int opcao = -1;
        
        do {
            opcao = exibirMenuPrincipal();
            switch (opcao) {
                case 1 -> ordenarProdutos();
                case 2 -> embaralharProdutos();
                case 3 -> fazerBuscaSequencial();
                case 4 -> fazerBuscaBinaria();
                case 5 -> listarProdutos();
                case 0 -> System.out.println("FLW VLW OBG VLT SMP.");
            }
            pausa();
        }while (opcao != 0);
        teclado.close();
    }                        
}
