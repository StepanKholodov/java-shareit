package ru.practicum.shareit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Точка входа в приложение ShareIt.
 */
@SpringBootApplication
public class ShareItApp {

	/**
	 * Запускает Spring-контекст приложения.
	 *
	 * @param args аргументы командной строки
	 */
	public static void main(String[] args) {
		SpringApplication.run(ShareItApp.class, args);
	}

}
