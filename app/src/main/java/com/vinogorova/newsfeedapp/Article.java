package com.vinogorova.newsfeedapp;

public class Article {

    private String title;
    private String date;
    private String section;
    private String author;
    private String url;

    //Constructor for creating Article objects
    public Article(String title, String date, String section, String author, String url){
        this.title = title;
        this.date = date;
        this.section = section;
        this.author = author;
        this.url = url;
    }

    /**
     * This method returns String title value
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * This method returns String date value
     * @return date
     */
    public String getDate() {
        return date;
    }

    /**
     * This method returns String section value
     * @return section
     */
    public String getSection() {
        return section;
    }

    /**
     * This method returns String author value
     * @return author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * This method returns String url value
     * @return url
     */
    public String getUrl() {
        return url;
    }
}
