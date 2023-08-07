import clients.OrderClient;
import clients.UserClient;
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
import providers.OrderProvider;
import providers.UserProvider;

import java.util.Objects;

public class GetOrderUserTest {
    private final UserClient userClient = new UserClient();
    private final OrderClient orderClient = new OrderClient();
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @Step("Получаем список заказов авторизованного пользователя")
    @DisplayName("Успешное получение заказов авторизованного пользователя")
    @Description("Получаем код - 200, В теле ответа ingredients, _id, status, number, createdAt, updatedAt, total, totalToday содержат ненулевые значения")
    public void ordersWithAuthBeGet() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        //создание и получение токена
        accessToken = userClient.create(createUserRequest)
                .extract().jsonPath().get("accessToken");
        //создание заказа
        CreateOrderRequest createOrderRequest = OrderProvider.getRandomCreateOrderRequest();
        Integer number = orderClient.createWithAuth(createOrderRequest, accessToken)
                .extract().jsonPath().get("order.number");

        //получение заказов
        orderClient.getOrdersUserWithAuth(accessToken)
                .statusCode(200)
                .body("orders.ingredients", Matchers.notNullValue())
                .body("orders._id", Matchers.notNullValue())
                .body("orders.status", Matchers.notNullValue())
                .body("orders.number", Matchers.contains(number))
                .body("orders.createdAt", Matchers.notNullValue())
                .body("orders.updatedAt", Matchers.notNullValue())
                .body("total", Matchers.notNullValue())
                .body("totalToday", Matchers.notNullValue());
    }

    @Test
    @Step("Получаем список заказов неавторизованного пользователя")
    @DisplayName("Нельзя получить список заказов неавторизованного пользователя")
    @Description("Получаем код - 401. В теле ответа есть сообщение об ошибке 'Пользователь должен быть авторизован'")
    public void orderWithoutAuthDontBeGet() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        //создание и получение токена
        accessToken = userClient.create(createUserRequest)
                .extract().jsonPath().get("accessToken");
        //создание заказа 1
        CreateOrderRequest createOrderRequest = OrderProvider.getRandomCreateOrderRequest();
        orderClient.createWithAuth(createOrderRequest, accessToken);
        //попытка получения списка заказов
        orderClient.getOrdersUserWithoutAuth()
                .statusCode(401)
                .body("message", Matchers.equalTo("You should be authorised"));
    }
    @After
    public void tearDown() {
        if( !(Objects.equals(accessToken, null)) && !(Objects.equals(accessToken, "")) ) {
            userClient.delete(accessToken)
                    .statusCode(202);
        }
    }
}

