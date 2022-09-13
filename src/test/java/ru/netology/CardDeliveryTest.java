package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Keys;

import java.security.Key;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        Configuration.headless = true;
        Configuration.holdBrowserOpen = false;
        open("http://localhost:9999/");
        String date = LocalDate.now().plusDays(3).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + date);
    }

    @Test
    void happyPathTest() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//div[contains(text(),\"Успешно\")]").should(Condition.visible, Duration.ofSeconds(15));
    }

    // не административный центр РФ
    @Test
    void shouldNotPassIfCityIsNotAvailableForDelivery() {
        $x("//input[@placeholder=\"Город\"]").setValue("Бостон");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Доставка в выбранный город недоступна\"]").should(Condition.visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldNotPassIfCityWithLatinLetters() {
        $x("//input[@placeholder=\"Город\"]").setValue("Moscow");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Доставка в выбранный город недоступна\"]").should(Condition.visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldNotPassIfCityWithNumbers() {
        $x("//input[@placeholder=\"Город\"]").setValue("777");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Доставка в выбранный город недоступна\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    @Test
    void shouldNotPassIfCityWithSpecialCharacter() {
        $x("//input[@placeholder=\"Город\"]").setValue("Москва>");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Доставка в выбранный город недоступна\"]").should(Condition.visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldNotPassIfFieldCityIsEmpty() {
        $x("//input[@placeholder=\"Город\"]").setValue("");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"city\"].input_invalid  .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }

    // дата ранее трех дней
    @Test
    void shouldNotPassIfDateOneDayEarly() {
        String date = LocalDate.now().plusDays(2).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + date);
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"date\"] .input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldPassIfDateOneDayLater() {
        String date = LocalDate.now().plusDays(4).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + date);
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//div[contains(text(),\"Успешно\")]").should(Condition.visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldNotPassIfDayOnDateIncorrectFormat() {
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE);
        $x("//input[@placeholder=\"Дата встречи\"]").setValue("31.09.2022");
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"date\"] .input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldNotPassIfMonthOnDateIncorrectFormat() {
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE);
        $x("//input[@placeholder=\"Дата встречи\"]").setValue("30.13.2022");
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"date\"] .input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldNotPassIfYearOnDateIncorrectFormat() {
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE);
        $x("//input[@placeholder=\"Дата встречи\"]").setValue("30.09.22");
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"date\"] .input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldNotPassIfDateYesterday() {
        String date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + date);
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"date\"] .input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }

    // флажок не выставлен
    @Test
    void shouldNotPassWithoutAgreementCheckBox() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"agreement\"].input_invalid").should(Condition.visible, Duration.ofSeconds(15));
    }
    // телефон: пустое поле
    @Test
    void shouldNotPassWithoutPhoneNumber() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"phone\"].input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }
    // телефон: "+" и 1 цифра
    @Test
    void shouldNotPassWithOneNumberInPhoneNumber() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+7");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"phone\"].input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }
    // телефон: "+" и 2 цифры
    @Test
    void shouldNotPassWithTwoNumbersInPhoneNumber() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"phone\"].input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }
    // телефон: "+" и 10 цифр
    @Test
    void shouldNotPassWithTenNumbersInPhoneNumber() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+711555257");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"phone\"].input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }
    // телефон: "+" и 12 цифр
    @Test
    void shouldNotPassWithTwelveNumbersInPhoneNumber() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+791155525757");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"phone\"].input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }
    // телефон: без "+"
    @Test
    void shouldNotPassWithoutPlusInPhoneNumber() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"phone\"].input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }
    // телефон: "+" в конце
    @Test
    void shouldNotPassWithPlusAtTheEndOfPhoneNumber() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("79115552575+");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"phone\"].input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }
    // телефон: с кирилицей
    @Test
    void shouldNotPassWithCyrillicInPhoneNumber() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("телефон");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"phone\"].input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }
    // телефон: с латиницей
    @Test
    void shouldNotPassWithLatinLettersInPhoneNumber() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("phone");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"phone\"].input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }
    // телефон: со спецсимволом
    @Test
    void shouldNotPassWithSpecialCharacterInPhoneNumber() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+7911555257>");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"phone\"].input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: латиница
    @Test
    void shouldNotPassIfLatinLettersInNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("James Bond");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: цифры
    @Test
    void shouldNotPassIfNumbersInNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("1111 2222");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: спецсимвол
    @Test
    void shouldNotPassIfSpecialCharacterInNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев>");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: апостроф
    @Test
    void shouldPassIfApostropheInNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья О'Генри");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//div[contains(text(),\"Успешно\")]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: ъ вначале
    @Test
    void shouldNotPassSolidMarkIfBeforeTheFieldNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("ъИлья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: ь вначале
    @Test
    void shouldNotPassSoftSignIfBeforeTheFieldNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("ьИлья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: буква ё
    @Test
    void shouldPassLetterYoInTheFieldNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарёв");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//div[contains(text(),\"Успешно\")]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: дефис вначале
    @Test
    void shouldNotPassHyphenBeforeTheFieldNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("-Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: дефис вконце
    @Test
    void shouldNotPassHyphenAtTheEndOfTheFieldNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев-");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: дефис посередине
    @Test
    void shouldPassHyphenInTheMiddleOfTheFieldNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев-Иванов");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//div[contains(text(),\"Успешно\")]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: пробел перед именем
    @Test
    void shouldNotPassSpaceBeforeNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue(" Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: пробел перед фамилией
    @Test
    void shouldNotPass2SpacesBetweenNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья  Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: только имя
    @Test
    void shouldNotPassNameOnlyInTheFieldNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: только фамилия
    @Test
    void shouldNotPassSurnameOnlyInTheFieldNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: имя из 1 буквы, фамилия нормальная
    @Test
    void shouldNotPassIfNameContainsOnly1Letter() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("И Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: имя из 2 букв, фамилия нормальная
    @Test
    void shouldPassIfNameContains2Letter() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Ян Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//div[contains(text(),\"Успешно\")]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: имя нормальное, фамилия из 1 буквы
    @Test
    void shouldNotPassIfSurnameContainsOnly1Letter() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Ч");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: имя нормальное, фамилия из 2 букв
    @Test
    void shouldPassIfSurnameContains2Letter() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Хо");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//div[contains(text(),\"Успешно\")]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: пустое
    @Test
    void shouldNotPassEmptyFieldNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Поле обязательно для заполнения\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    // поле ФИО: ФИО с отчеством
    @Test
    void shouldNotPassWithFullNameFieldNameAndSurname() {
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Сергеевич Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.\"]").should(Condition.visible, Duration.ofSeconds(15));
    }

    // xpath:
    //1. //teg - значит ищем на всей странице по тегу
    //2. //teg1/teg2 - значит ищем в дочернем теге teg2
    //3. //teg1[@teg2] - ищем по названию атрибута
    //4. //teg1[@teg2="value"] - ищем по значению атрибута
    //5. //*[@teg2="value"]- можно не привязываться к начальному тегу, заменив его на *
    //6. //teg1[text()="текст"] - поиск по тексту веб элемента
    //7. //teg1[contains(text(),"текст")] - поиск по части текста веб элемента
    //8. //teg1[contains(@teg2,"teg2 value")] - поиск по содержанию любого другого атрибута
    //9. //teg1[contains(text(),"текст1")]|//teg1[contains(text(),"текст2")] - поиск одного и того же элемента через логическое выражение или
    //10. //teg1[contains(text(),"текст")]/../../.. - поиск по части текста веб элемента + поднятие на ступени вверх
    //11. //teg1[contains(text(),"текст")]/ancestor::teg3 - поднятся к teg3 от teg1
    // css локаторы
    // по id: #id
    // по классу: .class
    // по атрибуту: наличие атрибута [atribut] или атрибут с определенным значением [atr='value']
//    S1, S2: S1 или/и S2
//    S1 S2: один из родителей S1, дочерний S2 (на любой гглубине вложенности)
//    S1 > S2
//    S1 + S2
//    S1 ~ S2
//    teg[atr]
//    teg[atr='text']

}