package com.example.reservasmedicasmobile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class HCaptchaVerifier {

    private static final String SECRET_KEY = "ES_27c4...";

    public static boolean verifyHCaptcha(String hCaptchaToken) {
        try {
            String url = "https://hcaptcha.com/siteverify";
            String params = "secret=" + URLEncoder.encode(SECRET_KEY, "UTF-8") +
                    "&response=" + URLEncoder.encode(hCaptchaToken, "UTF-8");
            URL obj = new URL(url + "?" + params);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            // Procesa la respuesta
            // Retorna true si la verificación fue exitosa
            return response.toString().contains("\"success\": true");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
