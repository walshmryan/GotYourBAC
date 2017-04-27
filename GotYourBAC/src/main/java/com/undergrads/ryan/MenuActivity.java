package com.undergrads.ryan;

/**
 * Created by sarahmedeiros on 4/24/17.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.view.Menu;
import com.google.example.games.basegameutils.BaseGameUtils;
import java.util.ArrayList;


public class MenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener,
        google_mainmenu.gMainListener, GoogleApiClient.OnConnectionFailedListener,
        game_picker_fragment.gamePickerListener, pick_game_mode.gameModeListener, GoogleApiClient.ConnectionCallbacks,
        StroopGame.PlayGameListener, stroop_game_done.stroopGameListener,OnInvitationReceivedListener,
        OnTurnBasedMatchUpdateReceivedListener{

    private GoogleApiClient mGoogleApiClient;
    private boolean mResolvingError = false;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    private static final String DIALOG_ERROR = "dialog_error";
    public TurnBasedMatch mMatch;
    public StroopTurn mTurnData;
    // Local convenience pointers
    public TextView mDataView;
    public TextView mTurnTextView;
    public boolean isDoingTurn = false;
    public static final String TAG = "game";
    private AlertDialog mAlertDialog;
    private TurnBasedMatch mTurnBasedMatch;
    private boolean mResolvingConnectionFailure = false;
    private static final int RC_SIGN_IN = 9001;
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;

    private double stroopScore;
    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //set up drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //set up nav view
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View hView =  navigationView.getHeaderView(0);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
//            TextView nav_user = (TextView)hView.findViewById(R.id.txtName);
            TextView nav_user_email = (TextView)hView.findViewById(R.id.txtEmail);
//            nav_user.setText(name);
            nav_user_email.setText(email);
        }


        HomeFragment fragment = new HomeFragment();
        String homeFrag = "Home Screen";
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment,homeFrag)
                .addToBackStack(homeFrag)
                .commit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestServerAuthCode("479703936056-ev6vmikc6n4r89qs1vgeihpkv4t3rn8a.apps.googleusercontent.com")
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,/* FragmentActivity */ this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        Toast.makeText(MenuActivity.this, R.string.welcome, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        String bac = "BAC";

        //load the correct fragment based on which menu icon is clicked
        if (id == R.id.nav_bac) {
            BacActivity fragment = new BacActivity();
            //go to bac calc
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(bac)
                    .commit();
        }else if(id == R.id.nav_home){
            HomeFragment fragment = new HomeFragment();
            String homeFrag = "Home Screen";
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(homeFrag)
                    .commit();

        }else if (id == R.id.nav_games) {
//          go to games page
//            SkeletonActivity fragment = new SkeletonActivity();
            google_mainmenu fragment = new google_mainmenu();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .commit();
        } else if (id == R.id.nav_ice) {
            FragmentICE fragment = new FragmentICE();
            String editUser = "ice";
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(editUser)
                    .commit();
        } else if (id == R.id.nav_manage) {
            EditUser fragment = new EditUser();
            String editUser = "Edit User";
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(editUser)
                    .commit();
        } else if (id == R.id.nav_logout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            Intent i = new Intent(this, MenuActivity.class);
            intent.putExtra("finish", true); // if you are checking for this in your other Activities
            startActivity(i);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
         Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void goToGamePickerFrag() {
        String gamePicker = "pick game";

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

        game_picker_fragment fragment = new game_picker_fragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment, gamePicker)
                .commit();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }
    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }
    public GoogleApiClient getGoogleApiClient() {

        return mGoogleApiClient;
    }

    @Override
    public void goToStroop(View view) {
        String gameMode = "game mode";
        pick_game_mode fragment = new pick_game_mode();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment,gameMode)
                .commit();
    }

    @Override
    public void goToTilt() {
//        StroopBaselineFragment fragment = new StroopBaselineFragment();
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.frame_layout, fragment)
//                .commit();
    }
//    @Override
//    public void signOnClick() {
//        findViewById(R.id.sign_in_button).setOnClickListener(this);
//    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);

        if (request == RC_SIGN_IN) {
            mSignInClicked = false;
            mResolvingConnectionFailure = false;
            if (response == Activity.RESULT_OK) {
                mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, request, response, R.string.signin_other_error);
            }
        } else if (request == RC_LOOK_AT_MATCHES) {
            // Returning from the 'Select Match' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            TurnBasedMatch match = data
                    .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                updateMatch(match);
            }

            Log.d(TAG, "Match = " + match);
        } else if (request == RC_SELECT_PLAYERS) {
            // Returned from 'Select players to Invite' dialog

            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }

            // get the invitee list
            final ArrayList<String> invitees = data
                    .getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

            // get automatch criteria
            Bundle autoMatchCriteria = null;

            int minAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
            int maxAutoMatchPlayers = data.getIntExtra(
                    Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

            if (minAutoMatchPlayers > 0) {
                autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                        minAutoMatchPlayers, maxAutoMatchPlayers, 0);
            } else {
                autoMatchCriteria = null;
            }

            TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                    .addInvitedPlayers(invitees)
                    .setAutoMatchCriteria(autoMatchCriteria).build();

            // Start the match
            Games.TurnBasedMultiplayer.createMatch(mGoogleApiClient, tbmc).setResultCallback(
                    new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                            processResult(result);
                        }
                    });
