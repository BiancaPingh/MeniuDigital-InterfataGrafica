package unitbv.devops.meniudigitalui.service;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class AsyncTaskExecutor {
    private static final ExecutorService executor = Executors.newFixedThreadPool(4);

    public static <T> void executeAsync(
            Supplier<T> backgroundTask,
            Consumer<T> onSuccess,
            Consumer<Throwable> onError,
            Runnable onFinally
    ) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return backgroundTask.get();
            }

            @Override
            protected void succeeded() {
                try {
                    if (onSuccess != null) {
                        onSuccess.accept(getValue());
                    }
                } finally {
                    if (onFinally != null) {
                        onFinally.run();
                    }
                }
            }

            @Override
            protected void failed() {
                try {
                    Throwable ex = getException();
                    if (onError != null) {
                        onError.accept(ex);
                    } else {
                        showError(ex);
                    }
                } finally {
                    if (onFinally != null) {
                        onFinally.run();
                    }
                }
            }
        };

        executor.submit(task);
    }

    public static void executeAsync(
            Runnable backgroundTask,
            Runnable onSuccess,
            Consumer<Throwable> onError,
            Runnable onFinally
    ) {
        executeAsync(
                () -> {
                    backgroundTask.run();
                    return null;
                },
                result -> {
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                },
                onError,
                onFinally
        );
    }

    private static void showError(Throwable ex) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Operation failed");
        alert.setContentText(ex.getMessage());
        alert.showAndWait();
    }

    public static void shutdown() {
        executor.shutdown();
    }
}

