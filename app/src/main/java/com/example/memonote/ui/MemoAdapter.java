package com.example.memonote.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memonote.R;
import com.example.memonote.data.Memo;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MemoAdapter extends RecyclerView.Adapter<MemoAdapter.MemoViewHolder> {

    public interface MemoClickListener {
        void onMemoClicked(Memo memo);
    }

    private final MemoClickListener listener;
    private final List<Memo> memos = new ArrayList<>();

    public MemoAdapter(MemoClickListener listener) {
        this.listener = listener;
    }

    public void submitList(List<Memo> items) {
        memos.clear();
        if (items != null) {
            memos.addAll(items);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MemoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo, parent, false);
        return new MemoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoViewHolder holder, int position) {
        Memo memo = memos.get(position);
        holder.title.setText(memo.title);
        holder.category.setText(memo.category);
        holder.updatedAt.setText(DateFormat.getDateTimeInstance().format(new Date(memo.updatedAt)));
        holder.imageIndicator.setVisibility(memo.imagePath == null || memo.imagePath.isEmpty() ? View.GONE : View.VISIBLE);
        holder.itemView.setOnClickListener(v -> listener.onMemoClicked(memo));
    }

    @Override
    public int getItemCount() {
        return memos.size();
    }

    static class MemoViewHolder extends RecyclerView.ViewHolder {
        final TextView title;
        final TextView category;
        final TextView updatedAt;
        final ImageView imageIndicator;

        MemoViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.memo_title);
            category = itemView.findViewById(R.id.memo_category);
            updatedAt = itemView.findViewById(R.id.memo_updated_at);
            imageIndicator = itemView.findViewById(R.id.memo_image_indicator);
        }
    }
}
