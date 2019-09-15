package org.techtown.thread.scanthevoca_1st;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

public class Menu2Fragment extends Fragment {

    View view;
    int bound;

    private MySQLiteOpenHelper mySQLiteOpenHelper;

    TextView txtQuestion;
    EditText edtxtAnswer;
    Button btnSubmit;
    ImageView imageView01;
    Animation animation;

    List<Voca> vocas;

    Question question;

    //empty public constructor
    public Menu2Fragment() {

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_menu2, container, false);

        txtQuestion = rootView.findViewById(R.id.txtQuestion);
        edtxtAnswer = rootView.findViewById(R.id.edTxtAnswer);
        btnSubmit = rootView.findViewById(R.id.btnSubmit);


        imageView01 = rootView.findViewById(R.id.imageView);
        animation = new AlphaAnimation(0, 1);


        mySQLiteOpenHelper = new MySQLiteOpenHelper(Menu2Fragment.this.getActivity());


        vocas = mySQLiteOpenHelper.getAllVocas();
        bound = vocas.size();


        //when bound is not zero, get a new Question.
        if (bound == 0) {
            Toast.makeText(getActivity(), "비어있음.", Toast.LENGTH_SHORT).show();
            btnSubmit.setEnabled(false);
        } else {
            question = new Question();
            question.getNewQuestion();
            txtQuestion.setText(question.mean);
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myAns = edtxtAnswer.getText().toString().trim();


                if (myAns.equals(question.word)) {
                    Toast.makeText(getActivity(), "correct", Toast.LENGTH_SHORT).show();
                    edtxtAnswer.setText("");

                    imageView01.setImageResource(R.drawable.correct);
                    imageView01.setVisibility(View.VISIBLE);
                    animation.setDuration(2000);
                    imageView01.setVisibility(View.INVISIBLE);
                    imageView01.setAnimation(animation);

                    Voca voca = new Voca();
                    voca.setWord(question.word);

                    mySQLiteOpenHelper.zeromemo(voca);

                } else {
                    Toast.makeText(getActivity(), "try it again", Toast.LENGTH_SHORT).show();

                    imageView01.setImageResource(R.drawable.incorrect);
                    imageView01.setVisibility(View.VISIBLE);
                    animation.setDuration(2000);
                    imageView01.setVisibility(View.INVISIBLE);
                    imageView01.setAnimation(animation);
                    show();

                }

                if (bound != 0) {
                    question.getNewQuestion();
                    txtQuestion.setText(question.mean);
                } else {
                    btnSubmit.setEnabled(false);
                    txtQuestion.setText("");
                }
                edtxtAnswer.setText("");
            }
        });




        return rootView;
    }

    class Question {
        String word;
        String mean;

        Question() {
        }

        Question(String word, String mean) {
            this.word = word;
            this.mean = mean;
        }

        void getNewQuestion() {
            Random rnd = new Random();
            int p = rnd.nextInt(bound); // 0 <= p < bound
            word = vocas.get(p).getWord();
            mean = vocas.get(p).getMean();
            vocas.remove(p);
            bound = vocas.size();
        }
    }


    void show() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //builder.setTitle("AlertDialog Title");
        builder.setMessage(question.word);
        final AlertDialog ad = builder.create();

        //builder.show();
        ad.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               ad.dismiss();
                           }
                       }
                , 2000);
        //ad.dismiss();

    }

}
