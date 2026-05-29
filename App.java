import java.nio.charset.Charset;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.function.Function;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class App {

	/** Nome do arquivo de dados. O arquivo deve estar localizado na raiz do projeto */
    static String nomeArquivoDados;
    
    /** Scanner para leitura de dados do teclado */
    static Scanner teclado;

    /** Quantidade de produtos cadastrados atualmente na lista */
    static int quantosProdutos = 0;

    static ABB<String, Produto> produtosCadastradosPorNome;
    
    static ABB<Integer, Produto> produtosCadastradosPorId;
    
    static void limparTela() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /** Gera um efeito de pausa na CLI. Espera por um enter para continuar */
    static void pausa() {
        System.out.println("Digite enter para continuar...");
        teclado.nextLine();
    }

    /** Cabeçalho principal da CLI do sistema */
    static void cabecalho() {
        System.out.println("AEDs II COMÉRCIO DE COISINHAS");
        System.out.println("=============================");
    }
    
    static <T extends Number> T lerOpcao(String mensagem, Class<T> classe) {
        
    	T valor;
        
    	System.out.println(mensagem);
    	try {
            valor = classe.getConstructor(String.class).newInstance(teclado.nextLine());
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException 
        		| InvocationTargetException | NoSuchMethodException | SecurityException e) {
            return null;
        }
        return valor;
    }
    
    /** Imprime o menu principal, lê a opção do usuário e a retorna (int).
     * Perceba que poderia haver uma melhor modularização com a criação de uma classe Menu.
     * @return Um inteiro com a opção do usuário.
    */
    static int menu() {
        cabecalho();
        System.out.println("1 - Carregar produtos por nome/descrição");
        System.out.println("2 - Carregar produtos por id");
        System.out.println("3 - Procurar produto, por nome");
        System.out.println("4 - Procurar produto, por id");
        System.out.println("0 - Sair");
        System.out.print("Digite sua opção: ");
        return Integer.parseInt(teclado.nextLine());
    }
    
    /**
     * Lê os dados de um arquivo-texto e armazena em árvores binárias de busca.
     * Arquivo-texto no formato: N (quantidade de produtos) <br/>
     * tipo;descrição;preçoDeCusto;margemDeLucro;[dataDeValidade] <br/>
     * Deve haver uma linha para cada um dos produtos. 
     * @param nomeArquivoDados Nome do arquivo de dados a ser aberto.
     * @param produtosPorNome Árvore que será preenchida com produtos organizados por nome/descrição.
     * @param produtosPorId Árvore que será preenchida com produtos organizados por ID.
     */
    static void lerProdutos(String nomeArquivoDados, ABB<String, Produto> produtosPorNome, ABB<Integer, Produto> produtosPorId) {
    	
    	Scanner arquivo = null;
    	int numProdutos;
    	String linha;
    	Produto produto;
    	
    	try {
    		arquivo = new Scanner(new File(nomeArquivoDados), Charset.forName("UTF-8"));
    		
    		numProdutos = Integer.parseInt(arquivo.nextLine());
    		
    		for (int i = 0; i < numProdutos; i++) {
    			linha = arquivo.nextLine();
    			produto = Produto.criarDoTexto(linha);
    			
    			// Inserir na árvore por nome (descrição)
    			produtosPorNome.inserir(produto.getDescricao(), produto);
    			
    			// Inserir na árvore por ID
    			produtosPorId.inserir(produto.getIdProduto(), produto);
    		}
    		quantosProdutos = numProdutos;
    		
    	} catch (IOException excecaoArquivo) {
    		System.err.println("Erro ao abrir o arquivo: " + excecaoArquivo.getMessage());
    	} finally {
    		if (arquivo != null) {
    			arquivo.close();
    		}
    	}
    }
    
    
    /** Localiza um produto na árvore de produtos organizados por id, a partir do código de produto informado pelo usuário, e o retorna. 
    *  Em caso de não encontrar o produto, retorna null. Imprime o número de comparações e tempo de execução */
    static Produto localizarProdutoID(ABB<Integer, Produto> produtosCadastrados) {
        
    	if (produtosCadastrados == null || produtosCadastrados.vazia()) {
    		System.out.println("Nenhum produto foi carregado. Carregue produtos por ID primeiro.");
    		return null;
    	}
    	
    	Integer idProcurado = lerOpcao("Digite o ID do produto a procurar: ", Integer.class);
    	if (idProcurado == null) {
    		System.out.println("ID inválido!");
    		return null;
    	}
    	
    	try {
    		Produto resultado = localizarProduto(produtosCadastrados, idProcurado);
    		System.out.println("\nProduto encontrado!");
    		System.out.println("Comparações realizadas: " + produtosCadastrados.getComparacoes());
    		System.out.println("Tempo de execução: " + produtosCadastrados.getTempo() + " ms");
    		return resultado;
    	} catch (NoSuchElementException e) {
    		System.out.println("\nProduto não encontrado!");
    		System.out.println("Comparações realizadas: " + produtosCadastrados.getComparacoes());
    		System.out.println("Tempo de execução: " + produtosCadastrados.getTempo() + " ms");
    		return null;
    	}
    }
    
    /** Localiza um produto na árvore de produtos organizados por nome, a partir do nome de produto informado pelo usuário, e o retorna. 
     *  A busca não é sensível ao caso. Em caso de não encontrar o produto, retorna null. Imprime o número de comparações e tempo de execução */
    static Produto localizarProdutoNome(ABB<String, Produto> produtosCadastrados) {
        
    	if (produtosCadastrados == null || produtosCadastrados.vazia()) {
    		System.out.println("Nenhum produto foi carregado. Carregue produtos por nome primeiro.");
    		return null;
    	}
    	
    	String nomeProcurado = teclado.nextLine();
    	System.out.print("Digite o nome/descrição do produto a procurar: ");
    	nomeProcurado = teclado.nextLine();
    	
    	try {
    		Produto resultado = localizarProduto(produtosCadastrados, nomeProcurado);
    		System.out.println("\nProduto encontrado!");
    		System.out.println("Comparações realizadas: " + produtosCadastrados.getComparacoes());
    		System.out.println("Tempo de execução: " + produtosCadastrados.getTempo() + " ms");
    		return resultado;
    	} catch (NoSuchElementException e) {
    		System.out.println("\nProduto não encontrado!");
    		System.out.println("Comparações realizadas: " + produtosCadastrados.getComparacoes());
    		System.out.println("Tempo de execução: " + produtosCadastrados.getTempo() + " ms");
    		return null;
    	}
    }
    
    /** Método genérico que localiza um produto em uma árvore binária de busca usando uma chave genérica.
     *  Pode ser utilizado para buscar por qualquer tipo de chave (ID, Nome, etc).
     *  @param <K> Tipo genérico da chave
     *  @param produtosCadastrados A árvore com produtos a ser pesquisada
     *  @param procurado A chave do produto a ser localizado
     *  @return O produto associado à chave procurada
     *  @throws NoSuchElementException se o produto não for encontrado
     */
    static <K> Produto localizarProduto(ABB<K, Produto> produtosCadastrados, K procurado) {
    	return produtosCadastrados.pesquisar(procurado);
    }
    
    private static void mostrarProduto(Produto produto) {
    	
        cabecalho();
        String mensagem = "Dados inválidos para o produto!";
        
        if (produto != null){
            mensagem = String.format("Dados do produto:\n%s", produto);
        }
        
        System.out.println(mensagem);
    }
    
    public static void main(String[] args) {
		teclado = new Scanner(System.in, Charset.forName("UTF-8"));
        nomeArquivoDados = "produtos.txt";
        
        produtosCadastradosPorNome = new ABB<>();
        produtosCadastradosPorId = new ABB<>();
        
        int opcao = -1;
      
        do{
            opcao = menu();
            switch (opcao) {
                case 1 -> {
                    produtosCadastradosPorNome = new ABB<>();
                    produtosCadastradosPorId = new ABB<>();
                    lerProdutos(nomeArquivoDados, produtosCadastradosPorNome, produtosCadastradosPorId);
                    System.out.println("Produtos carregados por nome com sucesso!");
                }
                case 2 -> {
                    produtosCadastradosPorNome = new ABB<>();
                    produtosCadastradosPorId = new ABB<>();
                    lerProdutos(nomeArquivoDados, produtosCadastradosPorNome, produtosCadastradosPorId);
                    System.out.println("Produtos carregados por ID com sucesso!");
                }
                case 3 -> mostrarProduto(localizarProdutoNome(produtosCadastradosPorNome));
                case 4 -> mostrarProduto(localizarProdutoID(produtosCadastradosPorId));
            }
            pausa();
        }while(opcao != 0);       

        teclado.close();    
    }
}
