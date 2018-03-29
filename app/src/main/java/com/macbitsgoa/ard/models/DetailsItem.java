package com.macbitsgoa.ard.models;


public class DetailsItem {

    private String title;
    private String tag;
    private String desc;
    private String email;
    private String photoUrl;
    private String name;

    public DetailsItem(){
    }

    public DetailsItem(String title,String tag){
        this.title=title;
        this.tag=tag;
    }

    public DetailsItem(String desc,String email,String photoUrl,String name){
        this.desc=desc;
        this.photoUrl=photoUrl;
        this.email=email;
        this.name=name;
    }

    public String getTitle(){
        return title;
    }

    public String getPhotoUrl(){
        return photoUrl;
    }

    public String getName(){
        return name;
    }

    public String getDesc(){
        return desc;
    }

    public String getEmail(){
        return email;
    }

    public String getTag(){
        return tag;
    }

    public void setTitle(String title){
        this.title=title;
    }

    public void setPhotoUrl(String photoUrl){
        this.photoUrl=photoUrl;
    }

    public void setDesc(String desc){
        this.desc=desc;
    }

    public void setEmail(String email){
        this.email=email;
    }

    public void setTag(String tag){
        this.tag=tag;
    }

    public void setName(String name){
        this.name=name;
    }

}
