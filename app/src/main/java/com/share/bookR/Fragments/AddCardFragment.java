package com.share.bookR.Fragments;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ReplacementSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.share.bookR.R;
import com.google.android.material.textfield.TextInputEditText;


public class AddCardFragment extends Fragment {


    private TextInputEditText expiryDate,cardNumber,nickName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_add_card, container, false);
        init(view);
        expiryDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int start, int before) {
                if (charSequence.length()==1&&Integer.parseInt(charSequence.toString().replaceAll("/",""))>1){
                    expiryDate.setText("0"+expiryDate.getText().toString()+"/");
                    expiryDate.setSelection(3);
                }
                if (charSequence.length()==2&&Integer.parseInt(charSequence.toString().replaceAll("/",""))>12) {
                    expiryDate.setText("12/");
                    expiryDate.setSelection(3);
                } else if (charSequence.length()==2){
                    expiryDate.setText(expiryDate.getText().toString()+"/");
                    expiryDate.setSelection(3);
                }
                if (charSequence.toString().contains("/")){
                    if (charSequence.toString().replace("/","").contains("/")){
                        expiryDate.setText(expiryDate.getText().toString().substring(0,1));
                        expiryDate.setSelection(expiryDate.getText().toString().length());
                    }
                }
                if (charSequence.length()>=5){
                    //expiryDate.setText(expiryDate.getText().toString().substring(0,6));
                    //expiryDate.setText(charSequence.toString().substring(0,6));
                    nickName.requestFocus();
                }

                /*if (charSequence.length() == 2) {
                    if(start==2 && before==1 && !charSequence.toString().contains("/")){
                        expiryDate.setText(""+charSequence.toString().charAt(0));
                        expiryDate.setSelection(1);
                    }
                    else {
                        expiryDate.setText(charSequence + "/");
                        expiryDate.setSelection(3);
                    }
                }*/
                Log.v("tag",""+charSequence.length());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        cardNumber.addTextChangedListener(new TextWatcher() {
            private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
            private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
            private static final char DIVIDER = '-';

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()==16)
                    expiryDate.requestFocus();
                /*if (charSequence.toString().startsWith("4"))
                    cardNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visa, 0);
                else if(charSequence.toString().startsWith("5"))
                    cardNumber.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.mastercard, 0);*/
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Object[] paddingSpans = editable.getSpans(0, editable.length(), DashSpan.class);
                for (Object span : paddingSpans) {
                    editable.removeSpan(span);
                }

                addSpans(editable);
            }
            private static final int GROUP_SIZE = 4;

            private void addSpans(Editable editable) {

                final int length = editable.length();
                for (int i = 1; i * (GROUP_SIZE) < length; i++) {
                    int index = i * GROUP_SIZE;
                    editable.setSpan(new DashSpan(), index - 1, index,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        });



        return view;
    }

    private void init(View view) {
        expiryDate=view.findViewById(R.id.expiry_date);
        cardNumber=view.findViewById(R.id.card_number);
        nickName=view.findViewById(R.id.nick_name);

    }
    public class DashSpan extends ReplacementSpan {

        @Override
        public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, Paint.FontMetricsInt fm) {
            float padding = paint.measureText(" ", 0, 1);
            float textSize = paint.measureText(text, start, end);
            return (int) (padding + textSize);
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y,
                         int bottom, @NonNull Paint paint) {
            canvas.drawText(text.subSequence(start, end) + " ", x, y, paint);
        }
    }
}