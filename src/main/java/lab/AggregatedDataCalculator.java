package lab;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

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

            iterationTotalTime5000 += calculateWithIteration(commitList5000);
            iterationTotalTime50000 += calculateWithIteration(commitList50000);
            iterationTotalTime250000 += calculateWithIteration(commitList250000);

//            streamAPITotalTime5000 += calculateWithStreamAPI(commitList5000, 0);
//            streamAPITotalTime50000 += calculateWithStreamAPI(commitList50000, 0);
//            streamAPITotalTime250000 += calculateWithStreamAPI(commitList250000, 0);

            customCollectorTotalTime5000 += calculateWithCustomCollector(commitList5000);
            customCollectorTotalTime50000 += calculateWithCustomCollector(commitList50000);
            customCollectorTotalTime250000 += calculateWithCustomCollector(commitList250000);
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

    public static Long calculateWithIteration(List<Commit> commitList) {
        List<Commit> threadSafeList = new CopyOnWriteArrayList<>();
        Instant start = Instant.now();
        long countedCommits = 0;
        Iterator<Commit> iter = commitList.listIterator();
        Commit.Author author = commitList.get(0).getAuthor();
        while (iter.hasNext()) {
            Commit commit = iter.next();
            if (commit.getAuthor().equals(author)
                    && commit.getCreationTime().isAfter(LocalDateTime.parse("2023-01-01T01:00:00"))
                    && commit.getCreationTime().isBefore(LocalDateTime.parse("2024-01-01T01:00:00"))
                    && (commit.getStatus() == CommitStatus.COMPLETED || commit.getStatus() == CommitStatus.PENDING)
                    && commit.getChangedFiles(0).size() > 2
                    && commit.getAuthor().email().contains("@")) {
                threadSafeList.add(commit);
                countedCommits++;
            }
        }
        //System.out.println(countedCommits);
        Instant end = Instant.now();
        return Duration.between(start, end).toMillis();
    }

    public static List<Commit> calculateWithStreamAPI(List<Commit> commitList, List<Commit> threadSafeList, long delay) {
        Instant start = Instant.now();
        Commit.Author author = commitList.get(0).getAuthor();
        commitList.parallelStream() //параллельный стрим
                .filter(commit -> commit.getAuthor().equals(author))
                .filter(commit -> commit.getCreationTime().isAfter(LocalDateTime.parse("2023-01-01T01:00:00")))
                .filter(commit -> commit.getCreationTime().isBefore(LocalDateTime.parse("2024-01-01T01:00:00")))
                .filter(commit -> (commit.getStatus() == CommitStatus.COMPLETED || commit.getStatus() == CommitStatus.PENDING))
                .filter(commit -> commit.getChangedFiles(delay).size() > 2)
                .filter(commit -> commit.getAuthor().email().contains("@"))
                .forEach(threadSafeList::add);
        //System.out.println(threadSafeList.size());

        Instant end = Instant.now();
        return threadSafeList;

    }

    public static Long calculateWithStreamAPIconsistent(List<Commit> commitList, long delay) {
        List<Commit> threadSafeList = new CopyOnWriteArrayList<>();
        Instant start = Instant.now();
        Commit.Author author = commitList.get(0).getAuthor();
        commitList.stream() //параллельный стрим
                .filter(commit -> commit.getAuthor().equals(author))
                .filter(commit -> commit.getCreationTime().isAfter(LocalDateTime.parse("2023-01-01T01:00:00")))
                .filter(commit -> commit.getCreationTime().isBefore(LocalDateTime.parse("2024-01-01T01:00:00")))
                .filter(commit -> (commit.getStatus() == CommitStatus.COMPLETED || commit.getStatus() == CommitStatus.PENDING))
                .filter(commit -> commit.getChangedFiles(delay).size() > 2)
                .filter(commit -> commit.getAuthor().email().contains("@"))
                .forEach(threadSafeList::add);
        //System.out.println(threadSafeList.size());

        Instant end = Instant.now();
        return Duration.between(start, end).toMillis();

    }

    public static Long calculateWithForkJoin(List<Commit> commitList, long delay) {
        Instant start = Instant.now();

        // Инициализируем ForkJoinPool и запускаем задачу
        ForkJoinPool pool = ForkJoinPool.commonPool();
        Commit.Author author = commitList.get(0).getAuthor();
        CommitFilterTask task = new CommitFilterTask(commitList, author, delay);
        List<Commit> result = pool.invoke(task);

        Instant end = Instant.now();
        return Duration.between(start, end).toMillis();
    }
    public static Long calculateWithCustomCollector(List<Commit> commitList) {
        Instant start = Instant.now();

        Commit.Author author = commitList.get(0).getAuthor();
        long countedCommits = commitList.parallelStream()
                .filter(commit -> commit.getAuthor().equals(author))
                .collect(new CustomCommitCounter());
        System.out.println(countedCommits);

        Instant end = Instant.now();
        return Duration.between(start, end).toMillis();

    }

    /*public static Long calculateWithCustomSpliterator(List<Commit> commitList, long delay) {
        Instant start = Instant.now();
        Commit.Author author = commitList.get(0).getAuthor();
        List<Commit> threadSafeList = new CopyOnWriteArrayList<>();

        CommitSpliterator spliterator = new CommitSpliterator(commitList);
        spliterator.forEachRemaining(commit -> {
            if (filterCommit(commit, author, delay)) {
                threadSafeList.add(commit);
            }
        });

        Instant end = Instant.now();
        return Duration.between(start, end).toMillis();
    }

    private static boolean filterCommit(Commit commit, Commit.Author author, long delay) {
        return commit.getAuthor().equals(author) &&
                commit.getCreationTime().isAfter(LocalDateTime.parse("2023-01-01T01:00:00")) &&
                commit.getCreationTime().isBefore(LocalDateTime.parse("2024-01-01T01:00:00")) &&
                (commit.getStatus() == CommitStatus.COMPLETED || commit.getStatus() == CommitStatus.PENDING) &&
                commit.getChangedFiles(delay).size() > 2 &&
                commit.getAuthor().email().contains("@");
    }*/
}
