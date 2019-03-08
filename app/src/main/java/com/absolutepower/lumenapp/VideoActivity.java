package com.absolutepower.lumenapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.twilio.video.CameraCapturer;
import com.twilio.video.ConnectOptions;
import com.twilio.video.LocalAudioTrack;
import com.twilio.video.LocalDataTrack;
import com.twilio.video.LocalParticipant;
import com.twilio.video.LocalVideoTrack;
import com.twilio.video.RemoteAudioTrack;
import com.twilio.video.RemoteAudioTrackPublication;
import com.twilio.video.RemoteDataTrack;
import com.twilio.video.RemoteDataTrackPublication;
import com.twilio.video.RemoteParticipant;
import com.twilio.video.RemoteVideoTrack;
import com.twilio.video.RemoteVideoTrackPublication;
import com.twilio.video.Room;
import com.twilio.video.TwilioException;
import com.twilio.video.Video;
import java.util.ArrayList;
import okhttp3.OkHttpClient;

public class VideoActivity extends AppCompatActivity {

    private static final String TAG = VideoActivity.class.getName();
    private static Context mContext;
    private String room;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mContext = getApplicationContext();

        //Get permissions to use camera and microphone for video
        if (!checkPermissionForCameraAndMicrophone()) {
            requestPermissionForCameraAndMicrophone();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        //Get the questionIndex which will be the room name
        Intent intent = getIntent();
        room = intent.getStringExtra("questionIndex");

        //Connect to the room to begin tutoring session
        connectToRoom();
    }

    public static Context getContext() {
        return mContext;
    }

