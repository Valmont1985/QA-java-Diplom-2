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
import clients.UserClient;

import java.util.Objects;


public class CreateUserTest {
    private final UserClient userClient = new UserClient();
    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    @Step("Создаем нового пользователя")
    @DisplayName("Успешное создание пользователя")
    @Description("Получаем код - 200, В теле ответа есть success (true), email, имя, accessToken, refreshToken")
    public void uniqueUserShouldBeCreated() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        //создание
        //из тела берем access token
        accessToken = userClient.create(createUserRequest)
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
    @Step("Создаем пользователя, который уже зарегистрирован")
    @DisplayName("Пользователь, который уже зарегистрирован - не будет создан")
    @Description("Получаем код - 403, тело ответа: success (false), message 'Пользователь уже существует'")
    public void sameUserDontBeCreated() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserRequest();
        //создание
        accessToken = userClient.create(createUserRequest)
                .statusCode(200)
                .extract().jsonPath().get("accessToken");
        //повторное создание
        userClient.create(createUserRequest)
                .statusCode(403)
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("User already exists"));
    }

    @Test
    @Step("Создаем пользователя с пустым полем Email")
    @DisplayName("Пользователь не создается, если не указан email")
    @Description("Получаем код - 403, тело ответа: success (false), message 'Необходимо заполнить поля имя, email и пароль'")
    public void userDontBeCreatedWithoutEmail() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserWithoutEmailRequest();
        //создание
        userClient.create(createUserRequest)
                .statusCode(403)
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("Email, password and name are required fields"));
    }

    @Test
    @Step("Создаем пользователя с пустым полем Пароль")
    @DisplayName("Пользователь не создается, если не указать пароль")
    @Description("Получаем код - 403, тело ответа: success (false), message 'Необходимо заполнить поля имя, email и пароль'")
    public void userDontBeCreatedWithoutPassword() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserWithoutPasswordRequest();
        //создание
        userClient.create(createUserRequest)
                .statusCode(403)
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("Email, password and name are required fields"));
    }

    @Test
    @Step("Создаем пользователя с пустым полем Имя")
    @DisplayName("Пользователь не создается, если не указать имя")
    @Description("Получаем код - 403, тело ответа: success (false), message 'Необходимо заполнить поля имя, email и пароль'")
    public void userDontBeCreatedWithoutName() {
        CreateUserRequest createUserRequest = UserProvider.getRandomCreateUserWithoutNameRequest();
        //создание
        userClient.create(createUserRequest)
                .statusCode(403)
                .body("success", Matchers.equalTo(false))
                .and()
                .body("message", Matchers.equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        if( !(Objects.equals(accessToken, null)) ) {
            userClient.delete(accessToken)
                    .statusCode(202);
        }
    }
}