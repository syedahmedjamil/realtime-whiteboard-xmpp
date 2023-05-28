package com.example.whiteboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

public class CommentListAdapter extends RecyclerView.Adapter {
    private List<CommentMessage> commentList = new ArrayList<>();
    private Context context;

    public CommentListAdapter(Context context , List<CommentMessage> list)
    {
        commentList = list;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root = null;
        if (viewType == 1)
             root = LayoutInflater.from(context).inflate(R.layout.recycler_view_message_send,parent,false);
        else
            root = LayoutInflater.from(context).inflate(R.layout.recycler_view_message_recv,parent,false);
        return new CommentListViewHolder(root,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((CommentListViewHolder)holder).bind(commentList.get(position).message);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    @Override
    public int getItemViewType(int position){
        String author = commentList.get(position).author;
        if(author.equals(MainActivity.currentUser))
            return 1;
        else
            return 0;

    }

    public void addItem(CommentMessage commentMessage)
    {
        commentList.add(commentMessage);
        notifyItemChanged(commentList.size()-1);
    }

    private class CommentListViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView messageBody;

        public CommentListViewHolder(@NonNull View itemView, int viewType){
            super(itemView);
            if(viewType == 1)
                messageBody = itemView.findViewById(R.id.sent_message_body);
            else
                messageBody = itemView.findViewById(R.id.recv_message_body);
        }

        public void bind(String message)
        {
            messageBody.setText(message);
        }
    }
}
