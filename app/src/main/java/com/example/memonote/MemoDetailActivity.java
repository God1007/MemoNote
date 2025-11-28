package com.example.memonote;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.memonote.data.Memo;
import com.example.memonote.data.MemoDatabase;
import com.example.memonote.data.MemoRepository;

import java.text.DateFormat;
import java.util.Date;

public class MemoDetailActivity extends AppCompatActivity {

    public static final String EXTRA_MEMO_ID = "extra_memo_id";

    private MemoRepository repository;
    private Memo memo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_detail);

        repository = new MemoRepository(MemoDatabase.getInstance(this).memoDao());
        long memoId = getIntent().getLongExtra(EXTRA_MEMO_ID, -1L);
        if (memoId == -1L) {
            finish();
            return;
        }
        loadMemo(memoId);
    }

    private void loadMemo(long memoId) {
        repository.getById(memoId, memo -> {
            if (memo == null) {
                Toast.makeText(this, R.string.memo_missing, Toast.LENGTH_SHORT).show();
                finish();
                return;
            }
            this.memo = memo;
            bindMemo(memo);
        });
    }

    private void bindMemo(Memo memo) {
        TextView title = findViewById(R.id.detail_title);
        TextView content = findViewById(R.id.detail_content);
        TextView category = findViewById(R.id.detail_category);
        TextView timestamps = findViewById(R.id.detail_timestamps);
        ImageView imageView = findViewById(R.id.detail_image);
        Button editButton = findViewById(R.id.detail_edit);
        Button deleteButton = findViewById(R.id.detail_delete);
        Button syncButton = findViewById(R.id.detail_sync);

        title.setText(memo.title);
        content.setText(memo.content);
        category.setText(memo.category);
        String timeText = getString(R.string.timestamp_format, DateFormat.getDateTimeInstance().format(new Date(memo.createdAt)), DateFormat.getDateTimeInstance().format(new Date(memo.updatedAt)));
        timestamps.setText(timeText);

        if (memo.imagePath != null && !memo.imagePath.isEmpty()) {
            imageView.setImageURI(Uri.parse(memo.imagePath));
        }

        editButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditMemoActivity.class);
            intent.putExtra(EditMemoActivity.EXTRA_MEMO_ID, memo.id);
            startActivity(intent);
        });

        deleteButton.setOnClickListener(v -> repository.delete(memo, this::finish));
        syncButton.setOnClickListener(v -> repository.syncToCloud(() -> Toast.makeText(this, R.string.synced, Toast.LENGTH_SHORT).show()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (memo != null) {
            loadMemo(memo.id);
        }
    }
}
