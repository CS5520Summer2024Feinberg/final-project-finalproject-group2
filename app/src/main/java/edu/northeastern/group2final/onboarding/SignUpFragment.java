package edu.northeastern.group2final.onboarding;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;

import java.util.Collections;
import java.util.List;

import edu.northeastern.group2final.databinding.FragmentSignUpBinding;


public class SignUpFragment extends Fragment {
    public static final int SIGN_IN_RESULT_CODE = 666;
    private static final String TAG = "SignUpFragment";
    private FragmentSignUpBinding binding;

    public SignUpFragment() {
        // Required empty public constructor
    }

    public static SignUpFragment newInstance(String param1, String param2) {
        SignUpFragment fragment = new SignUpFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.emailSignInButton.setOnClickListener(v -> launchSignInFlow(SignUpType.EMAIL));
        binding.googleSignInButton.setOnClickListener(v ->launchSignInFlow(SignUpType.GOOGLE));
    }

    public void launchSignInFlow(SignUpType signUpType) {
        List<AuthUI.IdpConfig> providers;
        switch(signUpType) {
            case EMAIL:
                providers = Collections.singletonList(
                        new AuthUI.IdpConfig.EmailBuilder().build());
                break;
            case GOOGLE:
                providers = Collections.singletonList(
                        new AuthUI.IdpConfig.GoogleBuilder().build());
                break;
            default:
                throw new IllegalArgumentException("Invalid sign-in type");
        }

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();
        startActivityForResult(signInIntent, SIGN_IN_RESULT_CODE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_RESULT_CODE) {
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == Activity.RESULT_OK)
                Log.i(TAG, "Successfully signed in user ");
            else
                Log.i(TAG, "Failed signed in user ");
        }
    }
}