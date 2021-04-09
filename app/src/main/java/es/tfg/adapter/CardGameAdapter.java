package es.tfg.adapter;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import es.tfg.R;
import es.tfg.model.CardViewGames;

public class CardGameAdapter extends RecyclerView.Adapter<CardGameAdapter.GameViewHolder> {
    private ArrayList<CardViewGames> data;
    private ItemClickListener mClickListener;

    public CardGameAdapter(ArrayList<CardViewGames> data) {
        this.data = data;
    }

    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GameViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_game, parent, false));
    }

    @Override
    public void onBindViewHolder(GameViewHolder holder, int position) {
        CardViewGames cardViewGames = data.get(position);
        byte[] decodedString = Base64.decode(cardViewGames.getImage(), Base64.DEFAULT);
        holder.imgGame.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
        holder.txtGame.setText(cardViewGames.getName());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public CardViewGames getItem(int id) {
        return data.get(id);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);

    }

    class GameViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgGame;
        TextView txtGame;

        public GameViewHolder(View itemView) {
            super(itemView);
            imgGame = (ImageView) itemView.findViewById(R.id.imageGame);
            txtGame = (TextView) itemView.findViewById(R.id.nameGame);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}