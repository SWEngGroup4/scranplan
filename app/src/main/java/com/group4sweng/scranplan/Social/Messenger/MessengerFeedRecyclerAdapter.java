package com.group4sweng.scranplan.Social.Messenger;

import android.view.View;

import com.group4sweng.scranplan.Social.FeedFragment;
import com.group4sweng.scranplan.Social.FeedRecyclerAdapter;
import com.group4sweng.scranplan.UserInfo.UserInfoPrivate;

import java.util.List;

public class MessengerFeedRecyclerAdapter extends FeedRecyclerAdapter {
    /**
     * Constructor to add all variables
     *
     * @param feedFragment
     * @param dataset
     * @param user
     * @param view
     */
    public MessengerFeedRecyclerAdapter(FeedFragment feedFragment, List<FeedPostPreviewData> dataset, UserInfoPrivate user, View view) {
        super(feedFragment, dataset, user, view);
    }
}
