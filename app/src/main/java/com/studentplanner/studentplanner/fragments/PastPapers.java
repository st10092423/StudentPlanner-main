package com.studentplanner.studentplanner.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.studentplanner.studentplanner.R;
import com.studentplanner.studentplanner.utils.EmptyData;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PastPapers extends Fragment {
    private Spinner gradeSpinner, termSpinner, subjectSpinner, languageSpinner, yearSpinner;
    private RadioGroup paperTypeRadioGroup;
    private RadioButton paperType1, paperType2;
    private Button searchButton;
    private Context context;
    private Activity activity;
    private ListView pdfListView;
    private AmazonS3 s3Client;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pastpapers, container, false);
        initFragment ();

        gradeSpinner = rootView.findViewById(R.id.grade_spinner);
        termSpinner = rootView.findViewById(R.id.term_spinner);
        subjectSpinner = rootView.findViewById(R.id.subject_spinner);
        languageSpinner = rootView.findViewById(R.id.language_spinner);
        yearSpinner = rootView.findViewById(R.id.year_spinner);
        paperTypeRadioGroup = rootView.findViewById(R.id.paper_type_radio_group);
        paperType1 = rootView.findViewById(R.id.paper_type_1);
        paperType2 = rootView.findViewById(R.id.paper_type_2);
        searchButton = rootView.findViewById(R.id.search_button);
        pdfListView = rootView.findViewById(R.id.pdf_listview);
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context, "eu-north-1:867cb15b-c429-4681-a4cc-8507d66f8db1", Regions.US_EAST_1);
        s3Client = new AmazonS3Client(credentialsProvider);




        ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.grades,
                android.R.layout.simple_spinner_item
        );
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);

        ArrayAdapter<CharSequence> termAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.terms,
                android.R.layout.simple_spinner_item
        );
        termAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        termSpinner.setAdapter(termAdapter);

        ArrayAdapter<CharSequence> subjectAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.subjects,
                android.R.layout.simple_spinner_item
        );
        subjectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subjectSpinner.setAdapter(subjectAdapter);

        ArrayAdapter<CharSequence> languageAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.languages,
                android.R.layout.simple_spinner_item
        );
        languageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(languageAdapter);

        ArrayAdapter<CharSequence> yearAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.years,
                android.R.layout.simple_spinner_item
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedGrade = gradeSpinner.getSelectedItem().toString();
                String selectedTerm = termSpinner.getSelectedItem().toString();
                String selectedSubject = subjectSpinner.getSelectedItem().toString();
                String selectedLanguage = languageSpinner.getSelectedItem().toString();
                String selectedYear = yearSpinner.getSelectedItem().toString();
                int selectedPaperType = paperTypeRadioGroup.getCheckedRadioButtonId();
                boolean isPaper1 = selectedPaperType == R.id.paper_type_1;

                String directoryPath = "PastPapers\\" + selectedYear + "\\" + selectedLanguage + "\\"
                        + selectedGrade + "\\" + selectedTerm + "\\" + selectedSubject + "\\P" + (isPaper1 ? "1" : "2") + "\\";

                // Get a list of all files in the directory
                File directory = new File(directoryPath);
                File[] files = directory.listFiles();

                // Check if there are any files in the directory
                if (files != null && files.length > 0) {
                    // Iterate through the files and check if they are PDFs
                    for (File file : files) {
                        if (file.isFile() && file.getName().toLowerCase().endsWith(".pdf")) {
                            // This is a PDF file, you can now implement logic to allow the user to download it
                            Log.d("Found PDF", file.getAbsolutePath());
                        }
                    }
                } else {

                }
            }
        });

        return rootView;
    }

        private void initFragment () {
            context = getContext();
            activity = getActivity();
            activity.setTitle(context.getString(R.string.past_papers));
            setHasOptionsMenu(true);
        }
    private void updatePdfListView(List<String> pdfFiles) {
        ArrayAdapter<String> pdfListAdapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_list_item_1,
                pdfFiles
        );

        pdfListView.setAdapter(pdfListAdapter);

        // Optionally, you can handle item clicks on the ListView
        pdfListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedFile = pdfFiles.get(position);
            }
        });
    }
    private void fetchFilesFromS3() {
        String bucketName = "hacka";
        String prefix = "2022/";

        List<String> pdfFiles = new ArrayList<>();

        ObjectListing objectListing = s3Client.listObjects(bucketName, prefix);

        for (S3ObjectSummary objectSummary : objectListing.getObjectSummaries()) {
            String key = objectSummary.getKey();
            if (key.endsWith(".pdf")) {
                pdfFiles.add(key);
            }
        }


    }
}









