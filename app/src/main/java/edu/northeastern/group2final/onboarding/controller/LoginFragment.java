package edu.northeastern.group2final.onboarding.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;

import edu.northeastern.group2final.R;
import edu.northeastern.group2final.databinding.FragmentLoginBinding;
import edu.northeastern.group2final.onboarding.util.AuthEvent;
import edu.northeastern.group2final.onboarding.viewmodel.AuthViewModel;
import edu.northeastern.group2final.suggestion.controller.SuggestionsActivity;

public class LoginFragment extends Fragment {
    private AuthViewModel viewModel;
    private FragmentLoginBinding binding;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        viewModel.getAuthEvent().observe(getViewLifecycleOwner(), this::handleAuthState);
        viewModel.getAuthResultLiveData().observe(getViewLifecycleOwner(), this::showLoginResult);

        binding.btnLogin.setOnClickListener(v -> attemptLogin());
    }

    private void handleAuthState(AuthEvent event) {
        switch (event.getType()) {
            case SIGN_IN_SUCCESS:
                navigateToSuggestionsActivity();
                break;

            case SIGN_UP_SUCCESS:
                Snackbar.make(binding.loginConstraintLayout,
                        "New account created. Welcome, " + (event.getDisplayName() != null ? event.getDisplayName() : "User"),
                        Snackbar.LENGTH_SHORT).show();
                navigateToSuggestionsActivity();
                break;
        }
    }

    private void navigateToSuggestionsActivity() {
        Intent intent = new Intent(getActivity(), SuggestionsActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void showLoginResult(String result) {
        Snackbar.make(binding.loginConstraintLayout, result, Snackbar.LENGTH_SHORT).show();
    }

    private void attemptLogin() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (!email.isEmpty() && !password.isEmpty()) {
            viewModel.login(email, password);
        } else {
            Snackbar.make(binding.loginConstraintLayout, "Please enter email and password", Snackbar.LENGTH_SHORT).show();
        }
    }
}