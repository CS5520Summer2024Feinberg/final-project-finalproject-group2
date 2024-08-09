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
import edu.northeastern.group2final.onboarding.viewmodel.LoginViewModel;
import edu.northeastern.group2final.suggestion.controller.SuggestionsActivity;

public class LoginFragment extends Fragment {
    private static final int SIGN_IN_RESULT_CODE = 666;
    private static final String TAG = "LoginFragment";
    private static LoginViewModel viewModel;
    private FragmentLoginBinding binding;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        return fragment;
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

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        viewModel.getAuthenticationStateLiveData().observe(
                getViewLifecycleOwner(), authenticationState -> {
                    switch (authenticationState) {
                        case AUTHENTICATED:
                            Snackbar.make(
                                    binding.loginConstraintLayout,
                                    viewModel.getGreetingMessage(),
                                    Snackbar.LENGTH_SHORT).show();

                            Intent intent = new Intent(getActivity(), SuggestionsActivity.class);
                            startActivity(intent);
                            break;
                        case UNAUTHENTICATED:
                            binding.btnLogin.setOnClickListener(v -> {
                                String email = binding.etEmail.getText().toString().trim();
                                String password = binding.etPassword.getText().toString().trim();
                                if (!email.isEmpty() && !password.isEmpty()) {
                                    viewModel.login(email, password);
                                } else {
                                    Snackbar.make(binding.loginConstraintLayout, "Please enter email and password",
                                            Snackbar.LENGTH_SHORT).show();
                                }
                            });
                            break;
                        case INVALID_AUTHENTICATION:
                            Snackbar.make(binding.loginConstraintLayout, "Invalid credentials. Please try again.",
                                    Snackbar.LENGTH_SHORT).show();
                            break;
                    }
                }
        );

        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                viewModel.login(email, password);
            } else {
                Snackbar.make(binding.loginConstraintLayout, "Please enter email and password",
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}