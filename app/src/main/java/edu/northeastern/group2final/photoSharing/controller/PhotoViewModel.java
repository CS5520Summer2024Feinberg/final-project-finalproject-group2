package edu.northeastern.group2final.photoSharing.controller;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import edu.northeastern.group2final.photoSharing.model.PhotoRepository;

public class PhotoViewModel extends ViewModel {
    private final PhotoRepository photoRepository;
    private final MutableLiveData<List<Uri>> photoUrisLiveData;

    public PhotoViewModel() {
        photoRepository = new PhotoRepository();
        photoUrisLiveData = new MutableLiveData<>(photoRepository.getPhotoUris());
    }

    public LiveData<List<Uri>> getPhotoUris() {
        return photoUrisLiveData;
    }

    public void addPhoto(Uri uri) {
        photoRepository.addPhoto(uri);
        photoUrisLiveData.setValue(photoRepository.getPhotoUris());
    }

    public void removePhoto(int position) {
        photoRepository.removePhoto(position);
        photoUrisLiveData.setValue(photoRepository.getPhotoUris());
    }
}

