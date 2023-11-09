package fpt.edu.nhom4.Entity;

import java.util.ArrayList;
import java.util.List;

public class TransItem {
    private static final TransItem ourInstance = new TransItem();
    private List<BudgetEntity> cartItems = new ArrayList<>();

    public static TransItem getInstance() {
        return ourInstance;
    }

    private TransItem() {
    }

    public List<BudgetEntity> getCartItems() {
        return cartItems;
    }

    public void addItem(BudgetEntity item) {
        cartItems.add(item);
    }

    public void clearItems() {
        cartItems.clear();
    }


}
