package com.gv.jhipsterapp001.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Product.
 */
@Table("product")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("title")
    private String title;

    @Column("keywords")
    private String keywords;

    @Column("description")
    private String description;

    @Column("rating")
    private Integer rating;

    @Column("date_added")
    private LocalDate dateAdded;

    @Column("date_modified")
    private LocalDate dateModified;

    @Transient
    @JsonIgnoreProperties(value = { "products", "customer" }, allowSetters = true)
    private WishList wishList;

    @Transient
    @JsonIgnoreProperties(value = { "parent", "products" }, allowSetters = true)
    private Set<Category> categories = new HashSet<>();

    @Column("wish_list_id")
    private Long wishListId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Product id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Product title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public Product keywords(String keywords) {
        this.setKeywords(keywords);
        return this;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return this.description;
    }

    public Product description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRating() {
        return this.rating;
    }

    public Product rating(Integer rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public LocalDate getDateAdded() {
        return this.dateAdded;
    }

    public Product dateAdded(LocalDate dateAdded) {
        this.setDateAdded(dateAdded);
        return this;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public LocalDate getDateModified() {
        return this.dateModified;
    }

    public Product dateModified(LocalDate dateModified) {
        this.setDateModified(dateModified);
        return this;
    }

    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
    }

    public WishList getWishList() {
        return this.wishList;
    }

    public void setWishList(WishList wishList) {
        this.wishList = wishList;
        this.wishListId = wishList != null ? wishList.getId() : null;
    }

    public Product wishList(WishList wishList) {
        this.setWishList(wishList);
        return this;
    }

    public Set<Category> getCategories() {
        return this.categories;
    }

    public void setCategories(Set<Category> categories) {
        if (this.categories != null) {
            this.categories.forEach(i -> i.removeProduct(this));
        }
        if (categories != null) {
            categories.forEach(i -> i.addProduct(this));
        }
        this.categories = categories;
    }

    public Product categories(Set<Category> categories) {
        this.setCategories(categories);
        return this;
    }

    public Product addCategory(Category category) {
        this.categories.add(category);
        category.getProducts().add(this);
        return this;
    }

    public Product removeCategory(Category category) {
        this.categories.remove(category);
        category.getProducts().remove(this);
        return this;
    }

    public Long getWishListId() {
        return this.wishListId;
    }

    public void setWishListId(Long wishList) {
        this.wishListId = wishList;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", keywords='" + getKeywords() + "'" +
            ", description='" + getDescription() + "'" +
            ", rating=" + getRating() +
            ", dateAdded='" + getDateAdded() + "'" +
            ", dateModified='" + getDateModified() + "'" +
            "}";
    }
}
