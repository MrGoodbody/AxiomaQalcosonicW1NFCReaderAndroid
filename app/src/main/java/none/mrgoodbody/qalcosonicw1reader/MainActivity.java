package none.mrgoodbody.qalcosonicw1reader;

import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
NfcAdapter.ReaderCallback readerCallback ;
TextView txtText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        txtText= (TextView) findViewById(R.id.txtText);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
       readerCallback= new w1NFCCallback(adapter,this);
        if (adapter != null && adapter.isEnabled()) {
            adapter.enableReaderMode(this,readerCallback
                    ,
                    NfcAdapter.FLAG_READER_NFC_A |
                            NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F |
                            NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_NFC_BARCODE /*|
                            NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS*/,
                    null);

        } else if (adapter != null && !adapter.isEnabled()) {
            meldungZeigen("NFC ist deaktiert, NFC aktivieren und App neustarten.");

        } else {
            meldungZeigen("Kein NFC Adapter gefunden.");

        }

    }
   public void meldungZeigen(String meldung) {
       MainActivity.this.runOnUiThread(new Runnable() {

           @Override
           public void run() {

               txtText.append(meldung+"\r\n");
           }
       });

    }

}