package edu.northeastern.group2final.photoSharing;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.Manifest;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import edu.northeastern.group2final.R;


public class PhotoSharingActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private RecyclerView recyclerView;
    // New constant to save linked list recovery list data
    private static final String PHOTOS_KEY = "photos";
    private PhotoAdapter photoAdapter;
    private List<Uri> photoUris;
    private static final int REQUEST_PERMISSION_MEDIA_ACCESS = 2;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL = 22;
    private static final int REQUEST_PERMISSION_CAMERA = 3;
    private static final int REQUEST_IMAGE_PICK = 200;
    // save savedInstanceState as a class member variable
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_sharing);

        // Store savedInstanceState as a class member variable.
        this.savedInstanceState = savedInstanceState;

        // Select the permission request method according to the Android version
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

        photoUris = new ArrayList<>();
        if (savedInstanceState != null) {
            // Recover list data
            photoUris = savedInstanceState.getParcelableArrayList(PHOTOS_KEY);
        }
        photoAdapter = new PhotoAdapter(photoUris, this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(photoAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FloatingActionButton fabAddPhoto = findViewById(R.id.fab_add_photo);
        fabAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoSourceDialog();
            }
        });

        FloatingActionButton fabSharePhotos = findViewById(R.id.fab_share_photos);
        fabSharePhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePhotosToInstagram();
            }
        });

        //Specify what action a specific gesture performs, in this case swiping right or left deletes the entry
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Toast.makeText(PhotoSharingActivity.this, "Delete an item", Toast.LENGTH_SHORT).show();
                int position = viewHolder.getLayoutPosition();
                photoUris.remove(position);

                photoAdapter.notifyItemRemoved(position);

            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // Override the onSaveInstanceState method to save the linked list.
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the list of links to the Bundle
        outState.putParcelableArrayList(PHOTOS_KEY, new ArrayList<>(photoUris));
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
                    photoUris.add(imageUri);
                    photoAdapter.notifyItemInserted(photoUris.size() - 1);
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
                    photoUris.add(selectedImageUri);
                    photoAdapter.notifyItemInserted(photoUris.size() - 1);
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
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return uri;
    }

    private void showConfirmationDialog(Uri imageUri) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        ImageView imageView = new ImageView(this);
        imageView.setImageURI(imageUri);

        builder.setView(imageView)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        photoUris.add(imageUri);
                        photoAdapter.notifyItemInserted(photoUris.size() - 1);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dispatchTakePictureIntent();
                    }
                });
        builder.create().show();
    }

    private void sharePhotosToInstagram() {
        if (photoUris.isEmpty()) {
            Toast.makeText(this, "No photos to share", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setType("image/*");

        ArrayList<Uri> uris = new ArrayList<>(photoUris);
        shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        shareIntent.setPackage("com.instagram.android");

        startActivity(Intent.createChooser(shareIntent, "Share photos"));

    }

    private boolean isInstagramInstalled() {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo("com.instagram.android", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}


