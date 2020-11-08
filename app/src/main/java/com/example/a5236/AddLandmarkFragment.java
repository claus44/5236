package com.example.a5236;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AddLandmarkFragment extends Fragment {
    private static final String TAG = "AddLandmarkFragment";
    private static final int Image_Capture_Code = 1;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    ImageView mImageView;
    EditText titleEditText,descriptionEditText, hintEditText, difficultyEditText;
    Button createLandmarkBtn, retryBtn, backBtn;
    ProgressBar loadingProgressBar;
    LoginActivity mContext;

    FusedLocationProviderClient fusedLocationProviderClient;
    private String coordinates;

    public AddLandmarkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_landmark, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mImageView = view.findViewById(R.id.imageView);
        titleEditText = view.findViewById(R.id.title);
        descriptionEditText = view.findViewById(R.id.description);
        hintEditText = view.findViewById(R.id.hint);
        difficultyEditText = view.findViewById(R.id.difficulty);
        createLandmarkBtn = view.findViewById(R.id.createLandmark);
        retryBtn = view.findViewById(R.id.retry);
        backBtn = view.findViewById(R.id.backButton);
        loadingProgressBar = view.findViewById(R.id.loading);

        mContext = (LoginActivity) getActivity();

        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(mContext);

        titleEditText.addTextChangedListener(txtWatcher);
        descriptionEditText.addTextChangedListener(txtWatcher);
        hintEditText.addTextChangedListener(txtWatcher);
        difficultyEditText.addTextChangedListener(txtWatcher);

        //set image
        try {
            updatePhotoView();
        } catch (IOException e) {
            e.printStackTrace();
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(AddLandmarkFragment.this)
                        .navigate(R.id.action_addLandmarkFragment_to_landmarksListFragment);

            }
        });

        retryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.a5236.fileprovider", LoginActivity.getPhoto());
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent,Image_Capture_Code);
            }
        });

        createLandmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadingProgressBar.setVisibility(View.VISIBLE);
            final String title = titleEditText.getText().toString();
            final String desc = descriptionEditText.getText().toString();
            final String hint = hintEditText.getText().toString();
            final int difficulty = Integer.parseInt(difficultyEditText.getText().toString());
            if(ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION ) == PackageManager.PERMISSION_GRANTED){
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if(location != null){
                            Double latitude = location.getLatitude();
                            Double longtitude = location.getLongitude();
                            coordinates = longtitude + "," + latitude;
                            mDatabase = FirebaseDatabase.getInstance().getReference();
                            mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    tryCreateLandmark(snapshot, title, difficulty, desc, hint);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) { }
                            });
                        }
                    }
                });
            }else{
                // permission denied
                ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.ACCESS_FINE_LOCATION} , 44);
            }
            }
        });


    }

    private final TextWatcher txtWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after)
        { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count)
        {}
        @Override
        public void afterTextChanged(Editable s) {
            int checksPassed = 0;
            if(titleEditText.getText().toString().length()!=0){
                checksPassed++;
            }else{
                titleEditText.setError(mContext.getResources().getString(R.string.title_error));
            }
            if(descriptionEditText.getText().toString().length()!=0 ){
                checksPassed++;
            }else{
                descriptionEditText.setError(mContext.getResources().getString(R.string.desc_error));
            }
            if(hintEditText.getText().toString().length() != 0 ){
                checksPassed++;
            }else{
                hintEditText.setError(mContext.getResources().getString(R.string.hint_error));
            }
            if(difficultyEditText.getText().toString().length() != 0 &&
                    Integer.parseInt(difficultyEditText.getText().toString()) > 0 &&
                    Integer.parseInt(difficultyEditText.getText().toString()) < 11){
                checksPassed++;
            }else{
                difficultyEditText.setError(mContext.getResources().getString(R.string.diff_error));
            }
            if(checksPassed == 4){
                createLandmarkBtn.setEnabled(true);
            }else{
                createLandmarkBtn.setEnabled(false);
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Image_Capture_Code) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.a5236.fileprovider", LoginActivity.getPhoto());
                try {
                    updatePhotoView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
                NavHostFragment.findNavController(AddLandmarkFragment.this)
                        .navigate(R.id.action_addLandmarkFragment_to_landmarksListFragment);
            }
        }
    }

    public void tryCreateLandmark(DataSnapshot dataSnapshot, final String title, final int diff, final String desc, final String hint){
        boolean landmarkExists = checkTitleExists(dataSnapshot,title);
        if (!landmarkExists) {
            byte[] data = covertBitmapToStoreInFirebase(LoginActivity.getCurrentBitmap());
            mStorageRef = FirebaseStorage.getInstance().getReference();
            StorageReference fileStorageRef = mStorageRef.child("images/" + title);
            UploadTask uploadTask = fileStorageRef.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {

                @Override
                public void onFailure(@NonNull Exception exception) {
                    loadingProgressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext().getApplicationContext(), "Failed to create landmark", Toast.LENGTH_LONG).show();

                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String imageRef = "images/" + title;
                    List<String> foundByUsers = new ArrayList<String>();
                    foundByUsers.add(LoginActivity.getLoggedInUser().getUserId());
                    Landmark landmark = new Landmark(title, diff, coordinates, imageRef, desc, hint, LoginActivity.getLoggedInUser().getUserId(), foundByUsers);
                    mDatabase.child("Landmarks").child(title).setValue(landmark);
                    updateLandmarkList(landmark);
                    Toast.makeText(getContext().getApplicationContext(), "Landmark Successfully Created", Toast.LENGTH_LONG).show();
                    NavHostFragment.findNavController(AddLandmarkFragment.this)
                                    .navigate(R.id.action_addLandmarkFragment_to_landmarksListFragment);
                }
            });

        }else{
            loadingProgressBar.setVisibility(View.GONE);
            Toast.makeText(getContext().getApplicationContext(), "Landmark Title Already Exists", Toast.LENGTH_LONG).show();
        }
    }
    private boolean checkTitleExists(DataSnapshot dataSnapshot, final String title){
        boolean landmarkExists = false;
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            if (ds.getKey().equals("Landmarks")){
                HashMap<String, Object> hm = (HashMap<String, Object>) ds.getValue();
                landmarkExists = hm.containsKey(title);
            }
        }
        return landmarkExists;
    }

    private void updateLandmarkList(Landmark landmark){
        HashMap<String, List<Landmark>> currentLandmarks = LoginActivity.getLandmarkItemList();
        List<Landmark> currentFound = currentLandmarks.get("Found");
        currentFound.add(landmark);
        currentLandmarks.put("Found", currentFound);
        LoginActivity.setLandmarkItemList(currentLandmarks);
    }

    public byte[] covertBitmapToStoreInFirebase(Bitmap bitmap){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        return data;
    }

    private void updatePhotoView() throws IOException {

        if (LoginActivity.getPhoto() == null || !LoginActivity.getPhoto().exists()) {
            mImageView.setImageDrawable(null);
        } else {
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.example.a5236.fileprovider", LoginActivity.getPhoto());
            Bitmap bitmap = handleSamplingAndRotationBitmap(mContext,uri);
            LoginActivity.setCurrentBitmap(bitmap);
            mImageView.setImageBitmap(bitmap);
        }
    }
    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage);
        return img;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage) throws IOException {

        InputStream input = context.getContentResolver().openInputStream(selectedImage);
        ExifInterface ei;
        if (Build.VERSION.SDK_INT > 23)
            ei = new ExifInterface(input);
        else
            ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }
    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }





}