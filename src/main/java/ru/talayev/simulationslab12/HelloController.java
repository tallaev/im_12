package ru.vorotov.simulationslab12;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.*;

public class HelloController implements Initializable {
    @FXML
    private Label currentState;
    @FXML
    private TextArea statsArea;

    int state = 0;
    double t = 0;
    double tau = 0;
    double total_time = 0;

    double[][] Q = {{-0.4, 0.3, 0.1}, {0.4, -0.8, 0.4}, {0.1, 0.4, -0.5}};

    double[] finalProbs = {8.0 / 21.0, 19.0 / 63.0, 20.0 / 63.0};
    List<State> states = new ArrayList<>();

    Random random = new Random();

    boolean isRunning = true;

    @FXML
    protected void onHelloButtonClick() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                t += 0.1;
                if (t > tau) {
                    switchState();
                }

                if (!isRunning) timer.cancel();
            }
        }, 0, 50);
    }

    private void switchState() {
        Platform.runLater(() -> {
            tau = Math.log(random.nextDouble()) / Q[state][state];
            states.get(state)
                    .setTime(
                            states.get(state).getTime() + tau
                    );
            total_time += tau;
            t = 0;

            double A = random.nextDouble();
            for (int i = 0; i < states.size(); i++) {
                if (i == state) continue;
                A -= -Q[state][i] / Q[state][state];
                if (A <= 0) {
                    state = i;
                    break;
                }
            }

            currentState.setText(states.get(state).getName());
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        states.add(new State("Солнечно"));
        states.add(new State("Пасмурно"));
        states.add(new State("Дождь"));
    }

    public void onStopButtonClick(ActionEvent actionEvent) {
        isRunning = false;

        statsArea.appendText("Общее количество времени: " + total_time + "\n");
        for (State item : states) {
            statsArea.appendText("В состоянии " + item.getName() + " проведено " + item.getTime() + " времени. " + item.getTime() / total_time + "\n");
        }

        double chi_square = 0;

        for (int i = 0; i < states.size(); i++) {
            chi_square += (states.get(i).getTime() * states.get(i).getTime()) / (total_time * finalProbs[i]);
        }
        chi_square -= total_time;

        statsArea.appendText("Хи-квадрат: " + chi_square + "\n");

        if (chi_square > 6) {
            statsArea.appendText("Различия значимы");
        } else {
            statsArea.appendText("Различия незначимы");
        }

    }
}