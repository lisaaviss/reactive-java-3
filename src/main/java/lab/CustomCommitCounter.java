package lab;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class CustomCommitCounter implements Collector<Commit, CustomCommitCounter.CommitCountAccumulator, Long> {

    @Override
    public Supplier<CommitCountAccumulator> supplier() {
        return CommitCountAccumulator::new;
    }

    @Override
    public BiConsumer<CommitCountAccumulator, Commit> accumulator() {
        List<Commit> threadSafeList = new CopyOnWriteArrayList<>();
        return (accumulator, commit) -> {
            if (
                    commit.getBranch().isProtected()
                    & commit.getCreationTime().isAfter(LocalDateTime.parse("2023-01-01T01:00:00"))
                    & commit.getCreationTime().isBefore(LocalDateTime.parse("2024-01-01T01:00:00"))
                    & (commit.getStatus() == CommitStatus.COMPLETED || commit.getStatus() == CommitStatus.PENDING)
                    & commit.getChangedFiles(0).size() > 2
                    & commit.getAuthor().email().contains("@")
            ) {
                accumulator.incrementCount();
                threadSafeList.add(commit);
            }
        };
    }

    @Override
    public BinaryOperator<CommitCountAccumulator> combiner() {
        return (acc1, acc2) -> {
            acc1.combine(acc2);
            return acc1;
        };
    }

    @Override
    public Function<CommitCountAccumulator, Long> finisher() {
        return CommitCountAccumulator::getCount;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of();
    }

    static class CommitCountAccumulator {
        private long count = 0;

        public void incrementCount() {
            count++;
        }

        public void combine(CommitCountAccumulator other) {
            this.count += other.count;
        }

        public long getCount() {
            return count;
        }
    }
}
