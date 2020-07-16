package tandel.amit.myqrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

//implementing onclicklistener
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //View Objects
    private Button buttonScan, buttonGenerate;
    private EditText ipAddress, ssid, password, inputText;

    private ImageView qrImage;
    String inputValue;
    public final static int QRcodeWidth = 500 ;
    Bitmap bitmap ;

    //qr code scanner object
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //View objects
        buttonScan = findViewById(R.id.btnScan);
        buttonGenerate = findViewById(R.id.btnGenerateQR);
        ipAddress = findViewById(R.id.etIpAddress);
        ssid = findViewById(R.id.etSSID);
        password = findViewById(R.id.etPasswrd);
        inputText = findViewById(R.id.etTextInput);
        qrImage = findViewById(R.id.qrImageView);

        //intializing scan object
        qrScan = new IntentIntegrator(this);

        //attaching onclick listener
        buttonScan.setOnClickListener(this);
        buttonGenerate.setOnClickListener(this);
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //setting values to textviews
                    ipAddress.setText(obj.getString("Cubo IP"));
                    ssid.setText(obj.getString("SSID"));
                    password.setText(obj.getString("Password"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    Bitmap TextToImageEncode(String value) throws WriterException{
        BitMatrix bitMatrix;
        bitMatrix = new MultiFormatWriter().encode(value, BarcodeFormat.QR_CODE,QRcodeWidth,QRcodeWidth,null);
        int bitMatrixWidth = bitMatrix.getWidth();
        int bitMatrixHeight = bitMatrix.getHeight();
        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];
        for (int y=0; y<bitMatrixHeight;y++){
            int offset = y*bitMatrixWidth;
            for (int x=0; x<bitMatrixWidth;x++){
                pixels[offset+x] = bitMatrix.get(x,y)?getResources().getColor(R.color.Black):getResources().getColor(R.color.White);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth,bitMatrixHeight,Bitmap.Config.ARGB_4444);
        bitmap.setPixels(pixels,0,500,0,0,bitMatrixWidth,bitMatrixHeight);
        return bitmap;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnScan:
                //initiating the qr code scan
                qrScan.initiateScan();
                break;

            case R.id.btnGenerateQR:
                inputValue = inputText.getText().toString();
                try {
                    bitmap = TextToImageEncode(inputValue);
                    qrImage.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }
}