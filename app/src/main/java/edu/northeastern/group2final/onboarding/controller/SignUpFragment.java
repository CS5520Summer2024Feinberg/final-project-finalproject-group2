package edu.northeastern.group2final.onboarding.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import edu.northeastern.group2final.databinding.FragmentSignUpBinding;
import edu.northeastern.group2final.onboarding.util.AuthEvent;
import edu.northeastern.group2final.onboarding.util.SignUpType;
import edu.northeastern.group2final.onboarding.viewmodel.AuthViewModel;
import edu.northeastern.group2final.suggestion.controller.SuggestionsActivity;


public class SignUpFragment extends Fragment {
    private static final int SIGN_IN_RESULT_CODE = 666;
    private ActivityResultLauncher<Intent> signInLauncher;

    private FragmentSignUpBinding binding;
    private AuthViewModel viewModel;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                result -> {
                    IdpResponse response = result.getIdpResponse();
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        viewModel.handleSignUpResult(response);
                    } else {
                        Log.d("SignUpFragment", "Sign-in failed: " + (response == null ? "Unknown error" : response.getError().getMessage()));
                    }
                });
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AuthViewModel.class);

        binding.emailSignInButton.setOnClickListener(v -> launchSignInFlow(SignUpType.EMAIL));
        binding.googleSignInButton.setOnClickListener(v -> launchSignInFlow(SignUpType.GOOGLE));

        viewModel.getAuthResultLiveData().observe(getViewLifecycleOwner(), this::handleAuthResult);
        viewModel.getAuthEvent().observe(getViewLifecycleOwner(), this::handleAuthEvent);
    }

    private void handleAuthEvent(AuthEvent event) {
        switch (event.getType()) {
            case SIGN_UP_SUCCESS:
                Snackbar.make(binding.getRoot(),
                        "Account created successfully. Welcome, " + (event.getDisplayName() != null ? event.getDisplayName() : "User"),
                        Snackbar.LENGTH_SHORT).show();
                navigateToSuggestionsActivity();
                break;
            case SIGN_IN_SUCCESS:
                // This could happen if a user has an account but uses the sign-up flow
                Snackbar.make(binding.getRoot(),
                        "Signed in to existing account. Welcome back, " + (event.getDisplayName() != null ? event.getDisplayName() : "User"),
                        Snackbar.LENGTH_SHORT).show();
                navigateToSuggestionsActivity();
                break;
        }
    }

    private void navigateToSuggestionsActivity() {
        Intent intent = new Intent(requireContext(), SuggestionsActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    public void launchSignInFlow(SignUpType signUpType) {
        List<AuthUI.IdpConfig> providers = viewModel.getProviders(signUpType);

        try {
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .setIsSmartLockEnabled(false)
                    .build();

            // do goes in there
            signInLauncher.launch(signInIntent);
        } catch (Exception e) {
            Log.e("SignUpFragment", "Error launching sign-in flow", e);
        }
    }

    private void handleAuthResult(String result) {
        Snackbar.make(binding.getRoot(), result, Snackbar.LENGTH_LONG).show();
    }
}