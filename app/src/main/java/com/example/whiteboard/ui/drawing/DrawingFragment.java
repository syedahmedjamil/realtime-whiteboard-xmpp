package com.example.whiteboard.ui.drawing;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whiteboard.CommentListAdapter;
import com.example.whiteboard.CommentMessage;
import com.example.whiteboard.MainActivity;
import com.example.whiteboard.R;
import com.example.whiteboard.SmackXMPP;
import com.example.whiteboard.tools.BrushDrawingView;
import com.example.whiteboard.tools.OnPhotoEditorListener;
import com.example.whiteboard.tools.PhotoEditor;
import com.example.whiteboard.tools.ViewType;
import com.example.whiteboard.tools.WhiteboardLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputLayout;

import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.xdata.FormField;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.util.ArrayList;
import java.util.List;

import static android.os.Looper.getMainLooper;

public class DrawingFragment extends Fragment implements OnPhotoEditorListener , BrushDrawingView.OnDrawingListener {

    private DrawingViewModel drawingViewModel;
    private Handler mainHandler;
    private PhotoEditor photoEditor;

    private ImageButton imageButton_brush;
    private ImageButton imageButton_text;
    private ImageButton imageButton_eraser;
    private ImageButton imageButton_stickyNote;
    private ImageButton imageButton_comment;
    private ImageButton imageButton_shapes;
    private ImageButton imageButton_upload;
    private ImageButton selectedButton;

    private ImageButton imageButton_yellow;
    private ImageButton imageButton_red;
    private ImageButton imageButton_blue;
    private ImageButton imageButton_green;
    private ImageButton imageButton_purple;
    private ImageButton imageButton_orange;
    private ImageButton imageButton_black;
    private ImageButton selectecColor;

    private ImageButton textDone;
    private ImageButton textCancel;
    private TextInputLayout textInputLayout;

    EditText reply_editText;

    private LinearLayout bottomToolbar;
    private LinearLayout brushProperties;
    private LinearLayout eraserProperties;
    private LinearLayout textProperties;
    private LinearLayout commentProperties;
    private Slider slider;
    private SeekBar seekBar;

    private RecyclerView commentListRecyclerView;
    private CommentListAdapter commentListAdapter;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String currentUser = MainActivity.currentUser;
        List<CommentMessage> commentList = new ArrayList<>();
//        commentList.add(new CommentMessage("Can you provide an update on this ?", MainActivity.currentUser));
//        commentList.add(new CommentMessage("Okay, give me a sec !", "some user 1"));
//        commentList.add(new CommentMessage("Waiting as well." , "some user 2"));
//        commentList.add(new CommentMessage(":)", MainActivity.currentUser));
//        commentList.add(new CommentMessage("What else are you upto these days ?", "some user 2"));
//        commentList.add(new CommentMessage("Nothing except completing my final year project", MainActivity.currentUser));
//        commentList.add(new CommentMessage("Oh nice ... Good luck then !", "some user 2"));
//        commentList.add(new CommentMessage("Ok I'm done, check the image now.", "some user 1" ));
//        commentList.add(new CommentMessage("Dude, that is perfect! real life saver. Thanks", MainActivity.currentUser));

