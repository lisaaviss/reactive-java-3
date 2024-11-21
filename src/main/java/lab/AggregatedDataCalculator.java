package lab;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

public class AggregatedDataCalculator {

    private static final int NUM_EXPERIMENTS =  1;
    private static final Random random = new Random();

    public static StringBuilder calculateAggregatedData() {
        StringBuilder result = new StringBuilder();
        long iterationTotalTime5000 = 0;
        long iterationTotalTime50000 = 0;
        long iterationTotalTime250000 = 0;


        long streamAPITotalTime5000 = 0;
        long streamAPITotalTime50000 = 0;
        long streamAPITotalTime250000 = 0;

        long customCollectorTotalTime5000 = 0;
        long customCollectorTotalTime50000 = 0;
        long customCollectorTotalTime250000 = 0;

        for (int i =0; i < NUM_EXPERIMENTS; i++){
            List<Commit> commitList5000 = CollectionFill.collectionFill(5000);
            List<Commit> commitList50000 = CollectionFill.collectionFill(50000);
            List<Commit> commitList250000 = CollectionFill.collectionFill(250000);
        }

        result.append("Среднее время выполнения итерационного метода для 5000 элементов: "
                + iterationTotalTime5000/NUM_EXPERIMENTS + "мс\n");
        result.append("Среднее время выполнения итерационного метода для 50000 элементов: "
                + iterationTotalTime50000/NUM_EXPERIMENTS + "мс\n");
        result.append("Среднее время выполнения итерационного метода для 250000 элементов: "
                + iterationTotalTime250000/NUM_EXPERIMENTS + "мс\n\n");

        result.append("Среднее время выполнения метода Stream API для 5000 элементов: "
                + streamAPITotalTime5000/NUM_EXPERIMENTS + "мс\n");
        result.append("Среднее время выполнения метода Stream API для 50000 элементов: "
                + streamAPITotalTime50000/NUM_EXPERIMENTS + "мс\n");
        result.append("Среднее время выполнения метода Stream API для 250000 элементов: "
                + streamAPITotalTime250000/NUM_EXPERIMENTS + "мс\n\n");

        result.append("Среднее время выполнения итерационного метода для 5000 элементов: "
                + customCollectorTotalTime5000/NUM_EXPERIMENTS + "мс\n");
        result.append("Среднее время выполнения итерационного метода для 50000 элементов: "
                + customCollectorTotalTime50000/NUM_EXPERIMENTS + "мс\n");
        result.append("Среднее время выполнения итерационного метода для 250000 элементов: "
                + customCollectorTotalTime250000/NUM_EXPERIMENTS + "мс\n");
        return result;
    }

    public static List<Commit> calculateWithReactive(List<Commit> commitList, List<Commit> threadSafeList, long delay) {
        Commit.Author author = commitList.get(0).getAuthor();
        Observable.fromIterable(commitList)
                .filter(commit -> commit.getAuthor().equals(author))
                .filter(commit -> commit.getCreationTime().isAfter(LocalDateTime.parse("2023-01-01T01:00:00")))
                .filter(commit -> commit.getCreationTime().isBefore(LocalDateTime.parse("2024-01-01T01:00:00")))
                .filter(commit -> commit.getStatus() == CommitStatus.COMPLETED || commit.getStatus() == CommitStatus.PENDING)
                .filter(commit -> commit.getChangedFiles(delay).size() > 2)
                .filter(commit -> commit.getAuthor().email().contains("@"))
                .observeOn(Schedulers.computation()) // Переключение на многопоточную обработку
                .subscribe(threadSafeList::add, Throwable::printStackTrace);

        return threadSafeList;
    }

    public static List<Commit> calculateWithStreamAPI(List<Commit> commitList, List<Commit> threadSafeList, long delay) {
        Commit.Author author = commitList.get(0).getAuthor();
        commitList.parallelStream() //параллельный стрим
                .filter(commit -> commit.getAuthor().equals(author))
                .filter(commit -> commit.getCreationTime().isAfter(LocalDateTime.parse("2023-01-01T01:00:00")))
                .filter(commit -> commit.getCreationTime().isBefore(LocalDateTime.parse("2024-01-01T01:00:00")))
                .filter(commit -> (commit.getStatus() == CommitStatus.COMPLETED || commit.getStatus() == CommitStatus.PENDING))
                .filter(commit -> commit.getChangedFiles(delay).size() > 2)
                .filter(commit -> commit.getAuthor().email().contains("@"))
                .forEach(threadSafeList::add);
        return threadSafeList;

    }
}
