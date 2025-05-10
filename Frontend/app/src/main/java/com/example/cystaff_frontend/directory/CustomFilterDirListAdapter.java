package com.example.cystaff_frontend.directory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.cystaff_frontend.R;

import java.util.HashMap;
import java.util.List;

/**
 * Describes an adapter for filtering items in the directory given a custom filter.
 */
public class CustomFilterDirListAdapter extends BaseExpandableListAdapter {
    // Define context
    private Context context;

    // Create an array list for mapping
    private List<String> titleList;

    // Hashmap for child information
    private HashMap<String, List<String>> detailedList;

    // Hashmap for checkbox check states
    private HashMap<Integer, boolean[]> childCheckStates;

    // Classes defined in this class for easier use of child and group views
    private ChildViewHolder childViewHolder;
    private GroupViewHolder groupViewHolder;

    /**
     * The constructor - Initializes variables
     *
     * @param cont         - The current context
     * @param titleList    - The list of titles
     * @param detailedList - The list of children
     */
    public CustomFilterDirListAdapter(Context cont, List<String> titleList, HashMap<String, List<String>> detailedList) {
        // Instantiate class variables
        context = cont;
        this.titleList = titleList;
        this.detailedList = detailedList;
        childCheckStates = new HashMap<Integer, boolean[]>();
    }

    @Override
    public int getGroupCount() {
        return titleList.size();
    }

    @Override
    public int getChildrenCount(int titlePos) {
        return detailedList.get(titleList.get(titlePos)).size();
    }

    @Override
    public Object getGroup(int titlePos) {
        return titleList.get(titlePos);
    }

    /**
     * Used to get data for a child in a group
     *
     * @param titlePos the position of the group that the child resides in
     * @param childPos the position of the child with respect to other
     *            children in the group
     * @return the list of data for the given child
     */
    @Override
    public Object getChild(int titlePos, int childPos) {
        return detailedList.get(titleList.get(titlePos)).get(childPos);
    }

    @Override
    public long getGroupId(int titlePos) {
        return titlePos;
    }

    @Override
    public long getChildId(int titlePos, int childPos) {
        return childPos;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Obtains the current groups view
     *
     * @param titlePos   - The position of the title
     * @param isExpanded - Whether or not the group is expanded
     * @param view       - The current view
     * @param parent     - The parent view
     * @return the Group's View
     */
    @Override
    public View getGroupView(int titlePos, boolean isExpanded, View view, ViewGroup parent) {
        // Get the title name
        String groupText = (String) getGroup(titlePos);

        // If the state of the view is null, assign it the necessary values, else, just get the tag
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.directory_filter_header, null);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.groupText = (TextView) view.findViewById(R.id.listHeader);
            view.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) view.getTag();
        }

        // Set the text of the group
        groupViewHolder.groupText.setText(groupText);
        return view;
    }

    /**
     * Obtains the child's view. This allows for review cycling to be done and check marks kept track of
     * no matter how much scrolling, going in out of the pop up, etc. is done
     *
     * @param titlePos    - The title/group position
     * @param childPos    - The child position within the title/group
     * @param isLastChild - Whether or not it is the last child in the group
     * @param view        - The current view
     * @param parent      - The parent view
     * @return - Returns the child's view
     */
    @Override
    public View getChildView(int titlePos, int childPos, boolean isLastChild, View view, ViewGroup parent) {
        // Get child text
        String childText = (String) getChild(titlePos, childPos);

        // If the view is null, assign it the necessary values, else, just get the tag
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.directory_filter_child, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.childText = (TextView) view.findViewById(R.id.dirFilterName);
            childViewHolder.checkBox = (CheckBox) view.findViewById(R.id.dirFilterCheckBox);
            view.setTag(R.layout.directory_filter_child, childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) view.getTag(R.layout.directory_filter_child);
        }

        // Set the text of the child
        childViewHolder.childText.setText(childText);

        // Set the onCheckedChangeListener null to avoid bugs
        childViewHolder.checkBox.setOnCheckedChangeListener(null);

        // Check if the hash map already has the title position
        // If so, set the check state accordingly, if not, set it to false
        if (childCheckStates.containsKey(titlePos)) {
            boolean[] getChecked = childCheckStates.get(titlePos);
            if (getChecked[childPos]) {
                childViewHolder.checkBox.setButtonDrawable(R.drawable.baseline_check_box_24);
            } else {
                childViewHolder.checkBox.setButtonDrawable(R.drawable.baseline_check_box_outline_blank_24);
            }
        } else {
            boolean[] getChecked = new boolean[getChildrenCount(titlePos)];
            childCheckStates.put(titlePos, getChecked);
            childViewHolder.checkBox.setButtonDrawable(R.drawable.baseline_check_box_outline_blank_24);
        }
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    /**
     * Used for changing a child's check box. Using this allows keeping track of what has been and hasn't been tracked
     *
     * @param titlePos   - The current position of the title/group
     * @param childPos   - The current position of the child within the title
     * @param view       - The current view
     * @param checkOrNot - Whether or not the check box should be checked
     */
    public void setChildCheckBox(int titlePos, int childPos, View view, boolean checkOrNot) {
        // If the view is null, assign it the necessary values, else, just get the tag
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.directory_filter_child, null);
            childViewHolder = new ChildViewHolder();
            childViewHolder.childText = (TextView) view.findViewById(R.id.dirFilterName);
            childViewHolder.checkBox = (CheckBox) view.findViewById(R.id.dirFilterCheckBox);
            view.setTag(R.layout.directory_filter_child, childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) view.getTag(R.layout.directory_filter_child);
        }

        // Set the drawable and hashmap values according to checkOrNot
        if (checkOrNot) {
            childViewHolder.checkBox.setButtonDrawable(R.drawable.baseline_check_box_24);
        } else {
            childViewHolder.checkBox.setButtonDrawable(R.drawable.baseline_check_box_outline_blank_24);
        }

        // Update the check status hashmap
        boolean[] getChecked = childCheckStates.get(titlePos);
        getChecked[childPos] = checkOrNot;
        childCheckStates.put(titlePos, getChecked);
    }

    /**
     * Define the group view holder
     * It is for simplicity and neatness of code
     */
    public final class GroupViewHolder {
        TextView groupText;
    }

    /**
     * Define the child view holder
     * It is for simplicity and neatness of code
     */
    public final class ChildViewHolder {
        TextView childText;
        CheckBox checkBox;
    }
}
