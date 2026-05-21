import java.util.Comparator;

public interface IOrdenador<T extends Comparable<T>> extends IMedicao{
    public T[] ordenar(T[] dados);
    public T[] ordenar(T[] dados, Comparator<T> comparador);
    public long getMovimentacoes();
}
