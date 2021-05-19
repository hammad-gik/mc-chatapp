package com.example.chatapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.example.chatapp.models.ChatDialog;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 *
 * create an instance of this fragment.
 */
public class ChatListFragment extends Fragment {

    ArrayList<ChatDialog> chats = new ArrayList<>();

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    DialogsList dialogsList;

    Button startChatButton;

    public ChatListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);

        dialogsList = (DialogsList) view.findViewById(R.id.chats_list);

        startChatButton = (Button) view.findViewById(R.id.start_chat_button);

        DialogsListAdapter dialogsListAdapter = new DialogsListAdapter<ChatDialog>(new ImageLoader() {
            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {

                if(!url.equals(""))
                    Picasso.get().load(url).into(imageView);
            }
        });

        dialogsList.setAdapter(dialogsListAdapter);

        startChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startChat("My first chat");
            }
        });

        getChatsList();

        return view;
    }

    private void getChatsList() {

        firestore.collection("chats")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("Chat List", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("Chat List", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void startChat(String firstMessage) {

        // Create a new user with a first and last name
        Map<String, Object> chat = new HashMap<>();
        chat.put("id", "123");
        chat.put("dialogPhoto", "");
        chat.put("dialogName", firstMessage);
        chat.put("unreadCount", 0);

        firestore.collection("chats")
                .add(chat)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("Chat List", "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Chat List", "Error adding document", e);
                    }
                });
    }
}