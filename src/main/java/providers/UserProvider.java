package providers;

import org.apache.commons.lang3.RandomStringUtils;
import pojo.CreateUserRequest;

public class UserProvider {
    //Генерируем email с доменом yandex.ru
    private static final String DOMAIN = "@yandex.ru";

    public static String generateRandomEmail() {
        String email_beginning = RandomStringUtils.randomAlphabetic(7);
        return email_beginning + DOMAIN;
    }
    //Получаем данные
    public static CreateUserRequest getRandomCreateUserRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail(generateRandomEmail());
        createUserRequest.setPassword(RandomStringUtils.randomAlphabetic(7));
        createUserRequest.setName(RandomStringUtils.randomAlphabetic(7));

        return createUserRequest;
    }
    public static CreateUserRequest getRandomCreateUserWithoutEmailRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail(null);
        createUserRequest.setPassword(RandomStringUtils.randomAlphabetic(7));
        createUserRequest.setName(RandomStringUtils.randomAlphabetic(7));

        return createUserRequest;
    }
    public static CreateUserRequest getRandomCreateUserWithoutPasswordRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail(generateRandomEmail());
        createUserRequest.setPassword(RandomStringUtils.randomAlphabetic(0));
        createUserRequest.setName(RandomStringUtils.randomAlphabetic(7));

        return createUserRequest;
    }
    public static CreateUserRequest getRandomCreateUserWithoutNameRequest() {
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setEmail(generateRandomEmail());
        createUserRequest.setPassword(RandomStringUtils.randomAlphabetic(7));
        createUserRequest.setName(RandomStringUtils.randomAlphabetic(0));

        return createUserRequest;
    }
}