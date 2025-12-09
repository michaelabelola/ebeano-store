package com.michael.ebeano;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.android.material.textfield.TextInputEditText;
import com.michael.ebeano.models.ProductItem;
import com.michael.ebeano.services.ProductService;

import java.util.UUID;

public class AddProductFragment extends Fragment {

    MaterialToolbar toolbar;
    TextInputEditText name;
    TextInputEditText shortDesc;
    TextInputEditText longDesc;
    TextInputEditText price;
    ImageView imagePreview;
    Button pickImage;
    Button save;
    ProgressBar progressSave;

    Uri selectedImage;
    ActivityResultLauncher<Intent> imagePicker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        toolbar = view.findViewById(R.id.toolbarAddProduct);
        if (toolbar != null) {
            toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());
        }
        imagePreview = view.findViewById(R.id.imagePreview);
        pickImage = view.findViewById(R.id.btnPickImage);
        name = view.findViewById(R.id.inputName);
        shortDesc = view.findViewById(R.id.inputShortDesc);
        longDesc = view.findViewById(R.id.inputLongDesc);
        price = view.findViewById(R.id.inputPrice);
        progressSave = view.findViewById(R.id.progressSave);
        save = view.findViewById(R.id.btnSave);

        imagePicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                if (uri != null) {
                    selectedImage = uri;
                    if (imagePreview != null) imagePreview.setImageURI(uri);
                }
            }
        });

        pickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            imagePicker.launch(Intent.createChooser(intent, getString(R.string.pick_image)));
        });
        save.setOnClickListener(v -> doSave());
    }

    void doSave() {
        String n = textOf(name);
        String sd = textOf(shortDesc);
        String ld = textOf(longDesc);
        String pr = textOf(price);
        if (TextUtils.isEmpty(n)) { name.setError(getString(R.string.required)); return; }
        if (TextUtils.isEmpty(pr)) { price.setError(getString(R.string.required)); return; }
        if (selectedImage == null) { Toast.makeText(requireContext(), R.string.image_required, Toast.LENGTH_SHORT).show(); return; }
        double p;
        try { p = Double.parseDouble(pr); } catch (Exception e) { price.setError("Invalid"); return; }

        setLoading(true);
        // Upload image first
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "guest";
        String filename = UUID.randomUUID().toString() + ".jpg";
        String path = "products/" + uid + "/" + filename;
        StorageReference ref = FirebaseStorage.getInstance().getReference().child(path);
        ref.putFile(selectedImage)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveProduct(n, sd, ld, p, uri != null ? uri.toString() : "");
                }).addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(requireContext(), getString(R.string.uploading_image) + " failed", Toast.LENGTH_SHORT).show();
                }))
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(requireContext(), getString(R.string.uploading_image) + " failed", Toast.LENGTH_SHORT).show();
                });
    }

    void saveProduct(String n, String sd, String ld, double p, String imageUrl) {
        ProductItem item = new ProductItem();
        item.name = n;
        item.shortDescription = sd;
        item.longDescription = ld;
        item.price = p;
        item.imageUrl = imageUrl;
        new ProductService().add(item, created -> {
            setLoading(false);
            Toast.makeText(requireContext(), R.string.product_added, Toast.LENGTH_SHORT).show();
            if (getParentFragmentManager() != null) getParentFragmentManager().popBackStack();
        });
    }

    void setLoading(boolean loading) {
        if (progressSave != null) progressSave.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (save != null) save.setEnabled(!loading);
        if (pickImage != null) pickImage.setEnabled(!loading);
        if (name != null) name.setEnabled(!loading);
        if (shortDesc != null) shortDesc.setEnabled(!loading);
        if (longDesc != null) longDesc.setEnabled(!loading);
        if (price != null) price.setEnabled(!loading);
    }

    static String textOf(TextInputEditText e) { return e == null ? "" : e.getText() == null ? "" : e.getText().toString().trim(); }
}
