package lab;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class CommitFilterTask extends RecursiveTask<List<Commit>> {
    private final List<Commit> commits;
    private final Commit.Author author;
    private final long delay;
    private static final int THRESHOLD = 1000;

    public CommitFilterTask(List<Commit> commits, Commit.Author author, long delay) {
        this.commits = commits;
        this.author = author;
        this.delay = delay;
    }

    @Override
    protected List<Commit> compute() {
        // Если размер задачи меньше порога, выполняем фильтрацию последовательно
        if (commits.size() <= THRESHOLD) {
            return filterCommits(commits);
        }
        int mid = commits.size() / 2;
        CommitFilterTask leftTask = new CommitFilterTask(commits.subList(0, mid), author, delay);
        CommitFilterTask rightTask = new CommitFilterTask(commits.subList(mid, commits.size()), author, delay);

        // Запускаем подзадачи
        leftTask.fork();
        List<Commit> rightResult = rightTask.compute();
        List<Commit> leftResult = leftTask.join();

        // Объединяем результаты подзадач
        leftResult.addAll(rightResult);
        return leftResult;
    }

    private List<Commit> filterCommits(List<Commit> commitList) {
        List<Commit> result = new CopyOnWriteArrayList<>();
        LocalDateTime startTime = LocalDateTime.parse("2023-01-01T01:00:00");
        LocalDateTime endTime = LocalDateTime.parse("2024-01-01T01:00:00");

        for (Commit commit : commitList) {
            if (commit.getAuthor().equals(author) &&
                    commit.getCreationTime().isAfter(startTime) &&
                    commit.getCreationTime().isBefore(endTime) &&
                    (commit.getStatus() == CommitStatus.COMPLETED || commit.getStatus() == CommitStatus.PENDING) &&
                    commit.getChangedFiles(delay).size() > 2 &&
                    commit.getAuthor().email().contains("@")) {

                result.add(commit);
            }
        }
        return result;
    }
}

