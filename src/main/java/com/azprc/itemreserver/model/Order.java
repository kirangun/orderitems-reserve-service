package com.azprc.itemreserver.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Order
 */
@SuppressWarnings("serial")
public class Order implements Serializable {
    @JsonProperty("id")
    private String id = null;

    @JsonProperty("products")
    private List<Product> products = null;

    @JsonProperty("complete")
    private Boolean complete = false;

    public Order id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Get id
     *
     * @return id
     **/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Order products(List<Product> products) {
        this.products = products;
        return this;
    }

    public Order addProductsItem(Product productsItem) {
        if (this.products == null) {
            this.products = new ArrayList<Product>();
        }
        this.products.add(productsItem);
        return this;
    }

    /**
     * Get products
     *
     * @return products
     **/
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public Order complete(Boolean complete) {
        this.complete = complete;
        return this;
    }

    /**
     * Get complete
     *
     * @return complete
     **/
    public Boolean isComplete() {
        return complete;
    }

    public void setComplete(Boolean complete) {
        this.complete = complete;
    }

    @Override
    public boolean equals(java.lang.Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(this.id, order.id) && Objects.equals(this.products, order.products)
                && Objects.equals(this.complete, order.complete);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, products, complete);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Order {\n");

        sb.append("    id: ").append(toIndentedString(id)).append("\n");
        sb.append("    products: ").append(toIndentedString(products)).append("\n");
        sb.append("    complete: ").append(toIndentedString(complete)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(java.lang.Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
