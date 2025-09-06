package com.eventhub.AuthMicroService.dao;

import com.eventhub.AuthMicroService.dto.ToActivateUser;
import com.eventhub.AuthMicroService.models.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Repository
public class InMemoryUserDAO {

    private static class UserInfo {
        User user;
        String activation_code;

        public UserInfo(User user, String activation_code) {
            this.user = user;
            this.activation_code = activation_code;
        }
    }

    private final int CODE_LENGTH = 6;
    private final static String CHARS = "QWERTYUIOPASDFGHJKLZXCVBNM1234567890";
    private final StringBuilder stringBuilder = new StringBuilder();
    private final static Random random = new Random();
    private final static ScheduledExecutorService sheduler = Executors.newScheduledThreadPool(1);

    private final Map<UUID, UserInfo> USERS = new HashMap<>();


    public ToActivateUser addUser(User user) {
        UUID uuid = UUID.nameUUIDFromBytes(user.getUsername().getBytes());

        stringBuilder.setLength(0);
        for (byte x=0; x<CODE_LENGTH; x++) {
            int index = random.nextInt(0, CHARS.length() + 1);
            stringBuilder.append(CHARS.charAt(index));
        }

        USERS.put(uuid, new UserInfo(
                user,
                stringBuilder.toString()
        ));
        //Удаление пользователя из мапы через 5 минут
        sheduler.schedule(() -> USERS.remove(uuid), 60, TimeUnit.SECONDS);

        return new ToActivateUser(uuid, stringBuilder.toString());
    }


    public boolean isCodeCorrectByUserUUID(UUID user_uuid, String input_code) {
        var user_info = USERS.get(user_uuid);
        return user_info != null && user_info.activation_code.equals(input_code);
    }


    public User findByUUID(UUID user_uuid) {
        return USERS.get(user_uuid).user;
        //TODO: еще удалить из хэшмапы
    }


    @Scheduled(fixedDelay = 15000)
    public void test() {
        System.out.println(USERS.toString());
    }
}
