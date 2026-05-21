import java.time.Duration;
import java.time.LocalDateTime;

public class BuscaSequencial<T> implements IBuscador<T> {

    private long comparacoes;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private T[] dados;

    public BuscaSequencial(T[] dados){
        this.dados = dados;
    }

    @Override
    public long getComparacoes() {
        return comparacoes;
    }

    @Override
    public double getTempo() {
        if(inicio==null)
            throw new IllegalStateException("Não foi feita nenhuma busca.");
        
        return Duration.between(inicio, fim).toNanos();
    }

    @Override
    public T buscar(T dado) {
        comparacoes = 0;
        int pos = 0;
        T encontrado = null;
        inicio = LocalDateTime.now();
        while(pos < dados.length && encontrado==null) {
            comparacoes++;
            if(dados[pos].equals(dado)){
                encontrado = dados[pos];
            }
            pos++;
        }
        fim = LocalDateTime.now();
        return encontrado;
    }
    
}
