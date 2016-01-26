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

import java.lang.reflect.Field;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProgramCategoriesFragment extends PageFragment {

    private OnFragmentInteractionListener listener;
    private int selectedCategory;
    private TopicInfo selectedTopic;
    private ArrayList<ProgramCategoryButton> buttons = new ArrayList<>();

    public static ProgramCategoriesFragment newInstance(int selectedCategory, TopicInfo selectedTopic) {
        ProgramCategoriesFragment fragment = new ProgramCategoriesFragment();
        Bundle args = new Bundle();
        args.putInt(ProgramListFragment.ARGUMENT_CATEGORY, selectedCategory);
        args.putSerializable(ProgramListFragment.ARGUMENT_TOPIC_ID, selectedTopic);
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
            selectedTopic = (TopicInfo) arguments.getSerializable(ProgramListFragment.ARGUMENT_TOPIC_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_program_categories, container, false);

        int blackColor = getResources().getColor(R.color.radio_black);
        int redColor = getResources().getColor(R.color.radio_red);

        ViewGroup contentTop = (ViewGroup) v.findViewById(R.id.content_top);
        addButton(contentTop, null, ProgramCategoryButton.CATEGORY_RECOMMENDED);
        addButton(contentTop, null, ProgramCategoryButton.CATEGORY_ALL_BY_NAME);
        addButton(contentTop, null, ProgramCategoryButton.CATEGORY_ALL_BY_TOPIC);

        // TODO load topics in parent fragment, when complete - update and add topic buttons here (show "indlæser..." in the meantime)
        ViewGroup contentMiddle = (ViewGroup) v.findViewById(R.id.content_middle);
        for (TopicInfo t : Storage.get().getTopics()) {
            addButton(contentMiddle, t, ProgramCategoryButton.CATEGORY_TOPIC_BY_NAME);
        }

        ViewGroup contentBottom = (ViewGroup) v.findViewById(R.id.content_bottom);
        addButton(contentBottom, null, ProgramCategoryButton.CATEGORY_INACTIVE_BY_NAME);

        updateSelectedButton();

        return v;
    }

    private ProgramCategoryButton addButton(ViewGroup content, TopicInfo topic, int category) {
        ProgramCategoryButton button = new ProgramCategoryButton(getActivity(), null);
        button.setCategoryAndTopic(category, topic);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    ProgramCategoryButton button = (ProgramCategoryButton) v;
                    listener.OnProgramCategorySelected(button.getCategory(), button.getTopic());
                    selectedCategory = button.getCategory();
                    selectedTopic = button.getTopic();
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
            boolean isSelected = (selectedCategory == b.getCategory()) && (((selectedTopic == null) && (b.getTopic() == null)) || (selectedTopic.getTopicId().equals(b.getTopic().getTopicId())));
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

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Radio24syvApp.getInstance().trackScreenView("Podcast Categories Screen");
    }

    public interface OnFragmentInteractionListener {
        void OnProgramCategorySelected(int categoryId, TopicInfo topic);
    }
}
