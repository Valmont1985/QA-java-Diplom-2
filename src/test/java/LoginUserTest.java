import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import pojo.CreateUserRequest;
import pojo.LoginUserRequest;
import providers.UserProvider;
import clients.UserClient;

import java.util.Objects;

public class LoginUserTest {
    private final UserClient userClient = new UserClient();
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @Step("Авторизация под существующим пользователем")
    @DisplayName("Успешная авторизация под существующим пользователем")
    @Description("Получаем код: 200, В теле ответа есть success (true), email, имя, accessToken, refreshToken")
    public void existUserShouldBeAuth() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        //создание
        userClient.create(createUserRequest);
        //авторизация верными данными
        LoginUserRequest loginUserRequest = LoginUserRequest.from(createUserRequest);
        accessToken = userClient.login(loginUserRequest)
                .statusCode(200)
                .body("success", Matchers.equalTo(true))
                .and()
                .body("user.email", Matchers.equalTo(createUserRequest.getEmail().toLowerCase()))
                .and()
                .body("user.name", Matchers.equalTo(createUserRequest.getName()))
                .and()
                .body("accessToken", Matchers.notNullValue())
                .and()
                .body("refreshToken", Matchers.notNullValue())
                .extract().jsonPath().get("accessToken");
    }

    @Test
    @Step("Авторизация с неправильным логином")
    @DisplayName("Пользователь не проходит авторизацию, если введен неверный логин")
    @Description("Получаем код: 401, В теле ответа success (false) и сообщение об ошибке 'Email или пароль не верные'")
    public void unsuccessfulAuthWithIncorrectEmail() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        //создание
        accessToken = userClient.create(createUserRequest)
                .extract().jsonPath().get("accessToken");
        //авторизации с неверным логином
        LoginUserRequest loginUserRequest = LoginUserRequest.from(createUserRequest);
        loginUserRequest.setEmail(RandomStringUtils.randomAlphabetic(8));

        userClient.login(loginUserRequest)
                .statusCode(401)
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("email or password are incorrect"));
    }
    @Test
    @Step("Авторизация с неправильным паролем")
    @DisplayName("Пользователь не проходит авторизацию, если введен неверный пароль")
    @Description("Получаем код: 401, В теле ответа success false и сообщение об ошибке")
    public void unsuccessfulAuthWithIncorrectPassword() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        //создание
        accessToken = userClient.create(createUserRequest)
                .extract().jsonPath().get("accessToken");
        //авторизации с неверным паролем
        LoginUserRequest loginUserRequest = LoginUserRequest.from(createUserRequest);
        loginUserRequest.setPassword(RandomStringUtils.randomAlphabetic(8));

        userClient.login(loginUserRequest)
                .statusCode(401)
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("email or password are incorrect"));
    }
    @After
    public void tearDown() {
        if( !(Objects.equals(accessToken, null)) ) {
            userClient.delete(accessToken)
                    .statusCode(202);
        }
    }
}