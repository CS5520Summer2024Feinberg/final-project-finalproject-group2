package edu.northeastern.group2final.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import edu.northeastern.group2final.R;
import edu.northeastern.group2final.databinding.FragmentWelcomeBinding;

public class WelcomeFragment extends Fragment {
    private static final int SIGN_IN_RESULT_CODE = 666;
    private static final String TAG = "WelcomeFragment";

    private static LoginViewModel viewModel;

    private FragmentWelcomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        observeAuthenticationState();

        binding.signInBtn.setOnClickListener(
                v -> launchSignInFlow()
        );
    }

    private void observeAuthenticationState() {

        viewModel.getAuthenticationStateLiveData().observe(
                getViewLifecycleOwner(), authenticationState -> {
                    if (authenticationState == LoginViewModel.AuthenticationState.AUTHENTICATED) {
                        // User is authenticated
                        binding.signInBtn.setText(getString(R.string.logout_button_text));



                        binding.signInBtn.setOnClickListener(v -> {
                            // Implement logout
                            AuthUI.getInstance().signOut(requireContext());
                        });
                        binding.welcomeTv.setText(
                                "User: " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + " is currently logged in" );

                    } else {
                        // User is not authenticated
                        binding.signInBtn.setText(getString(R.string.login_button_text));
                        binding.signInBtn.setOnClickListener(v -> launchSignInFlow());
                        binding.welcomeTv.setText("Please Log in");
                    }
                }
        );
    }

    private void launchSignInFlow() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build();

        startActivityForResult(signInIntent, WelcomeFragment.SIGN_IN_RESULT_CODE);
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