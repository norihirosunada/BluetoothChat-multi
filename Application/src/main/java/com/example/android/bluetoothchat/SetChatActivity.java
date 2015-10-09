package com.example.android.bluetoothchat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;

/**
 * Created by norihirosunada on 15/10/06.
 */
public class SetChatActivity extends Activity {
    /**
     * Return Intent extra
     */
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    /**
     * Member fields
     */
    private BluetoothAdapter mBtAdapter;

    Intent intent;

    ListView pairedListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // タイトルバー非表示
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.paired_device_list);

        // ユーザーが取り消したときのためにリザルトにCANCELEDをセット
        setResult(Activity.RESULT_CANCELED);

        // デバイスの検出を実行するためのボタンの初期化
        Button setButton = (Button) findViewById(R.id.button_connect);
        setButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connect();
            }
        });


        // array adaptersの初期化.
        // 一つは接続履歴のあるデバイス、もうひとつは新規のデバイス
        ArrayAdapter<String> pairedDevicesArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.device_name);
//        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // ペアリング済みのデバイスのListViewのセット
        ListView pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        pairedListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);


        // local Bluetooth adapterを取得する
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // ペアリング済みのデバイスをセットする
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // ペアリングされたデバイスが有るなら　それぞれをArrayAdapterに加える
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesArrayAdapter.add(noDevices);
        }
    }

    private void connect(){

        // 複数のデバイスのMAC addressを取得する, つまりはViewの中の最後の17のchar　×台数
        String[] address = new String[pairedListView.getCount()];
        SparseBooleanArray checkeditempositions = pairedListView.getCheckedItemPositions();

        for(int i=0; i<=pairedListView.getCount();i++){
            if(checkeditempositions.get(i) == true) {
                //マッピングされている(選択されている)項目だった場合は文字列に連結する
                int key = checkeditempositions.keyAt(i);
                String info = pairedListView.getItemAtPosition(key).toString();
                address[i] = info.substring(info.length() - 17);
            }
        }


        // Intentを作りMAC addressを渡す
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DEVICE_ADDRESS, address);


        // resultをセットしこのアクティビティを終了する
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
