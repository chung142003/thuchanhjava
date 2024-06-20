package book.book.src.main.java.fit.hutech.book.controllers;

import book.book.src.main.java.fit.hutech.book.entities.Book;
import book.book.src.main.java.fit.hutech.book.entities.Item;
import book.book.src.main.java.fit.hutech.book.services.BookService;
import book.book.src.main.java.fit.hutech.book.services.CartService;
import book.book.src.main.java.fit.hutech.book.services.CategoryService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.util.Objects;


@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    private final CategoryService categoryService;

    private final CartService cartService;

    @GetMapping
    public String showAllBooks(
            @NotNull Model model,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        model.addAttribute("books", bookService.getAllBooks(pageNo, pageSize, sortBy));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("totalPages", bookService.getAllBooks(pageNo, pageSize, sortBy).size() / pageSize);

        return "book/list";
        }
    @GetMapping("/add")
    public String addBookForm (@NotNull Model model) {
        model.addAttribute("book", new Book());
        model.addAttribute("categories",
                categoryService.getAllCategories());
        return "book/add";
    }

        @PostMapping("/add")
        public String addBook (
                @Valid @ModelAttribute("book") Book book,
                @NotNull BindingResult bindingResult,
                Model model){
            if (bindingResult.hasErrors()) {
                var errors = bindingResult.getAllErrors()
                        .stream()
                        .map(DefaultMessageSourceResolvable::getDefaultMessage)
                        .toArray(String[]::new);
                model.addAttribute("errors", errors);
                model.addAttribute("categories",
                        categoryService.getAllCategories());
                return "book/add";
            }
            bookService.addBook(book);
            return "redirect:/books";
        }

    @PostMapping("/add-to-cart")
    public String addToCart(HttpSession session,
                            @RequestParam long id,
                            @RequestParam String name,
                            @RequestParam double price,
                            @RequestParam(defaultValue = "1") int quantity)
    {
        var cart = cartService.getCart(session);
        cart.addItems(new Item(id, name, price, quantity));
        cartService.updateCart(session, cart);
        return "redirect:/books";
    }
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable long id) {
        bookService.getBookById(id)
                .ifPresentOrElse(
                        book -> bookService.deleteBookById(id),
                        () -> { throw new IllegalArgumentException("Book not found"); });
                            return "redirect:/books";
    }
    @GetMapping("/edit/{id}")
    public String editBookForm(@NotNull Model model, @PathVariable long id)
    {
        var book = bookService.getBookById(id);
        model.addAttribute("book", book.orElseThrow(() -> new
                IllegalArgumentException("Book not found"));
        model.addAttribute("categories",
                categoryService.getAllCategories());
        return "book/edit";
    }
    @PostMapping("/edit")
    public String editBook(@Valid @ModelAttribute("book") Book book,
                           @NotNull BindingResult bindingResult,
                           Model model) {
        if (bindingResult.hasErrors()) {
            var errors = bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toArray(String[]::new);
            model.addAttribute("errors", errors);
            model.addAttribute("categories",
                    categoryService.getAllCategories());
            return "book/edit";
        }
        bookService.updateBook(book);
        return "redirect:/books";
    }
    public void updateBook(@NotNull Book book) {
        Book existingBook = bookRepository.findById(book.getId()).orElse(null);
        Objects.requireNonNull(existingBook).setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setPrice(book.getPrice());
        existingBook.setCategory(book.getCategory());
        bookRepository.save(existingBook);
    }
    @GetMapping("/search")
    public String searchBook(
            @NotNull Model model,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") Integer pageNo,
            @RequestParam(defaultValue = "20") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy) {
        model.addAttribute("books", bookService.searchBook(keyword));
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages",
                bookService
                        .getAllBooks(pageNo, pageSize, sortBy)
                        .size() / pageSize);
        model.addAttribute("categories",
                categoryService.getAllCategories());
        return "book/list";
    }
}
