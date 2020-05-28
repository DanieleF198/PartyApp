package com.spotify.sdk.android.authentication.sample;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LobbyAdapter extends RecyclerView.Adapter<LobbyAdapter.LobbyViewHolder>{
    private List<Lobby> mLobbyList;
    private User mhostUser;

    public static class LobbyViewHolder extends RecyclerView.ViewHolder{
        public TextView textView1;
        public TextView textView2;
        public TextView textView3;
        public TextView textView4;

        public LobbyViewHolder(View itemView){
            super(itemView);
            textView1 = itemView.findViewById(R.id.lobbyRowName);
            textView2 = itemView.findViewById(R.id.genreOrMoodRow);
            textView3 = itemView.findViewById(R.id.partecipantNumberRow);
            textView4 = itemView.findViewById(R.id.hostID);
        }
    }

    public LobbyAdapter(List<Lobby> lobbyList) {
        mLobbyList = lobbyList;
    }

    @NonNull
    @Override
    public LobbyViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lobby_row, parent, false);
        LobbyViewHolder lvh = new LobbyViewHolder(v);
        return lvh;
    }

    @Override
    public void onBindViewHolder(@NonNull LobbyViewHolder holder, int position) {
        Lobby currentItem = mLobbyList.get(position);
        holder.textView1.setText(currentItem.getName());
        if(currentItem.getGenre() != null)
        {
            holder.textView2.setText(currentItem.getGenre());
        }
        else{
            holder.textView2.setText(currentItem.getMood());
        }
        holder.textView3.setText("" + currentItem.getPartecipantNumber());

        UserService userService = RetrofitInstance.getRetrofitInstance().create(UserService.class);

        Call<User> callHost = userService.getUserById(currentItem.getHostID());

        callHost.enqueue(new Callback<User>(){

            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if(!response.isSuccessful()){
                    Log.d("UserHost Not Success", "some error");
                    return;
                }
                mhostUser = response.body();
                holder.textView4.setText(mhostUser.getUsername());
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.d("UserHost onFailure", "some error");
            }
        });
    }

    @Override
    public int getItemCount() {
        return mLobbyList.size();
    }

}
