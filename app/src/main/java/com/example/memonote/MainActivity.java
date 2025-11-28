package com.example.memonote;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memonote.data.Memo;
import com.example.memonote.data.MemoDatabase;
import com.example.memonote.data.MemoRepository;
import com.example.memonote.ui.MemoAdapter;
import com.example.memonote.util.PasswordManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements MemoAdapter.MemoClickListener {

    private MemoRepository repository;
    private MemoAdapter adapter;
    private EditText searchInput;
    private Spinner categorySpinner;
    private String currentCategory = "";
    private PasswordManager passwordManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        passwordManager = new PasswordManager(this);
        repository = new MemoRepository(MemoDatabase.getInstance(this).memoDao());
        setupList();
        setupFilters();
        verifyPassword();
        // 如果业务希望通过“界面跳转”来进入备忘录列表（而不是弹窗解锁），
        // 可以在跳转前完成鉴权，并在跳转 Intent 中携带已验证标记，
        // 例如：startActivity(new Intent(this, MainActivity.class).putExtra("fromAuth", true));
        // 然后在这里检测标记并直接调用 loadMemos("")，跳过 verifyPassword()
        // （不要忘了删除或条件化调用 verifyPassword，以免仍然弹出密码框）。
    }

    private void setupList() {
        RecyclerView recyclerView = findViewById(R.id.memo_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MemoAdapter(this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton addButton = findViewById(R.id.add_memo_button);
        addButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditMemoActivity.class);
            startActivity(intent);
        });
    }

    private void setupFilters() {
        searchInput = findViewById(R.id.search_input);
        categorySpinner = findViewById(R.id.category_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentCategory = position == 0 ? "" : parent.getItemAtPosition(position).toString();
                loadMemos(searchInput.getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                currentCategory = "";
                loadMemos(searchInput.getText().toString());
            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadMemos(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void verifyPassword() {
        if (!passwordManager.hasPassword()) {
            showSetPasswordDialog();
        } else {
            showLoginDialog();
        }
    }

    private void showSetPasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_set_password, null);
        EditText passwordInput = dialogView.findViewById(R.id.dialog_password);
        EditText confirmInput = dialogView.findViewById(R.id.dialog_confirm_password);

        new AlertDialog.Builder(this)
                .setTitle(R.string.set_password)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(R.string.save, (dialog, which) -> {
                    String pwd = passwordInput.getText().toString();
                    String confirm = confirmInput.getText().toString();
                    if (pwd.isEmpty() || !pwd.equals(confirm)) {
                        Toast.makeText(this, R.string.password_mismatch, Toast.LENGTH_SHORT).show();
                        showSetPasswordDialog();
                    } else {
                        passwordManager.savePassword(pwd);
                        loadMemos("");
                    }
                })
                .show();
    }

    private void showLoginDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_password, null);
        EditText passwordInput = dialogView.findViewById(R.id.dialog_password);

        new AlertDialog.Builder(this)
                .setTitle(R.string.enter_password)
                .setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(R.string.unlock, (dialog, which) -> {
                    String pwd = passwordInput.getText().toString();
                    if (passwordManager.validate(pwd)) {
                        loadMemos(searchInput.getText().toString());
                    } else {
                        Toast.makeText(this, R.string.invalid_password, Toast.LENGTH_SHORT).show();
                        showLoginDialog();
                    }
                })
                .show();
    }

    private void loadMemos(String keyword) {
        repository.search(keyword, currentCategory, this::updateList);
    }

    private void updateList(List<Memo> memos) {
        adapter.submitList(memos);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMemos(searchInput.getText().toString());
    }

    @Override
    public void onMemoClicked(Memo memo) {
        Intent intent = new Intent(this, MemoDetailActivity.class);
        intent.putExtra(MemoDetailActivity.EXTRA_MEMO_ID, memo.id);
        startActivity(intent);
    }
}
