package com.michael.ebeano;

import com.michael.ebeano.models.CartLine;
import com.michael.ebeano.models.ProductItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CartManager {
    static CartManager manager;
    Map<String, CartLine> lines = new LinkedHashMap<>();
    public static CartManager get() {
        if (manager == null) manager = new CartManager();
        return manager;
    }
    public void clear() { lines.clear(); }
    public void add(ProductItem item, int qty) {
        CartLine l = lines.get(item.id);
        if (l == null) lines.put(item.id, new CartLine(item, qty)); else l.qty += qty;
    }
    public void update(String id, int qty) {
        CartLine l = lines.get(id);
        if (l != null) {
            if (qty <= 0) lines.remove(id); else l.qty = qty;
        }
    }
    public void remove(String id) { lines.remove(id); }
    public List<CartLine> all() { return new ArrayList<>(lines.values()); }
    public double subtotal() {
        double s = 0;
        for (CartLine l : lines.values()) s += l.item.price * l.qty;
        return s;
    }
    public double tax() { return subtotal() * 0.13; }
    public double total() { return subtotal() + tax(); }
}