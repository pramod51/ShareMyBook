package com.share.bookR.BookAdapter;

public class BookModel {
    String title,bookUrl,description,price,rating,authorName,isbn;

    public BookModel(String title, String bookUrl, String description, String price, String rating, String authorName, String isbn) {
        this.title = title;
        this.bookUrl = bookUrl;
        this.description = description;
        this.price = price;
        this.rating = rating;
        this.authorName = authorName;
        this.isbn = isbn;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getPrice() {
        return price;
    }

    public String getRating() {
        return rating;
    }


    public String getTitle() {
        return title;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public String getDescription() {
        return description;
    }

}
