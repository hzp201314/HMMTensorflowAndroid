package com.nisco.family.common.model;

/**
 * Created by tianzy on 2018/8/23.
 */

public class SelectItem {

    private String name;
    private String no;
    private String type;
    private int position;

    public SelectItem() {
    }

    public SelectItem(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public SelectItem(String name, int position) {
        this.name = name;
        this.position = position;
    }

    public SelectItem(String name, String type, int position) {
        this.name = name;
        this.type = type;
        this.position = position;
    }
    public SelectItem(String name, int position, String type) {
        this.name = name;
        this.position = position;
        this.type = type;
    }

    public SelectItem(String name, String no, String type, int position) {
        this.name = name;
        this.no = no;
        this.type = type;
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
