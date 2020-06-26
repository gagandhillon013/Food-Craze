package com.example.foodcraze.Callback;

import com.example.foodcraze.Model.CommentModel;

import java.util.List;

public interface CommentCallbackListener {
    void onCommentLoadSuccess(List<CommentModel> commentModels);
    void onCommentLoadFailed(String message);
}
