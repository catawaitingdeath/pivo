package org.example.pivo;

/**
 * Утилиты для подавления Exception
 */
public class Safety {
    /**
     * Интерфейс для запуска логики не возвращающей значений
     */
    @FunctionalInterface
    public interface UnsafeRunnable {
        /**
         * Не имплементированный метод, который может выбросить проверяемую ошибку
         *
         * @throws Throwable может выбросить любую ошибку. Ошибка должна быть обработана на вызывающей стороне
         */
        void run() throws Throwable;
    }

    /**
     * Интерфейс для запуска логики  возвращающей значения
     */
    @FunctionalInterface
    public interface UnsafeCallable<T> {
        /**
         * Не имплементированный метод, который может выбросить проверяемую ошибку
         *
         * @return результат вычислений
         * @throws Throwable может выбросить любую ошибку. Ошибка должна быть обработана на вызывающей стороне
         */
        T call() throws Throwable;
    }

    /**
     * Запуск логики из {@link UnsafeRunnable}
     *
     * @param r логика не возвращающая значения
     */
    public static void run(UnsafeRunnable r) {
        try {
            r.run();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Запуск логики из {@link UnsafeRunnable}
     *
     * @param r логика не возвращающая значения
     */
    public static void runSilent(UnsafeRunnable r) {
        try {
            r.run();
        } catch (Throwable ignore) {
        }
    }


    /**
     * Запуск логики из {@link UnsafeCallable}
     *
     * @param r   логика, возвращающая значения
     * @param <T> тип данных, который будет возвращен {@link UnsafeCallable}
     * @return результат вычислений внутри {@link UnsafeCallable}
     */
    public static <T> T call(UnsafeCallable<T> r) {
        try {
            return r.call();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Запуск логики из {@link UnsafeCallable}
     *
     * @param r   логика, возвращающая значения
     * @param <T> тип данных, который будет возвращен {@link UnsafeCallable}
     * @return результат вычислений внутри {@link UnsafeCallable}
     */
    public static <T> T callSilent(UnsafeCallable<T> r) {
        try {
            return r.call();
        } catch (Throwable ignored) {
            return null;
        }
    }
}