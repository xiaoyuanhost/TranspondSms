package com.tim.tsms.transpondsms.model.vo;

public class LogVo {
    private String from;
    private String content;
    private String rule;
    private int senderImageId;
    private String time;
    private String jsonExtra;

    public LogVo(String from, String content, String time, String rule,int senderImageId,String jsonExtra) {
        this.from = from;
        this.content = content;
        this.time = time;
        this.rule = rule;
        this.senderImageId = senderImageId;
        this.jsonExtra = jsonExtra;
    }

    public LogVo() {

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

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public String getTime() {
        return time;
    }

    public int getSenderImageId() {
        return senderImageId;
    }

    public String getJsonExtra() {
        return jsonExtra;
    }

    public void setJsonExtra(String jsonExtra) {
        this.jsonExtra = jsonExtra;
    }

    public void setSenderImageId(int senderImageId) {
        this.senderImageId = senderImageId;
    }

    @Override
    public String toString() {
        return "LogVo{" +
                "from='" + from + '\'' +
                ", content='" + content + '\'' +
                ", rule='" + rule + '\'' +
                ", senderImageId=" + senderImageId +
                ", time='" + time + '\'' +
                ", jsonExtra='" + jsonExtra + '\'' +
                '}';
    }
}
