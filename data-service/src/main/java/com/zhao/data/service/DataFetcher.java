package com.zhao.data.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class DataFetcher {

    public String getAirQuality(String city) {
        try {
            // 使用免费的公共天气API
            URL url = new URL("http://www.nmc.cn/rest/weather?stationid=57516");

            System.out.println("请求URL: " + url.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            System.out.println("响应码: " + responseCode);

            if (responseCode == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                // 返回模拟数据继续开发
                return "{\"status\":\"ok\",\"data\":{\"aqi\":58,\"pm25\":26,\"city\":\"" + city + "\"}}";
            }

        } catch (Exception e) {
            // 直接返回模拟数据
            return "{\"status\":\"ok\",\"data\":{\"aqi\":58,\"pm25\":26,\"city\":\"" + city + "\"}}";
        }
    }

    public String getWeatherData(String city) {
        try {
            // 模拟天气数据获取
            Thread.sleep(500); // 模拟网络延迟

            // 返回模拟天气数据
            return String.format("{\"city\":\"%s\",\"temperature\":%d,\"humidity\":%d,\"weather\":\"%s\",\"updateTime\":\"%s\"}",
                    city,
                    15 + (int)(Math.random() * 20), // 15-35°C
                    40 + (int)(Math.random() * 50), // 40-90%
                    getRandomWeather(),
                    java.time.LocalDateTime.now().toString());

        } catch (Exception e) {
            return "{\"city\":\"" + city + "\",\"temperature\":25,\"humidity\":65,\"weather\":\"晴\",\"updateTime\":\"2025-11-29T17:30:00\"}";
        }
    }

    private String getRandomWeather() {
        String[] weathers = {"晴", "多云", "阴", "小雨", "阵雨", "雾"};
        return weathers[(int)(Math.random() * weathers.length)];
    }

    public String getTrafficData(String location) {
        // 模拟交通数据
        return String.format("{\"location\":\"%s\",\"congestionLevel\":\"%s\",\"updateTime\":\"%s\"}",
                location,
                getRandomCongestionLevel(),
                java.time.LocalDateTime.now().toString());
    }

    private String getRandomCongestionLevel() {
        String[] levels = {"畅通", "轻度拥堵", "中度拥堵", "严重拥堵"};
        return levels[(int)(Math.random() * levels.length)];
    }
}