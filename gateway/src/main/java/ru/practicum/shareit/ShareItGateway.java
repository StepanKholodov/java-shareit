package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа в приложение ShareIt Gateway.
 */
@SpringBootApplication
public class ShareItGateway {

	/**
	 * Запускает Spring-контекст приложения.
	 *
	 * @param args аргументы командной строки
	 */
	public static void main(String[] args) {
		SpringApplication.run(ShareItGateway.class, args);
	}
}