    private Room.Listener roomListener() {
        return new Room.Listener() {
            @Override
            public void onConnected(Room room) {
                TextView textView = findViewById(R.id.textView7);
                textView.setText("Connected to " + room.getName());

            }
            public void onDisconnected(Room room, TwilioException e){
                TextView textView = findViewById(R.id.textView7);
                textView.setText("Disconnected from " + room.getName());
                mContext = getApplicationContext();
                if (GetToken.shouldRemoveQuestion == true) { //Participant is a tutor
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);
                } else { //Participant is a student
                    Intent intent = new Intent(mContext, RatingActivity.class);
                    mContext.startActivity(intent);
                }
            }
            public void onRecordingStarted(Room room){}
            public void onRecordingStopped(Room room){}
            public void onParticipantConnected(Room room, RemoteParticipant participant) {
                TextView textView = findViewById(R.id.textView7);
                textView.setText(participant.getIdentity() + " has joined the room.");
                participant.setListener(remoteParticipantListener());
            }
            public void onParticipantDisconnected(Room room, RemoteParticipant participant) {
                TextView textView = findViewById(R.id.textView7);
                textView.setText(participant.getIdentity() + " has left the room.");
            }
            public void onConnectFailure(Room room, TwilioException e){}

        };
    }

    public void connectToRoom() {
        // Create an audio track
        boolean enable = true;
        LocalAudioTrack localAudioTrack = LocalAudioTrack.create(mContext, enable);

        // A video track requires an implementation of VideoCapturer
        CameraCapturer cameraCapturer = new CameraCapturer(mContext, CameraCapturer.CameraSource.FRONT_CAMERA);

        // Create a video track
        LocalVideoTrack localVideoTrack = LocalVideoTrack.create(mContext, enable, cameraCapturer);

        // Rendering a local video track requires an implementation of VideoRenderer
        com.twilio.video.VideoView mirrorVideoView = findViewById(R.id.videoView2);

        mirrorVideoView.setMirror(true);

        // Render a local video track to preview your camera
        localVideoTrack.addRenderer(mirrorVideoView);

        ArrayList<LocalAudioTrack> localAudioTracks = new ArrayList<>();
        localAudioTracks.add(localAudioTrack);

        ArrayList<LocalVideoTrack> localVideoTracks = new ArrayList<>();
        localVideoTracks.add(localVideoTrack);

        ArrayList<LocalDataTrack> localDataTracks = new ArrayList<>();

        ConnectOptions connectOptions = new ConnectOptions.Builder(GetToken.accessToken)
                .roomName(room)
                .audioTracks(localAudioTracks)
                .videoTracks(localVideoTracks)
                .dataTracks(localDataTracks)
                .build();

        final Room mRoom = Video.connect(mContext, connectOptions, roomListener());

        //When "end call" button is pressed, hang up the call and go back to home activity
        Button mNewAccountButton = findViewById(R.id.button7);
        mNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRoom.disconnect();
            }
        });

    }

    private boolean checkPermissionForCameraAndMicrophone() {
        int resultCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return (resultCamera == PackageManager.PERMISSION_GRANTED) && (resultMic == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermissionForCameraAndMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            Toast.makeText(this, "Camera and Microphone permissions needed. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    private RemoteParticipant.Listener remoteParticipantListener() {
        final com.twilio.video.VideoView primaryVideoView = findViewById(R.id.videoView);
        return new RemoteParticipant.Listener() {
            @Override
            public void onVideoTrackSubscribed(RemoteParticipant participant,
                                                RemoteVideoTrackPublication remoteVideoTrackPublication,
                                                RemoteVideoTrack remoteVideoTrack) {
                primaryVideoView.setMirror(false);
                remoteVideoTrack.addRenderer(primaryVideoView);
            }
            public void onAudioTrackSubscribed(RemoteParticipant participant,
                                               RemoteAudioTrackPublication remoteAudioTrackPublication,
                                               RemoteAudioTrack remoteVideoTrack) {}
            public void onAudioTrackUnsubscribed(RemoteParticipant participant,
                                               RemoteAudioTrackPublication remoteAudioTrackPublication,
                                               RemoteAudioTrack remoteVideoTrack) {}
            public void onVideoTrackUnsubscribed(RemoteParticipant participant,
                                               RemoteVideoTrackPublication remoteVideoTrackPublication,
                                               RemoteVideoTrack remoteVideoTrack) {}
            public void onVideoTrackSubscriptionFailed(RemoteParticipant participant,
                                               RemoteVideoTrackPublication remoteVideoTrackPublication,
                                               TwilioException e) {}
            public void onDataTrackSubscriptionFailed(RemoteParticipant participant,
                                                       RemoteDataTrackPublication remoteDataTrackPublication,
                                                       TwilioException e) {}
            public void onAudioTrackSubscriptionFailed(RemoteParticipant participant,
                                                       RemoteAudioTrackPublication remoteAudioTrackPublication,
                                                       TwilioException e) {}
            public void onDataTrackPublished(RemoteParticipant participant,
                                             RemoteDataTrackPublication remoteDataTrackPublication) {}
            public void onAudioTrackPublished(RemoteParticipant participant,
                                             RemoteAudioTrackPublication remoteAudioTrackPublication) {}
            public void onDataTrackUnpublished(RemoteParticipant participant,
                                               RemoteDataTrackPublication remoteDataTrackPublication) {}
            public void onAudioTrackUnpublished(RemoteParticipant participant,
                                               RemoteAudioTrackPublication remoteAudioTrackPublication) {}
            public void onDataTrackSubscribed(RemoteParticipant participant,
                                             RemoteDataTrackPublication remoteDataTrackPublication,
                                             RemoteDataTrack remoteDataTrack) {}
            public void onDataTrackUnsubscribed(RemoteParticipant participant,
                                              RemoteDataTrackPublication remoteDataTrackPublication,
                                              RemoteDataTrack remoteDataTrack) {}
            public void onAudioTrackEnabled(RemoteParticipant participant,
                                             RemoteAudioTrackPublication remoteAudioTrackPublication) {}
            public void onVideoTrackPublished(RemoteParticipant participant,
                                                RemoteVideoTrackPublication remoteVideoTrackPublication) {}
            public void onVideoTrackUnpublished(RemoteParticipant participant,
                                                RemoteVideoTrackPublication remoteVideoTrackPublication) {}
            public void onVideoTrackDisabled(RemoteParticipant participant,
                                                RemoteVideoTrackPublication remoteVideoTrackPublication) {}
            public void onVideoTrackEnabled(RemoteParticipant participant,
                                                RemoteVideoTrackPublication remoteVideoTrackPublication) {}
            public void onAudioTrackDisabled(RemoteParticipant participant,
                                                RemoteAudioTrackPublication remoteAudoTrackPublication) {}
        };
    }
}
