package clients;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import pojo.CreateOrderRequest;
import static io.restassured.RestAssured.given;

public class OrderClient extends BaseClient {
    private final String orderBaseUri = "/api";
    @Step("Создать заказ с авторизацией")
    public ValidatableResponse createWithAuth(CreateOrderRequest createOrderRequest, String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .body(createOrderRequest)
                .when()
                .post(orderBaseUri + "/orders")
                .then();
    }
    @Step("Создать заказ без авторизации")
    public ValidatableResponse createWithoutAuth(CreateOrderRequest createOrderRequest) {
        return given()
                .spec(getSpec())
                .body(createOrderRequest)
                .when()
                .post(orderBaseUri + "/orders")
                .then();
    }

    @Step("Получить данные об ингредиентах")
    public ValidatableResponse getDataIngredients() {
        return given()
                .spec(getSpec())
                .when()
                .get(orderBaseUri + "/ingredients")
                .then();
    }
    @Step("Получить заказы пользователя с авторизацией")
    public ValidatableResponse getOrdersUserWithAuth(String accessToken) {
        return given()
                .spec(getSpec())
                .header("Authorization", accessToken)
                .when()
                .get(orderBaseUri + "/orders")
                .then();
    }
    @Step("Получить заказы пользователя без авторизации")
    public ValidatableResponse getOrdersUserWithoutAuth() {
        return given()
                .spec(getSpec())
                .when()
                .get(orderBaseUri + "/orders")
                .then();
    }
}

