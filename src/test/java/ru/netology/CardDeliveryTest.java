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
        Configuration.holdBrowserOpen = true;
        open("http://localhost:9999/");
    }

    public String generateDate(int days) {
        return LocalDate.now().plusDays(days).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Test
    void happyPathTest() {
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
        $x("//input[@placeholder=\"Город\"]").setValue("Бостон");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Доставка в выбранный город недоступна\"]").should(Condition.visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldNotPassIfCityWithLatinLetters() {
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
        $x("//input[@placeholder=\"Город\"]").setValue("Moscow");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Доставка в выбранный город недоступна\"]").should(Condition.visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldNotPassIfCityWithNumbers() {
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
        $x("//input[@placeholder=\"Город\"]").setValue("777");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Доставка в выбранный город недоступна\"]").should(Condition.visible, Duration.ofSeconds(15));
    }
    @Test
    void shouldNotPassIfCityWithSpecialCharacter() {
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
        $x("//input[@placeholder=\"Город\"]").setValue("Москва>");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Доставка в выбранный город недоступна\"]").should(Condition.visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldNotPassIfFieldCityIsEmpty() {
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(2);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"date\"] .input_invalid .input__sub").should(Condition.visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldPassIfDateOneDayLater() {
        String planningDate = generateDate(4);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(-1);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $("[data-test-id=\"agreement\"].input_invalid").should(Condition.visible, Duration.ofSeconds(15));
    }
    // телефон: пустое поле
    @Test
    void shouldNotPassWithoutPhoneNumber() {
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
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
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Сергеевич Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//span[text()=\"Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.\"]").should(Condition.visible, Duration.ofSeconds(15));
    }

    @Test
    void shouldPassIfDateInNotificationContentMatches() {
        String planningDate = generateDate(3);
        $x("//input[@placeholder=\"Дата встречи\"]").doubleClick().sendKeys(Keys.DELETE + planningDate);
        $x("//input[@placeholder=\"Город\"]").setValue("Архангельск");
        $x("//input[@name=\"name\"]").setValue("Илья Чухарев");
        $x("//input[@name=\"phone\"]").setValue("+79115552575");
        $("[data-test-id=\"agreement\"] .checkbox__box").click();
        $x("//span[contains(text(),\"Забронировать\")]").click();
        $x("//div[contains(text(),\"Успешно\")]").should(Condition.visible, Duration.ofSeconds(15));
        $(".notification__content").shouldHave(Condition.text("Встреча успешно забронирована на " + planningDate), Duration.ofSeconds(15)).shouldBe(Condition.visible);
    }
}