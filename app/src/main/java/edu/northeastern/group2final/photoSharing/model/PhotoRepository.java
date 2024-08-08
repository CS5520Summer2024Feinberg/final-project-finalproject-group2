package edu.northeastern.group2final.photoSharing.model;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class PhotoRepository {
    private List<Uri> photoUris;

    public PhotoRepository() {
        photoUris = new ArrayList<>();
    }

    public List<Uri> getPhotoUris() {
        return photoUris;
    }

    public void addPhoto(Uri uri) {
        photoUris.add(uri);
    }

    public void removePhoto(int position) {
        photoUris.remove(position);
    }
}
