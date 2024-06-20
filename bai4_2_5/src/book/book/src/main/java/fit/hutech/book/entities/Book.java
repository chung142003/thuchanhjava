package book.book.src.main.java.fit.hutech.book.entities;

import fit.hutech.book.validators.annotations.ValidCategoryId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jdk.jfr.Category;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.ArrayList;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title", length = 50, nullable = false)
    @Size(min = 1, max = 50, message = "Title must be between 1 and 50 characters")
    @NotBlank(message = "Title must not be blank")
    private String title;
    @Column(name = "author", length = 50, nullable = false)
    @Size(min = 1,max = 50, message = "Author must be between 1 and 50 characters") @NotBlank(message = "Author must not be blank")
    private String author;
    @Column(name = "price")
    @Positive(message = "Price must be greater than 0")
    private Double price;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    @ToString.Exclude
    @ValidCategoryId

    private Category category;
    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<ItemInvoice> itemInvoices = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Book book = (Book) o;
        return getId() != null && Objects.equals(getId(), book.getId());
    }
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
