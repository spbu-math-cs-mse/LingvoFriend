package com.lingvoFriend.backend.Services.AuthService;

import com.lingvoFriend.backend.Services.AuthService.dto.TelegramAuthDto;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

@Service
public class TelegramAuthService {

    private final String botToken = "7945790148:AAGv-0cFtmy0m7rHoiRXqsfb-_AKEsgaaqM";

    public boolean checkTelegramAuthorization(TelegramAuthDto telegramAuth) {
        Map<String, String> data = new TreeMap<>();
        data.put("auth_date", telegramAuth.getAuth_date());
        data.put("first_name", telegramAuth.getFirst_name());
        data.put("id", telegramAuth.getId());
        data.put("username", telegramAuth.getUsername());
        if (telegramAuth.getPhoto_url() != null) {
            data.put("photo_url", telegramAuth.getPhoto_url());
        }

        String dataCheckString = String.join("\n", data.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .toArray(String[]::new));

        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ignored) {}
        assert digest != null;
        byte[] secretKey = digest.digest(botToken.getBytes());

        String hash = new HmacUtils("HmacSHA256", secretKey)
                .hmacHex(dataCheckString.getBytes());

        return hash.equals(telegramAuth.getHash());
    }
}
