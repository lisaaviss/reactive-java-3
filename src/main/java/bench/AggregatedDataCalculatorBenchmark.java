package bench;

import lab.AggregatedDataCalculator;
import lab.CollectionFill;
import lab.Commit;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class AggregatedDataCalculatorBenchmark {

    private List<Commit> commitList500;
    private List<Commit> commitList2000;
    private List<Commit> threadSafeList;

    @Setup(Level.Trial)
    public void setUp() {
        commitList500 = CollectionFill.collectionFill(500);
        commitList2000 = CollectionFill.collectionFill(2000);
        threadSafeList = new CopyOnWriteArrayList<>();
    }
    @Group("ParallelStreamDelay")
    @Benchmark
    public List<Commit> benchmarkStreamAPI500() {
        return AggregatedDataCalculator.calculateWithStreamAPI(commitList500, threadSafeList, 1);
    }
    @Group("ParallelStreamDelay")
    @Benchmark
    public List<Commit> benchmarkStreamAPI2000() {
        return AggregatedDataCalculator.calculateWithStreamAPI(commitList2000, threadSafeList, 1);
    }
    //calculateWithReactive
    @Group("ParallelReactiveDelay")
    @Benchmark
    public List<Commit> ParallelReactiveDelay500() {
        return AggregatedDataCalculator.calculateWithStreamAPI(commitList500, threadSafeList, 1);
    }
    @Group("ParallelReactiveDelay")
    @Benchmark
    public List<Commit> ParallelReactiveDelay2000() {
        return AggregatedDataCalculator.calculateWithStreamAPI(commitList2000, threadSafeList, 1);
    }


}


