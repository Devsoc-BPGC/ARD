package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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

    public static final String TAG = ForumService.class.getSimpleName();

    public ForumService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        super.onHandleIntent(intent);
        getRootReference()
                .child(AHC.FDR_FORUM)
                .addListenerForSingleValueEvent(getForumVEL());
    }

    /**
     * Syncs firebase db with local realm db.
     *
     * @return ValueEventListener
     */
    public ValueEventListener getForumVEL() {
        return new ValueEventListener() {
            private Realm database;

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                //faq children
                if (dataSnapshot == null) {
                    AHC.logd(TAG, "No faq data at all");
                    return;
                }
                database = Realm.getDefaultInstance();
                faqParse(dataSnapshot.child(FaqItemKeys.FDR_FAQ));
                database.close();
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
                    if (updateDate == null) continue;
                    AHC.logd(TAG, "Faq last update " + updateDate);
                    database.executeTransaction(r -> {
                        FaqItem fi = r.where(FaqItem.class)
                                .equalTo(FaqItemKeys.KEY, key).findFirst();
                        if (fi == null) {
                            fi = r.createObject(FaqItem.class, key);
                            AHC.logd(TAG, "Creating new faq");
                        } else if (fi.getUpdateDate().getTime() == updateDate.getTime()) {
                            return;
                        }
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
            }
        };
    }
}
