package com.example.richa_000.sponnect;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
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
import android.widget.TextView;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class EditProfile extends AppCompatActivity {

    private static final String TAG = "EditProfile";

    private User me;
    public String userID;
    EditText mail;
    EditText nickname;
    EditText password1;
    EditText password2;
    EditText facebook;
    EditText instagram;
    EditText twitter;
    private static int PICK_IMAGE = 100;
    //Uri imageUri;
    Uri oldUri;
    ImageView profile_picture;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference usersRef = db.collection("users");
    private CollectionReference uploadsRef = db.collection("uploads");
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;

    private Typeface comfortaa_regular;
    private Typeface comfortaa_bold;
    private Typeface comfortaa_light;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userID = getIntent().getStringExtra("id");
        me = (User) getIntent().getSerializableExtra("user");

        comfortaa_regular = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Regular.ttf");
        comfortaa_bold = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Bold.ttf");
        comfortaa_light = Typeface.createFromAsset(this.getAssets(), "Comfortaa-Light.ttf");

        TextView text_headline_signup = findViewById(R.id.text_editprofile);
        TextView text_email = findViewById(R.id.text_email);
        TextView text_nickname = findViewById(R.id.text_nickname);
        TextView text_password = findViewById(R.id.text_password);
        TextView text_password2 = findViewById(R.id.text_password2);
        TextView text_facebook = findViewById(R.id.text_facebook);
        TextView text_twitter = findViewById(R.id.text_twitter);
        TextView text_instagram = findViewById(R.id.text_instagram);
        TextView text_img = findViewById(R.id.text_img);
        Button button_save = findViewById(R.id.button_save);

        text_headline_signup.setTypeface(comfortaa_bold);
        text_email.setTypeface(comfortaa_regular);
        text_nickname.setTypeface(comfortaa_regular);
        text_password.setTypeface(comfortaa_regular);
        text_password2.setTypeface(comfortaa_regular);
        text_facebook.setTypeface(comfortaa_regular);
        text_twitter.setTypeface(comfortaa_regular);
        text_instagram.setTypeface(comfortaa_regular);
        text_img.setTypeface(comfortaa_regular);
        button_save.setTypeface(comfortaa_regular);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");

        //Get all the User-Information to be displayed in the fields for editing
        mail = findViewById(R.id.edit_email_edit);
        mail.setText(me.geteMail());

        nickname = findViewById(R.id.edit_nickname_edit);
        nickname.setText(me.getNickname());

        password1 = findViewById(R.id.edit_password_edit);
        password2 = findViewById(R.id.edit_password_edit2);

        facebook = findViewById(R.id.edit_facebook_edit);
        facebook.setText(me.getSocialMedia().get(0));

        instagram = findViewById(R.id.edit_instagram_edit);
        instagram.setText(me.getSocialMedia().get(1));

        twitter = findViewById(R.id.edit_twitter_edit);
        twitter.setText(me.getSocialMedia().get(2));

        profile_picture = findViewById(R.id.profile_picture_e);
        oldUri = Uri.parse(me.getImageUri());
        Picasso.get().load(oldUri).into(profile_picture);

    }

    /**
     * If new Image is uploaded it is cropped
     * @param bitmap
     * @param pixels
     * @return
     */
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
            oldUri = data.getData();
            profile_picture.setImageURI(oldUri);
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

    public void save_new_information(View view){

        String Editpassword1 = password1.getText().toString();
        String Editpassword2 = password2.getText().toString();
        if(Editpassword1.isEmpty() || Editpassword2.isEmpty()){
            Toast.makeText(EditProfile.this, "Type in your password!", Toast.LENGTH_LONG).show();
        } else{
            if(Editpassword1.equals(Editpassword2)){
                String Editname = nickname.getText().toString();
                String Editmail = mail.getText().toString();
                String EditFacebook = facebook.getText().toString();
                String EditInstagram = instagram.getText().toString();
                String EditTwitter = twitter.getText().toString();

                ArrayList<String> socialMedia = new ArrayList<>();
                socialMedia.add(EditFacebook);
                socialMedia.add(EditInstagram);
                socialMedia.add(EditTwitter);

                DocumentReference refUser= usersRef.document(userID);
                refUser.update("nickname", Editname);
                refUser.update("eMail", Editmail);
                refUser.update("password", Editpassword1);
                refUser.update("socialMedia", socialMedia);

                if(me.getImageUri()!=null) {
                    oldUri = Uri.parse(me.getImageUri());
                }

                uploadFile(userID, me, oldUri);

            } else {
                Toast.makeText(EditProfile.this, "Passwords do not match", Toast.LENGTH_LONG).show();
            }
        }

    }

    private String getFileExtension(Uri uri){
        if(oldUri == null){
            return "png";
        } else {
            ContentResolver cR = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(cR.getType(uri));
        }
    }

    private void uploadFile(String id, User user, Uri uri){

        profile_picture.setDrawingCacheEnabled(true);
        profile_picture.buildDrawingCache();
        Bitmap bitmap = profile_picture.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        //Log.d(TAG, "uploadFile: Ist Bild da? "+data);

        StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(oldUri));



        //mUploadTask = fileReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        mUploadTask = fileReference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(EditProfile.this, "Success!", Toast.LENGTH_SHORT).show();
                fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri dlUri = uri;
                        //Upload upload = new Upload(id, dlUri.toString());
                        DocumentReference refUser = usersRef.document(id);
                        Log.d(TAG, "URI is " + dlUri);
                        String uriPicture = dlUri.toString();
                        refUser.update("imageUri", uriPicture);
                        user.setImageUri(uriPicture);
                        Intent mIntent = new Intent(EditProfile.this, Menu.class);
                        mIntent.putExtra("id", id);
                        mIntent.putExtra("user", user);
                        startActivity(mIntent);

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

            }
        });

    }



}
