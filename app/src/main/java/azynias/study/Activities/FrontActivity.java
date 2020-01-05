package azynias.study.Activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.support.v4.app.DialogFragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.example.inappbilling.inappbilling.util.util.IabHelper;
import com.example.inappbilling.inappbilling.util.util.IabResult;
import com.example.inappbilling.inappbilling.util.util.Purchase;


import azynias.study.DataHandlers.TierDBHandler;
import azynias.study.DataHandlers.UserPreferences;
import azynias.study.Fragments.FrontFragment;
import azynias.study.Fragments.SettingsDialogFragment;
import azynias.study.R;

public class FrontActivity extends AppCompatActivity{
    private BottomNavigationView bottomNavigationView;
    private boolean beginner;
    private UserPreferences userPrefs;
    IInAppBillingService mService;
    IabHelper helper;

    static final String ITEM_SKU = "android.test.purchased";
    static String productTier = "";
    static String tierBuy = "";
    static String hey = "";
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);


        Context context = getApplication();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final FrontFragment frontFrag = new FrontFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.dashboard_material_frag, frontFrag);

        ft.commit();

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_shop:
                        shopDialog();
                        return true;
                    case R.id.action_practice:
                        startActivity(new Intent(FrontActivity.this, QuizActivity.class));
                        return true;
                    case R.id.action_progress:
                        frontFrag.tiersLaunch();
                        return true;
                    case R.id.action_study:
                        if(unlocked())
                            startActivity(new Intent(FrontActivity.this, StudyActivity.class));
                        else {
                            Toast.makeText(FrontActivity.this, "The tier you are on has not been purchased!",
                                    Toast.LENGTH_SHORT).show();
                        }
                        return true;
                }
                return true;
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_settings) {
            showSettingsDialog();
        }
        else if(item.getItemId()==R.id.action_refresh) {
            TierDBHandler.getInstance(getBaseContext()).setUserPrefs();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            FrontFragment fragment = (FrontFragment) getSupportFragmentManager().findFragmentById(R.id.dashboard_material_frag);

            ft.detach(fragment).attach(fragment).commit();
        }
        return super.onOptionsItemSelected(item);
    }
    private void showSettings() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = getLayoutInflater().inflate(R.layout.settings_dialogue, null);

        TextView info = (TextView) view.findViewById(R.id.info_dialog);
        info.setText("View our tutorial at https://pastebin.com/P45WzB66");

        Button close = new Button(this);
        close.setText("Close");
        close.setBackgroundColor(Color.RED);



     //   LinearLayout layout = (LinearLayout) view.findViewById(R.id);

        final RadioGroup difficulty = (RadioGroup) findViewById(R.id.settings_choice);
        RadioButton multChoice = (RadioButton) findViewById(R.id.settings_mult_choice);
        RadioButton normal = (RadioButton) findViewById(R.id.settings_normal);

        difficulty.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                RadioButton ticked = (RadioButton) view.findViewById(checkedId);
                hey = ticked.getText().toString();
            }
        });

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            //    TierDBHandler.getInstance(;
                //TierDBHandler.getInstance(getContext()).arrangeTiers();
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
    }

    private void showSettingsDialog() {
        FragmentManager fm = getSupportFragmentManager();
        SettingsDialogFragment dialogFrag = SettingsDialogFragment.newInstance("Settings");
        dialogFrag.show(fm, "settings_dialogue");
    }

    private boolean unlocked() {
        int currentTier = tiers(UserPreferences.getInstance().getTier());
        int unlockedTier = tiers(UserPreferences.getInstance().getUnlockedTier());
        if(currentTier>unlockedTier)
            return false;
        return true;

    }

    private int tiers(String preceedingTier) {
        if(preceedingTier.equals("Bronze"))
            return 1;
        if(preceedingTier.equals("Copper"))
            return 2;
        if(preceedingTier.equals("Silver"))
            return 3;
        if(preceedingTier.equals("Gold"))
            return 4;
        if(preceedingTier.equals("Platinum"))
            return 5;
        if(preceedingTier.equals("Diamond"))
            return 6;
        return 0;
    }

    public String nextTier(String preceedingTier) {
        if(preceedingTier.equals("Bronze"))
            return "Copper";
        if(preceedingTier.equals("Copper"))
            return "Silver";
        if(preceedingTier.equals("Silver"))
            return "Gold";
        if(preceedingTier.equals("Gold"))
            return "Platinum";
        if(preceedingTier.equals("Platinum"))
            return "Diamond";
        return "";
    }

    public String[] getProducts(int start) {

        if(start==1) {
            return new String[] {"Copper ($1.50)", "Copper, and Silver ($2.50)", "Copper, Silver, and Gold ($3.50)"};
        }
        else if(start==2) { // end may be redundant here.
            return new String[] {"Silver ($1.50)", "Silver and Gold ($2.50)"};
        }
        else if(start==3) {
            return new String[] {"Gold ($1.50)"};
        }
        return null;
    }

    public String getProductKey(String radioText) {
        if(radioText.equals("Copper, Silver, and Gold")) {
            return "coppersilvergold";
        }
        else if(radioText.equals("Silver and Gold")) {
            return "silvergolds"; // its silvergolds not silvergold as google play console is retarded.
        }
        else if(radioText.equals("Copper, and Silver")) {
            return "copppersilver";
        }
        else if(radioText.equals("Copper")) {
            return "copper";
        }
        else if(radioText.equals("Silver")) {
            return "silver";
        }
        else if(radioText.equals("Gold")) {
            return "gold";
        }
        return null;
    }

    public int maxTiersPurchase() {
        String tier = UserPreferences.getInstance().getUnlockedTier();
        int current = tiers(tier);
        int max = tiers("Gold") - current; // change gold here
        return max;
    }

    public void initializeTierRadio(RadioGroup group) {
        int max = maxTiersPurchase();
        int start = tiers(UserPreferences.getInstance().getUnlockedTier());
        Log.d("tieraaas", UserPreferences.getInstance().getUnlockedTier());
        String[] products = getProducts(start);

        if (products.length > 0) {
            for (int i = 0; i < max; i++) {
                RadioButton tier = new RadioButton(this);
                String text = products[i];
                tier.setText(text);

                tier.setId(i);
                group.addView(tier);
            }
        }
    }

    private void shopDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = getLayoutInflater().inflate(R.layout.shop_dialog, null);
        final String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmdZuF42vm0fVde6NzihGwo3h9p0tpk7fRbRwrnQkZpzo5ypqvsoiWsOB5EWxQ/KML0PWUQxyr6JSpZRD90OHgUx6Y2MBqghyO5MUhc4xEsfGY6Si/x/L10sGuU6UR0KvccrVzUZ45VzuIX8hHtgdKvmzrctm6VN9/ysdX3V9bt+ZuK056ZKUbN+p7kRcc3HMCcYURXxxMbVcQ2DBH1C3tFJw3juU7uu2mnfmH5xMbnKY6GFZRrqsGy0HBoz4SeDsSHWwmlk5WLa1hhDmALf8VG1FswLNre+FoExtMtgtl8PigdQbR6aXm1Z1qs4lEKh8qnfo8Omu8IwbO8u3aOBMzwIDAQAB";

        ServiceConnection mServiceConn = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService = null;
            }

            @Override
            public void onServiceConnected(ComponentName name,
                                           IBinder service) {
                mService = IInAppBillingService.Stub.asInterface(service);
            }
        };

        TextView info = (TextView) view.findViewById(R.id.info_dialog);
        info.setText("Tiers for sale: ");

        Button close = new Button(this);
        close.setText("Purchase");
        close.setBackgroundColor(Color.RED);

        RadioGroup tiers = new RadioGroup(this);
        initializeTierRadio(tiers);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.initial_layout_dialog);
        LinearLayout layoutBox = (LinearLayout) view.findViewById(R.id.boxes_shop_dialog);

        layout.addView(tiers);
        layout.addView(close);
        helper = new IabHelper(this, base64EncodedPublicKey);

        tiers.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                tierBuy = ((RadioButton)group.getChildAt(checkedId)).getText().toString();
                productTier = getProductKey(tierBuy);
                Log.d("productkey", productTier+"");
            }
        });

        helper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result)
                                       {
                                           if (!result.isSuccess()) {
                                               Log.d("billingx", "In-app Billing setup failed: " +
                                                       result);
                                           } else {
                                               Log.d("billingx", "In-app Billing is set up OK");
                                           }
                                       }
                                   });
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        final IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
                = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result,
                                              Purchase purchase)
            {
                if (result.isFailure()) {
                    // Handle error
                    return;
                }
                else if (purchase.getSku().equals(ITEM_SKU)) {
                    String[] tiers = tierBuy.split(" ");
                    int last = tiers.length-1;
                    TierDBHandler.getInstance(FrontActivity.this).updateTiersPurchase(tiers[last]);
                }

            }
        };

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helper.launchPurchaseFlow(FrontActivity.this, productTier, 10001, mPurchaseFinishedListener, "tiersPurchased");
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data)
    {
        if (!helper.handleActivityResult(requestCode,
                resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



}
