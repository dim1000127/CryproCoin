package com.example.cryptocoin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cryptocoin.cryptovalutepojo.CryptoValute;
import com.example.cryptocoin.metadatapojo.Metadata;
import com.example.cryptocoin.retrofit.RetrofitSingleton;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.Map;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ActivityConvertCryptoValute extends AppCompatActivity implements View.OnClickListener {

    public static final String PRICE_MESSAGE="PRICE_MESSAGE";
    public static final String SYMBOL_MESSAGE="SYMBOL_MESSAGE";
    public static final String ID_MESSAGE="ID_MESSAGE";
    public static final String ASSET_NUMBER = "CONVERTNUMBER";
    public static final String TYPE_ASSET = "ASSET";

    private Subscription subscription;

    private TextView textViewSymbolFirstAsset;
    private TextView textViewSymbolSecondAsset;
    private ImageView imageViewLogoFirstAsset;
    private ImageView imageViewLogoSecondAsset;
    private EditText editTextValueFirstAsset;
    private EditText editTextValueSecondAsset;
    private LinearLayout layoutConvertFirstAsset;
    private LinearLayout layoutConvertSecondAsset;
    private Button buttonCalculateConvert1;
    private Button buttonCalculateConvert2;
    private Button buttonCalculateConvert3;
    private Button buttonCalculateConvert4;
    private Button buttonCalculateConvert5;
    private Button buttonCalculateConvert6;
    private Button buttonCalculateConvert7;
    private Button buttonCalculateConvert8;
    private Button buttonCalculateConvert9;
    private Button buttonCalculateConvert0;
    private Button buttonCalculateConvertComma;
    private ImageButton buttonCalculateConvertBackspace;

    private CryptoValute dataCryptoValute = null;
    private Metadata metadata = null;

    private double priceFirstAsset = 11;
    private double priceSecondAsset = 1;
    private double dollarPrice = 1;
    //private double rublePrice = 75;
    private String idCryptoValuteAsset = "1";
    private String symbolMessageFirstAsset = null;
    private String symbolMessageSecondAsset = null;
    private boolean isConvertFirst = true;
    private boolean isConvertSecond = true;

    ActivityResultLauncher<Intent> firstStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        editTextValueFirstAsset.setText("0");
                        editTextValueSecondAsset.setText("0");
                        if (intent.getStringExtra(TYPE_ASSET).equals(Const.CRYPTOVALUTE)) {

                            symbolMessageFirstAsset = intent.getStringExtra(SYMBOL_MESSAGE);
                            priceFirstAsset = intent.getDoubleExtra(PRICE_MESSAGE, 0);
                            idCryptoValuteAsset = String.valueOf(intent.getIntExtra(ID_MESSAGE, 1));
                            textViewSymbolFirstAsset.setText(symbolMessageFirstAsset);

                            Picasso.get().load(metadata.getData().get(idCryptoValuteAsset).getLogo()).into(imageViewLogoFirstAsset);

                        } else if (intent.getStringExtra(TYPE_ASSET).equals(Const.FIAT)) {
                            symbolMessageFirstAsset = intent.getStringExtra(SYMBOL_MESSAGE);
                            if(symbolMessageFirstAsset.equals(Const.USD_SYMBOL)){
                                textViewSymbolFirstAsset.setText(symbolMessageFirstAsset);
                                priceFirstAsset = dollarPrice;

                                Picasso.get().load(R.drawable.ic_usd_logo).into(imageViewLogoFirstAsset);
                            }
                            else if(symbolMessageFirstAsset.equals(Const.RUB_SYMBOL)){
                                textViewSymbolFirstAsset.setText(symbolMessageFirstAsset);
                                //priceFirstAsset = dollarPrice / rublePrice;
                                Picasso.get().load(R.drawable.ic_rub_logo).into(imageViewLogoFirstAsset);
                            }
                        }
                    }
                }
            });

    ActivityResultLauncher<Intent> secondStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        Intent intent = result.getData();
                        editTextValueFirstAsset.setText("0");
                        editTextValueSecondAsset.setText("0");

                        if (intent.getStringExtra(TYPE_ASSET).equals(Const.CRYPTOVALUTE)) {

                            symbolMessageSecondAsset = intent.getStringExtra(SYMBOL_MESSAGE);
                            priceSecondAsset = intent.getDoubleExtra(PRICE_MESSAGE, 0);
                            idCryptoValuteAsset = String.valueOf(intent.getIntExtra(ID_MESSAGE, 1));
                            textViewSymbolSecondAsset.setText(symbolMessageSecondAsset);

                            Picasso.get().load(metadata.getData().get(idCryptoValuteAsset).getLogo()).into(imageViewLogoSecondAsset);

                        } else if (intent.getStringExtra(TYPE_ASSET).equals(Const.FIAT)) {
                            symbolMessageSecondAsset = intent.getStringExtra(SYMBOL_MESSAGE);
                            if(symbolMessageSecondAsset.equals(Const.USD_SYMBOL)){
                                textViewSymbolSecondAsset.setText(symbolMessageSecondAsset);
                                priceSecondAsset = dollarPrice;
                                Picasso.get().load(R.drawable.ic_usd_logo).into(imageViewLogoSecondAsset);
                            }
                            else if(symbolMessageSecondAsset.equals(Const.RUB_SYMBOL)){
                                textViewSymbolSecondAsset.setText(symbolMessageSecondAsset);
                                //priceSecondAsset = dollarPrice/rublePrice;
                                Picasso.get().load(R.drawable.ic_rub_logo).into(imageViewLogoSecondAsset);
                            }
                        }
                    }
                }
            });

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_convert_cv);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("Конвертер валют");

        layoutConvertFirstAsset = (LinearLayout) findViewById(R.id.view_select_first_asset);
        layoutConvertSecondAsset = (LinearLayout) findViewById(R.id.view_select_second_asset);
        imageViewLogoFirstAsset = (ImageView) findViewById(R.id.image_logo_first_asset);
        imageViewLogoSecondAsset = (ImageView) findViewById(R.id.image_logo_second_asset);
        textViewSymbolFirstAsset = (TextView) findViewById(R.id.tv_symbol_first_asset);
        textViewSymbolSecondAsset = (TextView) findViewById(R.id.tv_symbol_second_asset);
        editTextValueFirstAsset = (EditText) findViewById(R.id.et_value_first_asset);
        editTextValueSecondAsset = (EditText) findViewById(R.id.et_value_second_asset);
        buttonCalculateConvert1 = (Button) findViewById(R.id.btn_calculate_convert_1);
        buttonCalculateConvert2 = (Button) findViewById(R.id.btn_calculate_convert_2);
        buttonCalculateConvert3 = (Button) findViewById(R.id.btn_calculate_convert_3);
        buttonCalculateConvert4 = (Button) findViewById(R.id.btn_calculate_convert_4);
        buttonCalculateConvert5 = (Button) findViewById(R.id.btn_calculate_convert_5);
        buttonCalculateConvert6 = (Button) findViewById(R.id.btn_calculate_convert_6);
        buttonCalculateConvert7 = (Button) findViewById(R.id.btn_calculate_convert_7);
        buttonCalculateConvert8 = (Button) findViewById(R.id.btn_calculate_convert_8);
        buttonCalculateConvert9 = (Button) findViewById(R.id.btn_calculate_convert_9);
        buttonCalculateConvert0 = (Button) findViewById(R.id.btn_calculate_convert_0);
        buttonCalculateConvertComma = (Button) findViewById(R.id.btn_calculate_convert_comma);
        buttonCalculateConvertBackspace = (ImageButton) findViewById(R.id.btn_calculate_convert_backspace);

        buttonCalculateConvert1.setOnClickListener(this);
        buttonCalculateConvert2.setOnClickListener(this);
        buttonCalculateConvert3.setOnClickListener(this);
        buttonCalculateConvert4.setOnClickListener(this);
        buttonCalculateConvert5.setOnClickListener(this);
        buttonCalculateConvert6.setOnClickListener(this);
        buttonCalculateConvert7.setOnClickListener(this);
        buttonCalculateConvert8.setOnClickListener(this);
        buttonCalculateConvert9.setOnClickListener(this);
        buttonCalculateConvert0.setOnClickListener(this);
        buttonCalculateConvertComma.setOnClickListener(this);
        buttonCalculateConvertBackspace.setOnClickListener(this);
        layoutConvertFirstAsset.setOnClickListener(this);
        layoutConvertSecondAsset.setOnClickListener(this);

        editTextValueFirstAsset.requestFocus();
        editTextValueFirstAsset.setShowSoftInputOnFocus(false);
        editTextValueSecondAsset.setShowSoftInputOnFocus(false);

        editTextValueFirstAsset.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!isConvertSecond) {
                    String valueCvStr = editTextValueFirstAsset.getText().toString();
                    if (valueCvStr.isEmpty() == false) {
                        if(!valueCvStr.equals(",")) {
                            isConvertFirst = true;
                            double valEtFirstAsset = Double.parseDouble(valueCvStr.replace(",", "."));
                            double calculateVal = valEtFirstAsset * priceFirstAsset / priceSecondAsset;
                            if (calculateVal >= 1 || calculateVal == 0) {
                                editTextValueSecondAsset.setText(String.format("%.2f", calculateVal));
                            } else {
                                editTextValueSecondAsset.setText(String.format("%.6f", calculateVal));
                            }
                        }
                    }
                    else{
                        editTextValueSecondAsset.setText("0");
                    }
                }
            }
        });

        editTextValueSecondAsset.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!isConvertFirst) {
                    String valueSecondAssetStr = editTextValueSecondAsset.getText().toString();
                    if (valueSecondAssetStr.isEmpty() == false) {
                        if(!valueSecondAssetStr.equals(",")) {
                            isConvertSecond = true;
                            double valEtSecondAsset = Double.parseDouble(valueSecondAssetStr.replace(",", "."));
                            double calculateVal = valEtSecondAsset / priceFirstAsset * priceSecondAsset;
                            if (calculateVal >= 1 || calculateVal == 0) {
                                editTextValueFirstAsset.setText(String.format("%.2f", calculateVal));
                            } else {
                                editTextValueFirstAsset.setText(String.format("%.6f", calculateVal));
                            }
                        }
                    }
                    else{
                        editTextValueFirstAsset.setText("0");
                    }
                }
            }
        });

        editTextValueFirstAsset.setFilters(new InputFilter[] {new DecimalFilter(6)});
        editTextValueSecondAsset.setFilters(new InputFilter[] {new DecimalFilter(6)});
        editTextValueFirstAsset.setText("0");
        editTextValueSecondAsset.setText("0");

        getCryptoValuteData();
    }

    private void getCryptoValuteData(){
        if (subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
        subscription = RetrofitSingleton.getCryptoValuteObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, Object>>() {
                    @Override
                    public void onCompleted() {
                        Log.d("onCompleted", "onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Map<String, Object> _cryptoValuteMetadata) {
                        CryptoValute _cryptoValute = (CryptoValute) _cryptoValuteMetadata.get(Const.CRYPTOVALUTE_KEY_MAP);
                        Metadata _metadata = (Metadata) _cryptoValuteMetadata.get(Const.METADATA_KEY_MAP);
                        dataCryptoValute = _cryptoValute;
                        metadata = _metadata;
                        textViewSymbolFirstAsset.setText(dataCryptoValute.getData().get(0).getSymbol());
                        textViewSymbolSecondAsset.setText(Const.USD_SYMBOL);
                        priceFirstAsset = dataCryptoValute.getData().get(0).getQuote().getUsdDataCoin().getPrice();
                        String idCryptoConvert = String.valueOf(dataCryptoValute.getData().get(0).getId());
                        Picasso.get().load(metadata.getData().get(idCryptoConvert).getLogo()).into(imageViewLogoFirstAsset);
                        Picasso.get().load(R.drawable.ic_usd_logo).into(imageViewLogoSecondAsset);
                    }
                });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.view_select_first_asset:
                convertFirstAssetSelect();
                break;
            case R.id.view_select_second_asset:
                convertSecondAssetSelect();
                break;
            case R.id.btn_calculate_convert_1:
                if (editTextValueFirstAsset.hasFocus()){
                    updateTextFirstAsset("1");
                }
                else if(editTextValueSecondAsset.hasFocus()){
                    updateTextSecondAsset("1");
                }
                break;
            case R.id.btn_calculate_convert_2:
                if (editTextValueFirstAsset.hasFocus()){
                    updateTextFirstAsset("2");
                }
                else if(editTextValueSecondAsset.hasFocus()){
                    updateTextSecondAsset("2");
                }
                break;
            case R.id.btn_calculate_convert_3:
                if (editTextValueFirstAsset.hasFocus()){
                    updateTextFirstAsset("3");
                }
                else if(editTextValueSecondAsset.hasFocus()){
                    updateTextSecondAsset("3");
                }
                break;
            case R.id.btn_calculate_convert_4:
                if (editTextValueFirstAsset.hasFocus()){
                    updateTextFirstAsset("4");
                }
                else if(editTextValueSecondAsset.hasFocus()){
                    updateTextSecondAsset("4");
                }
                break;
            case R.id.btn_calculate_convert_5:
                if (editTextValueFirstAsset.hasFocus() ){
                    updateTextFirstAsset("5");
                }
                else if(editTextValueSecondAsset.hasFocus() ){
                    updateTextSecondAsset("5");
                }
                break;
            case R.id.btn_calculate_convert_6:
                if (editTextValueFirstAsset.hasFocus()){
                    updateTextFirstAsset("6");
                }
                else if(editTextValueSecondAsset.hasFocus()){
                    updateTextSecondAsset("6");
                }
                break;
            case R.id.btn_calculate_convert_7:
                if (editTextValueFirstAsset.hasFocus()){
                    updateTextFirstAsset("7");
                }
                else if(editTextValueSecondAsset.hasFocus()){
                    updateTextSecondAsset("7");
                }
                break;
            case R.id.btn_calculate_convert_8:
                if (editTextValueFirstAsset.hasFocus()){
                    updateTextFirstAsset("8");
                }
                else if(editTextValueSecondAsset.hasFocus()){
                    updateTextSecondAsset("8");
                }
                break;
            case R.id.btn_calculate_convert_9:
                if (editTextValueFirstAsset.hasFocus()){
                    updateTextFirstAsset("9");
                }
                else if(editTextValueSecondAsset.hasFocus()){
                    updateTextSecondAsset("9");
                }
                break;
            case R.id.btn_calculate_convert_0:
                if (editTextValueFirstAsset.hasFocus()){
                    updateTextFirstAsset("0");
                }
                else if(editTextValueSecondAsset.hasFocus()){
                    updateTextSecondAsset("0");
                }
                break;
            case R.id.btn_calculate_convert_comma:
                if (editTextValueFirstAsset.hasFocus()){
                    if(!editTextValueFirstAsset.getText().toString().contains(",")) {
                        updateTextFirstAsset(",");
                    }
                }
                else if(editTextValueSecondAsset.hasFocus()){
                    if(!editTextValueSecondAsset.getText().toString().contains(",")) {
                        updateTextSecondAsset(",");
                    }
                }
                break;
            case R.id.btn_calculate_convert_backspace:
                if (editTextValueFirstAsset.hasFocus()){
                    isConvertSecond = false;
                    int cursorPos = editTextValueFirstAsset.getSelectionStart();
                    int textLenght = editTextValueFirstAsset.getText().length();
                    if(cursorPos != 0 && textLenght != 0){
                        SpannableStringBuilder selection = (SpannableStringBuilder) editTextValueFirstAsset.getText();
                        selection.replace(cursorPos-1, cursorPos, "");
                        if(selection.toString().matches("0+")){
                            editTextValueFirstAsset.setText("0");
                            editTextValueFirstAsset.setSelection(1);
                        }
                        else {
                            editTextValueFirstAsset.setText(selection);
                            editTextValueFirstAsset.setSelection(cursorPos - 1);
                        }
                    }
                }
                else if(editTextValueSecondAsset.hasFocus()){
                    isConvertFirst = false;
                    int cursorPos = editTextValueSecondAsset.getSelectionStart();
                    int textLenght = editTextValueSecondAsset.getText().length();
                    if(cursorPos != 0 && textLenght != 0){
                        SpannableStringBuilder selection = (SpannableStringBuilder) editTextValueSecondAsset.getText();
                        selection.replace(cursorPos-1, cursorPos, "");
                        if(selection.toString().matches("0+")){
                            editTextValueSecondAsset.setText("0");
                            editTextValueSecondAsset.setSelection(1);
                        }
                        else {
                            editTextValueSecondAsset.setText(selection);
                            editTextValueSecondAsset.setSelection(cursorPos - 1);
                        }
                    }
                }
                break;
        }
    }

    private void convertFirstAssetSelect(){
        if(dataCryptoValute!=null && metadata != null){
            Intent intent = new Intent(ActivityConvertCryptoValute.this, ActivitySelectCryptoValuteConvert.class);
            intent.putExtra(Const.CRYPTOVALUTE_INTENT, (Serializable) dataCryptoValute);
            intent.putExtra(Const.METADATA_INTENT, (Serializable) metadata);
            //intent.putExtra(Const.ASSET_NUMBER_INTENT, numberCryptoConvert);
            firstStartForResult.launch(intent);
        }
    }

    private void convertSecondAssetSelect(){
        if(dataCryptoValute!=null && metadata != null){
            Intent intent = new Intent(ActivityConvertCryptoValute.this, ActivitySelectCryptoValuteConvert.class);
            intent.putExtra(Const.CRYPTOVALUTE_INTENT, (Serializable) dataCryptoValute);
            intent.putExtra(Const.METADATA_INTENT, (Serializable) metadata);
            //intent.putExtra(Const.ASSET_NUMBER_INTENT, numberCryptoConvert);
            secondStartForResult.launch(intent);
        }
    }

    private void updateTextFirstAsset(String strToAdd){
        isConvertSecond = false;
        String oldStr = editTextValueFirstAsset.getText().toString();
        int cursorPos = editTextValueFirstAsset.getSelectionStart();
        String leftStr = oldStr.substring(0, cursorPos);
        String rightStr = oldStr.substring(cursorPos);
        if(leftStr.equals("") && strToAdd.equals(",")){
            strToAdd = "0,";
            cursorPos++;
        }
        editTextValueFirstAsset.setText(String.format("%s%s%s", leftStr, strToAdd, rightStr));
        //editTextValueFirstAsset.setSelection(cursorPos + 1);
        if(editTextValueFirstAsset.getText().toString().equals("")){
            editTextValueFirstAsset.setText(oldStr);
            editTextValueFirstAsset.setSelection(cursorPos);
        }
        else {
            editTextValueFirstAsset.setSelection(cursorPos + 1);
        }
    }

    private void updateTextSecondAsset(String strToAdd){
        isConvertFirst = false;
        String oldStr = editTextValueSecondAsset.getText().toString();
        int cursorPos = editTextValueSecondAsset.getSelectionStart();
        String leftStr = oldStr.substring(0, cursorPos);
        String rightStr = oldStr.substring(cursorPos);
        if(leftStr.equals("") && strToAdd.equals(",")){
            strToAdd = "0,";
            cursorPos++;
        }
        editTextValueSecondAsset.setText(String.format("%s%s%s", leftStr, strToAdd, rightStr));
        //editTextValueSecondAsset.setSelection(cursorPos + 1);
        if( editTextValueSecondAsset.getText().toString().equals("")){
            editTextValueSecondAsset.setText(oldStr);
            editTextValueSecondAsset.setSelection(cursorPos);
        }
        else {
            editTextValueSecondAsset.setSelection(cursorPos + 1);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
