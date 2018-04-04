package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.keys.FaqItemKeys;
import com.macbitsgoa.ard.models.FaqItem;
import com.macbitsgoa.ard.utils.AHC;

import java.util.Date;

import io.realm.Realm;

/**
 * Service to download faq items.
 *
 * @author Vikramadtiya Kukreja
 */

public class ForumService extends BaseIntentService {

    /**
     * Tag for this class.
     */
    public static final String TAG = ForumService.class.getSimpleName();

    public ForumService() {
        super(TAG);
    }

    DatabaseReference forumRef;

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        super.onHandleIntent(intent);
        setIntentRedelivery(true);
        forumRef = getRootReference().child(AHC.FDR_FORUM);
        forumRef.addValueEventListener(getForumVEL());
    }

    /**
     * Syncs firebase db with local realm db.
     *
     * @return ValueEventListener
     */
    private ValueEventListener getForumVEL() {
        return new ValueEventListener() {
            private Realm database;

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                //faq children
                if (dataSnapshot == null) {
                    AHC.logd(TAG, "No faq data at all");
                    return;
                }
                showDebugToast("Updating forum content");
                database = Realm.getDefaultInstance();
                faqParse(dataSnapshot.child(FaqItemKeys.FDR_FAQ));
                database.close();
                forumRef.removeEventListener(this);
            }

            private void faqParse(final DataSnapshot faqSnapshot) {
                if (faqSnapshot == null) {
                    AHC.logd(TAG, "Faqs were null");
                    return;
                }
                AHC.logd(TAG, "Total faqs on firebase = " + faqSnapshot.getChildrenCount());
                for (final DataSnapshot child : faqSnapshot.getChildren()) {
                    final String key = child.getKey();
                    final Date updateDate = child.child(FaqItemKeys.UPDATE).getValue(Date.class);
                    database.executeTransaction(r -> {
                        FaqItem fi = r.where(FaqItem.class)
                                .equalTo(FaqItemKeys.KEY, key).findFirst();
                        if (fi == null) {
                            fi = r.createObject(FaqItem.class, key);
                        } else if (fi.getUpdateDate().getTime() == updateDate.getTime()) {
                            return;
                        }
                        AHC.logd(TAG, "Creating/Updating faq " + key);
                        fi.setQuestion(child.child(FaqItemKeys.QUES).getValue(String.class));
                        fi.setAnswer(child.child(FaqItemKeys.ANS).getValue(String.class));
                        fi.setAuthor(child.child(FaqItemKeys.AUTHOR).getValue(String.class));
                        fi.setSection(child.child(FaqItemKeys.SECTION).getValue(String.class));
                        fi.setDesc(child.child(FaqItemKeys.DESC).getValue(String.class));
                        fi.setOriginalDate(child.child(FaqItemKeys.ORIGINAL).getValue(Date.class));
                        fi.setUpdateDate(child.child(FaqItemKeys.UPDATE).getValue(Date.class));
                        fi.setSubSection(child.child(FaqItemKeys.SUB_SECTION).getValue(String.class));
                        AHC.logd(TAG, "Sub section is " + fi.getSubSection());
                    });
                }
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {
                Log.e(TAG, "Database read error for forum node\n" + databaseError.toString());
                forumRef.removeEventListener(this);
            }
        };
    }
}
