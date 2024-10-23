package com.example.java_app;

/*import com.example.java_app.model.AppUser;
import com.example.java_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.java_app.model.Category;
import com.example.java_app.model.Services;
import com.example.java_app.model.Tariff;

import com.example.java_app.repository.CategoryRepository;
import com.example.java_app.repository.ServiceRepository;
import com.example.java_app.repository.TariffRepository;

import org.springframework.boot.CommandLineRunner;*/
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/*import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;*/

@SpringBootApplication
@EnableTransactionManagement
public class JavaAppApplication {

    //implements CommandLineRunner

    /*private final CategoryRepository categoryRepository;
    private final ServiceRepository serviceRepository;
    private final TariffRepository tariffRepository;*/

    /*@Autowired
    public JavaAppApplication(CategoryRepository categoryRepository,
                              ServiceRepository serviceRepository,
                              TariffRepository tariffRepository) {
        this.categoryRepository = categoryRepository;
        this.serviceRepository = serviceRepository;
        this.tariffRepository = tariffRepository;
    }*/

    public static void main(String[] args) {
        SpringApplication.run(JavaAppApplication.class, args);
    }

    /*@Override
    public void run(String... args) {
        // Заполнение таблицы Category
        List<Category> categories = Arrays.asList(
                new Category("Movies"),
                new Category("Music"),
                new Category("Bank")
        );
        categoryRepository.saveAll(categories);

        // Заполнение таблицы Service
        List<Services> service = Arrays.asList(
                new Services("Yandex"),
                new Services("Spotify"),
                new Services("SBER")
        );
        serviceRepository.saveAll(service);

        // Заполнение таблицы Tariff
        List<Tariff> tariffs = Arrays.asList(
                new Tariff(1, new BigDecimal("250.00")),
                new Tariff(6, new BigDecimal("1500.00")),
                new Tariff(12, new BigDecimal("3000.00"))
        );
        tariffRepository.saveAll(tariffs);

        System.out.println("Вспомогательные данные успешно загружены в базу данных.");
    }*/
}