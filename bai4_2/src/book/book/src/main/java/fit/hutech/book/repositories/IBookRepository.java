package book.book.src.main.java.fit.hutech.book.repositories;

import book.book.src.main.java.fit.hutech.book.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBookRepository extends {
    PagingAndSortingRepository<Book, Long>, JpaRepository<Book, Long> {
        default List<Book> findAllBooks(Integer pageNo, Integer pageSize,String sortBy) {
            return findAll(PageRequest.of(pageNo,pageSize,Sort.by(sortBy))).getContent();
        }
}
