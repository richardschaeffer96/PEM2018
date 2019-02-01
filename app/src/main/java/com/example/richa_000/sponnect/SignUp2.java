package com.example.richa_000.sponnect;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUp2 extends AppCompatActivity {

    private static final String TAG = "SignUpActivity2";
    private static int PICK_IMAGE = 100;
    Uri imageUri;
    ImageView profile_picture;

    private String SignUpMail;
    private String SignUpPassword;
    private String SignUpNickname;
    private String SignUpGender;
    private String SignUpAge;
    private EditText editTextFacebook;
    private EditText editTextInstagram;
    private EditText editTextTwitter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");
    private CollectionReference uploadsRef = db.collection("uploads");
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2);

        SignUpNickname = getIntent().getStringExtra("nickname");
        SignUpPassword = getIntent().getStringExtra("password");
        SignUpMail = getIntent().getStringExtra("mail");
        SignUpGender = getIntent().getStringExtra("gender");
        SignUpAge = getIntent().getStringExtra("age");
        editTextFacebook = findViewById(R.id.edit_facebook);
        editTextInstagram = findViewById(R.id.edit_instagram);
        editTextTwitter = findViewById(R.id.edit_twitter);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        profile_picture = findViewById(R.id.profile_picture);

    }

    public static Bitmap getRoundedRectBitmap(Bitmap bitmap, int pixels) {
        Bitmap result = null;
        try {
            result = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            int color = 0xff424242;
            Paint paint = new Paint();
            Rect rect = new Rect(0, 0, 200, 200);

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(100, 100, 75, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmap, rect, rect, paint);

        } catch (NullPointerException e) {
        } catch (OutOfMemoryError o) {
        }
        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            imageUri = data.getData();
            profile_picture.setImageURI(imageUri);
            Bitmap bm = ((BitmapDrawable)profile_picture.getDrawable()).getBitmap();
            //Bitmap bm = ((BitmapDrawable)profile_picture.getDrawable()).getBitmap();
            Bitmap resized = Bitmap.createScaledBitmap(bm, 200, 200, true);
            Bitmap converted_bm = getRoundedRectBitmap(resized, 200);
            profile_picture.setImageBitmap(converted_bm);
        }
    }

    public void upload_picture(View view){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    public void add_more(View view){

    }

    public void save(View view){

        Log.d(TAG, "Saving Data...");
        String SignUpFacebook = editTextFacebook.getText().toString();
        String SignUpInstagram = editTextInstagram.getText().toString();
        String SignUpTwitter = editTextTwitter.getText().toString();

        ArrayList<String> socialMedia = new ArrayList<>();
        socialMedia.add(SignUpFacebook);
        socialMedia.add(SignUpInstagram);
        socialMedia.add(SignUpTwitter);

        Log.d(TAG, "User age is: " + SignUpAge);
        int userAge = Integer.parseInt(SignUpAge);
        Log.d(TAG, "User age is: " + userAge);

        Log.d(TAG, "Creating User Document");
        final User user = new User(SignUpMail, SignUpNickname, SignUpPassword, SignUpGender, userAge, socialMedia);
        System.out.println(user.getNickname());
        usersRef.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String id = documentReference.getId();
                DocumentReference refUser= usersRef.document(id);
                refUser.update("id", id);
                uploadFile(id, user);
                Toast.makeText(SignUp2.this, "Data saved and logged in!\nHello "+user.getNickname(), Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUp2.this, "ERROR!", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        });


    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(String id, User user){

        profile_picture.setDrawingCacheEnabled(true);
        profile_picture.buildDrawingCache();
        Bitmap bitmap = profile_picture.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        //Log.d(TAG, "uploadFile: Ist Bild da? "+data);

        StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(imageUri));

        //mUploadTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        mUploadTask = fileReference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(SignUp2.this, "Success!", Toast.LENGTH_SHORT).show();
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri dlUri = uri;
                        //Upload upload = new Upload(id, dlUri.toString());
                        DocumentReference refUser = usersRef.document(id);
                        System.out.println("URI is " + dlUri);
                        Log.d(TAG, "URI is " + dlUri);
                        String uriPicture = dlUri.toString();
                        refUser.update("imageUri", uriPicture);
                        while(user.getImageUri() == null){
                            user.setImageUri(uriPicture);
                            System.out.println("Uri in User is: " + user.getImageUri());
                        }
                        Intent mIntent = new Intent(SignUp2.this, Menu.class);
                        mIntent.putExtra("id", id);
                        mIntent.putExtra("user", user);
                        startActivity(mIntent);

                    }
                });

                //String uploadId = mDatabaseRef.push().getKey();
                //mDatabaseRef.child(uploadId).setValue(upload);


                /*

                usersRef.add(user).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                String id = documentReference.getId();
                DocumentReference refUser= usersRef.document(id);
                refUser.update("id", id);
                Toast.makeText(SignUp.this, "Data saved and logged in!\nHello "+user.getNickname(), Toast.LENGTH_LONG).show();
                Intent mIntent = new Intent(SignUp.this, Menu.class);
                mIntent.putExtra("id", id);
                uploadFile(id);
                startActivity(mIntent);

                 */

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SignUp2.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

    }

}
