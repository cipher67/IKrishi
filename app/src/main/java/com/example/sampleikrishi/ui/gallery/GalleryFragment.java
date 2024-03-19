package com.example.sampleikrishi.ui.gallery;

import android.os.Bundle;
import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import android.view.inputmethod.InputMethodManager;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sampleikrishi.R;
import com.example.sampleikrishi.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    EditText noOfLabour, labourFee, equipmentName, equipmentRates;
    TextView totalResult;
    Spinner spinnerFertilizerQuantity, spinnerNumberOfFertilizer, spinnerSeedQuantity, spinnerNumberOfSeed;
    Button buttonCalculate;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        noOfLabour = root.findViewById(R.id.noOfLabour);
        labourFee = root.findViewById(R.id.labourFee);
        equipmentName = root.findViewById(R.id.equipmentName);
        equipmentRates = root.findViewById(R.id.equipmentRates);
        totalResult = root.findViewById(R.id.totalResult);
        spinnerFertilizerQuantity = root.findViewById(R.id.fertilizerSpinnerQuantity);
        spinnerNumberOfFertilizer = root.findViewById(R.id.numberOfFertilizerSpinner);
        spinnerSeedQuantity = root.findViewById(R.id.seedSpinnerQuantity);
        spinnerNumberOfSeed = root.findViewById(R.id.numberOfSeedSpinner);
        buttonCalculate = root.findViewById(R.id.buttonCalculate);

        ArrayAdapter<CharSequence> quantityAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.quantity_array_fertilizer, android.R.layout.simple_spinner_item);
        quantityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFertilizerQuantity.setAdapter(quantityAdapter);

        ArrayAdapter<CharSequence> quantityAdapterSeed = ArrayAdapter.createFromResource(getContext(),
                R.array.quantity_array_seed, android.R.layout.simple_spinner_item);
        quantityAdapterSeed.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSeedQuantity.setAdapter(quantityAdapterSeed);

        ArrayAdapter<CharSequence> numbersAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.numbers_1_to_5, android.R.layout.simple_spinner_item);
        numbersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumberOfFertilizer.setAdapter(numbersAdapter);

        ArrayAdapter<CharSequence> numbersAdapterSeed = ArrayAdapter.createFromResource(getContext(),
                R.array.numbers_1_to_5, android.R.layout.simple_spinner_item);
        numbersAdapterSeed.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNumberOfSeed.setAdapter(numbersAdapterSeed);

        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateTotalExpense();
                hideKeyboard();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void hideKeyboard() {
        View view = requireActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void calculateTotalExpense() {
        try {
            // Calculating Fertilizer Price
            String selectedFertilizerQuantity = spinnerFertilizerQuantity.getSelectedItem().toString();
            double fertilizerQuantityPrice = 0.0;
            switch (selectedFertilizerQuantity) {
                case "None":
                    fertilizerQuantityPrice = 0.0;
                    break;
                case "Packets: 500.0/packet":
                    fertilizerQuantityPrice = 500.0;
                    break;
                case "Kilogram(s): 200.0/kilo":
                    fertilizerQuantityPrice = 200.0;
                    break;
                case "Litre(s): 600.0/litre":
                    fertilizerQuantityPrice = 600.0;
                    break;
            }

            int numberOfFertilizer = Integer.parseInt(spinnerNumberOfFertilizer.getSelectedItem().toString());
            double labourPeople = Double.parseDouble(noOfLabour.getText().toString());
            double labourFees = Double.parseDouble(labourFee.getText().toString());
            double equipmentRate = Double.parseDouble(equipmentRates.getText().toString());

            double fertilizerPrice = fertilizerQuantityPrice * numberOfFertilizer;
            double labour = labourPeople * labourFees;

            // Calculating Seed Price
            String selectedSeedQuantity = spinnerSeedQuantity.getSelectedItem().toString();
            double seedQuantityPrice = 0.0;
            switch (selectedSeedQuantity) {
                case "None":
                    seedQuantityPrice = 0.0;
                    break;
                case "Packets: 500.0/packet":
                    seedQuantityPrice = 500.0;
                    break;
                case "Kilogram(s): 200.0/kilo":
                    seedQuantityPrice = 200.0;
                    break;
            }

            int numberOfSeeds = Integer.parseInt(spinnerNumberOfSeed.getSelectedItem().toString());
            double seedRate = seedQuantityPrice * numberOfSeeds;

            double totalExpense = fertilizerPrice + labour + equipmentRate + seedRate;

            // Display breakdown in the totalResult TextView
            String breakdown = "Seed Rate: Rs. " + seedRate + "\n"
                    + "Fertilizer Price: Rs. " + fertilizerPrice + "\n"
                    + "Labour Cost: Rs. " + labour + "\n"
                    + "Equipment Rate: Rs. " + equipmentRate;

            totalResult.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);

            totalResult.setText(breakdown + "\n______________________\n"+ "Total Expense: Rs. " + totalExpense);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Please enter valid numbers", Toast.LENGTH_SHORT).show();
        }
    }
}