/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.bluetoothchat;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

/**
 * This Activity appears as a dialog. It lists any paired devices and
 * devices detected in the area after discovery. When a device is chosen
 * by the user, the MAC address of the device is sent back to the parent
 * Activity in the result Intent.
 */
public class DeviceListActivity extends Activity {

    /**
     * Return Intent extra
     */
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    /**
     * Member fields
     */
    private BluetoothAdapter mBtAdapter;

    /**
     * Newly discovered devices
     */
    private ArrayAdapter<String> mNewDevicesArrayAdapter;

    ListView pairedListView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // タイトルバー非表示
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_device_list);

        // ユーザーが取り消したときのためにリザルトにCANCELEDをセット
        setResult(Activity.RESULT_CANCELED);

        // デバイスの検出を実行するためのボタンの初期化
        Button scanButton = (Button) findViewById(R.id.button_scan);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });

        //チャットを実行するためのボタンの初期化
        Button startchatButton = (Button) findViewById(R.id.button_start_chat);
        startchatButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                connect();
            }
        });



        // array adaptersの初期化.
        // 一つは接続履歴のあるデバイス、もうひとつは新規のデバイス
        ArrayAdapter<String> pairedDevicesArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.device_name);
        mNewDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);

        // ペアリング済みのデバイスのListViewのセット
        pairedListView = (ListView) findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesArrayAdapter);
        pairedListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // 新規に見つかったデバイスのListViewのセット
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);

        // デバイス検出時にブロードキャストに登録する
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // 検出終了時にブロードキャストに登録する
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        // local Bluetooth adapterを取得する
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();

        // ペアリング済みのデバイスをセットする
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        // ペアリングされたデバイスが有るなら　それぞれをArrayAdapterに加える
        if (pairedDevices.size() > 0) {
            findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesArrayAdapter.add(noDevices);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 検索中でないかを確認する
        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }

        // broadcast listenersを解除
        this.unregisterReceiver(mReceiver);
    }

    /**
     * BluetoothAdapterで検出を開始する
     */
    private void doDiscovery() {

        // activity_titleに検出中の表示
        setProgressBarIndeterminateVisibility(true);
        setTitle(R.string.scanning);

        // 新規のデバイスのsub-activity_titleの表示
        findViewById(R.id.title_new_devices).setVisibility(View.VISIBLE);

        // すでに検出中ならば中止する
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }

        // BluetoothAdapterから検出のリクエスト
        mBtAdapter.startDiscovery();
    }

    /**
     * ListViews内のすべてのデバイスのon-click listener
     */
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
//            // 接続するので検出を止める
//            mBtAdapter.cancelDiscovery();
//
//            // デバイスのMAC addressを取得する, つまりはViewの中の最後の17のchar
//            String info = ((TextView) v).getText().toString();
//            String address = info.substring(info.length() - 17);
//
//            // Intentを作りMAC addressを渡す
//            Intent intent = new Intent();
//            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
//
//            // resultをセットしこのアクティビティを終了する
//            setResult(Activity.RESULT_OK, intent);
//            finish();
        }
    };


    /**
     * 検出されたデバイスに反応するのと検出が終わったときにactivity_titleを変えるBroadcastReceiver　
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // デバイスを検出したとき
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // IntentからBluetoothDevice を持ってくる
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // ペアリング済みなら飛ばす
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // 検出が終わったらactivity_titleを変える
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setProgressBarIndeterminateVisibility(false);
                setTitle(R.string.select_device);
                if (mNewDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    mNewDevicesArrayAdapter.add(noDevices);
                }
            }
        }
    };

    private void connect(){
        // 接続するので検出を止める
        mBtAdapter.cancelDiscovery();

        // 複数のデバイスのMAC addressを取得する, つまりはViewの中の最後の17のchar　× 台数
//        ArrayList<String> address = new ArrayList<>();
        SparseBooleanArray checkeditempositions = pairedListView.getCheckedItemPositions();
        String[] address = new String[pairedListView.getCheckedItemCount()];

        for(int i=0; i<pairedListView.getCount();i++){
            if(checkeditempositions.get(i) == true) {
                //マッピングされている(選択されている)項目だった場合は文字列に連結する
                int key = checkeditempositions.keyAt(i);
                String info = pairedListView.getItemAtPosition(key).toString();
//                address.add(info.substring(info.length() - 17));
                address[i] = info.substring(info.length() - 17);
            }
        }

//        for(int i=0; i<address.size();i++)
//        Toast.makeText(this, i +"台目"+ address.get(i),Toast.LENGTH_SHORT).show();

        for(int i=0; i < address.length; i++)
            Toast.makeText(this,i + "台目" + address[i],Toast.LENGTH_SHORT).show();

        // Intentを作りMAC addressを渡す
//        Intent intent = new Intent();
//        intent.putStringArrayListExtra(EXTRA_DEVICE_ADDRESS, address);


        // resultをセットしこのアクティビティを終了する
//        setResult(Activity.RESULT_OK, intent);
        finish();
    }

}
