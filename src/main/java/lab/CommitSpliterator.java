package lab;

import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

public class CommitSpliterator implements Spliterator<Commit> {
    private final List<Commit> commits;
    private int current = 0;
    private final int end;

    public CommitSpliterator(List<Commit> commits) {
        this.commits = commits;
        this.end = commits.size();
    }

    @Override
    public boolean tryAdvance(Consumer<? super Commit> action) {
        if (current < end) {
            action.accept(commits.get(current++));
            return true;
        }
        return false;
    }

    @Override
    public Spliterator<Commit> trySplit() {
        int mid = (current + end) / 2;
        if (current >= mid) {
            return null;
        }
        CommitSpliterator spliterator = new CommitSpliterator(commits.subList(current, mid));
        current = mid;
        return spliterator;
    }

    @Override
    public long estimateSize() {
        return end - current;
    }

    @Override
    public int characteristics() {
        return Spliterator.ORDERED | Spliterator.SIZED | Spliterator.SUBSIZED;
    }
}

