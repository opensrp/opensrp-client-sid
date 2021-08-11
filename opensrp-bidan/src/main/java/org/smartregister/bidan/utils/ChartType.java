package org.smartregister.bidan.utils;

import java.util.ArrayList;
import java.util.List;

public enum ChartType {
    wfa_boys_0_to_5("Berat badan putra berdasarkan usia (WHO)"),
    wfa_girls_0_to_5("Berat badan putri berdasarkan usia (WHO)"),
//    wfa_boys_2_to_20("Weight vs Age"),
//    wfa_girls_2_to_20("Weight vs Age"),
//    hcfa_boys_0_to_5("Head Circumference vs Age"),
//    hcfa_girls_0_to_5("Head Circumference vs Age"),
    lfa_girls_0_to_5("Tinggi putri berdasarkan usia (WHO)"),
    lfa_boys_0_to_5("Tinggi putra berdasarkan usia (WHO)");
    private final String title;

    ChartType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static List<ChartType> femaleCharts() {
        List<ChartType> results = new ArrayList<>();
        for (ChartType value : ChartType.values()) {
            if (value.name().contains("girls"))
                results.add(value);
        }
        return results;
    }

    public static List<ChartType> maleCharts() {
        List<ChartType> results = new ArrayList<>();
        for (ChartType value : ChartType.values()) {
            if (value.name().contains("boys"))
                results.add(value);
        }
        return results;
    }

    @Override
    public String toString() {
        return title;
    }
}
