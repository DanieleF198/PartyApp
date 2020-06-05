package com.spotify.sdk.android.authentication.sample.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.spotify.sdk.android.authentication.sample.R;
import com.spotify.sdk.android.authentication.sample.ws.RetrofitInstance;
import com.spotify.sdk.android.authentication.sample.ws.model.Lobby;
import com.spotify.sdk.android.authentication.sample.ws.model.User;
import com.spotify.sdk.android.authentication.sample.ws.service.UserService;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LobbyAdapter extends RecyclerView.Adapter<LobbyAdapter.ViewHolder>{

    public interface OnItemClickListener {
        void onItemClick(Lobby lobby);
    }

    private List<Lobby> mLobbyList;
    private User mhostUser;
    private Context context;
    private OnItemClickListener listener;



    public LobbyAdapter(List<Lobby> lobbyList, Context context, LobbyAdapter.OnItemClickListener listener) {
        mLobbyList = lobbyList;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LobbyAdapter.ViewHolder onCreateViewHolder( ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.lobby_row, parent, false);
        return new LobbyAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LobbyAdapter.ViewHolder holder, int position) {
        holder.bind(mLobbyList.get(position), listener);
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


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView1;
        public TextView textView2;
        public TextView textView3;
        public TextView textView4;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView1 = itemView.findViewById(R.id.lobbyRowName);
            textView2 = itemView.findViewById(R.id.genreOrMoodRow);
            textView3 = itemView.findViewById(R.id.partecipantNumberRow);
            textView4 = itemView.findViewById(R.id.hostID);
        }

        public void bind(final Lobby lobby, final LobbyAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(lobby);
                }
            });
        }
    }
}
