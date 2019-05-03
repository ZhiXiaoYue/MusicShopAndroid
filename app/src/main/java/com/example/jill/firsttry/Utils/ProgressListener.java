package com.example.jill.firsttry.Utils;

public interface ProgressListener {
    void onProgress(long currentBytes, long contentLength, boolean done);
}

