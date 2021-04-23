package es.tfg.game;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.tfg.R;
import es.tfg.adapter.CardGameAdapter;
import es.tfg.model.CardViewGames;
import es.tfg.registration.SignIn;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class Game extends AppCompatActivity {
    private final int limit = 9;
    private RecyclerView rvGames;
    private NestedScrollView nestedScrollView;
    private ProgressBar progressBar;
    private ArrayList<CardViewGames> gameArray = new ArrayList<>();
    private CardGameAdapter adapter;
    private Bundle bundle = new Bundle();
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Toolbar bottom_toolbar = (Toolbar) findViewById(R.id.bottom_toolbar);
        setSupportActionBar(bottom_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        bottom_toolbar.setTitle("");
        bottom_toolbar.setSubtitle("");

        Toolbar top_toolbar = (Toolbar) findViewById(R.id.top_toolbar);
        setSupportActionBar(top_toolbar);
        getSupportActionBar().setTitle(R.string.game);

        nestedScrollView = (NestedScrollView) findViewById(R.id.scroll_game);
        rvGames = (RecyclerView) findViewById(R.id.rvGames);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);


        getData(page, limit);

        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (!v.canScrollVertically(1)) {
                    page++;
                    getData(page, limit);
                }
            }
        });

    }

    public void goSignIn(View view) {
        startActivity(new Intent(Game.this, SignIn.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    public void goGame(View view) {
        startActivity(new Intent(Game.this, Game.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void getData(int page, int limit) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getResources().getString(R.string.ip_heroku))
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        GameInterface gameInterface = retrofit.create(GameInterface.class);
        Call<String> call = gameInterface.STRING_CALL(page, limit);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    progressBar.setVisibility(View.GONE);
                    try {

                        JSONArray jsonArray = new JSONArray(response.body());

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonobject = jsonArray.getJSONObject(i);
                            gameArray.add(new CardViewGames(
                                    jsonobject.getString("_id"),
                                    jsonobject.getString("image"),
                                    jsonobject.getString("name")));
                        }

                        adapter = new CardGameAdapter(gameArray);
                        rvGames.setLayoutManager(new GridLayoutManager(Game.this, 3));
                        adapter.setClickListener(new CardGameAdapter.ItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Intent intent = new Intent(Game.this, GameView.class);
                                bundle.putString("id", adapter.getItem(position).getId());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                        rvGames.setAdapter(adapter);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                            if (!v.canScrollVertically(1)) {
                                nestedScrollView.setOnTouchListener(new View.OnTouchListener() {
                                    @Override
                                    public boolean onTouch(View v, MotionEvent event) {
                                        return true;
                                    }
                                });
                                rvGames.setNestedScrollingEnabled(true);
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }
        });
    }
}


