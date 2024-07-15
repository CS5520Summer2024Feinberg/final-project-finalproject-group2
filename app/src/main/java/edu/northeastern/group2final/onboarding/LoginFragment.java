package edu.northeastern.group2final.onboarding;

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
import com.google.firebase.auth.FirebaseAuth;

import edu.northeastern.group2final.R;
import edu.northeastern.group2final.SuggestionActivity;
import edu.northeastern.group2final.databinding.FragmentLoginBinding;

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
        observeAuthenticationState();

        binding.btnLogin.setOnClickListener( v -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if (!email.isEmpty() && !password.isEmpty()) {
                logIn(email, password);
            } else {
                Snackbar.make(binding.loginConstraintLayout, "Please enter email and password",
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void logIn(String email, String password) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        viewModel.getAuthenticationStateLiveData().observe(
                                getViewLifecycleOwner(), authenticationState -> {
                                    if (authenticationState == LoginViewModel.AuthenticationState.AUTHENTICATED) {
                                        String loggedInMessage = viewModel.getGreetingMessage();
                                        Snackbar.make(binding.loginConstraintLayout, loggedInMessage,
                                                Snackbar.LENGTH_SHORT).show();

                                        Intent intent = new Intent(getActivity(), SuggestionActivity.class);
                                        startActivity(intent);
                                    } else {
                                        Snackbar.make(binding.loginConstraintLayout, "Authentication Failed.",
                                                Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    } else {
                        // If sign in fails, display a message to the user.
                        viewModel.getAuthenticationStateLiveData().observe(
                                getViewLifecycleOwner(), authenticationState -> {
                                    if (authenticationState == LoginViewModel.AuthenticationState.INVALID_AUTHENTICATION) {
                                        Snackbar.make(binding.loginConstraintLayout, "Authentication Failed.",
                                                Snackbar.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                });
    }

    private void observeAuthenticationState() {
        viewModel.getAuthenticationStateLiveData().observe(
                getViewLifecycleOwner(), authenticationState -> {
                    switch (authenticationState) {
                        case AUTHENTICATED:
                            Intent intent = new Intent(getActivity(), SuggestionActivity.class);
                            startActivity(intent);
                            break;
                        case UNAUTHENTICATED:
                            binding.btnLogin.setOnClickListener( v -> {
                                String email = binding.etEmail.getText().toString().trim();
                                String password = binding.etPassword.getText().toString().trim();

                                if (!email.isEmpty() && !password.isEmpty()) {
                                    logIn(email, password);
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
    }
}