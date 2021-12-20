package com.share.bookR;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import static com.share.bookR.Constants.NEW_USER;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private NavHostFragment navHostFragment;
    private NavController navController;
    private BottomNavigationView bottomNavigationView;
    private AppBarConfiguration appBarConfiguration;
    private MaterialToolbar toolbar;
    private TextView mTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkAppUpdate();
        initViews();

        toolbar = findViewById(R.id.tool_bar);
        mTitle = findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        mTitle.setText(toolbar.getTitle());
        mTitle.setTextColor(getResources().getColor(R.color.white));


        setUpNavigation();

        mTitle.setText(toolbar.getTitle());

        SharedPreferences sharedPreferences=getSharedPreferences(Constants.SHARED_PREFS,MODE_PRIVATE);
        if (FirebaseAuth.getInstance().getCurrentUser()==null||sharedPreferences.getBoolean(NEW_USER,true)) {
            Editor editor = sharedPreferences.edit();
            editor.putBoolean(NEW_USER,false);
            if (FirebaseAuth.getInstance().getCurrentUser()!=null)
            editor.apply();
            NavGraph navGraph = navController.getGraph();
            navGraph.setStartDestination(R.id.bookStoreFragment);
            navController.setGraph(navGraph);
        }

    }

    private void checkAppUpdate() {
        final AppUpdateManager appUpdateManager= AppUpdateManagerFactory.create(MainActivity.this);
        Task<AppUpdateInfo> appUpdateInfoTask=appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo result) {
                if (result.updateAvailability()== UpdateAvailability.UPDATE_AVAILABLE&&result.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)){
                    try {
                        appUpdateManager.startUpdateFlowForResult(result,AppUpdateType.IMMEDIATE,MainActivity.this,REQUEST_CODE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setUpNavigation() {
        navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.libraryFragment, R.id.readingFragment,R.id.accountsFragment,R.id.bookStoreFragment).build();
        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupWithNavController(toolbar,navController,appBarConfiguration);
        //NavigationUI.setupActionBarWithNavController(this,navController,appBarConfiguration);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                    switch (destination.getId()){

                        case R.id.accountsFragment:
                        case R.id.bookStoreFragment:
                        case R.id.libraryFragment:
                        case R.id.readingFragment:
                            showBottomNav();
                            break;
                        /*default:
                            hideBottomNav();*/
                    }
                mTitle.setText(toolbar.getTitle());
            }
        });
    }

    private void hideBottomNav() {
        bottomNavigationView.setVisibility(View.INVISIBLE);
    }

    private void showBottomNav() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    private void initViews() {
        bottomNavigationView=findViewById(R.id.bottom_nav_view);
    }

    @Override
    public boolean onSupportNavigateUp() {
        //NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}