package com.macbitsgoa.ard.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.ard.R;
import com.macbitsgoa.ard.utils.ExpandableTextView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Viewholder for {@link com.macbitsgoa.ard.models.FaqItem} used in
 * {@link com.macbitsgoa.ard.adapters.ForumAdapter}.
 *
 * @author Vikramaditya Kukreja
 */
public class FaqViewHolder extends RecyclerView.ViewHolder {

    /**
     * TextView to display question data.
     */
    @BindView(R.id.tv_vh_fg_forum_general_question)
    TextView questionTV;

    /**
     * Expandable text view to display answer data.
     */
    @BindView(R.id.tv_vh_fg_forum_general_answer)
    ExpandableTextView answerTV;

    /**
     * Default constructor.
     *
     * @param itemView Nonnull inflated view.
     */
    public FaqViewHolder(@NonNull final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(v -> {
            answerTV.click();
        });
    }


    public void setQuestionTV(final String question) {
        this.questionTV.setText(question);
    }

    /**
     * Set answer data for faq. Clicks are handled automatically.
     *
     * @param answer Nonnull string to use as answer.
     * @param sba    SparseBooleanArray to maintain clicked info.
     */
    public void setAnswerTV(@NonNull final String answer, final SparseBooleanArray sba) {
        answerTV.setText(Html.fromHtml(answer), sba, getAdapterPosition());
    }
}
