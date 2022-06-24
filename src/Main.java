import modelo.Aluno;
import modelo.Genero;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
//        List<Aluno> alunosTurmaA = new ArrayList<>();
        List<Aluno> alunosTurmaA = new ArrayList<>(Arrays.asList(
           new Aluno("joao", 5.0, 20, Genero.MASCULINO),
                new Aluno("maria", 10.0, 25, Genero.FEMININO),
                new Aluno("pedro", 8.0, 19, Genero.MASCULINO),
                new Aluno("jessica", 6.0, 18, Genero.FEMININO),
                new Aluno("alice", 4.5, 22, Genero.FEMININO),
                new Aluno("jessica", 6.0, 18, Genero.FEMININO),
                new Aluno("alice", 4.5, 22, Genero.FEMININO),
                new Aluno("alice", 4.5, 22, Genero.FEMININO)
        ));

//        List<Aluno> alunosTurmaB = new ArrayList<>(Arrays.asList(
//                new Aluno("marcos", 4.5, Genero.MASCULINO),
//                new Aluno("maria", 8.0, Genero.FEMININO),
//                new Aluno("hugo", 7.5, Genero.MASCULINO)
//        ));
//
//        List<Aluno> alunosTurmaC = new ArrayList<>(Arrays.asList(
//                new Aluno("higor", 5.0, Genero.MASCULINO),
//                new Aluno("caio", 10.0, Genero.MASCULINO),
//                new Aluno("matheus", 8.0, Genero.MASCULINO),
//                new Aluno("henrique", 6.0, Genero.MASCULINO)
//        ));

        Double media = alunosTurmaA.stream()
                .collect(Collectors.averagingDouble(Aluno::nota));

        System.out.println("Media: " + media);

        Long totalDeElementos = alunosTurmaA
                .stream()
                .collect(Collectors.counting());

        System.out.println("Total de elementos: " + totalDeElementos);

//        Map<String, Double> mapaDeAlunos = alunosTurmaA
//                .stream()
//                .collect(Collectors.toMap(Aluno::nome, Aluno::nota));
//
//        System.out.println("Mapa: " + mapaDeAlunos);
//
//        System.out.println("Nota do aluno do mapa: " + mapaDeAlunos.get("joao"));

        System.out.println();

        Map<Genero, String> mapaDeAlunos2 = alunosTurmaA
                .stream()
                .collect(Collectors.toMap(Aluno::genero, Aluno::nome, (a, b) -> a + ", "+b, HashMap::new));

        System.out.println("Mapa de alunos 2 " + mapaDeAlunos2);
        System.out.println("Classe= " + mapaDeAlunos2.getClass());


        Map<Genero, List<Aluno>> agrupamento = alunosTurmaA
                .stream()
                .collect(Collectors.groupingBy(Aluno::genero));

        System.out.println("Agrupamento: " + agrupamento);

        Map<Genero, Set<Aluno>> agrupamentoComSet = alunosTurmaA
                .stream()
                .collect(Collectors.groupingBy(Aluno::genero, Collectors.toSet()));

        System.out.println("Agrupamento: " + agrupamentoComSet);

        Map<Genero, Long> agrupamentoCounting = alunosTurmaA
                .stream()
                .collect(Collectors.groupingBy(Aluno::genero, Collectors.counting()));

        System.out.println("Agrupamento com counting: " + agrupamentoCounting);

        String nomesConcatenados = alunosTurmaA
                .stream()
                .map(Aluno::nome)
                .collect(Collectors.joining("-"));

        System.out.println("Nomes concatenados: " + nomesConcatenados);

        Map<Boolean, List<Aluno>> aprovadosXReprovados = alunosTurmaA
                .stream()
                .collect(Collectors.partitioningBy(aluno -> aluno.nota() > 5));

        List<Aluno> alunos = aprovadosXReprovados.get(true);
        List<Aluno> alunos1 = aprovadosXReprovados.get(false);

        System.out.println("Alunos aprovados: " + alunos);

        System.out.println("Aprovados X Reprovados -> " + aprovadosXReprovados);

        System.out.println();

        Map<Aluno, List<Aluno>> mapaDeNomesPorGenero = alunosTurmaA
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.mapping(Function.identity(), Collectors.toList())));

        System.out.println("Mapa de nomes por genero: " + mapaDeNomesPorGenero);

        /**
         * um metodo que agrupe os alunos pelo tamanho do nome
         */
        //[4=5, 2=2, 7=4]

        Map<Integer, Long> correcao = alunosTurmaA
                .stream()
                .collect(Collectors.groupingBy(aluno -> aluno.nome().length(), Collectors.counting()));

        System.out.println("Correção exercicio: " + correcao);

        Map<String, Long> nomesETamanhos = Stream.of("banana", "uva", "maçã", "uva")
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));


        System.out.println("Nomes e tamanhos: " + nomesETamanhos);

        // COLETORES DE STREAM
        // groupingby
        // partitioningBy
        // toList()
        // joining()

        /**
         * |Método coletor | Descrição | Valor retornado |
         * |---------------|-----------|-----------------|
         * |averagingDouble(ToDoubleFunction f) <br> averagingInt(ToIntFunction f) <br> averagingLong(ToLongFunction f) |Calcula a média dos elementos |Double|
         * |counting() |Conta o número de elementos |Long|
         * |groupingBy(Function f) <br> groupingBy(Function f, Collector dc) <br> groupingBy(Function f, Supplier s, Collector dc) 	|Cria um *Map* de agrupamentos especificados pelo parâmetro *Function* e os parâmetros opcionais de _Supplier_ e _Collector_ |Map<K, List<T>>|
         * |joining(CharSequence cs) 	|Cria uma _string_ única usando o parâmetro _cs_ como delimitador entre os elementos |String|
         * |maxBy(Comparator c) <br> minBy(Comparator c) |Encontra o maior/menor elemento |Optional<T>|
         * |mapping(Function f, Collector dc) | Adiciona outro nível de coletor |Collector|
         * |partitioningBy(Predicate p) <br> partitioningBy(Predicate p, Collector dc) |Cria um _Map_ de partição _true_ e _false_ especificado pelo parâmetro _Predicate_ com um _Collector_ opcional |Map<Boolean, List<T>>|
         * |summarizingDouble(ToDoubleFunction f) <br> summarizingInt(ToIntFunction f) <br> summarizingLong(ToLongFunction f) | Calcula a média, mínimo, máximo, etc |DoubleSummaryStatistics IntSummaryStatistics LongSummaryStatistics|
         * |summingDouble(ToDoubleFunction f) <br> summingInt(ToIntFunction f) <br> summingLong(ToLongFunction f) |Calcula a soma dos elementos |Double <br> Integer <br> Long|
         * |toList() <br> toSet() |Cria um tipo arbitrário de _List_ ou _Set_ |List <br> Set|
         * |toCollection(Supplier s) |Cria uma coleção do tipo especificado |Collection|
         * |toMap(Function k, Function v) <br> toMap(Function k, Function v, BinaryOperator m) <br> toMap(Function k, Function v, BinaryOperator m, Supplier s) |Cria um mapa usando funções para mapear as chaves (_k_), valores (_v_), uma função de junção opcional (_m_), e um tipo opcional de mapa através de um _Supplier_ (_s_) |Map|
         */
    }


}
