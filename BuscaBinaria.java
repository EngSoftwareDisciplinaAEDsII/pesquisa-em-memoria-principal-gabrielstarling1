import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;

public class BuscaBinaria<T> implements IBuscador<T> {

    private long comparacoes;
    private LocalDateTime inicio;
    private LocalDateTime fim;
    private T[] dados;
    private Comparator<T> comparador;

    public BuscaBinaria(T[] dados, Comparator<T> comparador){
        this.dados = dados;
        this.comparador = comparador;
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
        int posInicio = 0;
        int posFinal = dados.length-1;
        inicio = LocalDateTime.now();
        T resultado = buscaBinaria(dado, posInicio, posFinal);
        fim = LocalDateTime.now();
        return resultado;
    }

    private T buscaBinaria(T dado, int posInicio, int posFinal){
        if(posFinal < posInicio)
            return null;
        else{
            int meio = (posInicio+posFinal)/2;
            comparacoes++;
            if(dados[meio].equals(dado))
                return dados[meio];
            else{ 
                comparacoes++;
                if(comparador.compare(dados[meio], dado) < 0)
                    return buscaBinaria(dado, meio+1, posFinal);
                else 
                    return buscaBinaria(dado, posInicio, meio-1);
            }
        }

    }
    
}
