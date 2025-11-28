package com.example.memonote;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memonote.data.Memo;
import com.example.memonote.data.MemoDatabase;
import com.example.memonote.data.MemoRepository;

import java.util.Date;

public class EditMemoActivity extends AppCompatActivity {

    public static final String EXTRA_MEMO_ID = "extra_memo_id";
    private MemoRepository repository;
    private EditText titleInput;
    private EditText contentInput;
    private Spinner categorySpinner;
    private ImageView imagePreview;
    private String imagePath = "";
    private long memoId = -1L;

    private final ActivityResultLauncher<String> pickImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            this::onImageSelected
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_memo);

        repository = new MemoRepository(MemoDatabase.getInstance(this).memoDao());
        titleInput = findViewById(R.id.input_title);
        contentInput = findViewById(R.id.input_content);
        categorySpinner = findViewById(R.id.input_category);
        imagePreview = findViewById(R.id.image_preview);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        Button saveButton = findViewById(R.id.save_button);
        Button pickImageButton = findViewById(R.id.pick_image_button);
        pickImageButton.setOnClickListener(v -> pickImageLauncher.launch("image/*"));
        saveButton.setOnClickListener(v -> saveMemo());

        memoId = getIntent().getLongExtra(EXTRA_MEMO_ID, -1L);
        if (memoId != -1L) {
            loadMemo(memoId);
        }
    }

    private void loadMemo(long id) {
        repository.getById(id, memo -> {
            if (memo != null) {
                titleInput.setText(memo.title);
                contentInput.setText(memo.content);
                setSpinnerSelection(memo.category);
                if (!TextUtils.isEmpty(memo.imagePath)) {
                    imagePath = memo.imagePath;
                    imagePreview.setImageURI(Uri.parse(imagePath));
                }
            }
        });
    }

    private void setSpinnerSelection(String category) {
        ArrayAdapter adapter = (ArrayAdapter) categorySpinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).toString().equalsIgnoreCase(category)) {
                categorySpinner.setSelection(i);
                break;
            }
        }
    }

    private void saveMemo() {
        String title = titleInput.getText().toString();
        String content = contentInput.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, R.string.fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        long now = new Date().getTime();
        if (memoId == -1L) {
            Memo memo = new Memo(title, content, category, now, now, imagePath);
            repository.insert(memo, id -> finish());
        } else {
            repository.getById(memoId, existing -> {
                if (existing != null) {
                    existing.title = title;
                    existing.content = content;
                    existing.category = category;
                    existing.imagePath = imagePath;
                    existing.updatedAt = now;
                    repository.update(existing, this::finish);
                }
            });
        }
    }

    private void onImageSelected(Uri uri) {
        if (uri != null) {
            imagePath = uri.toString();
            imagePreview.setImageURI(uri);
        }
    }
}
