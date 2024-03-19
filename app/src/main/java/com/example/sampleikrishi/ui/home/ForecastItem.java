package com.example.sampleikrishi.ui.home;

public class ForecastItem {
    private String formattedDate;
    private String maxTemp;
    private String minTemp;
    private String condition;
    private String iconUrl;

    public ForecastItem(String formattedDate, String maxTemp, String minTemp, String condition, String iconUrl) {
        this.formattedDate = formattedDate;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.condition = condition;
        this.iconUrl = iconUrl;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public String getMinTemp() {
        return minTemp;
    }

    public String getCondition() {
        return condition;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getDate() {
        return formattedDate;
    }
}
