package com.macbitsgoa.ard.services;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.macbitsgoa.ard.keys.FaqItemKeys;
import com.macbitsgoa.ard.models.FaqItem;
import com.macbitsgoa.ard.models.FaqSectionItem;
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
                faqSectionParse(dataSnapshot.child(FaqItemKeys.FDR_FAQ_SECTION));
                faqParse(dataSnapshot.child(FaqItemKeys.FDR_FAQ));
                database.close();
            }

            private void faqSectionParse(final DataSnapshot faqSectionShot) {
                if (faqSectionShot == null) {
                    AHC.logd(TAG, "Faq sections data null");
                    return;
                }
                //Always get latest faq sections
                database.executeTransaction(r -> r.delete(FaqSectionItem.class));
                for (final DataSnapshot childShot : faqSectionShot.getChildren()) {
                    database.executeTransaction(r -> {
                        FaqSectionItem fsi = r.where(FaqSectionItem.class)
                                .equalTo(FaqItemKeys.DB_FAQ_SECTION_KEY, childShot.getKey())
                                .findFirst();
                        if (fsi == null) {
                            fsi = r.createObject(FaqSectionItem.class, childShot.getKey());
                        }
                        fsi.setSectionTitle(childShot
                                .child(FaqItemKeys.FDR_FAQ_SECTION_TITLE)
                                .getValue(String.class));
                        fsi.setSectionPriority(childShot
                                .child(FaqItemKeys.FDR_FAQ_SECTION_PRIORITY)
                                .getValue(String.class));
                    });
                }
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
