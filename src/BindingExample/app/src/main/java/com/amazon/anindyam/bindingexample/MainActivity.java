package com.amazon.anindyam.bindingexample;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {


    private static final String TAG = "MainActivity";
    BackgroundService mService;
    boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startBGService();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton bindButton = findViewById(R.id.fab);
        bindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBound) {
                    Toast.makeText(view.getContext(), "Already bound", Toast.LENGTH_LONG).show();
                } else {
                    boolean result = bindToService();
                    Toast.makeText(view.getContext(), "Binding success: " + result, Toast.LENGTH_LONG).show();
                }
            }
        });

        FloatingActionButton getButton = findViewById(R.id.fab1);
        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mService != null) {
                    if (mBound) {
                        int count = mService.getBoundCount();
                        Toast.makeText(view.getContext(), "Bound service says I'm bound to: " + count, Toast.LENGTH_LONG).show();
                    } else {
                        Log.i(TAG, "Attempting to call unbound service");
                        try {
                            int count = mService.getBoundCount();
                            Toast.makeText(view.getContext(), "Unbound service says I'm bound to: " + count, Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            Log.e(TAG, "Cannot talk to unbound BG service");
                        }
                    }
                }
            }
        });


        FloatingActionButton unbindButton = findViewById(R.id.fab2);
        unbindButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mBound) {
                    Toast.makeText(view.getContext(), "Not bound", Toast.LENGTH_LONG).show();
                } else {
                    boolean result = unbindFromService();
                    Toast.makeText(view.getContext(), "Unbinding success: " + result, Toast.LENGTH_LONG).show();
                }
            }
        });

        FloatingActionButton startServiceButton = findViewById(R.id.fab3);
        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBound) {
                    Toast.makeText(view.getContext(), "Starting BG service while already bound", Toast.LENGTH_LONG).show();
                } else if (mService != null) {
                    Toast.makeText(view.getContext(), "Attempting to start BG service", Toast.LENGTH_LONG).show();
                }
                startBGService();
            }
        });

        FloatingActionButton killServiceButton = findViewById(R.id.fab4);
        killServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBound) {
                    mService.stopService();
                    Toast.makeText(view.getContext(), "Stopped BG service", Toast.LENGTH_LONG).show();
                } else if (mService != null) {
                    Toast.makeText(view.getContext(), "Attempting to stop unbound service", Toast.LENGTH_LONG).show();
                    mService.stopService();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "Activity started");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "Activity stopped");

    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            mService = binder.getService();
            Log.i(TAG, "Bound to BG service");
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.i(TAG, "Disconnected from BG service");
            mBound = false;
        }
    };

    private boolean startBGService() {
        Intent intent = new Intent(this, BackgroundService.class);
        return (null != startService(intent));
    }

    private boolean bindToService() {
        // Bind to BG Service
        Intent intent = new Intent(this, BackgroundService.class);
        Log.i(TAG, "Will bind to BG service now");
        boolean result = bindService(intent, connection, Context.BIND_NOT_FOREGROUND);
        Log.i(TAG, "Result from binding: " + result);
        return result;
    }

    private boolean unbindFromService() {
        Log.i(TAG, "Will unbind to BG service now");
        try {
            unbindService(connection);
        } catch (Exception e) {
            Log.e(TAG, "error unbinding: " + e);
            return false;
        }
        mBound = false;
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
