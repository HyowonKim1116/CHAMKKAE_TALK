package com.chamkkae.chamkkae;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    // 변수 선언
    ArrayList<String> inputList = new ArrayList<>();
    ArrayList<String> answerList = new ArrayList<>();

    Boolean STUDY = false;
    Boolean FIRST;
    Boolean CHANGE;
    Boolean TYPING;

    String KEY_INPUTLIST = "key_inputList";
    String KEY_ANSWERLIST = "key_answerList";
    String KEY_FIRST = "key_first";
    String KEY_CHANGE = "key_change";
    String KEY_TYPING = "key_typing";
    String word;

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button chatBtn = findViewById(R.id.chatBtn);
        Button orderBtn = findViewById(R.id.orderBtn);
        Button listBtn = findViewById(R.id.listBtn);
        Button yesBtn = findViewById(R.id.yesBtn);
        Button noBtn = findViewById(R.id.noBtn);
        Button turnBtn = findViewById(R.id.turnBtn);
        Button typeBtn = findViewById(R.id.typeBtn);
        Button timeBtn = findViewById(R.id.timeBtn);
        Button exitBtn = findViewById(R.id.exitBtn);
        EditText userChat = findViewById(R.id.userChat);
        ImageButton sendBtn = findViewById(R.id.sendBtn);
        ImageView puppyTear = findViewById(R.id.puppyTear);
        LinearLayout btnLayout = findViewById(R.id.btnLayout);
        LinearLayout chatLayout = findViewById(R.id.chatLayout);
        LinearLayout orderLayout = findViewById(R.id.orderLayout);
        ListView wordList = findViewById(R.id.wordList);

        inputList = getList(this, KEY_INPUTLIST);
        answerList = getList(this, KEY_ANSWERLIST);
        FIRST = getBool(this, KEY_FIRST);
        CHANGE = getBool(this, KEY_CHANGE);
        TYPING = getBool(this, KEY_TYPING);

        if (!CHANGE) {
            setAnswer("참깨톡에 놀러오신 걸 환영해요~");
        } else {
            CHANGE = false;
            btnLayout.setVisibility(View.INVISIBLE);
            orderLayout.setVisibility(View.VISIBLE);
            exitBtn.setText("뒤로 가기");
            setAnswer(getString(R.string.turn_chat));
        }

        // 첫 실행 대화 목록 설정
        if (FIRST) {
            FIRST = false;
            inputList.add("안녕!"); answerList.add("반가워요~ (๑>ᴗ<๑)");
            inputList.add("이름이 뭐야?"); answerList.add("참깨라고 불러주세요!");
            inputList.add("지금 뭐해?"); answerList.add("당신 생각하고 있어요~");
            inputList.add("배고파?"); answerList.add("저는 항상 배고파요!");
            inputList.add("귀여워라"); answerList.add("더 많이 귀여워해주세요! ｡>﹏<｡");
        }

        // 글자 타이핑 방식 버튼 설정
        if (!TYPING) {typeBtn.setText("한 글자씩 말해줘");}

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, inputList);
        wordList.setOnItemClickListener((parent, view, position, id) -> {
            AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
            EditText editText = new EditText(this);
            String key = parent.getItemAtPosition(position).toString();
            String value = answerList.get(inputList.indexOf(key));

            editText.setInputType(InputType.TYPE_CLASS_TEXT);
            editText.setHint("대답 내용");
            editText.setText(value);
            editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(95)}); // 글자수 제한
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {editText.setError("대답 내용을 입력하세요");}
                }
            });

            listDialog.setTitle("대답을 변경/삭제하시겠어요?");
            listDialog.setMessage("입력: "+key+"\n대답: "+value);
            listDialog.setIcon(R.drawable.logo);
            listDialog.setView(editText);

            listDialog.setPositiveButton("변경", (dialog, which) -> {
                String newValue = editText.getText().toString();
                answerList.set(inputList.indexOf(key), newValue);
                setAnswer("대답을 변경했습니다!");
            });
            listDialog.setNegativeButton("삭제", (dialog, which) -> {
                inputList.remove(key); answerList.remove(value);
                adapter.notifyDataSetChanged();
                setAnswer("대답을 삭제했습니다!");
            });
            listDialog.setNeutralButton("복사", (dialog, which) -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("input", key);
                clipboard.setPrimaryClip(clipData);
                setAnswer("입력 내용을 복사했습니다!");
            });

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); // 화면 고정
            listDialog.show();
        });

        View.OnClickListener btnListener = v -> {
            Animation appear_from_right = AnimationUtils.loadAnimation(this, R.anim.appear_from_right);
            Animation disappear_to_left = AnimationUtils.loadAnimation(this, R.anim.disappear_to_left);
            btnLayout.setAnimation(disappear_to_left);
            btnLayout.setVisibility(View.INVISIBLE);
            exitBtn.setText("뒤로 가기");
            switch (v.getId()) {
                case R.id.chatBtn:
                    chatLayout.setAnimation(appear_from_right);
                    chatLayout.setVisibility(View.VISIBLE);
                    userChat.setVisibility(View.VISIBLE);
                    yesBtn.setVisibility(View.GONE);
                    noBtn.setVisibility(View.GONE);
                    userChat.setText("");
                    STUDY = false;
                    setAnswer("무슨 대화를 나눌까요?");
                    break;
                case R.id.orderBtn:
                    orderLayout.setAnimation(appear_from_right);
                    orderLayout.setVisibility(View.VISIBLE);
                    setAnswer("무엇이든 시켜만 주세요!");
                    break;
                case R.id.listBtn:
                    wordList.setAnimation(appear_from_right);
                    wordList.setVisibility(View.VISIBLE);
                    wordList.setAdapter(adapter);
                    if (inputList.size() > 0) {
                        setAnswer("참깨가 학습한 대화 목록입니다~");
                    } else {
                        puppyTear.setVisibility(View.VISIBLE);
                        setAnswer("아직 학습한 대화가 없습니다..ㅠㅠ");
                    }
                    break;
            }
        };
        chatBtn.setOnClickListener(btnListener);
        orderBtn.setOnClickListener(btnListener);
        listBtn.setOnClickListener(btnListener);

        View.OnClickListener chatListener = v -> {
            switch (v.getId()) {
                case R.id.sendBtn:
                    String input = userChat.getText().toString();
                    userChat.setText("");
                    if (STUDY) {
                        STUDY = false;
                        inputList.add(word);
                        answerList.add(input);
                        setAnswer("말을 배웠어요!");
                    } else if (inputList.contains(input)) {
                        setAnswer(answerList.get(inputList.indexOf(input)));
                    } else {
                        word = input;
                        userChat.setVisibility(View.GONE);
                        yesBtn.setVisibility(View.VISIBLE);
                        noBtn.setVisibility(View.VISIBLE);
                        setAnswer("모르는 말이에요..\n대답을 가르쳐 주시겠어요?");
                    }
                    break;
                case R.id.yesBtn:
                    STUDY = true;
                    yesBtn.setVisibility(View.GONE);
                    noBtn.setVisibility(View.GONE);
                    userChat.setVisibility(View.VISIBLE);
                    setAnswer("어떻게 대답할까요?");
                    break;
                case R.id.noBtn:
                    yesBtn.setVisibility(View.GONE);
                    noBtn.setVisibility(View.GONE);
                    userChat.setVisibility(View.VISIBLE);
                    setAnswer("알겠습니다~");
                    break;
            }
        };
        sendBtn.setOnClickListener(chatListener);
        yesBtn.setOnClickListener(chatListener);
        noBtn.setOnClickListener(chatListener);

        // 글자 입력 시 전송 버튼 보이도록 설정
        userChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {sendBtn.setVisibility(View.VISIBLE);}
                else {sendBtn.setVisibility(View.GONE);}
            }
        });

        View.OnClickListener orderListener = v -> {
            switch (v.getId()) {
                case R.id.turnBtn:
                    CHANGE = true;
                    if (turnBtn.getText().equals("불 꺼줘")) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }
                    break;
                case R.id.typeBtn:
                    if (TYPING) {
                        TYPING = false;
                        typeBtn.setText("한 글자씩 말해줘");
                        setAnswer("한 번에 말하는 방식으로\n바꿨습니다!");
                    } else {
                        TYPING = true;
                        typeBtn.setText("한 번에 말해줘");
                        setAnswer("한 글자씩 말하는 방식으로\n바꿨습니다!");
                    }
                    break;
                case R.id.timeBtn:
                    Calendar cal = Calendar.getInstance();
                    int hour = cal.get(Calendar.HOUR_OF_DAY);
                    int min = cal.get(Calendar.MINUTE);
                    String ampm, m; int h;

                    // 삼항 연산자 이용
                    ampm = (hour < 12 ? "오전 " : "오후 ");
                    h = (hour > 12 ? hour-12 : hour != 0 ? hour : 12);
                    m = (min > 0 ? min+"분" : "정각");

                    setAnswer("지금은 " + ampm + h + "시 " + m + "입니다~");
                    break;
            }
        };
        turnBtn.setOnClickListener(orderListener);
        typeBtn.setOnClickListener(orderListener);
        timeBtn.setOnClickListener(orderListener);

        exitBtn.setOnClickListener(v -> {
            Animation appear_from_left = AnimationUtils.loadAnimation(this, R.anim.appear_from_left);
            Animation disappear_to_right = AnimationUtils.loadAnimation(this, R.anim.disappear_to_right);
            btnLayout.setAnimation(appear_from_left);
            btnLayout.setVisibility(View.VISIBLE);
            switch (exitBtn.getText().toString()) {
                case "뒤로 가기":
                    exitBtn.setText("종료하기");
                    setAnswer("무엇을 하고 싶으신가요?");
                    if(chatLayout.getVisibility() == View.VISIBLE) {
                        chatLayout.setAnimation(disappear_to_right);
                        chatLayout.setVisibility(View.GONE);
                    } else if(orderLayout.getVisibility() == View.VISIBLE) {
                        orderLayout.setAnimation(disappear_to_right);
                        orderLayout.setVisibility(View.GONE);
                    } else {
                        wordList.setAnimation(disappear_to_right);
                        wordList.setVisibility(View.GONE);
                        puppyTear.setVisibility(View.GONE);
                    }
                    break;
                case "종료하기":
                    saveList(this, KEY_INPUTLIST, inputList);
                    saveList(this, KEY_ANSWERLIST, answerList);
                    saveBool(this, KEY_FIRST, FIRST);
                    saveBool(this, KEY_CHANGE, CHANGE);
                    saveBool(this, KEY_TYPING, TYPING);

                    AlertDialog.Builder exitDialog = new AlertDialog.Builder(this);
                    exitDialog.setTitle("참깨톡을 종료하시겠어요?");
                    exitDialog.setIcon(R.drawable.puppy);

                    exitDialog.setPositiveButton("확인", (dialog, which) ->
                            android.os.Process.killProcess(android.os.Process.myPid())
                    );
                    exitDialog.setNegativeButton("취소", (dialog, id) -> {});
                    exitDialog.show();
                    break;
            }
        });
    }

    // 타이핑 함수
    public void setAnswer(String string) {
        TextView puppyChat = findViewById(R.id.puppyChat);
        if (TYPING) {
            Handler handler = new Handler();
            for (int i=1; i<string.length()+1; i++) {
                String str = string.substring(0, i);
                handler.postDelayed(() -> puppyChat.setText(str), 70*i);
            }
        } else {
            puppyChat.setText(string);
        }
    }

    // 리스트 저장 함수
    private void saveList(Context context, String key, ArrayList<String> list) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        JSONArray jsonArray = new JSONArray();

        for (int i=0; i<list.size(); i++) {jsonArray.put(list.get(i));}

        if (!list.isEmpty()) {editor.putString(key, jsonArray.toString());}
        else {editor.putString(key, null);}

        editor.apply();
    }

    // 리스트 불러오기 함수
    private ArrayList<String> getList(Context context, String key) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        String json = pref.getString(key, null);
        ArrayList<String> list = new ArrayList<>();

        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i=0; i<jsonArray.length(); i++) {
                    String str = jsonArray.optString(i);
                    list.add(str);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    // Boolean 저장 함수
    private void saveBool(Context context, String key, Boolean bool) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, bool);
        editor.apply();
    }

    // Boolean 불러오기 함수
    private Boolean getBool(Context context, String key) {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (key.equals(KEY_CHANGE)) {return pref.getBoolean(key, false);}
        else {return pref.getBoolean(key, true);}
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveList(this, KEY_INPUTLIST, inputList);
        saveList(this, KEY_ANSWERLIST, answerList);
        saveBool(this, KEY_FIRST, FIRST);
        saveBool(this, KEY_CHANGE, CHANGE);
        saveBool(this, KEY_TYPING, TYPING);
    }

    // 뒤로 가기 버튼 제어
    @Override
    public void onBackPressed() {}
}