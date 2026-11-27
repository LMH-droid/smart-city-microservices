package com.zhao.data.controller;

import com.zhao.data.entity.AirQualityData;
import com.zhao.data.service.DataFetcher;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/api/data")
public class DataController {

    private final Random random = new Random();
    private final DataFetcher dataFetcher;

    public DataController(DataFetcher dataFetcher) {
        this.dataFetcher = dataFetcher;
    }

    @GetMapping("/air-quality/{city}")
    public Map<String, Object> getAirQuality(@PathVariable String city) {
        try {
            // 使用DataFetcher获取真实数据
            String airQualityData = dataFetcher.getAirQuality(city);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("city", city);
            response.put("data", airQualityData);
            response.put("source", "external-api");
            return response;

        } catch (Exception e) {
            // 备用方案：返回模拟数据
            return getSimulatedAirQuality(city);
        }
    }

    @GetMapping("/weather/{city}")
    public Map<String, Object> getWeather(@PathVariable String city) {
        try {
            // 使用DataFetcher获取天气数据
            String weatherData = dataFetcher.getWeatherData(city);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("city", city);
            response.put("data", weatherData);
            response.put("source", "simulated");
            return response;

        } catch (Exception e) {
            // 备用方案
            return getSimulatedWeather(city);
        }
    }

    @GetMapping("/traffic/{location}")
    public Map<String, Object> getTraffic(@PathVariable String location) {
        try {
            String trafficData = dataFetcher.getTrafficData(location);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("location", location);
            response.put("data", trafficData);
            response.put("source", "simulated");
            return response;

        } catch (Exception e) {
            return getSimulatedTraffic(location);
        }
    }

    @GetMapping("/environment/{city}")
    public Map<String, Object> getEnvironmentData(@PathVariable String city) {
        Map<String, Object> envData = new HashMap<>();
        envData.put("city", city);
        envData.put("noiseLevel", 45 + random.nextInt(30)); // 45-75分贝
        envData.put("waterQuality", getRandomWaterQuality());
        envData.put("greenCoverage", 30 + random.nextInt(40)); // 30-70%
        envData.put("updateTime", java.time.LocalDateTime.now().toString());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", envData);
        return response;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "数据服务运行正常 - " + System.currentTimeMillis();
    }

    // 模拟数据备用方法
    private Map<String, Object> getSimulatedAirQuality(String city) {
        int aqi = 30 + random.nextInt(150);
        int pm25 = 10 + random.nextInt(100);

        Map<String, Object> airData = new HashMap<>();
        airData.put("city", city);
        airData.put("aqi", aqi);
        airData.put("pm25", pm25);
        airData.put("qualityLevel", calculateQualityLevel(aqi));
        airData.put("updateTime", java.time.LocalDateTime.now().toString());
        airData.put("source", "simulated");

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", airData);
        return response;
    }

    private Map<String, Object> getSimulatedWeather(String city) {
        Map<String, Object> weather = new HashMap<>();
        weather.put("city", city);
        weather.put("temperature", 15 + random.nextInt(20));
        weather.put("humidity", 40 + random.nextInt(50));
        weather.put("weather", getRandomWeather());
        weather.put("windSpeed", 1 + random.nextInt(6));
        weather.put("updateTime", java.time.LocalDateTime.now().toString());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", weather);
        return response;
    }

    private Map<String, Object> getSimulatedTraffic(String location) {
        Map<String, Object> traffic = new HashMap<>();
        traffic.put("location", location);
        traffic.put("congestionLevel", getRandomCongestionLevel());
        traffic.put("averageSpeed", 20 + random.nextInt(40));
        traffic.put("updateTime", java.time.LocalDateTime.now().toString());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", traffic);
        return response;
    }

    private String calculateQualityLevel(int aqi) {
        if (aqi <= 50) return "优";
        else if (aqi <= 100) return "良";
        else if (aqi <= 150) return "轻度污染";
        else if (aqi <= 200) return "中度污染";
        else return "重度污染";
    }

    private String getRandomWeather() {
        String[] weathers = {"晴", "多云", "阴", "小雨", "阵雨", "雾"};
        return weathers[random.nextInt(weathers.length)];
    }

    private String getRandomCongestionLevel() {
        String[] levels = {"畅通", "轻度拥堵", "中度拥堵", "严重拥堵"};
        return levels[random.nextInt(levels.length)];
    }

    private String getRandomWaterQuality() {
        String[] qualities = {"Ⅰ类", "Ⅱ类", "Ⅲ类", "Ⅳ类", "Ⅴ类"};
        return qualities[random.nextInt(qualities.length)];
    }
}