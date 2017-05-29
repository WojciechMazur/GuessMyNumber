package com.wickedmonkstudio.guessmynumber;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ScoreActivity extends Activity {

    private TextView scoreText;
    private TextView rankingTextView;
    private Button playAgainButton;
    private SharedPreferences sharedPreferences;
    private String playerName = "";
    private Map<String, Integer> rankingMap;
    private int score;

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        openNameInputDialog();
        rankingMap = loadRanking();


    }

    private void saveRanking(Map<String, Integer> inputMap){
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("PlayerRanking", Context.MODE_PRIVATE);
        if(sharedPreferences!=null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("Ranking_Map").commit();
            editor.putString("Ranking_Map", jsonString);
            editor.commit();
        }
    }

    private Map<String, Integer> loadRanking() {
        Map<String, Integer> outputMap = new HashMap<>();
        SharedPreferences  sharedPreferences = getApplicationContext().getSharedPreferences("PlayerRanking", Context.MODE_PRIVATE);
        try{
            if(sharedPreferences!=null){
                String jsonString = sharedPreferences.getString("Ranking_Map", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysIterator = jsonObject.keys();
                while(keysIterator.hasNext()){
                    String key = keysIterator.next();
                    Integer value = (Integer) jsonObject.get(key);
                    outputMap.put(key, value);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
        return outputMap;
    }

    private static Map<String, Integer> sortByComarator(Map<String, Integer> unsortedMap, boolean ascending){
        List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(unsortedMap.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            @Override
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                if(ascending){
                    return o1.getValue().compareTo(o2.getValue());
                }else{
                    return o2.getValue().compareTo(o1.getValue());
                }
            }
        });

        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for(Map.Entry<String, Integer> entry : list){
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);
        scoreText=(TextView)findViewById(R.id.scoreText);
        playAgainButton=(Button)findViewById(R.id.playAgainButton);
        rankingTextView= (TextView)findViewById(R.id.rankingTextView);

        Intent intent = getIntent();
        score = Integer.parseInt(intent.getStringExtra("score"));
        scoreText.setText(score + " clicks!");

        playAgainButton.setOnClickListener((v -> {
            startActivity(new Intent(ScoreActivity.this, MainActivity.class));
        }
        ));

    }

    private void openNameInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter your name");
        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setMessage("Congratulation!!!\nPlease enter your name if you want to be classified in ranking.");
        builder.setView(input);



        builder.setPositiveButton("Save", (dialog, which) -> {
                    playerName = input.getText().toString();
                    rankingMap.put(playerName, score);

                    rankingMap = sortByComarator(rankingMap, true);

                    if(rankingMap.size()>5)
                        rankingMap = trim(rankingMap, 5);
                    saveRanking(rankingMap);
                    showRanking();
                }
        );
        builder.setNegativeButton("Skip", (dialog, which) -> {
            dialog.cancel();
            showRanking();
        });
        builder.show();
    }

    private void showRanking() {
        int counter =1;
        for(Map.Entry<String, Integer> entry : rankingMap.entrySet()){
            rankingTextView.append("\n"+counter++ +". "+entry.getKey() + " " + entry.getValue());
        }
    }

    private Map<String,Integer> trim(Map<String, Integer> rankingMap, final int i) {
        Map<String, Integer> result = new LinkedHashMap<>();
        int counter=0;
        for(Map.Entry<String, Integer> entry : rankingMap.entrySet()) {
            if(counter++<i)
                result.put(entry.getKey(), entry.getValue());
            else break;
        }
        return result;
    }
}