//            showSpinner();
        }
    }


    private void processResult(TurnBasedMultiplayer.CancelMatchResult result) {
//        dismissSpinner();

        if (!checkStatusCode(null, result.getStatus().getStatusCode())) {
            return;
        }

        isDoingTurn = false;

        showWarning("Match",
                "This match is canceled.  All other players will have their game ended.");
    }

    private void processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        TurnBasedMatch match = result.getMatch();
//        dismissSpinner();

        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }

        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            updateMatch(match);
            return;
        }

        startMatch(match);
    }



    private void processResult(TurnBasedMultiplayer.LeaveMatchResult result) {
        TurnBasedMatch match = result.getMatch();
//        dismissSpinner();
        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }
        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);
        showWarning("Left", "You've left this match.");
    }


    public void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
        TurnBasedMatch match = result.getMatch();
//        dismissSpinner();
        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }
        if (match.canRematch()) {
            askForRematch();
        }

        isDoingTurn = (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN);

        if (isDoingTurn) {
            updateMatch(match);
            return;
        }

//        setViewVisibility();
    }

    // startMatch() happens in response to the createTurnBasedMatch()
    // above. This is only called on success, so we should have a
    // valid match object. We're taking this opportunity to setup the
    // game, saving our initial state. Calling takeTurn() will
    // callback to OnTurnBasedMatchUpdated(), which will show the game
    // UI.
    public void startMatch(TurnBasedMatch match) {
        mTurnData = new StroopTurn();
        // Some basic turn data
        mTurnData.data = "First turn";

        mMatch = match;

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, match.getMatchId(),
                mTurnData.persist(), myParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });

    }
    // Upload your new gamestate, then take a turn, and pass it on to the next
    // player.
    public void onDoneClicked() {

        String nextParticipantId = getNextParticipantId();
        // Create the next turn
        mTurnData.turnCounter += 1;

        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, mMatch.getMatchId(),
                mTurnData.persist(), nextParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });

        mTurnData = null;
    }
    // If you choose to rematch, then call it and wait for a response.
    public void rematch() {
//        showSpinner();
        Games.TurnBasedMultiplayer.rematch(mGoogleApiClient, mMatch.getMatchId()).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                        processResult(result);
                    }
                });
        mMatch = null;
        isDoingTurn = false;
    }

    // Rematch dialog
    public void askForRematch() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Do you want a rematch?");

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Sure, rematch!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                rematch();
                            }
                        })
                .setNegativeButton("No.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

        alertDialogBuilder.show();
    }


    @Override
    public void startMatchButton() {
        Intent intent =
                Games.TurnBasedMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 7, true);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }


    // This is the main function that gets called when players choose a match
    // from the inbox, or else create a match and want to start it.
    public void updateMatch(TurnBasedMatch match) {
        mMatch = match;

        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                showWarning("Canceled!", "This game was canceled!");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                showWarning("Expired!", "This game is expired.  So sad!");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                showWarning("Waiting for auto-match...",
                        "We're still waiting for an automatch partner.");
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    showWarning(
                            "Complete!",
                            "This game is over; someone finished it, and so did you!  There is nothing to be done.");
                    break;
                }

                // Note that in this state, you must still call "Finish" yourself,
                // so we allow this to continue.
                showWarning("Complete!",
                        "This game is over; someone finished it!  You can only finish it now.");
        }

        // OK, it's active. Check on turn status.
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                mTurnData = StroopTurn.unpersist(mMatch.getData());
                setGameplayUI();
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                // Should return results.
                showWarning("Alas...", "It's not your turn.");
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                showWarning("fGood inititative!",
                        "Still waiting for invitations.\n\nBe patient!");
        }

        mTurnData = null;

//        setViewVisibility();
    }

    /**
     * Get the next participant. In this function, we assume that we are
     * round-robin, with all known players going before all automatch players.
     * This is not a requirement; players can go in any order. However, you can
     * take turns in any order.
     *
     * @return participantId of next player, or null if automatching
     */
    public String getNextParticipantId() {

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (mMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            return null;
        }
    }


    // Update the visibility based on what state we're in.

