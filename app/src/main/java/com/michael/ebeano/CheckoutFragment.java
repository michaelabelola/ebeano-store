package com.michael.ebeano;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.michael.ebeano.models.CartLine;
import com.michael.ebeano.models.OrderDoc;
import com.michael.ebeano.models.OrderItem;
import com.michael.ebeano.services.OrderService;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CheckoutFragment extends Fragment {

    EditText firstName;
    EditText lastName;
    EditText address;
    EditText email;
    EditText phone;
    Spinner payment;
    EditText cardNumber;
    EditText expiry;
    EditText cvv;
    Button submit;
    View cardFieldsContainer;
    ProgressBar progressSubmit;
    TextView summarySubtotal;
    TextView summaryTax;
    TextView summaryTotal;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firstName = view.findViewById(R.id.firstName);
        lastName = view.findViewById(R.id.lastName);
        address = view.findViewById(R.id.address);
        email = view.findViewById(R.id.email);
        phone = view.findViewById(R.id.phone);
        payment = view.findViewById(R.id.payment);
        cardNumber = view.findViewById(R.id.cardNumber);
        expiry = view.findViewById(R.id.expiry);
        cvv = view.findViewById(R.id.cvv);
        submit = view.findViewById(R.id.btnSubmit);
        cardFieldsContainer = view.findViewById(R.id.cardFieldsContainer);
        progressSubmit = view.findViewById(R.id.progressSubmit);
        summarySubtotal = view.findViewById(R.id.summarySubtotal);
        summaryTax = view.findViewById(R.id.summaryTax);
        summaryTotal = view.findViewById(R.id.summaryTotal);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, new String[]{"Credit Card","Debit Card","Portal"});
        payment.setAdapter(adapter);
        payment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                String pm = (String) payment.getSelectedItem();
                boolean card = pm != null && !pm.equalsIgnoreCase("Portal");
                if (cardFieldsContainer != null) {
                    cardFieldsContainer.setVisibility(card ? View.VISIBLE : View.GONE);
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Set order summary values
        NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.getDefault());
        if (summarySubtotal != null) summarySubtotal.setText(currency.format(CartManager.get().subtotal()));
        if (summaryTax != null) summaryTax.setText(currency.format(CartManager.get().tax()));
        if (summaryTotal != null) summaryTotal.setText(currency.format(CartManager.get().total()));
        submit.setOnClickListener(v -> submit());
    }

    void submit() {
        String fn = firstName.getText().toString().trim();
        String ln = lastName.getText().toString().trim();
        String ad = address.getText().toString().trim();
        String em = email.getText().toString().trim();
        String ph = phone.getText().toString().replaceAll("[^0-9]"," ").trim().replace(" ", "");
        String pm = (String) payment.getSelectedItem();
        String cn = cardNumber.getText().toString().replaceAll("[^0-9]","");
        String ex = expiry.getText().toString().trim();
        String cv = cvv.getText().toString().trim();
        if (TextUtils.isEmpty(fn)) { firstName.setError("Required"); return; }
        if (TextUtils.isEmpty(ln)) { lastName.setError("Required"); return; }
        if (TextUtils.isEmpty(ad)) { address.setError("Required"); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(em).matches()) { email.setError("Invalid"); return; }
        if (ph.length() < 10) { phone.setError("Invalid"); return; }
        if (TextUtils.isEmpty(pm)) { Toast.makeText(requireContext(), "Select payment", Toast.LENGTH_SHORT).show(); return; }
        boolean isPortal = pm.equalsIgnoreCase("Portal");
        if (!isPortal) {
            if (!luhn(cn)) { cardNumber.setError("Invalid card"); return; }
            if (!validExpiry(ex)) { expiry.setError("Invalid"); return; }
            if (!cv.matches("^\\d{3,4}$")) { cvv.setError("Invalid"); return; }
        }
        List<CartLine> lines = CartManager.get().all();
        if (lines.isEmpty()) { Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show(); return; }
        double subtotal = CartManager.get().subtotal();
        double tax = CartManager.get().tax();
        double total = CartManager.get().total();
        List<OrderItem> items = new ArrayList<>();
        for (CartLine l : lines) {
            items.add(new OrderItem(l.item.id, l.item.name, l.item.price, l.qty, l.item.imageUrl));
        }
        OrderDoc order = new OrderDoc();
        order.userId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        order.firstName = fn;
        order.lastName = ln;
        order.address = ad;
        order.email = em;
        order.phone = ph;
        order.paymentMethod = pm;
        order.subtotal = subtotal;
        order.tax = tax;
        order.total = total;
        order.items = items;
        order.createdAt = com.google.firebase.Timestamp.now();
        if (progressSubmit != null) progressSubmit.setVisibility(View.VISIBLE);
        if (submit != null) submit.setEnabled(false);
        new OrderService().create(order, id -> {
            if (progressSubmit != null) progressSubmit.setVisibility(View.GONE);
            if (submit != null) submit.setEnabled(true);
            CartManager.get().clear();
            Fragment parent = getParentFragment();
            if (parent instanceof CartFragment) {
                ((CartFragment) parent).openThankYou();
            }
        });
    }

    boolean luhn(String s) {
        if (TextUtils.isEmpty(s) || s.length() < 12) return false;
        int sum = 0; boolean alt = false;
        for (int i = s.length() - 1; i >= 0; i--) {
            int n = s.charAt(i) - '0';
            if (n < 0 || n > 9) return false;
            if (alt) { n *= 2; if (n > 9) n -= 9; }
            sum += n; alt = !alt;
        }
        return sum % 10 == 0;
    }

    boolean validExpiry(String ex) {
        if (TextUtils.isEmpty(ex) || !ex.contains("/")) return false;
        String[] p = ex.split("/");
        if (p.length != 2) return false;
        try {
            int m = Integer.parseInt(p[0]);
            int y = Integer.parseInt(p[1]);
            if (m < 1 || m > 12) return false;
            Calendar c = Calendar.getInstance(Locale.getDefault());
            int cy = c.get(Calendar.YEAR) % 100;
            int cm = c.get(Calendar.MONTH) + 1;
            if (y < cy) return false;
            if (y == cy && m < cm) return false;
            return true;
        } catch (Exception e) { return false; }
    }
}
