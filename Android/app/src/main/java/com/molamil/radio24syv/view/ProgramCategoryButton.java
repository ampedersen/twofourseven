package com.molamil.radio24syv.view;

import android.content.Context;
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
    private String topicId;

    public ProgramCategoryButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        this.setOrientation(LinearLayout.HORIZONTAL);

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_program_category_button, this);

        // Attributes are null when instantiated from code
        if (attrs != null) {
            // Apply attributes from XML
            TypedArray a = context.getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.ProgramCategoryButton,
                    0, 0);

            try {
                setSelected(a.getBoolean(R.styleable.ProgramCategoryButton_selected, false));
                setCategory(a.getInteger(R.styleable.ProgramCategoryButton_category, CATEGORY_RECOMMENDED));
                setTopicId(a.getString(R.styleable.ProgramCategoryButton_topic_id));
            } finally {
                a.recycle();
            }
        }
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

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public void setTitle(String text) {
        TextView title = (TextView) findViewById(R.id.topic_text);
        title.setText(text);
    }

    public void setColor(int color) {
        TextView title = (TextView) findViewById(R.id.topic_text);
        title.setTextColor(color);
    }
}
