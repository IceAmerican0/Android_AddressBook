package com.example.adressbook.Bean;

public class AddressInfo {
    private String name;
    private String number;
    private String comment;
    private String image;
    private String category;
    private String code;

    public AddressInfo(String name, String number, String comment, String image,String category,String code) {
        this.name = name;
        this.number = number;
        this.comment = comment;
        this.image = image;
        this.category=category;
        this.code=code;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