//        boolean isSignedIn = (mGoogleApiClient != null) && (mGoogleApiClient.isConnected());

    // Switch to gameplay view.
    public void setGameplayUI() {
        isDoingTurn = true;
//        setViewVisibility();
//        mDataView.setText(mTurnData.data);
//        mTurnTextView.setText("Turn " + mTurnData.turnCounter);

        String stroopGame = "stroop mp";
        StroopGame fragment = new StroopGame();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment, stroopGame)
                .commit();

    }

    @Override
    public void onCheckGamesClicked() {
        Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_LOOK_AT_MATCHES);
    }

    // Generic warning/info dialog
    public void showWarning(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                    }
                });

        // create alert dialog
        mAlertDialog = alertDialogBuilder.create();

        // show it
        mAlertDialog.show();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected(): Connection successful");

        // Retrieve the TurnBasedMatch from the connectionHint
        if (connectionHint != null) {
            mTurnBasedMatch = connectionHint.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (mTurnBasedMatch != null) {
                if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
                    Log.d(TAG, "Warning: accessing TurnBasedMatch when not connected");
                }

                updateMatch(mTurnBasedMatch);
                return;
            }
        }

//        setViewVisibility();
//
//        // As a demonstration, we are registering this activity as a handler for
//        // invitation and match events.
//
//        // This is *NOT* required; if you do not register a handler for
//        // invitation events, you will get standard notifications instead.
//        // Standard notifications may be preferable behavior in many cases.
//        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);
//
//        // Likewise, we are registering the optional MatchUpdateListener, which
//        // will replace notifications you would get otherwise. You do *NOT* have
//        // to register a MatchUpdateListener.
//        Games.TurnBasedMultiplayer.registerMatchUpdateListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended():  Trying to reconnect.");
        mGoogleApiClient.connect();
//        setViewVisibility();


    }
    // Returns false if something went wrong, probably. This should handle
    // more cases, and probably report more accurate results.
    private boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                // This is OK; the action is stored by Google Play Services and will
                // be dealt with later.
                Toast.makeText(
                        this,
                        "Stored action for later.  (Please remove this toast before release.)",
                        Toast.LENGTH_SHORT).show();
                // NOTE: This toast is for informative reasons only; please remove
                // it from your final application.
                return true;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(match, statusCode,
                        R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_already_rematched);
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(match, statusCode,
                        R.string.network_error_operation_failed);
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                showErrorMessage(match, statusCode,
                        R.string.client_reconnect_required);
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                showErrorMessage(match, statusCode, R.string.internal_error);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(match, statusCode,
                        R.string.match_error_inactive_match);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_locally_modified);
                break;
            default:
                showErrorMessage(match, statusCode, R.string.unexpected_status);
                Log.d(TAG, "Did not have warning or string to deal with: "
                        + statusCode);
        }

        return false;
    }

    public void showErrorMessage(TurnBasedMatch match, int statusCode,
                                 int stringId) {

        showWarning("Warning", getResources().getString(stringId));
    }


    @Override
    public void gameDone(double total) {
        stroopScore = total;
        onDoneClicked();

        String gameDone = "game finished";
        stroop_game_done fragment = new stroop_game_done();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment, gameDone)
                .commit();
    }
    public void gameAborted(double score){
        stroopScore = score;
        String gameDone = "game finished";
        stroop_game_done fragment = new stroop_game_done();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment, gameDone)
                .commit();
    }


    public void onFinishClicked() {
//        showSpinner();
        Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient, mMatch.getMatchId())
                .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });

        isDoingTurn = false;
//        setViewVisibility();
    }
    @Override
    public double getScore() {
        return stroopScore;
    }

    @Override
    public void goToMainGameScreen() {
        String gamePicker = "pick game";

        game_picker_fragment fragment = new game_picker_fragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment, gamePicker)
                .commit();
    }

    // Handle notification events.
    @Override
    public void onInvitationReceived(Invitation invitation) {
        Toast.makeText(
                this,
                "An invitation has arrived from "
                        + invitation.getInviter().getDisplayName(), Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onInvitationRemoved(String invitationId) {
        Toast.makeText(this, "An invitation was removed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTurnBasedMatchReceived(TurnBasedMatch match) {
        Toast.makeText(this, "A match was updated.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTurnBasedMatchRemoved(String matchId) {
        Toast.makeText(this, "A match was removed.", Toast.LENGTH_SHORT).show();

    }


    public void signOutOfGoogle(){
        mSignInClicked = false;
        Games.signOut(mGoogleApiClient);
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        String googleMain = "google main menu";
        google_mainmenu fragment = new google_mainmenu();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment, googleMain)
                .commit();
    }

    @Override
    public void loadSinglePlayerGame() {
        String stroopGame = "stroop quick";
        StroopGame fragment = new StroopGame();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_layout, fragment, stroopGame)
                .commit();
    }

}