package book.book.src.main.java.fit.hutech.book.repositories;

import book.book.src.main.java.fit.hutech.book.entities.Category;
import jdk.jfr.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends    JpaRepository<Category, Long>
{
}
