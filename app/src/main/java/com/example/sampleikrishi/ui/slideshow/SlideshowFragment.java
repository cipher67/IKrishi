package com.example.sampleikrishi.ui.slideshow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.example.sampleikrishi.R;
import com.example.sampleikrishi.databinding.FragmentSlideshowBinding;

import java.util.Timer;
import java.util.TimerTask;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    private ViewPager viewPager;
    private ImageSliderAdapter imageSliderAdapter;
    private int[] images = {R.drawable.image1, R.drawable.image2, R.drawable.image3, R.drawable.image4, R.drawable.image5};
    private String[] urls = {
            "https://maandhan.in/",
            "https://maandhan.in/",
            "https://pmkisan.gov.in/",
            "https://pmkisan.gov.in/",
            "https://agribegri.com/"
    };
    private int currentPage = 0;
    private Timer timer;
    private final long DELAY_MS = 3000; // Delay in milliseconds before task is to be executed
    private final long PERIOD_MS = 3000; // Time in milliseconds between successive task executions.

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Find the phone number buttons
        Button phone1Button = root.findViewById(R.id.phone1_button);
        Button phone2Button = root.findViewById(R.id.phone2_button);
        Button phone3Button = root.findViewById(R.id.phone3_button);
        Button phone4Button = root.findViewById(R.id.phone4_button);
        Button phone5Button = root.findViewById(R.id.phone5_button);

        viewPager = root.findViewById(R.id.imageSlider);
        imageSliderAdapter = new ImageSliderAdapter(getContext(), images, urls);
        viewPager.setAdapter(imageSliderAdapter);

        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable update = new Runnable() {
            public void run() {
                if (currentPage == images.length) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer.schedule(new TimerTask() { // task to be scheduled
            @Override
            public void run() {
                handler.post(update);
            }
        }, DELAY_MS, PERIOD_MS);

        // If you want to stop the auto-scrolling when user interacts with the ViewPager
        viewPager.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case android.view.MotionEvent.ACTION_DOWN:
                case android.view.MotionEvent.ACTION_MOVE:
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    break;
                case android.view.MotionEvent.ACTION_UP:
                    if (timer == null) {
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                handler.post(update);
                            }
                        }, DELAY_MS, PERIOD_MS);
                    }
                    break;
            }
            return false;
        });

        // Set click listeners for the phone number buttons
        phone1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "9012345678";
                initiatePhoneCall(phoneNumber);
            }
        });

        phone2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "1234567890";
                initiatePhoneCall(phoneNumber);
            }
        });

        phone3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "101";
                initiatePhoneCall(phoneNumber);
            }
        });

        phone4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "9807654321";
                initiatePhoneCall(phoneNumber);
            }
        });

        phone5Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "102";
                initiatePhoneCall(phoneNumber);
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void initiatePhoneCall(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }
}