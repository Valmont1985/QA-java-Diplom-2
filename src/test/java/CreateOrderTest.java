import clients.OrderClient;
import clients.UserClient;
import providers.OrderProvider;
import providers.UserProvider;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.CreateOrderRequest;
import pojo.CreateUserRequest;

import java.util.Objects;

public class CreateOrderTest {
    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @Step("Создаем заказ с авторизацией пользователя")
    @DisplayName("Успешное создание заказа с ингредиентами и с авторизированным пользователем")
    @Description("Получаем код - 200, В теле ответа есть success (true), номер заказа и имя")
    public void orderWithAuthBeCreated() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        //создание и получение токена
        accessToken = userClient.create(createUserRequest)
                .extract().jsonPath().get("accessToken");
        //создание заказа
        CreateOrderRequest createOrderRequest = OrderProvider.getRandomCreateOrderRequest();
        orderClient.createWithAuth(createOrderRequest, accessToken)
                .statusCode(200)
                .body("name", Matchers.notNullValue())
                .and()
                .body("order.number", Matchers.notNullValue())
                .and()
                .body("success", Matchers.equalTo(true));
    }

    @Test
    @Step("Создаем заказ без авторизации пользователя")
    @DisplayName("Успешное создание заказа с ингредиентами и без авторизации")
    @Description("Получаем код - 200, В теле ответа есть success (true), номер заказа и имя")
    public void orderWithoutAuthDontBeCreated() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        //создание и получение токена
        accessToken = userClient.create(createUserRequest)
                .extract().jsonPath().get("accessToken");
        //создание заказа
        CreateOrderRequest createOrderRequest = OrderProvider.getRandomCreateOrderRequest();
        orderClient.createWithoutAuth(createOrderRequest)
                .statusCode(200)
                .body("name", Matchers.notNullValue())
                .and()
                .body("order.number", Matchers.notNullValue())
                .and()
                .body("success", Matchers.equalTo(true));

    }

    @Test
    @Step("Создаем заказ без ингредиентов")
    @DisplayName("Без ингредиентов заказ не создается")
    @Description("Получаем код - 400, В теле ответа есть success (false) и сообщение об ошибке 'Необходимо предоставить id ингредиентов'")
    public void orderWithoutIngredientsDontBeCreated() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        //создание и получение токена
        accessToken = userClient.create(createUserRequest)
                .extract().jsonPath().get("accessToken");
        //попытка создания заказа без ингредиентов
        CreateOrderRequest createOrderRequest = OrderProvider.getNullIngredients();
        orderClient.createWithAuth(createOrderRequest, accessToken)
                .statusCode(400)
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("Ingredient ids must be provided"));
    }

    @Test
    @Step("Создаем заказ с неверным хешем ингредиентов")
    @DisplayName("Не создается заказ при передаче неверного хеша ингредиента")
    @Description("Получаем код - 500")
    public void orderWithInvalidIngredientsDontBeCreated() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        //создание и получение токена
        accessToken = userClient.create(createUserRequest)
                .extract().jsonPath().get("accessToken");
        //попытка создания заказа c неверным ингредиентом
        CreateOrderRequest createOrderRequest = OrderProvider.getInvalidIngredients();
        orderClient.createWithAuth(createOrderRequest, accessToken)
                .statusCode(500);
    }

    @After
    public void tearDown() {
        if (!(Objects.equals(accessToken, null)) && !(Objects.equals(accessToken, ""))) {
            userClient.delete(accessToken)
                    .statusCode(202);
        }
    }
}
