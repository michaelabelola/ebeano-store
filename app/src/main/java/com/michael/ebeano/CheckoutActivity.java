package com.michael.ebeano;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.michael.ebeano.models.CartLine;
import com.michael.ebeano.models.OrderDoc;
import com.michael.ebeano.models.OrderItem;
import com.michael.ebeano.services.OrderService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        address = findViewById(R.id.address);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        payment = findViewById(R.id.payment);
        cardNumber = findViewById(R.id.cardNumber);
        expiry = findViewById(R.id.expiry);
        cvv = findViewById(R.id.cvv);
        submit = findViewById(R.id.btnSubmit);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, new String[]{"Credit Card","Debit Card","Portal"});
        payment.setAdapter(adapter);
        submit.setOnClickListener(v -> submit());
    }

    void submit() {
        String fn = firstName.getText().toString().trim();
        String ln = lastName.getText().toString().trim();
        String ad = address.getText().toString().trim();
        String em = email.getText().toString().trim();
        String ph = phone.getText().toString().replaceAll("[^0-9]"," ").trim().replace(" ","");
        String pm = (String) payment.getSelectedItem();
        String cn = cardNumber.getText().toString().replaceAll("[^0-9]","");
        String ex = expiry.getText().toString().trim();
        String cv = cvv.getText().toString().trim();
        if (TextUtils.isEmpty(fn)) { firstName.setError("Required"); return; }
        if (TextUtils.isEmpty(ln)) { lastName.setError("Required"); return; }
        if (TextUtils.isEmpty(ad)) { address.setError("Required"); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(em).matches()) { email.setError("Invalid"); return; }
        if (ph.length() < 10) { phone.setError("Invalid"); return; }
        if (TextUtils.isEmpty(pm)) { Toast.makeText(this, "Select payment", Toast.LENGTH_SHORT).show(); return; }
        if (!luhn(cn)) { cardNumber.setError("Invalid card"); return; }
        if (!validExpiry(ex)) { expiry.setError("Invalid"); return; }
        if (!cv.matches("^\\d{3,4}$")) { cvv.setError("Invalid"); return; }
        List<CartLine> lines = CartManager.get().all();
        if (lines.isEmpty()) { Toast.makeText(this, "Cart is empty", Toast.LENGTH_SHORT).show(); return; }
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
        order.createdAt = Timestamp.now();
        new OrderService().create(order, id -> {
            CartManager.get().clear();
            startActivity(new android.content.Intent(this, ThankYouActivity.class));
            finish();
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