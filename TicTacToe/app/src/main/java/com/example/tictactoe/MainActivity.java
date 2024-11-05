package com.example.tictactoe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button[][] buttons = new Button[3][3];
    private boolean playerXTurn = true;
    private int roundCount;
    private SharedPreferences sharedPreferences;
    private int wins, losses, draws;
    private boolean isBotGame = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = getSharedPreferences("TicTacToePrefs", MODE_PRIVATE);
        boolean isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false);

        if (isDarkTheme) {
            setTheme(R.style.AppTheme_Dark);
        } else {
            setTheme(R.style.AppTheme_Light);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadStats();

        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = (Button) gridLayout.getChildAt(i * 3 + j);
                buttons[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onButtonClick((Button) v);
                    }
                });
            }
        }

        Switch botSwitch = findViewById(R.id.botSwitch);
        botSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isBotGame = isChecked;
                resetGame();
            }
        });

        Button themeButton = findViewById(R.id.themeButton);
        themeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTheme();
            }
        });

        Button statsButton = findViewById(R.id.statsButton);
        statsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showStats();
            }
        });
    }

    private void onButtonClick(Button button) {
        if (button.getText().toString().equals("")) {
            if (playerXTurn) {
                button.setText("X");
            } else {
                button.setText("O");
            }
            roundCount++;

            if (checkForWin()) {
                if (playerXTurn) {
                    playerWins();
                } else {
                    playerLoses();
                }
            } else if (roundCount == 9) {
                draw();
            } else {
                playerXTurn = !playerXTurn;
                updateStatus();

                if (isBotGame && !playerXTurn) {
                    makeBotMove();
                }
            }
        }
    }

    private boolean checkForWin() {
        String[][] field = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }

        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")) {
                return true;
            }
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")) {
                return true;
            }
        }

        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")) {
            return true;
        }
        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")) {
            return true;
        }

        return false;
    }

    private void playerWins() {
        Toast.makeText(this, "Игрок X победил!", Toast.LENGTH_SHORT).show();
        wins++;
        saveStats();
        resetGame();
    }

    private void playerLoses() {
        Toast.makeText(this, "Игрок O победил!", Toast.LENGTH_SHORT).show();
        losses++;
        saveStats();
        resetGame();
    }

    private void draw() {
        Toast.makeText(this, "Ничья!", Toast.LENGTH_SHORT).show();
        draws++;
        saveStats();
        resetGame();
    }

    private void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
            }
        }
        roundCount = 0;
        playerXTurn = true;
        updateStatus();
    }

    private void updateStatus() {
        TextView statusTextView = findViewById(R.id.statusTextView);
        if (playerXTurn) {
            statusTextView.setText("Ходит игрок X");
        } else {
            statusTextView.setText("Ходит игрок O");
        }
    }

    private void changeTheme() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean isDarkTheme = sharedPreferences.getBoolean("isDarkTheme", false);

        if (isDarkTheme) {
            setTheme(R.style.AppTheme_Light);
            editor.putBoolean("isDarkTheme", false);
        } else {
            setTheme(R.style.AppTheme_Dark);
            editor.putBoolean("isDarkTheme", true);
        }
        editor.apply();

        recreate();
    }

    private void showStats() {
        String stats = "Победы: " + wins + "\nПоражения: " + losses + "\nНичьи: " + draws;
        new AlertDialog.Builder(this)
                .setTitle("Статистика")
                .setMessage(stats)
                .setPositiveButton("OK", null)
                .show();
    }

    private void loadStats() {
        wins = sharedPreferences.getInt("wins", 0);
        losses = sharedPreferences.getInt("losses", 0);
        draws = sharedPreferences.getInt("draws", 0);
    }

    private void saveStats() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("wins", wins);
        editor.putInt("losses", losses);
        editor.putInt("draws", draws);
        editor.apply();
    }

    private void makeBotMove() {
        Random random = new Random();
        int row, col;
        do {
            row = random.nextInt(3);
            col = random.nextInt(3);
        } while (!buttons[row][col].getText().toString().equals(""));

        buttons[row][col].setText("O");
        roundCount++;

        if (checkForWin()) {
            playerLoses();
        } else if (roundCount == 9) {
            draw();
        } else {
            playerXTurn = true;
            updateStatus();
        }
    }
}