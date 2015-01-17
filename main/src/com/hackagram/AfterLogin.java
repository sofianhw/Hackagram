package com.hackagram;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;

public class AfterLogin extends Activity implements OnClickListener{
	
		private EditText nama, harga, kondisi, notelepon;

		private TextView messageText;
		private Button uploadButton, btnselectpic;
		private ImageView imageview;
		private int serverResponseCode = 0;
		private ProgressDialog dialog = null;

		private String upLoadServerUri = null;
		private String imagepath=null;

		@Override
		public void onCreate(Bundle savedInstanceState) {

		    super.onCreate(savedInstanceState);
		    setContentView(R.layout.activity_main);

		    nama = (EditText)findViewById(R.id.nama_barang);
		    harga = (EditText)findViewById(R.id.harga_barang);
		    kondisi = (EditText)findViewById(R.id.kondisi_barang);
		    notelepon = (EditText)findViewById(R.id.no_telepon);

		    uploadButton = (Button)findViewById(R.id.uploadButton);
		    messageText  = (TextView)findViewById(R.id.messageText);
		    btnselectpic = (Button)findViewById(R.id.button_selectpic);
		    imageview = (ImageView)findViewById(R.id.imageView_pic);


		    btnselectpic.setOnClickListener(this);
		    uploadButton.setOnClickListener(this);
		    upLoadServerUri = "http://192.168.43.226/kambing/UploadToServer.php";
		}

		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
		    // Inflate the menu; this adds options to the action bar if it is present.
		    getMenuInflater().inflate(R.menu.menu, menu);
		    return true;
		}

		@Override
		public void onClick(View arg0) {

		    if(arg0==btnselectpic)
		    {
		        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

		        AlertDialog.Builder builder = new AlertDialog.Builder(AfterLogin.this);
		        builder.setTitle("Add Photo!");
		        builder.setItems(options, new DialogInterface.OnClickListener() {
		            @Override
		            public void onClick(DialogInterface dialog, int item) {
		                if (options[item].equals("Take Photo"))
		                {
		                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
		                    imagepath = f.getAbsolutePath();
		                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		                    startActivityForResult(intent, 1);
		                }
		                else if (options[item].equals("Choose from Gallery"))
		                {


		                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		                    startActivityForResult(intent, 2);

		                } else if (options[item].equals("Cancel")) {
		                    dialog.dismiss();
		                }
		            }
		        });
		        builder.show();


		    }
		    else if (arg0==uploadButton) {

		         dialog = ProgressDialog.show(AfterLogin.this, "", "Uploading file...", true);
		         messageText.setText("uploading started.....");
		         new Thread(new Runnable() {

		             public void run() {

		                  uploadFile(imagepath);
		                  //send();

		             }
		           }).start();     
		    }

		}


		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		    super.onActivityResult(requestCode, resultCode, data);
		    if (resultCode == RESULT_OK) {
		        if (requestCode == 1) {
		            File f = new File(Environment.getExternalStorageDirectory().toString());
		            for (File temp : f.listFiles()) {
		                if (temp.getName().equals("temp.jpg")) {
		                    f = temp;
		                    break;
		                }
		            }
		            try {
		                Bitmap bitmap;
		                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();

		                bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
		                        bitmapOptions);

		                // bitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, true);
		                imageview.setImageBitmap(bitmap);


		                String path = android.os.Environment
		                        .getExternalStorageDirectory()
		                        + File.separator
		                        + "Phoenix" + File.separator + "default";
		                f.delete();
		                OutputStream outFile = null;
		                File file = new File(path, String.valueOf(System.currentTimeMillis()) + ".jpg");
		                try {
		                    outFile = new FileOutputStream(file);
		                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
		                    outFile.flush();
		                    outFile.close();
		                } catch (FileNotFoundException e) {
		                    e.printStackTrace();
		                } catch (IOException e) {
		                    e.printStackTrace();
		                } catch (Exception e) {
		                    e.printStackTrace();
		                }
		            } catch (Exception e) {
		                e.printStackTrace();
		            }
		        } else if (requestCode == 2) {

		            Uri selectedImage = data.getData();
		            imagepath = getPath(selectedImage);
		            String[] filePath = { MediaStore.Images.Media.DATA };
		            Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
		            c.moveToFirst();
		            int columnIndex = c.getColumnIndex(filePath[0]);
		            String picturePath = c.getString(columnIndex);
		            c.close();
		            Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
		            //Log.w("path of image from gallery......******************.........", picturePath+"");
		            imageview.setImageBitmap(thumbnail);
		            messageText.setText("Uploading file path:" +imagepath);
		        }
		    }

		/*  if (requestCode == 1 && resultCode == RESULT_OK) {
		        //Bitmap photo = (Bitmap) data.getData().getPath(); 

		        Uri selectedImageUri = data.getData();
		        imagepath = getPath(selectedImageUri);
		        Bitmap bitmap=BitmapFactory.decodeFile(imagepath);
		        imageview.setImageBitmap(bitmap);
		        messageText.setText("Uploading file path:" +imagepath);

		    } */
		}
		     public String getPath(Uri uri) {
		            String[] projection = { MediaStore.Images.Media.DATA };
		            Cursor cursor = managedQuery(uri, projection, null, null, null);
		            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		            cursor.moveToFirst();
		            return cursor.getString(column_index);
		        }


		public int uploadFile(String sourceFileUri) {

		      String fileName = sourceFileUri;

		      HttpURLConnection conn = null;
		      DataOutputStream dos = null;  
		      String lineEnd = "\r\n";
		      String twoHyphens = "--";
		      String boundary = "*****";
		      int bytesRead, bytesAvailable, bufferSize;
		      byte[] buffer;
		      int maxBufferSize = 1 * 1024 * 1024; 
		      File sourceFile = new File(sourceFileUri); 

		      if (!sourceFile.isFile()) {

		           dialog.dismiss(); 

		           Log.e("uploadFile", "Source File not exist :"+imagepath);

		           runOnUiThread(new Runnable() {
		               public void run() {
		                   messageText.setText("Source File not exist :"+ imagepath);
		               }
		           }); 

		           return 0;

		      }
		      else
		      {
		           try { 

		                 // open a URL connection to the Servlet
		               FileInputStream fileInputStream = new FileInputStream(sourceFile);
		               URL url = new URL(upLoadServerUri);
		               String nm = nama.getText().toString();
		               String hrg = harga.getText().toString();
		               String knds = kondisi.getText().toString();
		               String notlp = notelepon.getText().toString();
		               // Open a HTTP  connection to  the URL
		               conn = (HttpURLConnection) url.openConnection(); 
		               conn.setDoInput(true); // Allow Inputs
		               conn.setDoOutput(true); // Allow Outputs
		               conn.setUseCaches(false); // Don't use a Cached Copy
		               conn.setRequestMethod("POST");
		               conn.setRequestProperty("Connection", "Keep-Alive");
		               conn.setRequestProperty("ENCTYPE", "multipart/form-data");
		               conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
		               conn.setRequestProperty("uploaded_file", fileName);
		               conn.setRequestProperty("uploaded_nama", nm);
		               conn.setRequestProperty("uploaded_harga", hrg);
		               conn.setRequestProperty("uploaded_kondisi", knds);
		               conn.setRequestProperty("uploaded_notelepon", notlp);

		               dos = new DataOutputStream(conn.getOutputStream());

		               dos.writeBytes(twoHyphens + boundary + lineEnd);
		               dos.writeBytes("Content-Disposition: form-data; name=uploaded_nama" + lineEnd); // name=uploaded_nama so you have to get PHP side using mobile_no
		               dos.writeBytes(lineEnd);
		               dos.writeBytes(nm); // nm is String variable
		               dos.writeBytes(lineEnd);

		               dos.writeBytes(twoHyphens + boundary + lineEnd);
		               dos.writeBytes("Content-Disposition: form-data; name=uploaded_harga" + lineEnd); // name=uploaded_nama so you have to get PHP side using mobile_no
		               dos.writeBytes(lineEnd);
		               dos.writeBytes(hrg); // nm is String variable
		               dos.writeBytes(lineEnd);

		               dos.writeBytes(twoHyphens + boundary + lineEnd);
		               dos.writeBytes("Content-Disposition: form-data; name=uploaded_kondisi" + lineEnd); // name=uploaded_nama so you have to get PHP side using mobile_no
		               dos.writeBytes(lineEnd);
		               dos.writeBytes(knds); // nm is String variable
		               dos.writeBytes(lineEnd);

		               dos.writeBytes(twoHyphens + boundary + lineEnd);
		               dos.writeBytes("Content-Disposition: form-data; name=uploaded_notelepon" + lineEnd); // name=uploaded_nama so you have to get PHP side using mobile_no
		               dos.writeBytes(lineEnd);
		               dos.writeBytes(notlp); // nm is String variable
		               dos.writeBytes(lineEnd);

		               dos.writeBytes(twoHyphens + boundary + lineEnd); 
		               dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
		                                         + fileName + "\"" + lineEnd);

		               dos.writeBytes(lineEnd);


		               // create a buffer of  maximum size
		               bytesAvailable = fileInputStream.available(); 

		               bufferSize = Math.min(bytesAvailable, maxBufferSize);
		               buffer = new byte[bufferSize];

		               // read file and write it into form...
		               bytesRead = fileInputStream.read(buffer, 0, bufferSize);  

		               while (bytesRead > 0) {

		                 dos.write(buffer, 0, bufferSize);
		                 bytesAvailable = fileInputStream.available();
		                 bufferSize = Math.min(bytesAvailable, maxBufferSize);
		                 bytesRead = fileInputStream.read(buffer, 0, bufferSize);   

		                }

		               // send multipart form data necesssary after file data...
		               dos.writeBytes(lineEnd);
		               dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

		               // Responses from the server (code and message)
		               serverResponseCode = conn.getResponseCode();
		               String serverResponseMessage = conn.getResponseMessage();

		               Log.i("uploadFile", "HTTP Response is : " 
		                       + serverResponseMessage + ": " + serverResponseCode);

		               if(serverResponseCode == 200){

		                   runOnUiThread(new Runnable() {
		                        public void run() {
		                            String msg = "File Upload Completed.\n\n See uploaded file here : \n\n"
		                                  +" C:/AppServ/www/kambing/uploads";
		                            messageText.setText(msg);
		                            Toast.makeText(AfterLogin.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
		                            //Intent i = new Intent(MainActivity.this,InputInfo.class);
		                            finish();
		                            //startActivity(i); 
		                        }
		                    });                
		               }    

		               //close the streams //
		               fileInputStream.close();
		               dos.flush();
		               dos.close();

		          }

		           catch (MalformedURLException ex) {

		              dialog.dismiss();  
		              ex.printStackTrace();

		              runOnUiThread(new Runnable() {
		                  public void run() {
		                      messageText.setText("MalformedURLException Exception : check script url.");
		                      Toast.makeText(AfterLogin.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
		                  }
		              });

		              Log.e("Upload file to server", "error: " + ex.getMessage(), ex);  
		          } catch (Exception e) {

		              dialog.dismiss();  
		              e.printStackTrace();

		              runOnUiThread(new Runnable() {
		                  public void run() {
		                      messageText.setText("Got Exception : see logcat ");
		                      Toast.makeText(AfterLogin.this, "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();
		                  }
		              });
		              Log.e("Upload file to server Exception", "Exception : "  + e.getMessage(), e);  
		          }
		          dialog.dismiss();       
		          return serverResponseCode; 

		       } // End else block 
		     }


		 }