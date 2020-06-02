package com.google.android.gms.samples.vision.barcodereader;

import android.view.View;

public interface SeeQuestionListener {
    void onAddQButtonClicked(Question q);

    void onViewClicked(Question q);

    void onViewLongClicked(View view, Question q);
}
