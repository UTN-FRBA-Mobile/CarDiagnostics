package com.cardiag.models.solutions;

/**
 * Created by lrocca on 13/06/2017.
 */

public class Step {
    private String imgId;
    private int position;
    private String desc;
    private long id;

    public Step(String imgId, String description) {
        this.setImgId(imgId);
        this.setDesc(description);
        this.setPosition(-1);
    }
    public void setImgId(String imgId) {
        this.imgId = imgId;
    }

    public String getImgId() {
        return imgId;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

}
