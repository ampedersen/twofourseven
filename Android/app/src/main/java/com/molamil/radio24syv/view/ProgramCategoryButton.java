package com.molamil.radio24syv.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.molamil.radio24syv.R;
import com.molamil.radio24syv.RadioPlayer;
import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.TopicInfo;

import org.w3c.dom.Text;

/**
 * Created by jens on 11/09/15.
 */
public class ProgramCategoryButton extends LinearLayout {

    public static final int CATEGORY_RECOMMENDED = 0;
    public static final int CATEGORY_ALL_BY_NAME = 1;
    public static final int CATEGORY_ALL_BY_TOPIC = 2;
    public static final int CATEGORY_TOPIC_BY_NAME = 3;
    public static final int CATEGORY_INACTIVE_BY_NAME = 4;

    private boolean selected;
    private int category;
    private TopicInfo topic;

    public ProgramCategoryButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.setOrientation(LinearLayout.HORIZONTAL);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_program_category_button, this);

//        // Attributes are null when instantiated from code (which we always do)
//        if (attrs != null) {
//            // Apply attributes from XML
//            TypedArray a = context.getTheme().obtainStyledAttributes(
//                    attrs,
//                    R.styleable.ProgramCategoryButton,
//                    0, 0);
//
//            try {
//                setSelected(a.getBoolean(R.styleable.ProgramCategoryButton_selected, false));
//                setCategory(a.getInteger(R.styleable.ProgramCategoryButton_category, CATEGORY_RECOMMENDED));
//                setTopicId(a.getString(R.styleable.ProgramCategoryButton_topic_id));
//            } finally {
//                a.recycle();
//            }
//        }
    }

    public boolean getSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;

        View selectedIndicator = findViewById(R.id.selected_indicator);
        int colorId;
        if (selected) {
            colorId = R.color.radio_gray;
        } else {
            colorId = R.color.transparent;
        }
        selectedIndicator.setBackgroundColor(getResources().getColor(colorId));
    }

    public void setSelectedIndicatorVisible(boolean show) {
        View selectedIndicator = findViewById(R.id.selected_indicator);
        int visibility;
        if (show) {
            visibility = View.VISIBLE;
        } else {
            visibility = View.GONE;
        }
        selectedIndicator.setVisibility(visibility);
    }

    public int getCategory() {
        return category;
    }

    public void setCategoryAndTopic(int category, TopicInfo topic) {
        this.category = category;
        this.topic = topic;

        TextView title = (TextView) findViewById(R.id.topic_text);
        title.setText(getTitleText());
        title.setTextColor(getTitleColor());
    }

    public TopicInfo getTopic() {
        return topic;
    }

    public void adjustTitleColorForBlackBackground() {
        TextView title = (TextView) findViewById(R.id.topic_text);
        boolean isTextBlack = (title.getTextColors().getDefaultColor() == getResources().getColor(R.color.radio_black));
        if (isTextBlack) {
            title.setTextColor(getResources().getColor(R.color.radio_white)); // White text
        }
    }

    private String getTitleText() {
        Resources r = getResources();
        switch (category) {
            case CATEGORY_RECOMMENDED:
                return r.getString(R.string.recommended);
            case CATEGORY_ALL_BY_NAME:
                return r.getString(R.string.alphabetically);
            case CATEGORY_ALL_BY_TOPIC:
                return r.getString(R.string.genre);
            case CATEGORY_TOPIC_BY_NAME:
                if (topic != null) {
                    return topic.getTopicText();
                } else {
                    return "Topic";
                }
            case CATEGORY_INACTIVE_BY_NAME:
                return r.getString(R.string.old_programs);
            default:
                return "Button"; // Return something
        }
    }

    private int getTitleColor() {
        Resources r = getResources();
        switch (category) {
            case CATEGORY_RECOMMENDED:
                return r.getColor(R.color.radio_red);
            case CATEGORY_ALL_BY_NAME:
                return r.getColor(R.color.radio_black);
            case CATEGORY_ALL_BY_TOPIC:
                return r.getColor(R.color.radio_black);
            case CATEGORY_TOPIC_BY_NAME:
                if (topic != null) {
                    return topic.getColorValue();
                } else {
                    return r.getColor(R.color.radio_black);
                }
            case CATEGORY_INACTIVE_BY_NAME:
                return r.getColor(R.color.radio_black);
            default:
                return r.getColor(R.color.radio_black); // Return something
        }
    }
}
