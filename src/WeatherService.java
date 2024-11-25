import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

public class WeatherService {

    public static void main(String[] args) {
        System.out.println("Система прогнозу погоди розпочинає роботу...");

        // Етап 1: Отримання даних про погоду з "серверів" (supplyAsync)
        CompletableFuture<List<Integer>> fetchWeatherDataTask = CompletableFuture.supplyAsync(() -> {
            System.out.println("Запит даних про погоду...");
            simulateNetworkDelay();
            return Arrays.asList(
                    ThreadLocalRandom.current().nextInt(-10, 35), // Сервер 1
                    ThreadLocalRandom.current().nextInt(-10, 35), // Сервер 2
                    ThreadLocalRandom.current().nextInt(-10, 35)  // Сервер 3
            );
        });

        // Етап 2: Обчислення середньої температури (thenApplyAsync)
        CompletableFuture<Double> calculateAverageTempTask = fetchWeatherDataTask.thenApplyAsync(temps -> {
            System.out.println("Обчислення середньої температури...");
            return temps.stream()
                    .mapToDouble(Integer::doubleValue)
                    .average()
                    .orElse(0.0);
        });

        // Етап 3: Генерація прогнозу (thenApplyAsync)
        CompletableFuture<String> generateForecastTask = calculateAverageTempTask.thenApplyAsync(avgTemp -> {
            System.out.println("Генерація прогнозу на наступний день...");
            double trend = ThreadLocalRandom.current().nextDouble(-5, 5); // Симуляція тренду
            double forecastTemp = avgTemp + trend;
            return String.format("Прогноз: %.1f°C (тренд: %+.1f°C)", forecastTemp, trend);
        });

        // Етап 4: Виведення результатів (thenAcceptAsync)
        generateForecastTask.thenAcceptAsync(forecast -> {
            System.out.println("Результати:");
            System.out.println(forecast);
        });

        // Етап 5: Повідомлення про завершення (thenRunAsync)
        generateForecastTask.thenRunAsync(() -> {
            System.out.println("Прогноз погоди завершено.");
        }).join(); // Очікуємо завершення

        System.out.println("Система прогнозу погоди завершила роботу.");
    }

    // Метод для симуляції затримки мережі
    private static void simulateNetworkDelay() {
        try {
            Thread.sleep(2000); // Затримка 2 секунди
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
