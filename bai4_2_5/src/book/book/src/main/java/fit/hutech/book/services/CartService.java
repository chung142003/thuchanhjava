package book.book.src.main.java.fit.hutech.book.services;

import book.book.src.main.java.fit.hutech.book.entities.Cart;
import book.book.src.main.java.fit.hutech.book.entities.Item;
import fit.hutech.book.daos.Cart;
import fit.hutech.book.daos.Item;
import fit.hutech.book.entities.Invoice;
import fit.hutech.book.entities.ItemInvoice;
import fit.hutech.book.repositories.IBookRepository;
import fit.hutech.book.repositories.IInvoiceRepository;
import fit.hutech.book.repositories.IItemInvoiceRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE, rollbackFor = {Exception.class, Throwable.class})

public class CartService {

    private static final String CART_SESSION_KEY = "cart";
    private final IInvoiceRepository invoiceRepository;
    private final IItemInvoiceRepository itemInvoiceRepository;
    private final IBookRepository bookRepository;

    public Cart getCart(@NotNull HttpSession session) {
        return Optional.ofNullable((Cart) session.getAttribute(CART_SESSION_KEY)).orElseGet(() -> {
                    Cart cart = new Cart();
                    session.setAttribute(CART_SESSION_KEY, cart);
                    return cart;
                });
    }
    public void updateCart(@NotNull HttpSession session, Cart cart) {
        session.setAttribute(CART_SESSION_KEY, cart);
    }
    public void removeCart(@NotNull HttpSession session) {
        session.removeAttribute(CART_SESSION_KEY);
    }
    public int getSumQuantity(@NotNull HttpSession session) {
        return getCart(session).getCartItems().stream()
                .mapToInt(Item::getQuantity)
                .sum();
    }
    public double getSumPrice(@NotNull HttpSession session) {
        return getCart(session).getCartItems().stream()
                .mapToDouble(item -> item.getPrice() *
                        item.getQuantity())
                .sum();
    }
    public void saveCart(@NotNull HttpSession session) {
        var cart = getCart(session);
        if (cart.getCartItems().isEmpty()) return;
        var invoice = new Invoice();
        invoice.setInvoiceDate(new Date(new Date().getTime()));
        invoice.setPrice(getSumPrice(session));
        invoiceRepository.save(invoice);
        cart.getCartItems().forEach(item -> {
            var items = new ItemInvoice();
            items.setInvoice(invoice);
            items.setQuantity(item.getQuantity());
            items.setBook(bookRepository.findById(item.getBookId())
                    .orElseThrow());
            itemInvoiceRepository.save(items);
        });
        removeCart(session);
    }
}
