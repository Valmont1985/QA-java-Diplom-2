package providers;

import org.apache.commons.lang3.RandomStringUtils;
import pojo.CreateOrderRequest;

public class OrderProvider {
    private static String[] ingredients = new String[]{
            "61c0c5a71d1f82001bdaaa6d",
            "61c0c5a71d1f82001bdaaa6f",
            "61c0c5a71d1f82001bdaaa70",
            "61c0c5a71d1f82001bdaaa71",
            "61c0c5a71d1f82001bdaaa72",
            "61c0c5a71d1f82001bdaaa6e",
            "61c0c5a71d1f82001bdaaa73",
            "61c0c5a71d1f82001bdaaa74",
            "61c0c5a71d1f82001bdaaa6c",
            "61c0c5a71d1f82001bdaaa75",
            "61c0c5a71d1f82001bdaaa76",
            "61c0c5a71d1f82001bdaaa77",
            "61c0c5a71d1f82001bdaaa78",
            "61c0c5a71d1f82001bdaaa79",
            "61c0c5a71d1f82001bdaaa7a"};
    public static CreateOrderRequest getRandomCreateOrderRequest() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();

        createOrderRequest.setIngredients(ingredients);

        return createOrderRequest;
    }
    public static CreateOrderRequest getNullIngredients() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setIngredients(new String[] {});

        return createOrderRequest;
    }
    public static CreateOrderRequest getInvalidIngredients() {
        CreateOrderRequest createOrderRequest = new CreateOrderRequest();
        createOrderRequest.setIngredients(new String[] {RandomStringUtils.randomAlphabetic(24)});

        return createOrderRequest;
    }
}