package com.tim.tsms.transpondsms.model;

public class LogModel {
    private String from;
    private String content;
    private Long ruleId;
    private Long time;
    private String jsonExtra;

    public LogModel(String from, String content,Long ruleId,String jsonExtra) {
        this.from = from;
        this.content = content;
        this.ruleId = ruleId;
        this.jsonExtra = jsonExtra;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public Long getTime() {
        return time;
    }

    public String getJsonExtra() {
        return jsonExtra;
    }

    public void setJsonExtra(String jsonExtra) {
        this.jsonExtra = jsonExtra;
    }

    @Override
    public String toString() {
        return "LogModel{" +
                "from='" + from + '\'' +
                ", content='" + content + '\'' +
                ", ruleId=" + ruleId +
                ", time=" + time +
                ", jsonExtra=" + jsonExtra +
                '}';
    }
}
