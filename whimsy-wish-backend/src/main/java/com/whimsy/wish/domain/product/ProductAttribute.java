package com.whimsy.wish.domain.product;

import com.whimsy.wish.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductAttribute extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Override equals and hashCode to ensure proper Set operation
    // This is crucial for preventing Hibernate collection issues
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductAttribute)) return false;
        ProductAttribute that = (ProductAttribute) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        // Use a constant value for new entities (with null id)
        return getId() == null ? 31 : getId().hashCode();
    }

    // Avoid including the product in toString to prevent infinite recursion
    @Override
    public String toString() {
        return "ProductAttribute{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}