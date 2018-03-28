package com.macbitsgoa.ard.viewholders;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.TextView;

import com.macbitsgoa.ard.R;

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
    TextView answerTV;

    @BindView(R.id.tv_vh_fg_forum_general_sub_section)
    TextView subSectionTV;

    private SparseBooleanArray sba;

    /**
     * Default constructor.
     *
     * @param itemView Nonnull inflated view.
     */
    public FaqViewHolder(@NonNull final View itemView, SparseBooleanArray sba) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.sba = sba;
        sba.put(getAdapterPosition(), false);
        itemView.setOnClickListener(v -> showAnswer());
    }

    private void showAnswer() {
        if (!sba.get(getAdapterPosition())) {
            sba.put(getAdapterPosition(), true);
            answerTV.setVisibility(View.VISIBLE);
            answerTV.setAlpha(1f);
        } else {
            sba.put(getAdapterPosition(), false);
            answerTV.setVisibility(View.GONE);
        }
    }


    public void setQuestionTV(final String question) {
        this.questionTV.setText(question);
    }

    public void hideSubSection() {
        subSectionTV.setVisibility(View.GONE);
    }

    public void setSubSection(final String subSection) {
        if (subSectionTV.getVisibility() != View.VISIBLE)
            subSectionTV.setVisibility(View.VISIBLE);
        subSectionTV.setText(subSection);
    }

    /**
     * Set answer data for faq. Clicks are handled automatically.
     *
     * @param answer Nonnull string to use as answer.
     */
    public void setAnswerTV(@NonNull final String answer) {
        answerTV.setText(Html.fromHtml(answer));
        if (sba.get(getAdapterPosition())) {
            answerTV.setVisibility(View.VISIBLE);
        } else {
            answerTV.setVisibility(View.GONE);
        }

    }
}
