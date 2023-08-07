package clients;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import pojo.CreateUserRequest;
import pojo.LoginUserRequest;
import static io.restassured.RestAssured.given;

public class UserClient extends BaseClient {
    private final String userBaseUri = "/api/auth";
    @Step("Создать пользователя")
    public ValidatableResponse create(CreateUserRequest createUserRequest) {
        return given()
                .spec(getSpec())
                .body(createUserRequest)
                .when()
                .post(userBaseUri + "/register")
                .then();
    }
    @Step("Авторизация пользователя")
    public ValidatableResponse login(LoginUserRequest loginUserRequest) {
        return given()
                .spec(getSpec())
                .body(loginUserRequest)
                .when()
                .post(userBaseUri + "/login")
                .then();
    }
    @Step("Удалить пользователя")
    public ValidatableResponse delete(String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .when()
                .delete(userBaseUri + "/user")
                .then();
    }
    @Step("Получить информацию о пользователе")
    public ValidatableResponse getData(String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .when()
                .get(userBaseUri + "/user")
                .then();
    }
    @Step("Обновить информацию о пользователе")
    public ValidatableResponse edit(String accessToken, CreateUserRequest createUserRequest) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .body(createUserRequest)
                .when()
                .patch(userBaseUri + "/user")
                .then();
    }
}

