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
import pojo.CreateUserRequest;
import providers.UserProvider;

import java.util.Objects;

public class EditUserDataTest {
    private final UserClient userClient = new UserClient();
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @Step("Изменяем информацию на авторизированном пользователе")
    @DisplayName("Успешное изменение информации о пользователе")
    @Description("Получаем код - 200, В теле ответа есть success (true), изменен email и имя")
    public void successfulEditInfoUser() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        //создание
        accessToken = userClient.create(createUserRequest)
                .statusCode(200)
                .extract().jsonPath().get("accessToken");
        //обновление информации
        createUserRequest = UserProvider.getRandomCreateUserRequest();
        userClient.edit(accessToken, createUserRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .and()
                .body("user.email", Matchers.equalTo(createUserRequest.getEmail().toLowerCase()))
                .and()
                .body("user.name", Matchers.equalTo((createUserRequest.getName())));
    }

    @Test
    @Step("Изменяем информацию на не авторизированном пользователе")
    @DisplayName("Пользователь не обновляется, если он не авторизован")
    @Description("Получаем код - 401, В теле ответа есть success (false), сообщение об ошибке 'Пользователь должен быть авторизован'")
    public void unsuccessfulEditInfoUserWithoutAuth() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        //попытка обновления информации без введенного токена
        accessToken = "";
        userClient.edit(accessToken, createUserRequest)
                .statusCode(401)
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("You should be authorised"));
    }

    @Test
    @Step("Изменяем email пользователя на другой, который уже используется у другого пользователя")
    @DisplayName("Пользователь не обновляется, если введен email, который есть у другого пользователя")
    @Description("Получаем код - 403, В теле ответа есть success (false), сообщение об ошибке 'Пользователь с таким email уже существует'")
    public void unsuccessfulEditInfoUserWithExistEmail() {
        //создаем пользователя 1
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        accessToken = userClient.create(createUserRequest)
                .statusCode(200)
                .extract().jsonPath().get("accessToken");
        //создаем пользователя 2
        CreateUserRequest createUserOneRequest = UserProvider.getRandomCreateUserRequest();
        userClient.create(createUserOneRequest);

        //попытка обновления email
        userClient.edit(accessToken, createUserOneRequest)
                .statusCode(403)
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("User with such email already exists"));
    }
    @After
    public void tearDown() {
        if( !(Objects.equals(accessToken, null)) && !(Objects.equals(accessToken, "")) ) {
            userClient.delete(accessToken)
                    .statusCode(202);
        }
    }
}
