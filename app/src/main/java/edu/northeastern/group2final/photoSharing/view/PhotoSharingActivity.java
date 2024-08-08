package edu.northeastern.group2final.photoSharing.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group2final.R;
import edu.northeastern.group2final.photoSharing.controller.PhotoViewModel;

public class PhotoSharingActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final String PHOTOS_KEY = "photos";
    private static final int REQUEST_PERMISSION_MEDIA_ACCESS = 2;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL = 22;
    private static final int REQUEST_PERMISSION_CAMERA = 3;
    private static final int REQUEST_IMAGE_PICK = 200;

    private RecyclerView recyclerView;
    private PhotoAdapter photoAdapter;
    private PhotoViewModel photoViewModel;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_sharing);

        // Save savedInstanceState as a class member variable
        this.savedInstanceState = savedInstanceState;

        // Initialize ViewModel
        photoViewModel = new ViewModelProvider(this).get(PhotoViewModel.class);

        // Initialize RecyclerView and Adapter
        recyclerView = findViewById(R.id.recycler_view);
        photoAdapter = new PhotoAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(photoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Observe ViewModel's photo data changes
        photoViewModel.getPhotoUris().observe(this, uris -> {
            photoAdapter.setPhotoUris(uris);
            photoAdapter.notifyDataSetChanged();
        });

        FloatingActionButton fabAddPhoto = findViewById(R.id.fab_add_photo);
        fabAddPhoto.setOnClickListener(v -> showPhotoSourceDialog());

        FloatingActionButton fabSharePhotos = findViewById(R.id.fab_share_photos);
        fabSharePhotos.setOnClickListener(v -> sharePhotosToInstagram());

        // Check permissions based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkPermissionsTiramisu(savedInstanceState);
        } else {
            checkPermissionsPreTiramisu(savedInstanceState);
        }
    }

    private void checkPermissionsTiramisu(Bundle savedInstanceState) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_PERMISSION_MEDIA_ACCESS);
        } else {
            initializeComponents(savedInstanceState);
        }
    }

    private void checkPermissionsPreTiramisu(Bundle savedInstanceState) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_MEDIA_ACCESS);
        } else {
            initializeComponents(savedInstanceState);
        }
    }

    private void initializeComponents(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Recover list data from ViewModel
            photoViewModel.getPhotoUris().getValue().addAll(
                    savedInstanceState.getParcelableArrayList(PHOTOS_KEY)
            );
        }

        // Setup RecyclerView with updated data from ViewModel
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(photoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup swiping action to delete items
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(PhotoSharingActivity.this, "Delete an item", Toast.LENGTH_SHORT).show();
                int position = viewHolder.getLayoutPosition();
                photoViewModel.removePhoto(position);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // Override the onSaveInstanceState method to save the linked list.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the list of links to the Bundle
        outState.putParcelableArrayList(PHOTOS_KEY, new ArrayList<>(photoViewModel.getPhotoUris().getValue()));
    }

    private void showPhotoSourceDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Photo Source")
                .setItems(new String[]{"Select From Photos", "Use Camera"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            selectPhotoFromGallery();
                        } else {
                            if (ContextCompat.checkSelfPermission(PhotoSharingActivity.this, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(PhotoSharingActivity.this,
                                        new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISSION_CAMERA);
                            } else {
                                dispatchTakePictureIntent();
                            }
                        }
                    }
                });
        builder.create().show();
    }

    private void selectPhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("PhotoSharingActivity", "Image capture successful");
            if (data != null && data.getExtras() != null) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                if (imageBitmap != null) {
                    Log.d("PhotoSharingActivity", "Image Bitmap created");
                    Uri imageUri = saveImageToExternalStorage(imageBitmap);
                    photoViewModel.addPhoto(imageUri);
                } else {
                    Log.d("PhotoSharingActivity", "Image Bitmap is null");
                    Toast.makeText(this, "Image capture failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("PhotoSharingActivity", "No data or extras");
                Toast.makeText(this, "No data or extras", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            Log.d("PhotoSharingActivity", "Image pick successful");
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    photoViewModel.addPhoto(selectedImageUri);
                } else {
                    Log.d("PhotoSharingActivity", "Selected image URI is null");
                    Toast.makeText(this, "Image selection failed", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("PhotoSharingActivity", "No data");
                Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("PhotoSharingActivity", "Operation result not OK");
            Toast.makeText(this, "Operation result not OK", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(this, "Permission denied to use camera", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_PERMISSION_MEDIA_ACCESS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initializeComponents(this.savedInstanceState);
                } else {
                    Toast.makeText(this, "Permission denied to access media files", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private Uri saveImageToExternalStorage(Bitmap bitmap) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "Image_" + System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        ContentResolver resolver = getContentResolver();
        Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (imageUri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(imageUri)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                Log.d("PhotoSharingActivity", "Image saved to external storage with URI: " + imageUri.toString());
            } catch (IOException e) {
                Log.e("PhotoSharingActivity", "Failed to save image", e);
                return null;
            }
        }

        return imageUri;
    }

    private void sharePhotosToInstagram() {
        List<Uri> photoUris = photoViewModel.getPhotoUris().getValue();

        if (photoUris == null || photoUris.isEmpty()) {
            Toast.makeText(this, "No photos to share", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, new ArrayList<>(photoUris));
        shareIntent.setType("image/*");
        shareIntent.setPackage("com.instagram.android");

        // getPackageManager()
        if (true) {
            startActivity(Intent.createChooser(shareIntent, "Share Photos"));
        } else {
            Toast.makeText(this, "Instagram is not installed", Toast.LENGTH_SHORT).show();
        }
    }
}



