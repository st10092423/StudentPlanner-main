package com.studentplanner.studentplanner.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public final class EmptyData {
    private final ImageView emptyImage;
    private final TextView emptyText;

    public EmptyData(ImageView emptyImage, TextView emptyText) {
        this.emptyImage = emptyImage;
        this.emptyText = emptyText;
    }

    public void emptyResultStatus(final boolean showEmptyResults) {
        int visibilityStatus = showEmptyResults ? View.VISIBLE: View.GONE;
        emptyImage.setVisibility(visibilityStatus);
        emptyText.setVisibility(visibilityStatus);
    }
}
