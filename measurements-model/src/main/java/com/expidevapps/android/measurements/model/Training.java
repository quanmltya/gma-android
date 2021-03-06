package com.expidevapps.android.measurements.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Training extends Location implements Cloneable {
    public static final long INVALID_ID = -1;

    public static final String ARG_ID = "training_id";

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_MINISTRY_ID = "ministry_id";
    private static final String JSON_TYPE = "type";
    private static final String JSON_DATE = "date";
    private static final String JSON_MCC = "mcc";
    private static final String JSON_PARTICIPANTS = "participants";
    private static final String JSON_COMPLETIONS = "gcm_training_completions";
    private static final String JSON_CREATED_BY = "created_by";

    public static final String TRAINING_TYPE_MC2 = "MC2";
    public static final String TRAINING_TYPE_T4T = "T4T";
    public static final String TRAINING_TYPE_CPMI = "CPMI";
    public static final String TRAINING_TYPE_OTHER = "Other";

    private long id;
    @NonNull
    private String ministryId = Ministry.INVALID_ID;
    private String name;
    @Nullable
    private LocalDate date;
    private String type;
    @NonNull
    private Ministry.Mcc mcc = Ministry.Mcc.UNKNOWN;
    @Nullable
    private String createdBy;
    @NonNull
    private final List<Completion> completions = new ArrayList<>();

    private int mParticipants = 0;

    public Training()
    {       
    }
    
    private Training(@NonNull final Training training)
    {
        super(training);
        this.id = training.id;
        this.ministryId = training.ministryId;
        this.name = training.name;
        this.date = training.date;
        this.type = training.type;
        this.mcc = training.mcc;
        this.createdBy = training.createdBy;
        this.setCompletions(training.completions);
        mParticipants = training.mParticipants;
        mDirty.clear();
        mDirty.addAll(training.mDirty);
        mTrackingChanges = training.mTrackingChanges;
    }

    @NonNull
    public static List<Training> listFromJson(@NonNull final JSONArray json) throws JSONException {
        final List<Training> trainings = new ArrayList<>();
        for (int i = 0; i < json.length(); i++) {
            trainings.add(fromJson(json.getJSONObject(i)));
        }
        return trainings;
    }

    @NonNull
    public static Training fromJson(@NonNull final JSONObject json) throws JSONException {
        final Training training = new Training();

        training.id = json.getLong(JSON_ID);
        training.ministryId = json.getString(JSON_MINISTRY_ID);
        training.name = json.getString(JSON_NAME);
        training.type = json.getString(JSON_TYPE);
        training.mcc = Ministry.Mcc.fromJson(json.getString(JSON_MCC));
        training.date = LocalDate.parse(json.getString(JSON_DATE));
        training.createdBy = json.getString(JSON_CREATED_BY);
        training.setLatitude(json.optDouble(JSON_LATITUDE, Double.NaN));
        training.setLongitude(json.optDouble(JSON_LONGITUDE, Double.NaN));

        final JSONArray completions = json.optJSONArray(JSON_COMPLETIONS);
        if (completions != null) {
            training.setCompletions(Completion.listFromJson(training.id, completions));
        }

        return training;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    @NonNull
    public String getMinistryId()
    {
        return ministryId;
    }

    public void setMinistryId(@NonNull final String ministryId) {
        this.ministryId = ministryId;
    }

    @Nullable
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
        if (mTrackingChanges)
        {
            mDirty.add(JSON_NAME);
        }
    }

    @Nullable
    public LocalDate getDate() {
        return date;
    }

    public void setDate(@Nullable final LocalDate date) {
        this.date = date;
        if (mTrackingChanges)
        {
            mDirty.add(JSON_DATE);
        }
    }

    @Nullable
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
        if (mTrackingChanges)
        {
            mDirty.add(JSON_TYPE);
        }
    }

    public Ministry.Mcc getMcc() {
        return mcc;
    }

    public void setMcc(@Nullable final String mcc) {
        setMcc(Ministry.Mcc.fromRaw(mcc));
    }

    public void setMcc(@NonNull final Ministry.Mcc mcc) {
        this.mcc = mcc;
    }

    @Nullable
    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(@Nullable String createdBy) {
        this.createdBy = createdBy;
    }

    public int getParticipants() {
        return mParticipants;
    }

    public void setParticipants(final int participants) {
        this.mParticipants = participants;
    }

    @Override
    public boolean canEdit(@Nullable final Assignment assignment) {
        return assignment != null && assignment.can(Task.EDIT_TRAINING, this);
    }

    @NonNull
    public List<Completion> getCompletions() {
        return Collections.unmodifiableList(completions);
    }

    public void setCompletions(@Nullable final List<Completion> completions) {
        this.completions.clear();
        if (completions != null) {
            this.completions.addAll(completions);
        }
    }

    public void addCompletion(@NonNull final Completion completion) {
        this.completions.add(completion);
    }

    @Override
    public Training clone()
    {
        return new Training(this);
    }
    
    public JSONObject toJson() throws JSONException
    {
        final JSONObject json = super.toJson();
        json.put(JSON_NAME, this.getName());
        json.put(JSON_MINISTRY_ID, this.getMinistryId());
        json.put(JSON_DATE, this.getDate());
        json.put(JSON_TYPE, this.getType());
        json.put(JSON_MCC, mcc.raw);
        json.put(JSON_PARTICIPANTS, this.getParticipants());
        return json;
    }

    public static class Completion extends Base {
        public static final long INVALID_ID = -1;

        private static final String JSON_ID = "id";
        private static final String JSON_TRAINING_ID = "training_id";
        private static final String JSON_PHASE = "phase";
        private static final String JSON_NUMBER_COMPLETED = "number_completed";
        private static final String JSON_DATE = "date";

        private long id;
        private long trainingId = Training.INVALID_ID;
        private int phase;
        private int numberCompleted;
        @Nullable
        private LocalDate date = LocalDate.now();

        public Completion()
        {
        }

        private Completion(@NonNull final Completion completion)
        {
            super(completion);
            this.id = completion.id;
            this.trainingId = completion.trainingId;
            this.phase = completion.phase;
            this.numberCompleted = completion.numberCompleted;
            this.date = completion.date;
        }

        @NonNull
        public static List<Completion> listFromJson(final long trainingId, @NonNull final JSONArray json)
                throws JSONException {
            final List<Completion> completions = new ArrayList<>();
            for (int i = 0; i < json.length(); i++) {
                completions.add(fromJson(trainingId, json.getJSONObject(i)));
            }
            return completions;
        }

        @NonNull
        public static Completion fromJson(final long trainingId, @NonNull final JSONObject json) throws JSONException {
            final Completion completion = new Completion();

            completion.id = json.getLong(JSON_ID);
            completion.trainingId = trainingId;
            completion.phase = json.getInt(JSON_PHASE);
            completion.numberCompleted = json.getInt(JSON_NUMBER_COMPLETED);
            completion.date = LocalDate.parse(json.getString(JSON_DATE));

            return completion;
        }

        public long getId()
        {
            return id;
        }

        public void setId(long id)
        {
            this.id = id;
        }

        public int getPhase()
        {
            return phase;
        }

        public void setPhase(int phase)
        {
            this.phase = phase;
            if (mTrackingChanges)
            {
                mDirty.add(JSON_PHASE);
            }
        }

        public int getNumberCompleted()
        {
            return numberCompleted;
        }

        public void setNumberCompleted(int numberCompleted)
        {
            this.numberCompleted = numberCompleted;
            if (mTrackingChanges)
            {
                mDirty.add(JSON_NUMBER_COMPLETED);
            }
        }

        @Nullable
        public LocalDate getDate() {
            return date;
        }

        public void setDate(@Nullable final LocalDate date) {
            this.date = date;
            if (mTrackingChanges)
            {
                mDirty.add(JSON_DATE);
            }
        }

        public long getTrainingId() {
            return trainingId;
        }

        public void setTrainingId(final long trainingId) {
            this.trainingId = trainingId;
            if (mTrackingChanges)
            {
                mDirty.add(JSON_TRAINING_ID);
            }
        }

        @Override
        public Completion clone()
        {
            return new Completion(this);
        }

        public JSONObject toJson() throws JSONException
        {
            final JSONObject json = super.toJson();
            json.put(JSON_TRAINING_ID, this.getTrainingId());
            json.put(JSON_PHASE, this.getPhase());
            json.put(JSON_DATE, this.getDate());
            json.put(JSON_NUMBER_COMPLETED, this.getNumberCompleted());
            return json;
        }
    }
}
