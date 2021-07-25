package org.smartregister.bidan.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChartData {
    private Meta meta;
    private List<Map<String, Object>> data = new ArrayList<>();

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public static class Meta {
        private String title;
        private List<Line> lines = new ArrayList<>();

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public List<Line> getLines() {
            return lines;
        }

        public void setLines(List<Line> lines) {
            this.lines = lines;
        }
    }

    public static class Line {
        private String tag, name;

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
