package brasileirao.negocio;

import brasileirao.dominio.DataDoJogo;
import brasileirao.dominio.Jogo;
import brasileirao.dominio.PosicaoTabela;
import brasileirao.dominio.Resultado;
import brasileirao.dominio.Time;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Brasileirao {

    private Map<Integer, List<Jogo>> brasileirao;
    private List<Jogo> jogos;
    private Predicate<Jogo> filtro;

    public Brasileirao(Path arquivo, Predicate<Jogo> filtro) throws IOException {
        this.jogos = lerArquivo(arquivo);
        this.filtro = filtro;
        this.brasileirao = jogos.stream()
                .filter(filtro) //filtrar por ano
                .collect(Collectors.groupingBy(
                        Jogo::rodada,
                        Collectors.mapping(Function.identity(), Collectors.toList())));
    }

    public Map<Jogo, Integer> mediaGolsPorJogo() {
        return null;
    }

    public IntSummaryStatistics estatisticasPorJogo() {
        return todosOsJogos().stream()
                .collect(Collectors.summarizingInt(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar()));
    }

    public List<Jogo> todosOsJogos() {
        return this.jogos.stream()
                .filter(filtro)
                .toList();
    }

    public Long totalVitoriasEmCasa() {
        return todosOsJogos().stream().filter(jogo -> jogo.mandantePlacar() > jogo.visitantePlacar()).count();
    }

    public Long totalVitoriasForaDeCasa() {
        return todosOsJogos().stream().filter(jogo -> jogo.mandantePlacar() < jogo.visitantePlacar()).count();
    }

    public Long totalEmpates() {
        return todosOsJogos().stream().filter(jogo -> jogo.mandantePlacar().equals(jogo.visitantePlacar())).count();
    }

    public Long totalJogosComMenosDe3Gols() {
        return todosOsJogos().stream().map(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar()).filter(gols -> gols < 3).count();
    }

    public Long totalJogosCom3OuMaisGols() {
        return todosOsJogos().stream().map(jogo -> jogo.mandantePlacar() + jogo.visitantePlacar()).filter(gols -> gols >= 3).count();
    }

    public Map<Resultado, Long> todosOsPlacares() {
        List<Resultado> resultados = todosOsJogos().stream().map(jogo -> new Resultado(jogo.mandantePlacar(), jogo.visitantePlacar()))
                .toList();
        return resultados.stream()
                .collect(Collectors
                .toMap(resultado -> resultado, resultado -> (long) Collections.frequency(resultados, resultado), (a, b) -> a
        ));
    }

    public Map.Entry<Resultado, Long> placarMaisRepetido() {
        return todosOsPlacares().entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).orElse(null);
    }

    public Map.Entry<Resultado, Long> placarMenosRepetido() {
        return todosOsPlacares().entrySet().stream().min(Comparator.comparing(Map.Entry::getValue)).orElse(null);
    }

    private List<Time> todosOsTimes() {
        List<Time> mandantes = todosOsJogos()
                .stream()
                .map(Jogo::mandante)
                .toList();

        List<Time> visitantes = todosOsJogos()
                .stream()
                .map(Jogo::visitante)
                .toList();

        return null;
    }

    /**
     * todos os jogos que cada time foi mandante
     *
     * @return Map<Time, List < Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoMandantes() {
        return todosOsJogos().stream().collect(Collectors.groupingBy(Jogo::mandante));
    }
    /**
     * todos os jogos que cada time foi visitante
     *
     * @return Map<Time, List < Jogo>>
     */
    private Map<Time, List<Jogo>> todosOsJogosPorTimeComoVisitante() {

        return todosOsJogos().stream().collect(Collectors.groupingBy(Jogo::visitante));
    }

    public Map<Time, List<Jogo>> todosOsJogosPorTime() {
        return Stream.concat(todosOsJogosPorTimeComoMandantes().entrySet().stream(), todosOsJogosPorTimeComoVisitante().entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> {
                            a.addAll(b);
                            return a;
                        }
                ));
    }
    //public Map<Time, Map<Boolean, List<Jogo>>> jogosParticionadosPorMandanteTrueVisitanteFalse() {
    //    return null;
    //}

    public Set<PosicaoTabela> tabela() {
        List<PosicaoTabela> posicaoTabelas = todosOsJogosPorTime().keySet()
                .stream()
                .map(time -> new PosicaoTabela(time,
                        totalDeVitoriasPorTime(time),
                        totalDeDerrotasPorTime(time),
                        totalDeEmpatesPorTime(time),
                        totalDeGolsPorTime(time),
                        totalDeGolsSofridosPorTime(time),
                        totalDeGolsPorTime(time) - totalDeGolsSofridosPorTime(time)))
                .sorted(Comparator.comparing(PosicaoTabela::getPontuacaoTotal).reversed()).toList();
        return new LinkedHashSet<>(posicaoTabelas);
    }

    public List<Jogo> lerArquivo(Path file) throws IOException {
        List<String> lista = Files.readAllLines(file);
        lista.replaceAll(x -> x.replace(":", "h"));

        List<String[]> campoArquivo = lista.stream()
                .skip(1)
                .map(linha -> linha.split(";"))
                .toList();

        return campoArquivo.stream()
                .map(campo ->
                        new Jogo(Integer.valueOf(campo[0]),
                                new DataDoJogo(LocalDate.parse(campo[1], DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                        LocalTime.parse(horaVazia(campo[2]), DateTimeFormatter.ofPattern("HH'h'mm")), getDayOfWeek(campo[3])),
                                new Time(campo[4]),
                                new Time(campo[5]),
                                new Time(campo[6]),
                                campo[7], Integer.valueOf(campo[8]), Integer.valueOf(campo[9]), campo[10], campo[11], campo[12])).toList();
    }

    private DayOfWeek getDayOfWeek(String dia) {
        return Map.of(
                "Segunda-feira", DayOfWeek.MONDAY,
                "Terça-feira", DayOfWeek.TUESDAY,
                "Quarta-feira", DayOfWeek.WEDNESDAY,
                "Quinta-feira", DayOfWeek.THURSDAY,
                "Sexta-feira", DayOfWeek.FRIDAY,
                "Sábado", DayOfWeek.SATURDAY,
                "Domingo", DayOfWeek.SUNDAY
        ).get(dia);
    }

    // METODOS EXTRA

    private Map<Integer, Integer> totalGolsPorRodada() {
        return null;
    }

    private Long totalDeGolsPorTime(Time time) {
        return Long.valueOf(todosOsJogosPorTime().get(time).stream().mapToInt(jogo -> {if(jogo.mandante().equals(time)){return jogo.mandantePlacar();
        } else if (jogo.visitante().equals(time)) {return jogo.visitantePlacar();}return 0;}).sum());
    }

    private Map<Integer, Double> mediaDeGolsPorRodada() { return null;
    }

    private String horaVazia(String hora) {
        if (hora.isEmpty()) {
            return "16h00";
        }
        return hora;
    }

    private Long totalDeVitoriasPorTime(Time time){
        return todosOsJogosPorTime().get(time).stream().filter(t -> t.vencedor().equals(time)).count();
    }

    private Long totalDeDerrotasPorTime(Time time){
        return todosOsJogosPorTime().get(time).stream().filter(t -> !t.vencedor().equals(time)).count();
    }
    private Long totalDeEmpatesPorTime(Time time){
        return todosOsJogosPorTime().get(time).stream().filter(t -> t.estadoVencedor().equals("-")).count();
    }

    private Long totalDeGolsSofridosPorTime(Time time){
        return Long.valueOf(todosOsJogosPorTime().get(time).stream().mapToInt(jogo -> {if(!jogo.mandante().equals(time)){return jogo.mandantePlacar();
        } else if (!jogo.visitante().equals(time)) {return jogo.visitantePlacar();}return 0;}).sum());
    }

}
