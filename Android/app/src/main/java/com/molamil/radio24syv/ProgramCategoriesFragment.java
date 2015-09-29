package com.molamil.radio24syv;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.molamil.radio24syv.storage.Storage;
import com.molamil.radio24syv.storage.model.ProgramInfo;
import com.molamil.radio24syv.storage.model.TopicInfo;
import com.molamil.radio24syv.view.ProgramCategoryButton;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramCategoriesFragment extends PageFragment {

    private OnFragmentInteractionListener listener;
    private int selectedCategory;
    private String selectedTopicId;
    private ArrayList<ProgramCategoryButton> buttons = new ArrayList<>();

    public static ProgramCategoriesFragment newInstance(int selectedCategory, String selectedTopicId) {
        ProgramCategoriesFragment fragment = new ProgramCategoriesFragment();
        Bundle args = new Bundle();
        args.putInt(ProgramListFragment.ARGUMENT_CATEGORY, selectedCategory);
        args.putString(ProgramListFragment.ARGUMENT_TOPIC_ID, selectedTopicId);
        fragment.setArguments(args);
        return fragment;
    }

    public ProgramCategoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null) {
            selectedCategory = arguments.getInt(ProgramListFragment.ARGUMENT_CATEGORY);
            selectedTopicId = arguments.getString(ProgramListFragment.ARGUMENT_TOPIC_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_program_categories, container, false);

        int blackColor = getResources().getColor(R.color.radio_black);
        int redColor = getResources().getColor(R.color.radio_red);

        ViewGroup contentTop = (ViewGroup) v.findViewById(R.id.content_top);
        addButton(inflater, contentTop, R.string.recommended, redColor, null, ProgramCategoryButton.CATEGORY_RECOMMENDED);
        addButton(inflater, contentTop, R.string.alphabetically, blackColor, null, ProgramCategoryButton.CATEGORY_ALL_BY_NAME);
        addButton(inflater, contentTop, R.string.genre, blackColor, null, ProgramCategoryButton.CATEGORY_ALL_BY_TOPIC);

        // TODO load topics in parent fragment, when complete - update and add topic buttons here (show "indlæser..." in the meantime)
        ViewGroup contentMiddle = (ViewGroup) v.findViewById(R.id.content_middle);
        for (TopicInfo t : Storage.get().getTopics()) {
            addButton(inflater, contentMiddle, t.getTopicText(), t.getColorValue(), t.getTopicId(), ProgramCategoryButton.CATEGORY_TOPIC_BY_NAME);
        }

        ViewGroup contentBottom = (ViewGroup) v.findViewById(R.id.content_bottom);
        addButton(inflater, contentBottom, R.string.old_programs, blackColor, null, ProgramCategoryButton.CATEGORY_INACTIVE_BY_NAME);

        updateSelectedButton();

        return v;
    }

//    private View addButton(LayoutInflater inflater, ViewGroup content, int textId, int color, boolean isSelected) {
//        return addButton(inflater, content, getResources().getText(textId).toString(), color, isSelected);
//    }

//    private View addButton(LayoutInflater inflater, ViewGroup content, String text, int color, boolean isSelected) {
//        Log.d("JJJ", "addButton " + text);
//        View button = inflater.inflate(R.layout.view_program_category_button, content, false);
//
//        View selectedIndicator = button.findViewById(R.id.selected_indicator);
//        int backgroundColor;
//        if (isSelected) {
//            backgroundColor = R.color.radio_gray;
//        } else {
//            backgroundColor = R.color.transparent;
//        }
//        selectedIndicator.setBackgroundColor(getResources().getColor(backgroundColor));
//
//        TextView title = (TextView) button.findViewById(R.id.topic_text);
//        title.setText(text);
//        title.setTextColor(color);
//
//        content.addView(button);
//
//        return button;
//    }
    private ProgramCategoryButton addButton(LayoutInflater inflater, ViewGroup content, int textId, int color, String topicId, int category) {
        return addButton(inflater, content, getResources().getText(textId).toString(), color, topicId, category);
    }

    private ProgramCategoryButton addButton(LayoutInflater inflater, ViewGroup content, String text, int color, String topicId, int category) {
        ProgramCategoryButton button = new ProgramCategoryButton(getActivity(), null);
        button.setTitle(text);
        button.setColor(color);
        button.setTopicId(topicId);
        button.setCategory(category);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    ProgramCategoryButton button = (ProgramCategoryButton) v;
                    listener.OnProgramCategorySelected(button.getCategory(), button.getTopicId());
                    selectedCategory = button.getCategory();
                    selectedTopicId = button.getTopicId();
                    updateSelectedButton();
                }
            }
        });
        content.addView(button);
        buttons.add(button);
        return button;
    }

    private void updateSelectedButton() {
        for (ProgramCategoryButton b : buttons) {
            boolean isSelected = (selectedCategory == b.getCategory()) && ((selectedTopicId == b.getTopicId()) || (selectedTopicId.equals(b.getTopicId())));
            b.setSelected(isSelected);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ProgramCategoriesFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void showProgramCategory(int selectedProgramCategory, String selectedProgramTopicId) {
        Log.d("JJJ", "show pårogrma cat " + selectedCategory + " " + selectedProgramTopicId);
    }

    public interface OnFragmentInteractionListener {
        void OnProgramCategorySelected(int categoryId, String topicId);
    }
}
