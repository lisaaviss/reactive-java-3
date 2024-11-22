package lab;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subscribers.ResourceSubscriber;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class AggregatedDataCalculator {

    public static StringBuilder calculateAggregatedData() {
        StringBuilder result = new StringBuilder();
        List<Commit> threadSafeList = new CopyOnWriteArrayList<>();

        List<Commit> commitList100000 = CollectionFill.collectionFill(100000);

        /*int calculateWithStreamAPIResult = calculateWithStreamAPI(commitList100000, threadSafeList, 0).size();
        int calculateWithReactiveResult = calculateWithReactive(commitList100000, threadSafeList, 0).size();*/
        int calculateWithReactiveFlowResult = calculateWithReactiveFlow(commitList100000, threadSafeList, 0).size();


        /*result.append("Количество собранное с помощью calculateWithStreamAPI: " + calculateWithStreamAPIResult +
        "\nКоличество собранное с помощью calculateWithReactive: " +  calculateWithReactiveResult +
                "\nКоличество собранное с помощью calculateWithReactive: " +   calculateWithReactiveFlowResult);*/

        result.append("Количество собранное с помощью calculateWithReactive: " +   calculateWithReactiveFlowResult);

        return result;
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



    public static List<Commit> calculateWithReactiveFlow(List<Commit> commitList, List<Commit> threadSafeList, long delay) {
        Flowable.fromIterable(commitList)
                // Фильтруем элементы реактивно
                .filter(commit -> commit.getAuthor().equals(commitList.get(0).getAuthor())) // Автор совпадает
                .filter(commit -> commit.getCreationTime().isAfter(LocalDateTime.parse("2023-01-01T01:00:00")))
                .filter(commit -> commit.getCreationTime().isBefore(LocalDateTime.parse("2024-01-01T01:00:00")))
                .filter(commit -> commit.getStatus() == CommitStatus.COMPLETED || commit.getStatus() == CommitStatus.PENDING)
                .filter(commit -> commit.getChangedFiles(delay).size() > 2)
                .filter(commit -> commit.getAuthor().email().contains("@"))
                // Асинхронная обработка с поддержкой backpressure
                .observeOn(Schedulers.computation()) // Асинхронная обработка на отдельном потоке
                .subscribe(new CommitSubscriber(threadSafeList)); // Наш кастомный Subscriber

        return threadSafeList;
    }

    static class CommitSubscriber implements Subscriber<Commit> {
        private final List<Commit> threadSafeList; // Результирующая коллекция
        private Subscription subscription; // Для управления потоком
        private int processed = 0; // Счетчик обработанных элементов

        public CommitSubscriber(List<Commit> threadSafeList) {
            this.threadSafeList = threadSafeList;
        }

        @Override
        public void onSubscribe(Subscription s) {
            this.subscription = s;
            s.request(1); // Запрашиваем первый элемент
        }

        @Override
        public void onNext(Commit commit) {
            // Добавляем элемент в потокобезопасный список
            threadSafeList.add(commit);
            processed++;

            // Запрашиваем следующий элемент
            subscription.request(1);
        }

        @Override
        public void onError(Throwable t) {
            System.err.println("Произошла ошибка: " + t.getMessage());
        }

        @Override
        public void onComplete() {
            System.out.println("Обработка завершена. Всего обработано: " + processed + " элементов.");
        }
    }
}