        commentListAdapter = new CommentListAdapter(getContext(),commentList);
        MainActivity.smackXMPP = new SmackXMPP(getContext(), MainActivity.currentUser, "jab_ber123", "autojetfx.home", MainActivity.hostIP);
        MainActivity.smackXMPP.connect();


    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_drawing_temp, container, false);

        commentListRecyclerView = root.findViewById(R.id.commentListRecyclerView);
        commentListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        commentListRecyclerView.setAdapter(commentListAdapter);
        bottomToolbar = root.findViewById(R.id.bottom_toolbar);
        brushProperties = root.findViewById(R.id.brush_properties);
        eraserProperties = root.findViewById(R.id.eraser_properties);
        textProperties = root.findViewById(R.id.text_properties);
        commentProperties = root.findViewById(R.id.comment_properties);
        reply_editText = root.findViewById(R.id.reply_message_edittext);
        FloatingActionButton fab = root.findViewById(R.id.send_message_button);
        MaterialButton clearAll = root.findViewById(R.id.clear_all_button);
        BrushDrawingView drawingView = root.findViewById(R.id.brush_drawing_view_fragment_drawing);
        drawingView.setOnDrawingListner(this);
        slider = root.findViewById(R.id.slider);
        seekBar = root.findViewById(R.id.seekBar);
        imageButton_brush = root.findViewById(R.id.imageButton_brush);
        imageButton_text = root.findViewById(R.id.imageButton_text);
        imageButton_eraser = root.findViewById(R.id.imageButton_eraser);
        imageButton_stickyNote = root.findViewById(R.id.imageButton_sticky_note);
        imageButton_comment = root.findViewById(R.id.imageButton_comment);
        imageButton_shapes = root.findViewById(R.id.imageButton_shapes);
        imageButton_upload = root.findViewById(R.id.imageButton_upload);

        textInputLayout = root.findViewById(R.id. textInputLayout);

        imageButton_yellow = root.findViewById(R.id.imageButton_yellow);
        imageButton_blue = root.findViewById(R.id.imageButton_blue);
        imageButton_red = root.findViewById(R.id.imageButton_red);
        imageButton_purple = root.findViewById(R.id.imageButton_purple);
        imageButton_green = root.findViewById(R.id.imageButton_green);
        imageButton_orange = root.findViewById(R.id.imageButton_orange);
        imageButton_black = root.findViewById(R.id.imageButton_black);

        textDone = root.findViewById(R.id.text_done);
        textCancel = root.findViewById(R.id.text_cancel);

        imageButton_brush.setOnClickListener(this::onToolbarItemClick);
        imageButton_text.setOnClickListener(this::onToolbarItemClick);
        imageButton_eraser.setOnClickListener(this::onToolbarItemClick);
        imageButton_stickyNote.setOnClickListener(this::onToolbarItemClick);
        imageButton_comment.setOnClickListener(this::onToolbarItemClick);
        imageButton_shapes.setOnClickListener(this::onToolbarItemClick);
        imageButton_upload.setOnClickListener(this::onToolbarItemClick);

        textDone.setOnClickListener(this::onDoneClick);
        textCancel.setOnClickListener(this::onCancelClick);

        imageButton_yellow.setOnClickListener(this::onBrushColorClick);
        imageButton_blue.setOnClickListener(this::onBrushColorClick);
        imageButton_red.setOnClickListener(this::onBrushColorClick);
        imageButton_purple.setOnClickListener(this::onBrushColorClick);
        imageButton_green.setOnClickListener(this::onBrushColorClick);
        imageButton_orange.setOnClickListener(this::onBrushColorClick);
        imageButton_black.setOnClickListener(this::onBrushColorClick);


        slider.setOnChangeListener(this::onSliderChange);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                onSliderChange(seekBar,progress,fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        fab.setOnClickListener(this::onSendMessageClick);

        clearAll.setOnClickListener((v)->{
            photoEditor.clearAllViews();
            onClearSelected();
        });
        photoEditor = new PhotoEditor.Builder(getContext(),root.findViewById(R.id.white_board_layout)).setPinchTextScalable(true).build();
        photoEditor.setOnPhotoEditorListener(this);
        mainHandler = new Handler(getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {

                PayloadItem payloadItem = (PayloadItem) msg.obj;
                DataForm dataForm = (DataForm) payloadItem.getPayload();

                switch (dataForm.getTitle()) {
                    case "onMove": {
                        float from_x = 0;
                        float from_y = 0;
                        float to_x = 0;
                        float to_y = 0;

                        String[] from_pos = dataForm.getField("from_pos").getFirstValue().split(",");
                        from_x = Float.parseFloat(from_pos[0]);
                        from_y = Float.parseFloat(from_pos[1]);

                        String[] to_pos = dataForm.getField("to_pos").getFirstValue().split(",");
                        to_x = Float.parseFloat(to_pos[0]);
                        to_y = Float.parseFloat(to_pos[1]);


                        drawingView.networkMove(from_x,from_y,to_x, to_y);
                    }
                    break;
                    case "onStart": {
                        float x, y;
                        float size;
                        int color;
                        String Xfermode;
                        x = y = 0;
                        String[] from_pos = dataForm.getField("pos").getFirstValue().split(",");
                        x = Float.parseFloat(from_pos[0]);
                        y = Float.parseFloat(from_pos[1]);

                        size = Float.parseFloat(dataForm.getField("size").getFirstValue());
                        color = Integer.parseInt(dataForm.getField("color").getFirstValue());
                        Xfermode = dataForm.getField("mode").getFirstValue();

                        drawingView.networkStart(x,y,size,color,Xfermode);

                    }
                    break;
                    case "onUp": {
                        float x, y;
                        x = y = 0;
                        String[] pos = dataForm.getField("pos").getFirstValue().split(",");
                        x = Float.parseFloat(pos[0]);
                        y = Float.parseFloat(pos[1]);
                        drawingView.networkUp();
                    }
                    break;
                    case "onTextMove": {
                        float x, y;
                        String tag;
                        x = y = 0;
                        String[] pos = dataForm.getField("pos").getFirstValue().split(",");
                        x = Float.parseFloat(pos[0]);
                        y = Float.parseFloat(pos[1]);

                        tag =  dataForm.getField("tagname").getFirstValue();
                        photoEditor.moveText(x, y, tag);

                    }
                    break;
                    case "onAddText": {
                        String text, tagname;
                        text = dataForm.getField("text").getFirstValue();
                        tagname = dataForm.getField("tagname").getFirstValue();

                        photoEditor.networkAddText(text,tagname);
                    }
                    break;
                    case "onRemoveText": {
                        String tagname;
                        tagname = dataForm.getField("tagname").getFirstValue();

                        photoEditor.networkUndoView(tagname);
                    }
                    break;
                    case "onClearSelected":{
                        photoEditor.clearAllViews();
                    }
                    break;
                    case "onAddReply":{
                        String text,author;
                        text = dataForm.getField("text").getFirstValue();
                        author = dataForm.getField("author").getFirstValue();

                        CommentMessage commentMessage = new CommentMessage(text,author);
                        commentListAdapter.addItem(commentMessage);
                    }
                }

                return true;
            }
        });
        MainActivity.smackXMPP.setHandler(mainHandler);
        imageButton_black.performClick();

        return root;
    }
    @Override
    public void onDestroy() {
        MainActivity.smackXMPP.disconnect();
        super.onDestroy();
    }



    //BrushViewChangeListener
    @Override
    public void onEditTextChangeListener(View rootView, String text, int colorCode) {

    }
    @Override
    public void onAddViewListener(String text, String tagName) {
        DataForm dataForm = new DataForm(DataForm.Type.submit);
        dataForm.setTitle("onAddText");
        FormField field = null;

        field = new FormField("text");
        field.setType(FormField.Type.text_single);
        field.addValue(text);
        dataForm.addField(field);

        field = new FormField("tagname");
        field.setType(FormField.Type.text_single);
        field.addValue(tagName);
        dataForm.addField(field);

        PayloadItem<DataForm> payloadItem = new PayloadItem<>(MainActivity.currentUser,dataForm);
        MainActivity.smackXMPP.publish(payloadItem);
    }
    @Override
    public void onAddCommentListener(float x, float y, String tagName) {
        DataForm dataForm = new DataForm(DataForm.Type.submit);
        dataForm.setTitle("onCommentAdd");
        FormField field = null;

        field = new FormField("pos");
        field.setType(FormField.Type.text_single);
        field.addValue(x + "," + y);
        dataForm.addField(field);

        field = new FormField("tagname");
        field.setType(FormField.Type.text_single);
        field.addValue(tagName);
        dataForm.addField(field);

        PayloadItem<DataForm> payloadItem = new PayloadItem<>(MainActivity.currentUser,dataForm);
        MainActivity.smackXMPP.publish(payloadItem);
    }
    @Override
    public void onRemoveViewListener(String tagName) {
        DataForm dataForm = new DataForm(DataForm.Type.submit);
        dataForm.setTitle("onRemoveText");
        FormField field = null;

        field = new FormField("tagname");
        field.setType(FormField.Type.text_single);
        field.addValue(tagName);
        dataForm.addField(field);

        PayloadItem<DataForm> payloadItem = new PayloadItem<>(MainActivity.currentUser,dataForm);
        MainActivity.smackXMPP.publish(payloadItem);

    }
    @Override
    public void onTextMoveListener(float deltaX, float deltaY, String viewTag) {
        DataForm dataForm = new DataForm(DataForm.Type.submit);
        dataForm.setTitle("onTextMove");
        FormField field = null;

        field = new FormField("pos");
        field.setType(FormField.Type.text_single);
        field.addValue(deltaX + "," + deltaY);
        dataForm.addField(field);

        field = new FormField("tagname");
        field.setType(FormField.Type.text_single);
        field.addValue(viewTag);
        dataForm.addField(field);

        PayloadItem<DataForm> payloadItem = new PayloadItem<>(MainActivity.currentUser,dataForm);
        MainActivity.smackXMPP.publish(payloadItem);
    }
    @Override
    public void onStartViewChangeListener(ViewType viewType) {
    }
    @Override
    public void onStopViewChangeListener(ViewType viewType) {

    }

    //OnDrawingListener
    @Override
    public void sendTouchStart(float x, float y, float brushSize, int color, String Xfermode) {
        DataForm dataForm = new DataForm(DataForm.Type.submit);
        dataForm.setTitle("onStart");
        FormField field = null;

        field = new FormField("pos");
        field.setType(FormField.Type.text_single);
        field.addValue(x + "," + y);
        dataForm.addField(field);

        field = new FormField("size");
        field.setType(FormField.Type.text_single);
        field.addValue(Float.toString(brushSize));
        dataForm.addField(field);

        field = new FormField("color");
        field.setType(FormField.Type.text_single);
        field.addValue(Integer.toString(color));
        dataForm.addField(field);

        field = new FormField("mode");
        field.setType(FormField.Type.text_single);
        field.addValue(Xfermode);
        dataForm.addField(field);

        PayloadItem<DataForm> payloadItem = new PayloadItem<>(MainActivity.currentUser,dataForm);
        MainActivity.smackXMPP.publish(payloadItem);
    }
    @Override
    public void sendTouchMove(float from_x, float from_y, float to_x, float to_y) {
        DataForm dataForm = new DataForm(DataForm.Type.submit);
        dataForm.setTitle("onMove");
        FormField field = null;

        field = new FormField("from_pos");
        field.setType(FormField.Type.text_single);
        field.addValue(from_x + "," + from_y);
        dataForm.addField(field);

        field = new FormField("to_pos");
        field.setType(FormField.Type.text_single);
        field.addValue(to_x + "," + to_y);
        dataForm.addField(field);

        PayloadItem<DataForm> payloadItem = new PayloadItem<>(MainActivity.currentUser,dataForm);
        MainActivity.smackXMPP.publish(payloadItem);
    }
    @Override
    public void sendTouchUp(float x, float y) {
        DataForm dataForm = new DataForm(DataForm.Type.submit);
        dataForm.setTitle("onUp");
        FormField field = null;

        field = new FormField("pos");
        field.setType(FormField.Type.text_single);
        field.addValue(x + "," + y);
        dataForm.addField(field);

        PayloadItem<DataForm> payloadItem = new PayloadItem<>(MainActivity.currentUser,dataForm);
        MainActivity.smackXMPP.publish(payloadItem);
    }

    private void onToolbarItemClick(View view)  {

        int id = view.getId();
        Resources resources = getResources();
        ImageButton clickedButton = (ImageButton) view;
        switch (id) {
            case R.id.imageButton_brush: {
                if (clickedButton != selectedButton) {
                    imageButton_brush.setImageDrawable(resources.getDrawable(R.drawable.toolbar_brush_selected));
                    imageButton_text.setImageDrawable(resources.getDrawable(R.drawable.toolbar_text));
                    imageButton_eraser.setImageDrawable(resources.getDrawable(R.drawable.toolbar_eraser));
                    imageButton_stickyNote.setImageDrawable(resources.getDrawable(R.drawable.toolbar_note));
                    imageButton_comment.setImageDrawable(resources.getDrawable(R.drawable.toolbar_comment));
                    imageButton_shapes.setImageDrawable(resources.getDrawable(R.drawable.toolbar_shapes));
                    imageButton_upload.setImageDrawable(resources.getDrawable(R.drawable.toolbar_upload));

                    closeCurrentProperties();
                    photoEditor.setAllowDraw(true);
                    photoEditor.resetDrawingView();
                }

                if(brushProperties.getVisibility() == View.GONE)
                    brushProperties.setVisibility(View.VISIBLE);
                else
                    brushProperties.setVisibility(View.GONE);

            }
            break;
            case R.id.imageButton_text: {
                if (clickedButton != selectedButton) {
                    imageButton_brush.setImageDrawable(resources.getDrawable(R.drawable.toolbar_brush));
                    imageButton_text.setImageDrawable(resources.getDrawable(R.drawable.toolbar_text_selected));
                    imageButton_eraser.setImageDrawable(resources.getDrawable(R.drawable.toolbar_eraser));
                    imageButton_stickyNote.setImageDrawable(resources.getDrawable(R.drawable.toolbar_note));
                    imageButton_comment.setImageDrawable(resources.getDrawable(R.drawable.toolbar_comment));
                    imageButton_shapes.setImageDrawable(resources.getDrawable(R.drawable.toolbar_shapes));
                    imageButton_upload.setImageDrawable(resources.getDrawable(R.drawable.toolbar_upload));

                    closeCurrentProperties();

                }

                if(textProperties.getVisibility() == View.GONE)
                    textProperties.setVisibility(View.VISIBLE);
                else
                    textProperties.setVisibility(View.GONE);
            }
            break;
            case R.id.imageButton_eraser: {
                if (clickedButton != selectedButton) {
                    imageButton_brush.setImageDrawable(resources.getDrawable(R.drawable.toolbar_brush));
                    imageButton_text.setImageDrawable(resources.getDrawable(R.drawable.toolbar_text));
                    imageButton_eraser.setImageDrawable(resources.getDrawable(R.drawable.toolbar_eraser_selected));
                    imageButton_stickyNote.setImageDrawable(resources.getDrawable(R.drawable.toolbar_note));
                    imageButton_comment.setImageDrawable(resources.getDrawable(R.drawable.toolbar_comment));
                    imageButton_shapes.setImageDrawable(resources.getDrawable(R.drawable.toolbar_shapes));
                    imageButton_upload.setImageDrawable(resources.getDrawable(R.drawable.toolbar_upload));

                    closeCurrentProperties();
                    photoEditor.brushEraser();
                    photoEditor.setAllowDraw(true);
                }

                if(eraserProperties.getVisibility() == View.GONE)
                    eraserProperties.setVisibility(View.VISIBLE);
                else
                    eraserProperties.setVisibility(View.GONE);
            }
            break;
            case R.id.imageButton_sticky_note: {
                if (clickedButton != selectedButton) {
                    imageButton_brush.setImageDrawable(resources.getDrawable(R.drawable.toolbar_brush));
                    imageButton_text.setImageDrawable(resources.getDrawable(R.drawable.toolbar_text));
                    imageButton_eraser.setImageDrawable(resources.getDrawable(R.drawable.toolbar_eraser));
                    imageButton_stickyNote.setImageDrawable(resources.getDrawable(R.drawable.toolbar_note_selected));
                    imageButton_comment.setImageDrawable(resources.getDrawable(R.drawable.toolbar_comment));
                    imageButton_shapes.setImageDrawable(resources.getDrawable(R.drawable.toolbar_shapes));
                    imageButton_upload.setImageDrawable(resources.getDrawable(R.drawable.toolbar_upload));
                }
            }
            break;
            case R.id.imageButton_comment: {
                if (clickedButton != selectedButton) {
                    imageButton_brush.setImageDrawable(resources.getDrawable(R.drawable.toolbar_brush));
                    imageButton_text.setImageDrawable(resources.getDrawable(R.drawable.toolbar_text));
                    imageButton_eraser.setImageDrawable(resources.getDrawable(R.drawable.toolbar_eraser));
                    imageButton_stickyNote.setImageDrawable(resources.getDrawable(R.drawable.toolbar_note));
                    imageButton_comment.setImageDrawable(resources.getDrawable(R.drawable.toolbar_comment_selected));
                    imageButton_shapes.setImageDrawable(resources.getDrawable(R.drawable.toolbar_shapes));
                    imageButton_upload.setImageDrawable(resources.getDrawable(R.drawable.toolbar_upload));

                    closeCurrentProperties();
                    photoEditor.setOnTapListener();
                }

                if(commentProperties.getVisibility() == View.GONE)
                    commentProperties.setVisibility(View.VISIBLE);
                else
                    commentProperties.setVisibility(View.GONE);
            }
            break;
            case R.id.imageButton_shapes: {
                if(clickedButton != selectedButton) {
                    imageButton_brush.setImageDrawable(resources.getDrawable(R.drawable.toolbar_brush));
                    imageButton_text.setImageDrawable(resources.getDrawable(R.drawable.toolbar_text));
                    imageButton_eraser.setImageDrawable(resources.getDrawable(R.drawable.toolbar_eraser));
                    imageButton_stickyNote.setImageDrawable(resources.getDrawable(R.drawable.toolbar_note));
                    imageButton_comment.setImageDrawable(resources.getDrawable(R.drawable.toolbar_comment));
                    imageButton_shapes.setImageDrawable(resources.getDrawable(R.drawable.toolbar_shapes_selected));
                    imageButton_upload.setImageDrawable(resources.getDrawable(R.drawable.toolbar_upload));
                }
            }
            break;
            case R.id.imageButton_upload: {
                if(clickedButton != selectedButton) {
                    imageButton_brush.setImageDrawable(resources.getDrawable(R.drawable.toolbar_brush));
                    imageButton_text.setImageDrawable(resources.getDrawable(R.drawable.toolbar_text));
                    imageButton_eraser.setImageDrawable(resources.getDrawable(R.drawable.toolbar_eraser));
                    imageButton_stickyNote.setImageDrawable(resources.getDrawable(R.drawable.toolbar_note));
                    imageButton_comment.setImageDrawable(resources.getDrawable(R.drawable.toolbar_comment));
                    imageButton_shapes.setImageDrawable(resources.getDrawable(R.drawable.toolbar_shapes));
                    imageButton_upload.setImageDrawable(resources.getDrawable(R.drawable.toolbar_upload_selected));
                }
            }

        }
        selectedButton = clickedButton;
    }
    private void onBrushColorClick(View view) {

        int id = view.getId();
        int color = 0;
        Resources resources = getResources();
        ImageButton clickedColor = (ImageButton) view;
        switch (id) {
            case R.id.imageButton_yellow: {
                if (clickedColor != selectecColor) {
                    imageButton_yellow.setBackground(resources.getDrawable(R.drawable.circle_yellow_faded));
                    imageButton_blue.setBackground(null);
                    imageButton_red.setBackground(null);
                    imageButton_purple.setBackground(null);
                    imageButton_green.setBackground(null);
                    imageButton_orange.setBackground(null);
                    imageButton_black.setBackground(null);
                    color = resources.getColor(R.color.yellow);
                    photoEditor.setBrushColor(color);
                }
            }
            break;
            case R.id.imageButton_blue: {
                if (clickedColor != selectecColor) {
                    imageButton_yellow.setBackground(null);
                    imageButton_blue.setBackground(resources.getDrawable(R.drawable.circle_blue_faded));
                    imageButton_red.setBackground(null);
                    imageButton_purple.setBackground(null);
                    imageButton_green.setBackground(null);
                    imageButton_orange.setBackground(null);
                    imageButton_black.setBackground(null);

                    color = resources.getColor(R.color.blue);
                    photoEditor.setBrushColor(color);
                }
            }
            break;
            case R.id.imageButton_red: {
                if (clickedColor != selectecColor) {
                    imageButton_yellow.setBackground(null);
                    imageButton_blue.setBackground(null);
                    imageButton_red.setBackground(resources.getDrawable(R.drawable.circle_red_faded));
                    imageButton_purple.setBackground(null);
                    imageButton_green.setBackground(null);
                    imageButton_orange.setBackground(null);
                    imageButton_black.setBackground(null);

                    color = resources.getColor(R.color.red);
                    photoEditor.setBrushColor(color);
                }
            }
            break;
            case R.id.imageButton_purple: {
                if (clickedColor != selectecColor) {
                    imageButton_yellow.setBackground(null);
                    imageButton_blue.setBackground(null);
                    imageButton_red.setBackground(null);
                    imageButton_purple.setBackground(resources.getDrawable(R.drawable.circle_purple_faded));
                    imageButton_green.setBackground(null);
                    imageButton_orange.setBackground(null);
                    imageButton_black.setBackground(null);

                    color = resources.getColor(R.color.purple);
                    photoEditor.setBrushColor(color);
                }
            }
            break;
            case R.id.imageButton_green: {
                if (clickedColor != selectecColor) {
                    imageButton_yellow.setBackground(null);
                    imageButton_blue.setBackground(null);
                    imageButton_red.setBackground(null);
                    imageButton_purple.setBackground(null);
                    imageButton_green.setBackground(resources.getDrawable(R.drawable.circle_green_faded));
                    imageButton_orange.setBackground(null);
                    imageButton_black.setBackground(null);

                    color = resources.getColor(R.color.green);
                    photoEditor.setBrushColor(color);
                }
            }
            break;
            case R.id.imageButton_orange: {
                if (clickedColor != selectecColor) {
                    imageButton_yellow.setBackground(null);
                    imageButton_blue.setBackground(null);
                    imageButton_red.setBackground(null);
                    imageButton_purple.setBackground(null);
                    imageButton_green.setBackground(null);
                    imageButton_orange.setBackground(resources.getDrawable(R.drawable.circle_orange_faded));
                    imageButton_black.setBackground(null);

                    color = resources.getColor(R.color.orange);
                    photoEditor.setBrushColor(color);
                }
            }
            break;
            case R.id.imageButton_black: {
                if (clickedColor != selectecColor) {
                    imageButton_yellow.setBackground(null);
                    imageButton_blue.setBackground(null);
                    imageButton_red.setBackground(null);
                    imageButton_purple.setBackground(null);
                    imageButton_green.setBackground(null);
                    imageButton_orange.setBackground(null);
                    imageButton_black.setBackground(resources.getDrawable(R.drawable.circle_black_faded));

                    color = resources.getColor(R.color.black);
                    photoEditor.setBrushColor(color);
                }
            }

        }
        selectecColor = clickedColor;
        Drawable wrappedDrawable = DrawableCompat.wrap(seekBar.getThumb());
        DrawableCompat.setTint(wrappedDrawable, color);
        wrappedDrawable = DrawableCompat.wrap(seekBar.getProgressDrawable());
        DrawableCompat.setTint(wrappedDrawable,color);

        //wrappedDrawable = DrawableCompat.wrap(imageButton_brush.getDrawable());
        //DrawableCompat.setTint(wrappedDrawable, color);



    }
    private void onSliderChange(Slider slider , float value) {
        photoEditor.setBrushSize(value*5);
    }
    private void onSliderChange(SeekBar slider , int value, boolean fromUser) {
        photoEditor.setBrushSize(value*5.0f);
    }
    private void onDoneClick(View view) {
        String text = textInputLayout.getEditText().getText().toString();
        textInputLayout.getEditText().setText("");
        photoEditor.addText(text,Color.BLACK);
        closeCurrentProperties();

    }
    private void onCancelClick(View view) {
        textInputLayout.getEditText().setText("");

    }
    private void onSendMessageClick(View view)
    {
        if(reply_editText.getText() == null || reply_editText.getText().toString().equals(""))
            return;

        String text = reply_editText.getText().toString();
        DataForm dataForm = new DataForm(DataForm.Type.submit);
        dataForm.setTitle("onAddReply");
        FormField field = null;

        field = new FormField("text");
        field.setType(FormField.Type.text_single);
        field.addValue(text);
        dataForm.addField(field);

        field = new FormField("author");
        field.setType(FormField.Type.text_single);
        field.addValue(MainActivity.currentUser);
        dataForm.addField(field);

        PayloadItem<DataForm> payloadItem = new PayloadItem<>(MainActivity.currentUser,dataForm);
        MainActivity.smackXMPP.publish(payloadItem);

        commentListAdapter.addItem(new CommentMessage(text,MainActivity.currentUser));
        commentListRecyclerView.smoothScrollToPosition(commentListAdapter.getItemCount());
        reply_editText.setText("");
    }
    private void onClearSelected() {
        DataForm dataForm = new DataForm(DataForm.Type.submit);
        dataForm.setTitle("onClearSelected");

        PayloadItem<DataForm> payloadItem = new PayloadItem<>(MainActivity.currentUser,dataForm);
        MainActivity.smackXMPP.publish(payloadItem);
    }

    private void closeCurrentProperties()
    {
        if (selectedButton == null)
             return;
        int id = selectedButton.getId();
        switch (id)
        {
            case R.id.imageButton_brush:{
                photoEditor.setAllowDraw(false);
                brushProperties.setVisibility(View.GONE);
            }
            break;
            case R.id.imageButton_text:{
                textProperties.setVisibility(View.GONE);
            }
            break;
            case R.id.imageButton_eraser:{
                eraserProperties.setVisibility(View.GONE);
                photoEditor.setAllowDraw(false);
            }
            break;
            case R.id.imageButton_comment:{
                commentProperties.setVisibility(View.GONE);
                photoEditor.removeOnTapListener();
            }
        }
    }


}










