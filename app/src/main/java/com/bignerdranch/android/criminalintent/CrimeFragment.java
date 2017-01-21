package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String ARG_HOLDER_POSITION = "holder_position";
    private static final String EXTRA_CHANGED_CRIME_HOLDER_POSITIONS =
            "com.bignerdranch.android.criminalintent.changed_crime_holder_positions";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_CONTACT = 1;


    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    public static CrimeFragment newInstance(UUID crimeId, int holderPosition) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        args.putInt(ARG_HOLDER_POSITION, holderPosition);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.get(getActivity())
                .updateCrime(mCrime);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
                setCrimeChanged(true);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Intentionally left blank
            }
        });

        mDateButton = (Button)v.findViewById(R.id.crime_date);
        DateFormat df = new DateFormat();
        Date date = mCrime.getDate();
        String formatString = "EEEE, MMM dd, yyyy";
        mDateButton.setText(df.format(formatString, date));
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());

                // Sets the return target to be this crime fragment
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Set the crime's solved property
                mCrime.setSolved(isChecked);
                setCrimeChanged(true);
            }
        });

        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND); // Define the action
                i.setType("text/plain"); // set the type to text/plain
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport()); // add the text
                // add the subject
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));

                // new intent for a chooser for a target intent
                // intent param is the intent
                // string param sets the text on the chooser
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        /* ACTION_PICK returns a selected item from data
        ContactsContract defines the database for contact related info
        Intent(Action, URI) URI is where the data is
        CONTENT_URI is the URI for the contacts table */
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        // Dummy category for testing behavior with no suitable activity
        //pickContact.addCategory(Intent.CATEGORY_HOME);

        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        // If assigned, will show suspect's name on button
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        /* Trying to launch a pickContact intent without a chooser and
        * without a suitable activity will crash the app. We want to check
        * first that there is an app that can handle it.
        *
        * PackageManager knows about all the components installed on the
        * device, including activities.
        *
        * Restricting to MATCH_DEFAULT_ONLY means only activities with
        * CATEGORY_DEFAULT, which means the activity should be considered for
        * a job. The pickContact intent is the job. Category Default is
        * automatically set in startActivity(Intent)
        *
        * resolveActivity() returns a ResolveInfo, which tells you about the
        * best activity found, or null if none found. In this case, it means
        * there is no contacts app. */
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);

        return v;
    }

    // Returns the holder position of the changed crime from the intent.
    // Returns -1 if nothing was changed or there was an error.
    public static int getChangedCrimeHolderPosition(Intent result) {
        return result.getIntExtra(EXTRA_CHANGED_CRIME_HOLDER_POSITIONS, -1);
    }

    // Sets the result of the activity to give the parent activity the crime holder position
    private void setCrimeChanged(boolean isCrimeChanged) {
        Intent data = new Intent();
        if (isCrimeChanged) {
            data.putExtra(EXTRA_CHANGED_CRIME_HOLDER_POSITIONS,
                    getArguments().getInt(ARG_HOLDER_POSITION));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data.
                    getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            // Uri points to the contact
            Uri contactUri = data.getData();
            // Specify which fields you want your query to return
            // values for (from the contactUri).
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            // Perform your query - the contactUri is like a "where"
            // clause here
            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);
            // The query returns all display names from the uri

            try {
                // Double-check that you actually got results
                if (c.getCount() == 0) {
                    return;
                }

                // Pull out the first column of the first row of data -
                // that is your suspect's name.
                c.moveToFirst(); // should only be one result anyway
                String suspect = c.getString(0); // column index 0, only one
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);
            } finally {
                c.close();
            }
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        // format: [day of week abbrev], [Month in year abbrev] [day in month]
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
}
