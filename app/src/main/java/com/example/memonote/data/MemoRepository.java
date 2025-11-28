package com.example.memonote.data;

import android.os.Handler;
import android.os.Looper;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class MemoRepository {
    private final MemoDao memoDao;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public MemoRepository(MemoDao memoDao) {
        this.memoDao = memoDao;
    }

    public void getAll(Consumer<List<Memo>> callback) {
        executorService.execute(() -> {
            List<Memo> data = memoDao.getAll();
            mainHandler.post(() -> callback.accept(data));
        });
    }

    public void search(String keyword, String category, Consumer<List<Memo>> callback) {
        executorService.execute(() -> {
            List<Memo> data = memoDao.search(keyword == null ? "" : keyword, category);
            mainHandler.post(() -> callback.accept(data));
        });
    }

    public void getById(long id, Consumer<Memo> callback) {
        executorService.execute(() -> {
            Memo memo = memoDao.getById(id);
            mainHandler.post(() -> callback.accept(memo));
        });
    }

    public void insert(Memo memo, Consumer<Long> onComplete) {
        executorService.execute(() -> {
            long id = memoDao.insert(memo);
            mainHandler.post(() -> onComplete.accept(id));
        });
    }

    public void update(Memo memo, Runnable onComplete) {
        executorService.execute(() -> {
            memoDao.update(memo);
            mainHandler.post(onComplete);
        });
    }

    public void delete(Memo memo, Runnable onComplete) {
        executorService.execute(() -> {
            memoDao.delete(memo);
            mainHandler.post(onComplete);
        });
    }

    public void syncToCloud(Runnable onComplete) {
        executorService.execute(() -> mainHandler.post(onComplete));
    }
}
