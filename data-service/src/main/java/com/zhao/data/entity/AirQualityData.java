package com.zhao.data.entity;

public class AirQualityData {
    private String city;
    private int aqi;
    private int pm25;
    private String qualityLevel;
    private String updateTime;

    public AirQualityData() {}

    public AirQualityData(String city, int aqi, int pm25, String qualityLevel) {
        this.city = city;
        this.aqi = aqi;
        this.pm25 = pm25;
        this.qualityLevel = qualityLevel;
        this.updateTime = java.time.LocalDateTime.now().toString();
    }

    // Getters and Setters
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public int getAqi() { return aqi; }
    public void setAqi(int aqi) { this.aqi = aqi; }

    public int getPm25() { return pm25; }
    public void setPm25(int pm25) { this.pm25 = pm25; }

    public String getQualityLevel() { return qualityLevel; }
    public void setQualityLevel(String qualityLevel) { this.qualityLevel = qualityLevel; }

    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }

    @Override
    public String toString() {
        return String.format("城市: %s, AQI: %d, PM2.5: %d, 质量等级: %s",
                city, aqi, pm25, qualityLevel);
    }
}